package ttftcuts.cuttingedge.treetap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

public class TreetapEvents {

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		if (event.map.getTextureType() == 0) {
			if (ModuleTreetap.ourrubbersap) {
				ModuleTreetap.rubbersap.setIcons(event.map.registerIcon("cuttingedge:treetap/sapstill"),event.map.registerIcon("cuttingedge:treetap/sapflowing"));
			}
			
			if (ModuleTreetap.ourmaplesap) {
				ModuleTreetap.maplesap.setIcons(event.map.registerIcon("cuttingedge:treetap/maplestill"),event.map.registerIcon("cuttingedge:treetap/mapleflowing"));
			}
		}
	}
}
