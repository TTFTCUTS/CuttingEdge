package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ttftcuts.cuttingedge.util.ItemUtil;
import ttftcuts.cuttingedge.util.ItemUtil.ItemMatcher;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class TacoComponent {
	public final String name;
	public List<ItemStack> stacks;
	public EnumComponentType type;
	public double size;
	public Map<TacoFlavour, Double> flavours;
	public ComponentMatcher matcher;
	public ItemStack displayStack;
	public ComponentSizer sizer;
	
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
		this.displayStack = stack;
	}
	
	public TacoComponent(String name, String orename, EnumComponentType type, double size, String iconpath, int colour) {
		this (name, type, size, iconpath, colour);
		this.stacks = OreDictionary.getOres(orename);
		this.displayStack = this.stacks.get(0);
	}
	
	public TacoComponent(String name, ComponentMatcher matcher, ItemStack displaystack, EnumComponentType type, double size, String iconpath, int colour) {
		this (name, matcher, type, size, iconpath, colour);
		this.displayStack = displaystack;
	}
	
	protected TacoComponent(String name, ComponentMatcher matcher, EnumComponentType type, double size, String iconpath, int colour) {
		this.flavours = new HashMap<TacoFlavour, Double>();
		this.name = name;
		this.type = type;
		this.size = size;
		this.iconpath = iconpath;
		this.colour = colour;
		this.matcher = matcher;
		this.matcher.parent = this;
	}
	
	protected TacoComponent(String name, EnumComponentType type, double size, String iconpath, int colour) {
		this (name, new ComponentMatcher(), type, size, iconpath, colour);
	}
	
	public TacoComponent addFlavour(TacoFlavour flavour, double amount) {
		this.flavours.put(flavour, amount);
		return this;
	}
	
	public TacoComponent setSizer(ComponentSizer sizer) {
		this.sizer = sizer;
		return this;
	}
	
	public static TacoComponent getComponent(ItemStack stack) {
		for (TacoComponent c : ModuleTacos.components.values()) {
			if (c.matcher.matches(stack)) {
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
	
	public String toString() {
		return "["+this.getClass().getSimpleName()+": "+this.name+"]";
	}
	
	public int getSize(ItemStack stack) {
		if (this.sizer != null) {
			return this.sizer.size(stack);
		}
		return 1;
	}
	
	public static abstract class ComponentSizer {
		public static final ComponentSizer potionSizer = new ComponentSizer() {
			@SuppressWarnings("unchecked")
			@Override
			public int size(ItemStack stack) {
				if (stack.getItem() == Items.potionitem) {
					List<PotionEffect> effects = Items.potionitem.getEffects(stack);
					if (effects != null && !effects.isEmpty()) {
						for (PotionEffect e : effects) {
							return e.getAmplifier() + 1;
						}
					}
				}
				return 1;
			}
		};
		
		public abstract int size(ItemStack stack);
	}
	
	public static class ComponentMatcher extends ItemMatcher {
		public TacoComponent parent;
		
		@Override
		public boolean matches(ItemStack stack) {
			for (ItemStack orestack : parent.stacks) {
				if (ItemUtil.areStacksEqual(orestack, stack)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class PotionMatcher extends ComponentMatcher {
		Potion potion;
		
		public PotionMatcher(Potion potion) {
			this.potion = potion;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(ItemStack stack) {
			if (stack.getItem() == Items.potionitem) {
				List<PotionEffect> effects = Items.potionitem.getEffects(stack);
				if (effects != null && !effects.isEmpty()) {
					for (PotionEffect e : effects) {
						if (e.getPotionID() == this.potion.id) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}
}
