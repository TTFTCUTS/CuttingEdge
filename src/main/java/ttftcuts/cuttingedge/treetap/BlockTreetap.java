package ttftcuts.cuttingedge.treetap;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import ttftcuts.cuttingedge.util.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTreetap extends BlockContainer {
	public BlockTreetap() {
		super(Material.cloth);
		this.setStepSound(soundTypeCloth);
		this.setBlockName("treetap.treetap");
		this.setHardness(1.0F);
		this.setBlockTextureName("cuttingedge:treetap/treetapicon");
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
		
        //CuttingEdge.logger.info("solid: "+solid+", tree: "+(tree != null)+", canPlace: "+this.canPlaceBlockAt(world, x, y, z));
        
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

		TileEntity te = world.getTileEntity(x, y, z);
		if (!world.isRemote && te != null && te instanceof TileTreetap) {
			TileTreetap tap = (TileTreetap)te;

			if (!entityplayer.isSneaking() && FluidUtil.fillPlayerItemFromFluidHandler(world, tap, entityplayer, tap.tank.getFluid())) {
				world.markBlockForUpdate(x, y, z);
				return true;
			}
		}
		return false;
	}
	
	@Override
    public int getRenderType() {
    	return RenderTreetap.renderId;
    }
    
    @Override
    public boolean isOpaqueCube() {
    	return false;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
    	return false;
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
    	int meta = world.getBlockMetadata(x, y, z);
    	ForgeDirection dir = ForgeDirection.getOrientation(meta);
    	double p = 1/16.0;
    	
    	double f = 3.5;
    	double s = 2;
    	
    	float minx = (float)( 0 - ((dir.offsetX == 0) ? -s*p : (f*p*Math.min(0, dir.offsetX))) );
    	float maxx = (float)( 1.0 - ((dir.offsetX == 0) ? s*p : (f*p*Math.max(0, dir.offsetX))) );
    	
    	float minz = (float)( 0 - ((dir.offsetZ == 0) ? -s*p : (f*p*Math.min(0, dir.offsetZ))) );
    	float maxz = (float)( 1.0 - ((dir.offsetZ == 0) ? s*p : (f*p*Math.max(0, dir.offsetZ))) );
    	
    	this.setBlockBounds(minx, 0, minz, maxx, (float)(1 - 3*p), maxz);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
