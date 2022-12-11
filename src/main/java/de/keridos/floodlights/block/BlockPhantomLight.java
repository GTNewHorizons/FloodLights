package de.keridos.floodlights.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.keridos.floodlights.reference.Names;
import de.keridos.floodlights.tileentity.TileEntityPhantomLight;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by Keridos on 01.10.14.
 * This Class implements the invislbe light block the mod uses to light up areas.
 */
public class BlockPhantomLight extends BlockFL implements ITileEntityProvider {
    public BlockPhantomLight() {
        super(Names.Blocks.PHANTOM_LIGHT, Material.glass, soundTypeCloth, 0.0F);
    }

    public BlockPhantomLight(String name, Material material, SoundType soundType, float hardness) {
        super(name, material, soundType, hardness);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
        return this.isCollidable();
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return false;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {}

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public int getLightValue() {
        return 15;
    }

    @Override
    public TileEntityPhantomLight createNewTileEntity(World world, int metadata) {
        return new TileEntityPhantomLight();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!(block instanceof BlockFL) && block != Blocks.air) {
            ((TileEntityPhantomLight) world.getTileEntity(x, y, z)).updateAllSources();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        ((TileEntityPhantomLight) world.getTileEntity(x, y, z)).updateAllSources();
        super.breakBlock(world, x, y, z, block, par6);
    }
}
