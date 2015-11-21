package ttftcuts.cuttingedge.tacos;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleTacosClient extends ModuleTacos {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		MinecraftForge.EVENT_BUS.register(new TacoTipHandler());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

}
