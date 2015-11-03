package ttftcuts.cuttingedge.portacart;

import java.util.List;

import ttftcuts.cuttingedge.CuttingEdge;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPortacart extends Item implements IBauble {
	
	public ItemPortacart() {
		this.setUnlocalizedName("portacart");
		this.setTextureName(CuttingEdge.MOD_ID+":portacart/portacart");
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}
	
	public static boolean placeCart(EntityPlayer player, World world, int x, int y, int z)
    {
		double dist = player.getDistance(x+0.5, y+0.5, z+0.5);
        if (dist <= 1.5 && BlockRailBase.func_150051_a(world.getBlock(x, y, z)))
        {
            if (!world.isRemote)
            {
            	EntityMinecart entityminecart = new EntityPortacart(world, x+0.5, y+0.5, z+0.5);

                world.spawnEntityInWorld(entityminecart);
                
                player.mountEntity(entityminecart);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean debug) {
		tooltip.add(StatCollector.translateToLocal("item.portacart.desc"));
		tooltip.add(StatCollector.translateToLocal("item.portacart.desc2"));
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		if (player.ridingEntity != null && player.ridingEntity instanceof EntityPortacart) {
			player.dismountEntity(player.ridingEntity);
			player.ridingEntity.setDead();
		}
	}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}
}
