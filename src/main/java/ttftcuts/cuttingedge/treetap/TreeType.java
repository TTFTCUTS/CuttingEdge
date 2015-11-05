package ttftcuts.cuttingedge.treetap;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class TreeType {
	public final Block trunk;
	public final int trunkMeta;
	public final Block leaves;
	public final int leafMeta;
	
	public final int canopyHeight;
	public final int radius;
	public final int maxHeight;
	
	public final Fluid fluid;
	public final double rate;
	
	public TreeType(Block trunk, int trunkMeta, Block leaves, int leafMeta, int canopyHeight, int radius, int maxHeight, Fluid fluid, double rate) {
		this.trunk = trunk;
		this.trunkMeta = trunkMeta;
		this.leaves = leaves;
		this.leafMeta = leafMeta;
		
		this.canopyHeight = canopyHeight;
		this.radius = radius;
		this.maxHeight = maxHeight;
		
		this.fluid = fluid;
		this.rate = rate;
	}
	
	public boolean isTrunk(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block == this.trunk && (this.trunkMeta == -1 || block.getDamageValue(world, x, y, z) == this.trunkMeta);
	}
	
	public boolean isLeaves(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block == this.leaves && (this.leafMeta == -1 || block.getDamageValue(world, x, y, z) == this.leafMeta);
	}
}
