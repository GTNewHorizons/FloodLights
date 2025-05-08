package de.keridos.floodlights.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.keridos.floodlights.client.gui.CreativeTabFloodlight;
import de.keridos.floodlights.reference.Names;
import de.keridos.floodlights.reference.Textures;
import de.keridos.floodlights.tileentity.TileEntityFL;

/**
 * Created by Keridos on 28.02.14. This Class describes the generic block this mod uses.
 */
public class BlockFL extends Block {

    protected String unlocName;
    public IIcon topIcon;
    public IIcon topOnIcon;
    public IIcon sideIcon;
    public IIcon botIcon;

    protected BlockFL(String unlocName, Material material, SoundType type, float hardness) {
        super(material);
        setStepSound(type);
        setHardness(hardness);
        setBlockName(unlocName);
        this.unlocName = unlocName;
        if (!unlocName.equals(Names.Blocks.PHANTOM_LIGHT) && !unlocName.equals(Names.Blocks.UV_LIGHTBLOCK)) {
            this.setCreativeTab(CreativeTabFloodlight.FL_TAB);
        }
    }

    @Override
    public String getUnlocalizedName() {
        return String
                .format("tile.%s%s", Textures.RESOURCE_PREFIX, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        topIcon = iconRegister
                .registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName() + "_top")));
        topOnIcon = iconRegister
                .registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName() + "_top_on")));
        botIcon = iconRegister
                .registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName() + "_bot")));
        sideIcon = iconRegister
                .registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName() + "_side")));
    }

    // For rotatable block, topIcon is front, botIcon is rear.
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == meta - 6) {
            return topOnIcon;
        } else if (meta > 5) {
            meta -= 6;
        }
        if (side == meta) {
            return topIcon;
        } else if (side == ForgeDirection.getOrientation(meta).getOpposite().ordinal()) {
            return botIcon;
        } else {
            return sideIcon;
        }
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(worldIn, player, x, y, z, meta);
        worldIn.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityFL) {
            TileEntityFL tileEntityFL = ((TileEntityFL) tileEntity);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            ArrayList<ItemStack> drops = new ArrayList<>();

            tileEntityFL.writeOwnToNBT(nbtTagCompound);
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 0);
            stack.setTagCompound(nbtTagCompound);

            drops.add(stack);
            return drops;
        }

        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityFL) {
            TileEntityFL tileFL = ((TileEntityFL) world.getTileEntity(x, y, z));
            if (itemStack.hasTagCompound()) {
                tileFL.readOwnFromNBT(itemStack.getTagCompound());
            }
            if (itemStack.hasDisplayName()) {
                tileFL.setCustomName(itemStack.getDisplayName());
            }
            tileFL.setOrientation(ForgeDirection.getOrientation(getFacing(entityLiving)));
            world.setBlockMetadataWithNotify(x, y, z, getFacing(entityLiving), 2);
        }
    }

    public int getFacing(EntityLivingBase entityLiving) {
        float rotationYaw = MathHelper.wrapAngleTo180_float(entityLiving.rotationYaw);
        float rotationPitch = (entityLiving.rotationPitch);
        int result = (rotationPitch < -45.0F ? 1
                : (rotationPitch > 45.0F ? 0
                        : ((MathHelper.floor_double(rotationYaw * 4.0F / 360.0F + 0.5D) & 3) + 2)));
        ForgeDirection[] direction = { ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH,
                ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST };
        return direction[result].ordinal();
    }
}
