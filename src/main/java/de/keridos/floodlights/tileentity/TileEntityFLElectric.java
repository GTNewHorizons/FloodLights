package de.keridos.floodlights.tileentity;

import static de.keridos.floodlights.util.GeneralUtil.isItemStackValidElectrical;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import crazypants.enderio.machine.wireless.WirelessChargedLocation;
import de.keridos.floodlights.compatability.ModCompatibility;
import de.keridos.floodlights.reference.Names;
import de.keridos.floodlights.util.MathUtil;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

/**
 * Created by Keridos on 04.05.2015. This Class
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2")
public class TileEntityFLElectric extends TileEntityMetaFloodlight implements IEnergyHandler, IEnergySink {

    protected boolean wasAddedToEnergyNet;
    protected double storageEU;
    protected EnergyStorage storage = new EnergyStorage(50000);
    protected Object wirelessCharcedLocation;

    public TileEntityFLElectric() {
        super();
        wasAddedToEnergyNet = false;
    }

    @Override
    public void readOwnFromNBT(NBTTagCompound nbtTagCompound) {
        super.readOwnFromNBT(nbtTagCompound);
        storage.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey(Names.NBT.STORAGE_EU)) {
            this.storageEU = nbtTagCompound.getDouble(Names.NBT.STORAGE_EU);
        }
    }

    @Override
    public void writeOwnToNBT(NBTTagCompound nbtTagCompound) {
        super.writeOwnToNBT(nbtTagCompound);
        storage.writeToNBT(nbtTagCompound);
        nbtTagCompound.setDouble(Names.NBT.STORAGE_EU, storageEU);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    public void setEnergyStored(int energyStored) {
        storage.setEnergyStored(energyStored);
    }

    @Optional.Method(modid = "IC2")
    @Override
    public double injectEnergy(ForgeDirection forgeDirection, double v, double v1) {
        if ((double) (storage.getMaxEnergyStored() - storage.getEnergyStored()) >= (v * 8.0D)) {
            storage.modifyEnergyStored(MathUtil.truncateDoubleToInt(v * 8.0D));
        } else {
            storageEU += v - ((double) (storage.getMaxEnergyStored() - storage.getEnergyStored()) / 8.0D);
            storage.modifyEnergyStored(MathUtil.truncateDoubleToInt(v * 8.0D));
        }
        return 0.0D;
    }

    @Optional.Method(modid = "IC2")
    @Override
    public int getSinkTier() {
        return 4;
    }

    @Optional.Method(modid = "IC2")
    @Override
    public double getDemandedEnergy() {
        return Math.max(4000D - storageEU, 0.0D);
    }

    @Optional.Method(modid = "IC2")
    @Override
    public boolean acceptsEnergyFrom(TileEntity tileEntity, ForgeDirection forgeDirection) {
        return true;
    }

    @Optional.Method(modid = "IC2")
    protected void addToIc2EnergyNetwork() {
        if (!worldObj.isRemote) {
            EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }

    @Optional.Method(modid = "IC2")
    protected void removeFromIc2EnergyNetwork() {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        onChunkUnload();
    }

    @Override
    public void onChunkUnload() {
        if (wasAddedToEnergyNet && ModCompatibility.IC2Loaded) {
            removeFromIc2EnergyNetwork();

            wasAddedToEnergyNet = false;
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return isItemStackValidElectrical(itemstack);
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return isItemStackValidElectrical(itemstack);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        World world = this.getWorldObj();
        if (ModCompatibility.IC2Loaded && !wasAddedToEnergyNet && !world.isRemote) {
            addToIc2EnergyNetwork();
            wasAddedToEnergyNet = true;
        }
        if (!world.isRemote) {
            if (inventory[0] != null) {
                if (ModCompatibility.IC2Loaded) {
                    if (inventory[0].getItem() instanceof IElectricItem) {
                        double dischargeValue = (storage.getMaxEnergyStored() - (double) storage.getEnergyStored())
                                / 8.0D;
                        storage.modifyEnergyStored(
                                MathUtil.truncateDoubleToInt(
                                        8 * ElectricItem.manager
                                                .discharge(inventory[0], dischargeValue, 4, false, true, false)));
                    }
                }
                if (inventory[0].getItem() instanceof IEnergyContainerItem) {
                    IEnergyContainerItem item = (IEnergyContainerItem) inventory[0].getItem();
                    int dischargeValue = Math.min(
                            item.getEnergyStored(inventory[0]),
                            (storage.getMaxEnergyStored() - storage.getEnergyStored()));
                    storage.modifyEnergyStored(item.extractEnergy(inventory[0], dischargeValue, false));
                }
            }
            if (ModCompatibility.EnderIOLoaded) {
                int taken = receiveEnergyFromWireless(storage.getMaxEnergyStored() - storage.getEnergyStored());
                if (taken > 0) {
                    storage.modifyEnergyStored(taken);
                }
            }
        }
    }

    @Optional.Method(modid = "EnderIO")
    protected int receiveEnergyFromWireless(int amount) {
        if (amount > 0) {
            if (!(wirelessCharcedLocation instanceof WirelessChargedLocation)) {
                wirelessCharcedLocation = new WirelessChargedLocation(this);
            }
            return ((WirelessChargedLocation) wirelessCharcedLocation).takeEnergy(amount);
        }
        return 0;
    }
}
