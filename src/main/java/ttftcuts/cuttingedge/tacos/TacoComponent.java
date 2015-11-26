package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ttftcuts.cuttingedge.util.ItemUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class TacoComponent {
	public final String name;
	public List<ItemStack> stacks;
	public EnumComponentType type;
	public double size;
	public Map<EnumFlavour, Double> flavours;
	
	@SideOnly(Side.CLIENT)
	public IIcon icon;
	public int colour;
	public String iconpath;
	
	public static Comparator<TacoComponent> tooltipSorter = new Comparator<TacoComponent>() {
		@Override
		public int compare(TacoComponent o1, TacoComponent o2) {
			if (o1.type != o2.type) {
				return o1.type.compareTo(o2.type);
			}
			return o1.stacks.get(0).getDisplayName().compareToIgnoreCase(o2.stacks.get(0).getDisplayName());
		}
	};
	
	public static Comparator<TacoComponent> renderSorter = new Comparator<TacoComponent>() {
		@Override
		public int compare(TacoComponent o1, TacoComponent o2) {
			if (o1.type != o2.type) {
				return o1.type.compareTo(o2.type);
			}
			return o1.name.compareToIgnoreCase(o2.name);
		}
	};
	
	public TacoComponent(String name, ItemStack stack, EnumComponentType type, double size, String iconpath, int colour) {
		this (name, type, size, iconpath, colour);
		this.stacks = new ArrayList<ItemStack>();
		this.stacks.add(stack);
	}
	
	public TacoComponent(String name, String orename, EnumComponentType type, double size, String iconpath, int colour) {
		this (name, type, size, iconpath, colour);
		this.stacks = OreDictionary.getOres(orename);
	}
	
	protected TacoComponent(String name, EnumComponentType type, double size, String iconpath, int colour) {
		this.flavours = new HashMap<EnumFlavour, Double>();
		this.name = name;
		this.type = type;
		this.size = size;
		this.iconpath = iconpath;
		this.colour = colour;
	}
	
	public TacoComponent addFlavour(EnumFlavour flavour, double amount) {
		this.flavours.put(flavour, amount);
		return this;
	}
	
	public static TacoComponent getComponent(ItemStack stack) {
		for (TacoComponent c : ModuleTacos.components.values()) {
			for (ItemStack orestack : c.stacks) {
				if (ItemUtil.areStacksEqual(orestack, stack)) {
					return c;
				}
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
	
	public String toString() {
		return "["+this.getClass().getSimpleName()+": "+this.name+"]";
	}
}
