package ttftcuts.cuttingedge.portacart;

import java.lang.reflect.Method;

import ttftcuts.cuttingedge.util.NetworkUtil;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class ClickEventHandler {
	private KeyBinding sneak;
	private Method unpressMethod;
	public ClickEventHandler() {
		sneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;
		unpressMethod = ReflectionHelper.findMethod(KeyBinding.class, sneak, new String[]{"unpressKey", "func_74505_d", "j"});
	}
	
	private void unpressSneak() {
		try {
			unpressMethod.invoke(sneak);
		} catch (Exception e) {
			// discard I guess.
		}
	}
	
	@SubscribeEvent
	public void onClick(PlayerInteractEvent event) {
		if (event.world.isRemote && event.entityPlayer != null && event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.isSneaking()) {
			double dist = event.entityPlayer.getDistance(event.x + 0.5, event.y + 0.5, event.z + 0.5);
			if (dist <= 1.5) {
				Block block = event.world.getBlock(event.x, event.y, event.z);
				if (block instanceof BlockRailBase){
					ItemStack belt = BaublesApi.getBaubles(event.entityPlayer).getStackInSlot(3);
					if (belt != null && belt.getItem() instanceof ItemPortacart) {
						unpressSneak();
						event.entityPlayer.setSneaking(false);
						NetworkUtil.INSTANCE.sendToServer(new PortacartMessage(event.x, event.y, event.z));
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
