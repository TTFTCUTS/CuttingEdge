package ttftcuts.cuttingedge;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class Module {

	public final String name;
	public boolean enabled = false;
	
	public Module(String name) {
		this.name = name;
	}
	
	public void configure(Configuration config) {
		this.enabled = config.get(name, "enabled", true).getBoolean();
	}
	
	public abstract void preInit(FMLPreInitializationEvent event);
	
	public abstract void init(FMLInitializationEvent event);
	
	public abstract void postInit(FMLPostInitializationEvent event);
	
	public Module makeClient() {
		return this;
	}
}
