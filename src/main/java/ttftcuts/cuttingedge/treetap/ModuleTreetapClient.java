package ttftcuts.cuttingedge.treetap;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleTreetapClient extends ModuleTreetap {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		MinecraftForge.EVENT_BUS.register(new TreetapEvents());
	}

}
