package ttftcuts.cuttingedge.portacart;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ttftcuts.cuttingedge.Module;

public class ModulePortacart extends Module {

	public ModulePortacart() {
		super("Portacart");
	}

	@Override
	public Module makeClient() {
		return new ModulePortacartClient();
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
