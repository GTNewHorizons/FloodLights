package de.keridos.floodlights.tileentity;

import net.minecraft.world.World;

import de.keridos.floodlights.handler.ConfigHandler;
import de.keridos.floodlights.init.ModBlocks;

/**
 * Created by Keridos on 15/09/2015. This Class
 */
public class TileEntityUVLight extends TileEntityFLElectric {

    public TileEntityUVLight() {
        super();
    }

    public void setLightUV(int x, int y, int z) {
        if (worldObj.setBlock(x, y, z, ModBlocks.blockUVLightBlock)) {
            TileEntityUVLightBlock light = (TileEntityUVLightBlock) worldObj.getTileEntity(x, y, z);
            light.addSource(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    public void UVSource(boolean remove) {
        for (int i = 1; i <= ConfigHandler.rangeUVFloodlight; i++) {
            int x = this.xCoord + this.orientation.offsetX * i;
            int y = this.yCoord + this.orientation.offsetY * i;
            int z = this.zCoord + this.orientation.offsetZ * i;
            if (remove) {
                if (worldObj.getBlock(x, y, z) == ModBlocks.blockUVLightBlock) {
                    TileEntityUVLightBlock light = (TileEntityUVLightBlock) worldObj.getTileEntity(x, y, z);
                    light.removeSource(this.xCoord, this.yCoord, this.zCoord);
                }
            } else if (worldObj.getBlock(x, y, z).isAir(worldObj, x, y, z)
                    || worldObj.getBlock(x, y, z) == ModBlocks.blockPhantomLight) {
                        worldObj.setBlockToAir(x, y, z);
                        worldObj.removeTileEntity(x, y, z);
                        setLightUV(x, y, z);
                    } else
                if (worldObj.getBlock(x, y, z) == ModBlocks.blockUVLightBlock) {
                    TileEntityUVLightBlock light = (TileEntityUVLightBlock) worldObj.getTileEntity(x, y, z);
                    light.addSource(this.xCoord, this.yCoord, this.zCoord);
                } else if (worldObj.getBlock(x, y, z).isOpaqueCube()) {
                    break;
                }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        World world = this.getWorldObj();
        if (!world.isRemote) {
            int realEnergyUsage = ConfigHandler.energyUsageUVFloodlight;
            if (active
                    && (storage.getEnergyStored() >= realEnergyUsage || storageEU >= (double) realEnergyUsage / 8.0D)) {
                if (update) {
                    UVSource(true);
                    UVSource(false);
                    world.setBlockMetadataWithNotify(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            this.getOrientation().ordinal() + 6,
                            2);
                    update = false;
                } else if (!wasActive) {
                    UVSource(false);
                    world.setBlockMetadataWithNotify(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) + 6,
                            2);
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
                        UVSource(true);
                        world.setBlockMetadataWithNotify(
                                this.xCoord,
                                this.yCoord,
                                this.zCoord,
                                world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) - 6,
                                2);
                        wasActive = false;
                    }
        }
    }
}
