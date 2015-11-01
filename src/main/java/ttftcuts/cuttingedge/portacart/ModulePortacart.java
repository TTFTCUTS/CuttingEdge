package ttftcuts.cuttingedge.portacart;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;
import ttftcuts.cuttingedge.util.EntityUtil;

public class ModulePortacart extends Module {

	public ModulePortacart() {
		super("portacart");
	}

	@Override
	public boolean shouldLoad() {
		return Loader.isModLoaded("Baubles");
	}
	
	@Override
	public Module makeClient() {
		return new ModulePortacartClient();
	}
	
	@Override
	public void configure(Configuration config) {

	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerItem(new ItemPortacart(), "portacart");
		
		EntityRegistry.registerModEntity(EntityPortacart.class, "portacart", EntityUtil.getNextEntityID(), CuttingEdge.instance, 64, 1, true);
		
		MinecraftForge.EVENT_BUS.register(new ClickEventHandler());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
