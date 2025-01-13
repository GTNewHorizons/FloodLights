package de.keridos.floodlights.tileentity;

import static de.keridos.floodlights.util.GeneralUtil.safeLocalize;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import de.keridos.floodlights.handler.ConfigHandler;
import de.keridos.floodlights.reference.Names;

/**
 * Created by Keridos on 01.10.14. This Class is the electric floodlight TileEntity.
 */
public class TileEntityElectricFloodlight extends TileEntityFLElectric {

    @Override
    public void updateEntity() {
        super.updateEntity();
        World world = this.getWorldObj();
        if (!world.isRemote) {
            int realEnergyUsage = ConfigHandler.energyUsage * (mode == 0 ? 1 : 2);
            if (timeout > 0) {
                timeout--;
                return;
            }
            if (active
                    && (storage.getEnergyStored() >= realEnergyUsage || storageEU >= (double) realEnergyUsage / 8.0D)) {
                if (update) {
                    removeSource(this.mode);
                    addSource(this.mode);
                    world.setBlockMetadataWithNotify(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            this.getOrientation().ordinal() + 6,
                            2);
                    update = false;
                } else if (!wasActive) {
                    addSource(this.mode);
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
                        removeSource(this.mode);
                        world.setBlockMetadataWithNotify(
                                this.xCoord,
                                this.yCoord,
                                this.zCoord,
                                world.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) - 6,
                                2);
                        wasActive = false;
                        timeout = ConfigHandler.timeoutFloodlights;
                        update = false;
                    }
        }
    }

    public void addSource(int mode) {
        if (mode == -1) {
            mode = this.mode;
        }
        if (mode == 0) {
            straightSource(false);
        } else if (mode == 1) {
            narrowConeSource(false);
        } else if (mode == 2) {
            wideConeSource(false);
        }
    }

    public void removeSource(int mode) {
        if (mode == -1) {
            mode = this.mode;
        }
        if (mode == 0) {
            straightSource(true);
        } else if (mode == 1) {
            narrowConeSource(true);
        } else if (mode == 2) {
            wideConeSource(true);
        }
    }

    public void changeMode(EntityPlayer player) {
        World world = this.getWorldObj();
        if (!world.isRemote) {
            int realEnergyUsage = ConfigHandler.energyUsage * (mode == 0 ? 1 : 4);
            removeSource(this.mode);
            mode = (mode == 2 ? 0 : mode + 1);
            if (active && (storage.getEnergyStored() >= realEnergyUsage || storageEU >= realEnergyUsage / 8.0D)) {
                addSource(this.mode);
            }
            String modeString = (mode == 0 ? Names.Localizations.STRAIGHT
                    : mode == 1 ? Names.Localizations.NARROW_CONE : Names.Localizations.WIDE_CONE);
            player.addChatMessage(
                    new ChatComponentText(safeLocalize(Names.Localizations.MODE) + ": " + safeLocalize(modeString)));
        }
    }
}
