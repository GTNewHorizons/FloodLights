package de.keridos.floodlights.init;

import cpw.mods.fml.common.registry.GameRegistry;
import de.keridos.floodlights.item.ItemCarbonDissolver;
import de.keridos.floodlights.item.ItemCarbonLantern;
import de.keridos.floodlights.item.ItemFL;
import de.keridos.floodlights.item.ItemGlowingFilament;
import de.keridos.floodlights.item.ItemLightBulb;
import de.keridos.floodlights.item.ItemLightDebugTool;
import de.keridos.floodlights.item.ItemMantle;
import de.keridos.floodlights.item.ItemRawFilament;
import de.keridos.floodlights.reference.Names;

/**
 * Created by Keridos on 06.10.14.
 * This Class manages all items that the mod uses.
 */
public class ModItems {
    public static final ItemFL rawFilament = new ItemRawFilament();
    public static final ItemFL glowingFilament = new ItemGlowingFilament();
    public static final ItemFL lightBulb = new ItemLightBulb();
    public static final ItemFL carbonDissolver = new ItemCarbonDissolver();
    public static final ItemFL carbonLantern = new ItemCarbonLantern();
    public static final ItemFL mantle = new ItemMantle();
    public static final ItemFL lightDebugTool = new ItemLightDebugTool();

    public static void init() {
        GameRegistry.registerItem(rawFilament, Names.Items.RAW_FILAMENT);
        GameRegistry.registerItem(glowingFilament, Names.Items.GLOWING_FILAMENT);
        GameRegistry.registerItem(lightBulb, Names.Items.ELECTRIC_INCANDESCENT_LIGHT_BULB);
        GameRegistry.registerItem(carbonDissolver, Names.Items.CARBON_DISSOLVER);
        GameRegistry.registerItem(carbonLantern, Names.Items.CARBON_LANTERN);
        GameRegistry.registerItem(mantle, Names.Items.MANTLE);
        GameRegistry.registerItem(lightDebugTool, Names.Items.LIGHT_DEBUG_TOOL);
    }
}
