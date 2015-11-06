package ttftcuts.cuttingedge.treetap;

import ttftcuts.cuttingedge.CuttingEdge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEvaporator extends BlockContainer {

	private IIcon topIcon;
	private IIcon frontIconUnlit;
	private IIcon frontIconLit;

	protected BlockEvaporator() {
		super(Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEvaporator();
	}

	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float px, float py, float pz)
    {
		if (!player.isSneaking()) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEvaporator) {
				if (!world.isRemote) {
					player.openGui(CuttingEdge.instance, ModuleTreetap.uiEvaporator, world, x, y, z);
				}
				return true;
			}
		}
		return false;
    }
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        this.setRotation(world, x, y, z);
    }
	
    private void setRotation(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Block block = world.getBlock(x, y, z - 1);
            Block block1 = world.getBlock(x, y, z + 1);
            Block block2 = world.getBlock(x - 1, y, z);
            Block block3 = world.getBlock(x + 1, y, z);
            byte meta = 3;

            if (block.func_149730_j() && !block1.func_149730_j())
            {
                meta = 3;
            }

            if (block1.func_149730_j() && !block.func_149730_j())
            {
                meta = 2;
            }

            if (block2.func_149730_j() && !block3.func_149730_j())
            {
                meta = 5;
            }

            if (block3.func_149730_j() && !block2.func_149730_j())
            {
                meta = 4;
            }

            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase ent, ItemStack stack)
    {
        int l = MathHelper.floor_double((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }
        else if (l == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }
        else if (l == 2)
        {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }
        else if (l == 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        IIcon icon = this.getIcon(side, world.getBlockMetadata(x, y, z));
        
        if (icon == this.frontIconUnlit) {
        	TileEntity te = world.getTileEntity(x, y, z);
        	if (te instanceof TileEvaporator) {
        		boolean burn = ((TileEvaporator) te).burning;
        		CuttingEdge.logger.info("Block - burning: "+burn);
        		if (burn) {
        			icon = this.frontIconLit;
        		}
        	}
        }
        
        return icon;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 || side == 0 ? this.topIcon : (side == meta || meta == 0 && side == 3 ? this.frontIconUnlit : this.blockIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        this.blockIcon = register.registerIcon("furnace_side");
        this.frontIconUnlit = register.registerIcon("furnace_front_off");
        this.frontIconLit = register.registerIcon("furnace_front_on");
        this.topIcon = register.registerIcon("furnace_top");
    }
}
