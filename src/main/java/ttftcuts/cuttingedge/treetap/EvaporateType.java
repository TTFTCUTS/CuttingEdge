package ttftcuts.cuttingedge.treetap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EvaporateType {
	public final FluidStack fluid;
	public final ItemStack output;
	
	public EvaporateType(FluidStack fluid, ItemStack output) {
		this.fluid = fluid;
		this.output = output;
	}
	
	public boolean fluidMatches(Fluid fluid) {
		return fluid == this.fluid.getFluid();
	}
}
