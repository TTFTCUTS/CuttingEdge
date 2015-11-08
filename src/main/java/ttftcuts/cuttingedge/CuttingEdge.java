package ttftcuts.cuttingedge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ttftcuts.cuttingedge.portacart.ModulePortacart;
import ttftcuts.cuttingedge.treetap.ModuleTreetap;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
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
    public static Map<String, Module> modulesByName;
    
    public static Configuration config;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	modules = new Module[]{
    		new ModulePortacart(),
    		new ModuleTreetap(),
    	};
    	proxy.getSidedModules();
    	
    	modulesByName = new HashMap<String,Module>();
    	for (Module m:modules) {
    		modulesByName.put(m.name, m);
    	}
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	
    	try {
    		config.load();
    		
    		for (Module m : modules) {
    			m.enabled = config.get(m.name, "enabled", true).getBoolean() && m.shouldLoad();
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
    
    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
    	processIMC(FMLInterModComms.fetchRuntimeMessages(this));
    }
    
    @Mod.EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent event) {
    	processIMC(event.getMessages());
    }
    
    public static void processIMC(List<FMLInterModComms.IMCMessage> messages) {
    	if (messages.size() == 0) { return; }
    	Map<Module, List<FMLInterModComms.IMCMessage>> messagemap = new HashMap<Module, List<FMLInterModComms.IMCMessage>>();
    	
    	for (FMLInterModComms.IMCMessage message : messages) {
    		String[] parts = message.key.split("\\.");
    		if (parts.length < 2) { 
    			logger.warn(String.format("Malformed IMC message '%s' from %s. Should 'MODULENAME.COMMAND'.", message.key, message.getSender()));
    			continue; 
    		}
    		
    		if (!modulesByName.containsKey(parts[0])) {
    			logger.warn(String.format("Malformed IMC message '%s' from %s. No matching module found.", message.key, message.getSender()));
    			continue;
    		}
    		
    		Module module = modulesByName.get(parts[0]);
    		
    		if (!messagemap.containsKey(module)) {
    			messagemap.put(module, new ArrayList<FMLInterModComms.IMCMessage>());
    		}
    		messagemap.get(module).add(message);
    	}
    	
    	for (Module m : messagemap.keySet()) {
    		m.handleIMC(messagemap.get(m));
    	}
    }
}
