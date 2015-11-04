package ttftcuts.cuttingedge.treetap;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSapBucket extends Item {
	
	public ItemSapBucket() {
		this.setMaxStackSize(1);
		this.setTextureName("cuttingedge:treetap/sapbucket");
		this.setUnlocalizedName("sapbucket");
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(Items.bucket);
	}
}
