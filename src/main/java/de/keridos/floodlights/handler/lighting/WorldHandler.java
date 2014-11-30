package de.keridos.floodlights.handler.lighting;

import de.keridos.floodlights.handler.ConfigHandler;
import de.keridos.floodlights.init.ModBlocks;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

import static de.keridos.floodlights.util.MathUtil.rotate;

/**
 * Created by Keridos on 03.10.14.
 * This Class stores every lighting block in its designated world and manages them.
 * Currently all algorithms for placing the lights are in this class.
 */
public class WorldHandler {
    private ConfigHandler configHandler = ConfigHandler.getInstance();
    private ArrayList<LightBlockHandle> lightBlocks = new ArrayList<LightBlockHandle>();
    private World world;
    private int lastPositionInList = 0;

    public WorldHandler(World worldinput) {
        this.world = worldinput;
        this.lastPositionInList = 0;
    }

    public int getDimensionID() {
        return this.world.provider.dimensionId;
    }

    public LightBlockHandle getFloodlightHandler(int x, int y, int z) {
        boolean added = false;
        if (lightBlocks.size() == 0) {
            added = true;
            lightBlocks.add(new LightBlockHandle(x, y, z));
        }
        for (LightBlockHandle f : lightBlocks) {
            int[] coords = {x, y, z};
            if (f.getCoords()[0] == coords[0] && f.getCoords()[1] == coords[1] && f.getCoords()[2] == coords[2]) {
                return f;
            }
        }
        if (!added) {
            lightBlocks.add(new LightBlockHandle(x, y, z));
        }
        return getFloodlightHandler(x, y, z);
    }

    private void straightSource(int sourceX, int sourceY, int sourceZ, ForgeDirection direction, boolean remove) {
        for (int i = 1; i <= configHandler.rangeStraightFloodlight; i++) {
            int x = sourceX + direction.offsetX * i;
            int y = sourceY + direction.offsetY * i;
            int z = sourceZ + direction.offsetZ * i;
            LightBlockHandle handler = getFloodlightHandler(x, y, z);
            if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
                if (remove) {
                    lightBlocks.get(lightBlocks.indexOf(handler)).removeSource(sourceX, sourceY, sourceZ);
                } else {
                    lightBlocks.get(lightBlocks.indexOf(handler)).addSource(sourceX, sourceY, sourceZ);
                }
            } else if (world.getBlock(x, y, z).isOpaqueCube() && !remove) {
                break;
            }
        }
    }

    private void narrowConeSource(int sourceX, int sourceY, int sourceZ, ForgeDirection direction, boolean remove) {
        boolean[] failedBeams = new boolean[9];    // for the additional beam to cancel when the main beams fail.
        for (int j = 0; j <= 16; j++) {
            if (j <= 8) {     // This is the main beams
                for (int i = 1; i <= configHandler.rangeConeFloodlight / 2; i++) {
                    // for 1st light:
                    if (i == 1) {
                        LightBlockHandle handler = getFloodlightHandler(sourceX + direction.offsetX, sourceY + direction.offsetY, sourceZ + direction.offsetZ);
                        if (world.getBlock(sourceX + direction.offsetX, sourceY + direction.offsetY, sourceZ + direction.offsetZ).isAir(world, sourceX + direction.offsetX, sourceY + direction.offsetY, sourceZ + direction.offsetZ)) {
                            if (remove) {
                                lightBlocks.get(lightBlocks.indexOf(handler)).removeSource(sourceX, sourceY, sourceZ);
                            } else {
                                lightBlocks.get(lightBlocks.indexOf(handler)).addSource(sourceX, sourceY, sourceZ);
                            }
                        } else if (world.getBlock(sourceX + direction.offsetX, sourceY + direction.offsetY, sourceZ + direction.offsetZ).isOpaqueCube() && !remove) {
                            return;
                        }
                    }
                    int a = 2 * i;
                    int b = 0;
                    int c = 0;
                    switch (j) {
                        case 0:
                            b += i;
                            break;
                        case 1:
                            b -= i;
                            break;
                        case 2:
                            c += i;
                            break;
                        case 3:
                            c -= i;
                            break;
                        case 4:
                            b += i;
                            c += i;
                            break;
                        case 5:
                            b += i;
                            c -= i;
                            break;
                        case 6:
                            b -= i;
                            c += i;
                            break;
                        case 7:
                            b -= i;
                            c -= i;
                            break;
                    }
                    int[] rotatedCoords = rotate(a, b, c, direction); // rotate the coordinate to the correct spot in the real world :)
                    int x = sourceX + rotatedCoords[0];
                    int y = sourceY + rotatedCoords[1];
                    int z = sourceZ + rotatedCoords[2];
                    LightBlockHandle handler = getFloodlightHandler(x, y, z);
                    if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
                        if (remove) {
                            lightBlocks.get(lightBlocks.indexOf(handler)).removeSource(sourceX, sourceY, sourceZ);
                        } else {
                            lightBlocks.get(lightBlocks.indexOf(handler)).addSource(sourceX, sourceY, sourceZ);
                        }
                    } else if (world.getBlock(x, y, z).isOpaqueCube() && !remove) {
                        if (i < configHandler.rangeConeFloodlight / 4) {   //This is for canceling the long rangs beams
                            failedBeams[j] = true;
                        }
                        break;
                    }
                }
            } else if (!failedBeams[j - 9]) { // This is for the inner beams at longer range
                for (int i = configHandler.rangeConeFloodlight / 4; i <= configHandler.rangeConeFloodlight / 2; i++) {
                    int a = 2 * i;
                    int b = 0;
                    int c = 0;
                    switch (j) {
                        case 9:
                            b += i / 2;
                            break;
                        case 10:
                            b -= i / 2;
                            break;
                        case 11:
                            c += i / 2;
                            break;
                        case 12:
                            c -= i / 2;
                            break;
                        case 13:
                            b += i / 2;
                            c += i / 2;
                            break;
                        case 14:
                            b += i / 2;
                            c -= i / 2;
                            break;
                        case 15:
                            b -= i / 2;
                            c += i / 2;
                            break;
                        case 16:
                            b -= i / 2;
                            c -= i / 2;
                            break;
                    }
                    int[] rotatedCoords = rotate(a, b, c, direction);
                    int x = sourceX + rotatedCoords[0];
                    int y = sourceY + rotatedCoords[1];
                    int z = sourceZ + rotatedCoords[2];
                    LightBlockHandle handler = getFloodlightHandler(x, y, z);
                    if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
                        if (remove) {
                            lightBlocks.get(lightBlocks.indexOf(handler)).removeSource(sourceX, sourceY, sourceZ);
                        } else {
                            lightBlocks.get(lightBlocks.indexOf(handler)).addSource(sourceX, sourceY, sourceZ);
                        }
                    } else if (world.getBlock(x, y, z).isOpaqueCube() && !remove) {
                        break;
                    }
                }
            }

        }
    }

    private void wideConeSource(int sourceX, int sourceY, int sourceZ, ForgeDirection direction, boolean remove) {
        for (int j = 0; j <= 8; j++) {
            for (int i = 1; i <= configHandler.rangeConeFloodlight / 2; i++) {
                int a = i;
                int b = 0;
                int c = 0;
                switch (j) {
                    case 0:
                        b += i;
                        break;
                    case 1:
                        b -= i;
                        break;
                    case 2:
                        c += i;
                        break;
                    case 3:
                        c -= i;
                        break;
                    case 4:
                        b += i;
                        c += i;
                        break;
                    case 5:
                        b += i;
                        c -= i;
                        break;
                    case 6:
                        b -= i;
                        c += i;
                        break;
                    case 7:
                        b -= i;
                        c -= i;
                        break;
                }
                int[] rotatedCoords = rotate(a, b, c, direction);
                int x = sourceX + rotatedCoords[0];
                int y = sourceY + rotatedCoords[1];
                int z = sourceZ + rotatedCoords[2];
                LightBlockHandle handler = getFloodlightHandler(x, y, z);
                if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
                    if (remove) {
                        lightBlocks.get(lightBlocks.indexOf(handler)).removeSource(sourceX, sourceY, sourceZ);
                    } else {
                        lightBlocks.get(lightBlocks.indexOf(handler)).addSource(sourceX, sourceY, sourceZ);
                    }
                } else if (world.getBlock(x, y, z).isOpaqueCube() && !remove) {
                    break;
                }
            }
        }
    }

    public void addSource(int sourceX, int sourceY, int sourceZ, ForgeDirection direction, int sourcetype) {
        if (sourcetype == 0) {
            straightSource(sourceX, sourceY, sourceZ, direction, false);
        } else if (sourcetype == 1) {
            narrowConeSource(sourceX, sourceY, sourceZ, direction, false);
        } else if (sourcetype == 2) {
            wideConeSource(sourceX, sourceY, sourceZ, direction, false);
        }
    }

    public void removeSource(int sourceX, int sourceY, int sourceZ, ForgeDirection direction, int sourcetype) {
        if (sourcetype == 0) {
            straightSource(sourceX, sourceY, sourceZ, direction, true);
        } else if (sourcetype == 1) {
            narrowConeSource(sourceX, sourceY, sourceZ, direction, true);
        } else if (sourcetype == 2) {
            wideConeSource(sourceX, sourceY, sourceZ, direction, true);
        }
    }

    public void updateRun() {
        World activeworld = DimensionManager.getWorld(world.provider.dimensionId);
        int j = lastPositionInList;
        for (int i = lastPositionInList; i < j + configHandler.refreshRate; i++) {
            if (i >= lightBlocks.size()) {
                lastPositionInList = 0;
                break;
            }
            lastPositionInList = i;
            LightBlockHandle f = (lightBlocks.get(i));
            int x = f.getCoords()[0];
            int y = f.getCoords()[1];
            int z = f.getCoords()[2];
            if (activeworld.getBlock(x, y, z).getUnlocalizedName().contains("blockLight") && f.sourceNumber() == 0) {
                activeworld.setBlockToAir(x, y, z);
                lightBlocks.remove(i);
                i--;
                j--;
            }
            if (f.sourceNumber() > 0 && activeworld.getBlock(x, y, z).isAir(activeworld, x, y, z)) {
                activeworld.setBlock(x, y, z, ModBlocks.blockFLLight);
            }
        }
    }

}