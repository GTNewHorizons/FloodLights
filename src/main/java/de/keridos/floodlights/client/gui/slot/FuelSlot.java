package de.keridos.floodlights.client.gui.slot;

import static de.keridos.floodlights.util.GeneralUtil.getBurnTime;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Keridos on 20.04.2015.
 * This Class
 */
public class FuelSlot extends Slot {
    public FuelSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return (getBurnTime(itemstack) > 0);
    }
}
