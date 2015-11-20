package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.List;

import ttftcuts.cuttingedge.CuttingEdge;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class TacoRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		ItemStack base = null;
		List<ItemStack> ingredients = new ArrayList<ItemStack>();
		
		CuttingEdge.logger.info("taco recipe");
		
		for (int i=0; i<grid.getSizeInventory(); i++) {
			ItemStack stack = grid.getStackInSlot(i);
			if (stack != null) {
				boolean container = stack.getItem() == ModuleTacos.tacoItem || TacoContainer.isContainer(stack);
				boolean component = TacoComponent.isComponent(stack);
				
				if (!(container || component)) {
					CuttingEdge.logger.info("item not container nor component: "+stack);
					return false;
				}
				
				if (container) {
					if (base != null) { 
						CuttingEdge.logger.info("not allowed two base items");
						return false; 
					}
					base = stack;
				}
				
				if (component) {
					ingredients.add(stack);
				}
			}
		}
		if (base == null || ingredients.isEmpty() || (ingredients.size() == 1 && ingredients.get(0) == base)) {
			CuttingEdge.logger.info("no base or no ingredients");
			return false;
		}
		
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack base = null;
		List<ItemStack> ingredients = new ArrayList<ItemStack>();
		
		for (int i=0; i<grid.getSizeInventory(); i++) {
			ItemStack stack = grid.getStackInSlot(i);
			if (stack != null) {
				boolean container = stack.getItem() == ModuleTacos.tacoItem || TacoContainer.isContainer(stack);
				boolean component = TacoComponent.isComponent(stack);
				
				if (container) {
					base = stack;
				}
				
				if (component) {
					ingredients.add(stack);
				}
			}
		}
		
		ItemStack output = base.getItem() == ModuleTacos.tacoItem ? base : new ItemStack(ModuleTacos.tacoItem);
		
		return output;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}


}
