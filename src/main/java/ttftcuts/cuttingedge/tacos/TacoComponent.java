package ttftcuts.cuttingedge.tacos;

import java.util.Comparator;
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
	
	public static Comparator<TacoComponent> sorter = new Comparator<TacoComponent>() {
		@Override
		public int compare(TacoComponent o1, TacoComponent o2) {
			if (o1.type != o2.type) {
				return o1.type.compareTo(o2.type);
			}
			return o1.stack.getDisplayName().compareToIgnoreCase(o2.stack.getDisplayName());
		}
	};
	
	public TacoComponent(String name, ItemStack stack, EnumComponentType type, double size) {
		this.flavours = new HashMap<EnumFlavour, Double>();
		this.name = name;
		this.stack = stack;
		this.type = type;
		this.size = size;
	}
	
	public TacoComponent addFlavour(EnumFlavour flavour, double amount) {
		this.flavours.put(flavour, amount);
		return this;
	}
	
	public static TacoComponent getComponent(ItemStack stack) {
		for (TacoComponent c : ModuleTacos.components.values()) {
			if (ItemUtil.areStacksEqual(c.stack, stack)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean isComponent(ItemStack stack) {
		return getComponent(stack) != null;
	}
	
	public static void register(TacoComponent component) {
		ModuleTacos.components.put(component.name, component);
	}
}
