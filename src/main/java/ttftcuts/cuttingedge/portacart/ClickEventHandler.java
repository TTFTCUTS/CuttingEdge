package ttftcuts.cuttingedge.portacart;

import ttftcuts.cuttingedge.CuttingEdge;
import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class ClickEventHandler {
	@SubscribeEvent
	public void onClick(PlayerInteractEvent event) {
		CuttingEdge.logger.info("1");
		if (event.entityPlayer != null && event.action == Action.RIGHT_CLICK_BLOCK ){//&& event.entityPlayer.isSneaking()) {
			CuttingEdge.logger.info("2");
			Block block = event.world.getBlock(event.x, event.y, event.z);
			CuttingEdge.logger.info(block);
			if (block instanceof BlockRailBase){
				CuttingEdge.logger.info("3");
				ItemStack belt = BaublesApi.getBaubles(event.entityPlayer).getStackInSlot(3);
				if (belt != null && belt.getItem() instanceof ItemPortacart) {
					CuttingEdge.logger.info("4");
					if (ItemPortacart.placeCart(belt, event.entityPlayer, event.world, event.x, event.y, event.z)) {
						CuttingEdge.logger.info("5");
						event.setCanceled(true);
					}
				}
			}
			
		}
	}
}
