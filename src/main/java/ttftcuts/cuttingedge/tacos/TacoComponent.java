package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;

import ttftcuts.cuttingedge.util.ItemUtil;

import net.minecraft.item.ItemStack;

public class TacoComponent {
	public final String name;
	public ItemStack stack;
	public EnumComponentType type;
	public double size;
	public Map<EnumFlavour, Double> flavours;
	
	public TacoComponent(String name, ItemStack stack, EnumComponentType type, double size) {
		this.flavours = new HashMap<EnumFlavour, Double>();
		this.name = name;
		this.stack = stack;
		this.type = type;
	}
	
	public TacoComponent addFlavour(EnumFlavour flavour, double amount) {
		this.flavours.put(flavour, amount);
		return this;
	}
	
	public static boolean isComponent(ItemStack stack) {
		for (TacoComponent c : ModuleTacos.components.values()) {
			if (ItemUtil.areStacksEqual(c.stack, stack)) {
				return true;
			}
		}
		return false;
	}
	
	public static void register(TacoComponent component) {
		ModuleTacos.components.put(component.name, component);
	}
}
