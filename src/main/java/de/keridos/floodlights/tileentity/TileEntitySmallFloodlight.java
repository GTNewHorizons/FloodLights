package de.keridos.floodlights.tileentity;

import static de.keridos.floodlights.util.MathUtil.rotate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import de.keridos.floodlights.handler.ConfigHandler;
import de.keridos.floodlights.reference.Names;

/**
 * Created by Keridos on 04.05.2015. This Class is the tile entity for the small floodlight.
 */
public class TileEntitySmallFloodlight extends TileEntityFLElectric {

    private boolean rotationState = false;

    public TileEntitySmallFloodlight() {
        super();
        this.rotationState = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey(Names.NBT.ROTATION_STATE)) {
            this.rotationState = nbtTagCompound.getBoolean(Names.NBT.ROTATION_STATE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean(Names.NBT.ROTATION_STATE, rotationState);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return (from.getOpposite().ordinal() == orientation.ordinal());
    }

    public void toggleRotationState() {
        rotationState = !rotationState;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean getRotationState() {
        return rotationState;
    }

    public void setRotationState(boolean rotationState) {
        this.rotationState = rotationState;
    }

    public void smallSource(boolean remove) {
        for (int i = 0; i < 5; i++) {
            int a = 0;
            int b = 0;
            int c = 0;
            if (i == 0) {
                a = 1;
                b = c = 0;
            } else if (i == 1) {
                a = c = 0;
                b = 1;
            } else if (i == 2) {
                a = c = 0;
                b = -1;
            } else if (i == 3) {
                a = b = 0;
                c = 1;
            } else if (i == 4) {
                a = b = 0;
                c = -1;
            }
            int[] rotatedCoords = rotate(a, b, c, this.orientation);
            int x = this.xCoord + rotatedCoords[0];
            int y = this.yCoord + rotatedCoords[1];
            int z = this.zCoord + rotatedCoords[2];
            setLightChecked(x, y, z, remove);
        }
    }

    public void updateEntity() {
        super.updateEntity();
        World world = this.getWorldObj();
        if (!world.isRemote) {
            int realEnergyUsage = ConfigHandler.energyUsageSmallFloodlight;
            if (timeout > 0) {
                timeout--;
                return;
            }
            if (active
                    && (storage.getEnergyStored() >= realEnergyUsage || storageEU >= (double) realEnergyUsage / 8.0D)) {
                if (update) {
                    smallSource(true);
                    smallSource(false);
                    world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    update = false;
                } else if (!wasActive) {
                    smallSource(false);
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
                        smallSource(true);
                        world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        wasActive = false;
                        timeout = ConfigHandler.timeoutFloodlights;
                        update = false;
                    }
        }
    }
}
