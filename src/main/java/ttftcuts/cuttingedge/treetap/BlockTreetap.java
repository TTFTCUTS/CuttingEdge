package ttftcuts.cuttingedge.treetap;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class BlockTreetap extends BlockContainer {
	
	public BlockTreetap() {
		super(Material.cloth);
		this.setStepSound(soundTypeCloth);
		this.setBlockName("treetap");
		this.setHardness(1.0F);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitx, float hity, float hitz, int meta) {
		return side;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
    {		
		ForgeDirection dir = ForgeDirection.getOrientation(side);
        boolean solid = world.isSideSolid(x - dir.offsetX, y, z - dir.offsetZ, dir);
        TreeType tree = getTappable(world, x - dir.offsetX, y, z - dir.offsetZ);
		
        return tree != null && solid && this.canPlaceBlockAt(world, x, y, z);
    }
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.isSideSolid(x - 1, y, z, EAST ) ||
               world.isSideSolid(x + 1, y, z, WEST ) ||
               world.isSideSolid(x, y, z - 1, SOUTH) ||
               world.isSideSolid(x, y, z + 1, NORTH);
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour)
    {
		int side = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int ox = x - dir.offsetX;
		int oz = z - dir.offsetZ;
		
		if (!world.isSideSolid(ox, y, oz, dir)) {
			this.dropBlockAsItem(world, x, y, z, 0, 0);
            world.setBlockToAir(x, y, z);
		}
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileTreetap(meta);
	}
	
	public static TreeType getTappable(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = block.getDamageValue(world, x, y, z);
		
		for (int i=0; i<ModuleTreetap.tappables.size(); i++) {
			TreeType tree = ModuleTreetap.tappables.get(i);
			
			if (block == tree.trunk && (tree.trunkMeta == -1 || meta == tree.trunkMeta)) {
				return tree;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitx, float hity, float hitz) {
		if (super.onBlockActivated(world, x, y, z, entityplayer, side, hitx, hity, hitz)) {
			return true;
		}

		ItemStack current = entityplayer.inventory.getCurrentItem();

		if (current != null) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null && tile instanceof TileTreetap) {
				TileTreetap tap = (TileTreetap)tile;
				
				if (FluidContainerRegistry.isEmptyContainer(current)) {
					FluidStack available = tap.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
					
					if (available != null) {
						ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
						
						FluidStack filling = FluidContainerRegistry.getFluidForFilledItem(filled);
						
						if (filling != null) {
							if (!entityplayer.capabilities.isCreativeMode) {
								if (current.stackSize > 1) {
									if (!entityplayer.inventory.addItemStackToInventory(filled)) {
										return false;
									} else {
										current.stackSize--;
									}
								} else {
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, filled);
									
								}
							}
							tap.drain(ForgeDirection.UNKNOWN, filling.amount, true);
							
							return true;
						}
					}
				} else if (current.getItem() instanceof IFluidContainerItem) {
					if (current.stackSize != 1) {
						return false;
					}
					
					if (!world.isRemote) {
						IFluidContainerItem container = (IFluidContainerItem) current.getItem();
						FluidStack liquid = container.getFluid(current);
						boolean mustDrain = liquid == null || liquid.amount == 0;
						if (mustDrain || !entityplayer.isSneaking()) {
							liquid = tap.drain(ForgeDirection.UNKNOWN, 1000, false);
							int qtyToFill = container.fill(current, liquid, true);
							tap.drain(ForgeDirection.UNKNOWN, qtyToFill, true);
						}
					}
				}
			}
		}
		
		return false;
	}
}
