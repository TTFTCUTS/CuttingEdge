package ttftcuts.cuttingedge;

import ttftcuts.cuttingedge.util.GUIUtil;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy {
	public void getSidedModules() {
		
	}
	
	public void preInit(FMLPreInitializationEvent event) {		
		for (Module m : CuttingEdge.modules) {
			CuttingEdge.logger.info("Module "+m.name+" "+(m.enabled?"enabled":"disabled")+": "+m);
			if (m.enabled) {
				m.preInit(event);
			}
		}
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(CuttingEdge.instance, new GUIUtil());
		
		for (Module m : CuttingEdge.modules) {
			if (m.enabled) {
				m.init(event);
			}
		}
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		for (Module m : CuttingEdge.modules) {
			if (m.enabled) {
				m.postInit(event);
			}
		}
	}
}
