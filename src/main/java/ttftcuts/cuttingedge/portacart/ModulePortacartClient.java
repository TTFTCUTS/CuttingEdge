package ttftcuts.cuttingedge.portacart;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModulePortacartClient extends ModulePortacart {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ClickEventHandler());
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		RenderingRegistry.registerEntityRenderingHandler(EntityPortacart.class, new RenderPortacart());
	}

}
