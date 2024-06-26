package de.keridos.floodlights.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import de.keridos.floodlights.FloodLights;
import de.keridos.floodlights.client.gui.GuiCarbonFloodlight;
import de.keridos.floodlights.client.gui.GuiElectricFloodlight;
import de.keridos.floodlights.client.gui.container.ContainerCarbonFloodlight;
import de.keridos.floodlights.client.gui.container.ContainerElectricFloodlight;
import de.keridos.floodlights.tileentity.TileEntityCarbonFloodlight;
import de.keridos.floodlights.tileentity.TileEntityFLElectric;

/**
 * Created by Keridos on 28.02.14. This Class handles the GUIs that this mod uses (will use soon).
 */
public class GuiHandler implements IGuiHandler {

    private static GuiHandler instance = null;

    private GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(FloodLights.instance, this);
    }

    public static GuiHandler getInstance() {
        if (instance == null) {
            instance = new GuiHandler();
        }
        return instance;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(x, y, z);

        switch (id) {
            case 0:
                if (entity != null && entity instanceof TileEntityCarbonFloodlight) {
                    return new ContainerCarbonFloodlight(player.inventory, (TileEntityCarbonFloodlight) entity);
                } else {
                    return null;
                }
            case 1:
                if (entity != null && entity instanceof TileEntityFLElectric) {
                    return new ContainerElectricFloodlight(player.inventory, (TileEntityFLElectric) entity);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(x, y, z);

        switch (id) {
            case 0:
                if (entity != null && entity instanceof TileEntityCarbonFloodlight) {
                    return new GuiCarbonFloodlight(player.inventory, (TileEntityCarbonFloodlight) entity);
                } else {
                    return null;
                }
            case 1:
                if (entity != null && entity instanceof TileEntityFLElectric) {
                    return new GuiElectricFloodlight(player.inventory, (TileEntityFLElectric) entity);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}
