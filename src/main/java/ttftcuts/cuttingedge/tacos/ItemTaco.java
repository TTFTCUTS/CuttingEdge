package ttftcuts.cuttingedge.tacos;

import ttftcuts.cuttingedge.CuttingEdge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemTaco extends ItemFood {

	protected IIcon emptyIcon;
	
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
		TacoData data = TacoData.getData(stack);
		if (data != null) {
			return (int)Math.floor(data.hunger);
		}
		return 0;
	}

	// get saturation restored
	@Override
	public float func_150906_h(ItemStack stack) {
		TacoData data = TacoData.getData(stack);
		if (data != null) {
			return (int)Math.floor(data.saturation);
		}
		return 0;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		TacoData data = TacoData.getData(stack);
		if (data != null && data.container != null) {
			return data.container.stack.getRarity();
		}
		return super.getRarity(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		
		this.emptyIcon = register.registerIcon(CuttingEdge.MOD_ID+":empty");
		
		for (TacoComponent c : ModuleTacos.components.values()) {
			c.icon = register.registerIcon(c.iconpath);
		}
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return EnumComponentType.values().length*EnumComponentType.rendercount;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		TacoData data = TacoData.getData(stack);
		if (data != null && data.iconComponents.size() > 0) {
			if (pass >= data.iconComponents.size()) {
				return this.emptyIcon;
			}
			return data.iconComponents.get(pass).icon;
		}
		return super.getIcon(stack, pass);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
		TacoData data = TacoData.getData(stack);
		if (data != null) {
			if (pass < data.iconComponents.size()) {
				return data.iconComponents.get(pass).colour;
			}
		}
		return 0xFFFFFF;
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
