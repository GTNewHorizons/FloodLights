package de.keridos.floodlights.init;

import cpw.mods.fml.common.registry.GameRegistry;
import de.keridos.floodlights.block.BlockCarbonFloodlight;
import de.keridos.floodlights.block.BlockElectricFloodlight;
import de.keridos.floodlights.block.BlockGrowLight;
import de.keridos.floodlights.block.BlockPhantomLight;
import de.keridos.floodlights.block.BlockSmallElectricFloodlight;
import de.keridos.floodlights.block.BlockUVLight;
import de.keridos.floodlights.block.BlockUVLightBlock;
import de.keridos.floodlights.item.itemBlock.ItemBlockSmallElectricMetaBlock;
import de.keridos.floodlights.reference.Names;
import de.keridos.floodlights.reference.Reference;
import de.keridos.floodlights.tileentity.TileEntityCarbonFloodlight;
import de.keridos.floodlights.tileentity.TileEntityElectricFloodlight;
import de.keridos.floodlights.tileentity.TileEntityGrowLight;
import de.keridos.floodlights.tileentity.TileEntityPhantomLight;
import de.keridos.floodlights.tileentity.TileEntitySmallFloodlight;
import de.keridos.floodlights.tileentity.TileEntityUVLight;
import de.keridos.floodlights.tileentity.TileEntityUVLightBlock;
import net.minecraft.block.Block;

/**
 * Created by Keridos on 04.10.14.
 * This Class manages all blocks and TileEntities that this mod uses.
 */
public class ModBlocks {
    public static Block blockElectricLight;
    public static Block blockCarbonLight;
    public static Block blockPhantomLight;
    public static Block blockSmallElectricLight;
    public static Block blockUVLight;
    public static Block blockUVLightBlock;
    public static Block blockGrowLight;

    public static void setupBlocks() {
        blockElectricLight = new BlockElectricFloodlight();
        blockCarbonLight = new BlockCarbonFloodlight();
        blockPhantomLight = new BlockPhantomLight();
        blockSmallElectricLight = new BlockSmallElectricFloodlight();
        blockUVLight = new BlockUVLight();
        blockUVLightBlock = new BlockUVLightBlock();
        blockGrowLight = new BlockGrowLight();
    }

    public static void registerBlocks() {
        GameRegistry.registerBlock(blockElectricLight, Names.Blocks.ELECTRIC_FLOODLIGHT);
        GameRegistry.registerBlock(blockCarbonLight, Names.Blocks.CARBON_FLOODLIGHT);
        GameRegistry.registerBlock(blockPhantomLight, Names.Blocks.PHANTOM_LIGHT);
        GameRegistry.registerBlock(
                blockSmallElectricLight, ItemBlockSmallElectricMetaBlock.class, Names.Blocks.SMALL_ELECTRIC_FLOODLIGHT);
        GameRegistry.registerBlock(blockUVLight, Names.Blocks.UV_FLOODLIGHT);
        GameRegistry.registerBlock(blockUVLightBlock, Names.Blocks.UV_LIGHTBLOCK);
        GameRegistry.registerBlock(blockGrowLight, Names.Blocks.GROW_LIGHT);
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(
                TileEntityElectricFloodlight.class,
                Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.ELECTRIC_FLOODLIGHT);
        GameRegistry.registerTileEntity(
                TileEntityCarbonFloodlight.class,
                Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.CARBON_FLOODLIGHT);
        GameRegistry.registerTileEntity(
                TileEntitySmallFloodlight.class,
                Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.SMALL_ELECTRIC_FLOODLIGHT);
        GameRegistry.registerTileEntity(
                TileEntityUVLight.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.UV_FLOODLIGHT);
        GameRegistry.registerTileEntity(
                TileEntityPhantomLight.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.PHANTOM_LIGHT);
        GameRegistry.registerTileEntity(
                TileEntityUVLightBlock.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.UV_LIGHTBLOCK);
        GameRegistry.registerTileEntity(
                TileEntityGrowLight.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.GROW_LIGHT);
    }
}
