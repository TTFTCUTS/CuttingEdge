package ttftcuts.cuttingedge.treetap;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleTreetapClient extends ModuleTreetap {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		MinecraftForge.EVENT_BUS.register(new TreetapEvents());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		RenderingRegistry.registerBlockHandler(new RenderTreetap(RenderingRegistry.getNextAvailableRenderId()));
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileTreetap.class, new RenderTileTreetap());
	}
	
}
