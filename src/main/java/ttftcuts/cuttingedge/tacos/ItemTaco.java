package ttftcuts.cuttingedge.tacos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemTaco extends ItemFood {

	public ItemTaco() {
		super(0, 0, false);
		this.setUnlocalizedName("tacos.taco");
	}
	public ItemTaco(int hunger, float sat, boolean wolf) {
		this();
	}
	public ItemTaco(int hunger, boolean wolf) {
		this();
	}

	// get hunger restored
	@Override
	public int func_150905_g(ItemStack stack) {
		return 0;
	}

	// get saturation restored
	@Override
	public float func_150906_h(ItemStack stack) {
		return 0;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return super.getRarity(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
	}

	@Override
	public int getRenderPasses(int metadata) {
		return super.getRenderPasses(metadata);
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return super.getIcon(stack, pass);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		TacoData data = TacoData.getData(stack);
		if (data != null && data.container != null) {
			return super.getItemStackDisplayName(stack) +": "+data.container.stack.getDisplayName();
		}
		return super.getItemStackDisplayName(stack);
	}

}
