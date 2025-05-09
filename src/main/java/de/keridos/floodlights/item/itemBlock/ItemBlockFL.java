package de.keridos.floodlights.item.itemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockFL extends ItemBlock {

    public ItemBlockFL(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flagIn) {
        super.addInformation(stack, player, tooltip, flagIn);

        if (stack.hasTagCompound()) {
            tooltip.add(StatCollector.translateToLocal("gui.floodlights:tooltipConfigured"));
        }
    }
}
