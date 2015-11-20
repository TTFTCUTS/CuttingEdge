package ttftcuts.cuttingedge.util;

import net.minecraft.item.ItemStack;

public class ItemUtil {
	public static boolean areStacksEqual(ItemStack a, ItemStack b) {
		if (a.getItem() != b.getItem()) {
			return false;
		}
		
		if (a.getItemDamage() != b.getItemDamage()) {
			return false;
		}
		
		if (a.hasTagCompound() != b.hasTagCompound()) {
			return false;
		}
		
		if (a.hasTagCompound() && (!a.getTagCompound().equals(b.getTagCompound()))) {
			return false;
		}
		
		return true;
	}
}
