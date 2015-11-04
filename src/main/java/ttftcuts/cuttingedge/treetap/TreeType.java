package ttftcuts.cuttingedge.treetap;

import net.minecraft.block.Block;
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
}
