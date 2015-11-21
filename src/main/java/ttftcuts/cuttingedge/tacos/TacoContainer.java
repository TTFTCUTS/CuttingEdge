package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;

import ttftcuts.cuttingedge.util.ItemUtil;

import net.minecraft.item.ItemStack;

public class TacoContainer {
	public final String name;
	public ItemStack stack;
	public Map<EnumComponentType, Double> capacities;
	
	public TacoContainer(String name, ItemStack stack) {
		this.capacities = new HashMap<EnumComponentType, Double>();
		this.name = name;
		this.stack = stack;
	}
	
	public TacoContainer setCapacity(EnumComponentType comp, Double amount) {
		this.capacities.put(comp, amount);
		return this;
	}
	
	public static TacoContainer getContainer(ItemStack stack) {
		for (TacoContainer c : ModuleTacos.containers.values()) {
			if (ItemUtil.areStacksEqual(c.stack, stack)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean isContainer(ItemStack stack) {
		return getContainer(stack) != null;
	}
	
	public static void register(TacoContainer container) {
		ModuleTacos.containers.put(container.name, container);
	}
}
