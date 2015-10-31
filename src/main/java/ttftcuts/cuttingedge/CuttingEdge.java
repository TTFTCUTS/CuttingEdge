package ttftcuts.cuttingedge;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ttftcuts.cuttingedge.portacart.ModulePortacart;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CuttingEdge.MOD_ID, name = "Cutting Edge", version = "$GRADLEVERSION")
public class CuttingEdge {

    public static final String MOD_ID = "cuttingedge";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
       
    @Mod.Instance
    public static CuttingEdge instance;
    
    @SidedProxy(serverSide="ttftcuts.cuttingedge.CommonProxy", clientSide="ttftcuts.cuttingedge.ClientProxy")
    public static CommonProxy proxy;
    
    public static Module[] modules;
    
    public static Configuration config;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	modules = new Module[]{
    		new ModulePortacart(),
    	};
    	proxy.getSidedModules();
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	
    	try {
    		config.load();
    		
    		for (Module m : modules) {
        		m.configure(config);
        	}
    	} catch (Exception e) {
    		logger.error("Could not load config.");
    	} finally {
    		config.save();
    	}

    	proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	proxy.init(event);
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit(event);
    }
}
