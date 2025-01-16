package de.keridos.floodlights.tileentity;

import static de.keridos.floodlights.util.GeneralUtil.safeLocalize;
import static de.keridos.floodlights.util.MathUtil.rotate;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import de.keridos.floodlights.handler.ConfigHandler;
import de.keridos.floodlights.reference.Names;
import de.keridos.floodlights.util.BlockPos;
import de.keridos.floodlights.util.GeneralUtil;
import de.keridos.floodlights.util.RandomUtil;

/**
 * Created by Keridos on 04.05.2015. This Class is the tile entity for the small floodlight.
 */
public class TileEntityGrowLight extends TileEntityFLElectric {

    private long nextGrowTick = 0;

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return (from.getOpposite().ordinal() == orientation.ordinal());
    }

    public void growSource(boolean remove) {
        int[] rotatedCoords = rotate(1, 0, 0, this.orientation);
        int x = this.xCoord + rotatedCoords[0];
        int y = this.yCoord + rotatedCoords[1];
        int z = this.zCoord + rotatedCoords[2];
        setLightChecked(x, y, z, remove);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        World world = this.getWorldObj();
        if (!world.isRemote) {
            int realEnergyUsage = ConfigHandler.energyUsageGrowLight;
            if (timeout > 0) {
                timeout--;
                return;
            }
            if (active
                    && (storage.getEnergyStored() >= realEnergyUsage || storageEU >= (double) realEnergyUsage / 8.0D)) {
                if (world.getWorldTime() > nextGrowTick) {
                    BlockPos blockPosTarget = new BlockPos(
                            this.xCoord + this.orientation.offsetX * 2,
                            this.yCoord + this.orientation.offsetY * 2,
                            this.zCoord + this.orientation.offsetZ * 2);
                    BlockPos blockPosFront = new BlockPos(
                            this.xCoord + this.orientation.offsetX,
                            this.yCoord + this.orientation.offsetY,
                            this.zCoord + this.orientation.offsetZ);
                    Block block = worldObj.getBlock(blockPosTarget.posX, blockPosTarget.posY, blockPosTarget.posZ);
                    Block blockFront = worldObj.getBlock(blockPosFront.posX, blockPosFront.posY, blockPosFront.posZ);
                    if (GeneralUtil.isBlockValidGrowable(block, world, blockPosTarget)
                            && blockFront.isAir(world, blockPosFront.posX, blockPosFront.posY, blockPosFront.posZ)) {
                        ((IGrowable) block).func_149853_b(
                                world,
                                RandomUtil.random,
                                blockPosTarget.posX,
                                blockPosTarget.posY,
                                blockPosTarget.posZ);
                    }
                    nextGrowTick = world.getWorldTime()
                            + RandomUtil.getRandomTickTimeoutFromFloatChance(ConfigHandler.chanceGrowLight);
                }
                if (update) {
                    if (mode == 0) {
                        growSource(true);
                        growSource(false);
                    }
                    world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    update = false;
                } else if (!wasActive) {
                    if (mode == 0) {
                        growSource(false);
                    }
                    world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
                if (storageEU >= (double) realEnergyUsage / 8.0D) {
                    storageEU -= (double) realEnergyUsage / 8.0D;
                } else {
                    storage.modifyEnergyStored(-realEnergyUsage);
                }
                wasActive = true;
            } else if ((!active
                    || (storage.getEnergyStored() < realEnergyUsage && storageEU < (double) realEnergyUsage / 8.0D))
                    && wasActive) {
                        if (mode == 0) {
                            growSource(true);
                        }
                        world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        wasActive = false;
                        timeout = ConfigHandler.timeoutFloodlights;
                        update = false;
                    }
        }
    }

    public void changeMode(EntityPlayer player) {
        World world = this.getWorldObj();
        if (!world.isRemote) {
            if (mode == 0) {
                growSource(true);
            }
            mode = (mode == 1 ? 0 : mode + 1);
            if (active
                    && (storage.getEnergyStored() >= ConfigHandler.energyUsage
                            || storageEU >= ConfigHandler.energyUsage / 8.0D)
                    && mode == 0) {
                growSource(false);
            }
            String modeString = (mode == 0 ? Names.Localizations.LIGHTING : Names.Localizations.DARK_LIGHT);
            player.addChatMessage(
                    new ChatComponentText(safeLocalize(Names.Localizations.MODE) + ": " + safeLocalize(modeString)));
        }
    }
}
