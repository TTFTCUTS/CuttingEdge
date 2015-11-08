package ttftcuts.cuttingedge.treetap;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemSapBucket extends Item {
	
	private String[] types = {"rubber", "maple"};
	private IIcon[] icons;
	
	public ItemSapBucket() {
		this.setMaxStackSize(1);
		this.setTextureName("cuttingedge:treetap/sapbucket");
		this.setUnlocalizedName("treetap.sapbucket");
		
		this.setHasSubtypes(true);
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(Items.bucket);
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dam) {
        return this.icons[dam % icons.length];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = MathHelper.clamp_int(stack.getItemDamage(), 0, types.length);
        return super.getUnlocalizedName() + "." + types[i];
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    	list.add(new ItemStack(item, 1, 0));
    	
    	if (Loader.isModLoaded("harvestcraft")) {
    		list.add(new ItemStack(item, 1,1));
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        icons = new IIcon[types.length];

        for (int x = 0; x < types.length; x++) {
            icons[x] = ir.registerIcon("cuttingedge:treetap/sapbucket_" + types[x]);
        }
    }
}
