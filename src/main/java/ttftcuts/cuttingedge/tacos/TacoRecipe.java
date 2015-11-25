package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class TacoRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		ItemStack base = null;
		List<ItemStack> ingredients = new ArrayList<ItemStack>();
		
		for (int i=0; i<grid.getSizeInventory(); i++) {
			ItemStack stack = grid.getStackInSlot(i);
			if (stack != null) {
				boolean container = stack.getItem() == ModuleTacos.tacoItem || TacoContainer.isContainer(stack);
				boolean component = TacoComponent.isComponent(stack);
				
				if (!(container || component)) {
					return false;
				}
				
				if (container) {
					if (base != null) { 
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
			return false;
		}
		
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack base = null;
		TacoContainer basecontainer = null;
		List<TacoComponent> ingredients = new ArrayList<TacoComponent>();
		
		for (int i=0; i<grid.getSizeInventory(); i++) {
			ItemStack stack = grid.getStackInSlot(i);
			if (stack != null) {
				TacoContainer container = TacoContainer.getContainer(stack);
				TacoComponent component = TacoComponent.getComponent(stack);
				
				if (stack.getItem() == ModuleTacos.tacoItem || container != null) {
					base = stack.copy();
					if (container != null) {
						basecontainer = container;
					}
				}
				
				if (component != null) {
					ingredients.add(component);
				}
			}
		}
		
		ItemStack output = base.getItem() == ModuleTacos.tacoItem ? base : new ItemStack(ModuleTacos.tacoItem);
		
		TacoData data = TacoData.getData(output);
		if (basecontainer != null) {
			data.setContainer(basecontainer);
		}
		
		for (TacoComponent comp : ingredients) {
			if (!data.addComponent(comp)) {
				return null;
			}
		}
		data.calculateHungerSaturation();
		
		TacoData.setData(output, data);
		
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
