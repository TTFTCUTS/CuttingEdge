package ttftcuts.cuttingedge.treetap;

import java.util.Random;

import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.util.FluidUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEvaporator extends BlockContainer {
	private final Random dropRand = new Random();
	
	private IIcon topIcon;
	private IIcon bottomIcon;
	private IIcon frontIconUnlit;
	private IIcon frontIconLit;

	protected BlockEvaporator() {
		super(Material.rock);
		this.setHardness(3.5F);
		this.setStepSound(soundTypeStone);
		this.setBlockName("treetap.evaporator");
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
				TileEvaporator evap = (TileEvaporator)tile;
				if (FluidUtil.fillFluidHandlerWithPlayerItem(world, evap, player)) {
					evap.markDirty();
					world.markBlockForUpdate(x, y, z);
					return true;
				}
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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEvaporator evap = (TileEvaporator)world.getTileEntity(x, y, z);

        if (evap != null)
        {
            for (int i1 = 0; i1 < evap.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = evap.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.dropRand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.dropRand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.dropRand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j1 = this.dropRand.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.dropRand.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.dropRand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.dropRand.nextGaussian() * f3);
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {
    	TileEvaporator evap = (TileEvaporator)world.getTileEntity(x, y, z);
        if (evap != null && evap.burning)
        {
            int meta = world.getBlockMetadata(x, y, z);
            float f = (float)x + 0.5F;
            float f1 = (float)y + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
            float f2 = (float)z + 0.5F;
            float f3 = 0.52F;
            float f4 = rand.nextFloat() * 0.6F - 0.3F;

            if (meta == 4)
            {
                world.spawnParticle("smoke", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 5)
            {
                world.spawnParticle("smoke", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 2)
            {
                world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 3)
            {
                world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
            }
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
    	meta = meta % 6;
        return side == 0 ? this.bottomIcon : side == 1 ? this.topIcon : (side == meta || meta == 0 && side == 3 ? this.frontIconUnlit : this.blockIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        this.blockIcon = register.registerIcon("cuttingedge:treetap/evap_side");
        this.frontIconUnlit = register.registerIcon("cuttingedge:treetap/evap_front_off");
        this.frontIconLit = register.registerIcon("cuttingedge:treetap/evap_front_on");
        this.topIcon = register.registerIcon("cuttingedge:treetap/evap_top");
        this.bottomIcon = register.registerIcon("cuttingedge:treetap/evap_bottom");
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
    	TileEvaporator evap = (TileEvaporator)world.getTileEntity(x, y, z);
    	if (evap != null) {
    		return evap.burning ? 13 : 0;
    	}
        return super.getLightValue(world, x, y, z);
    }
}
