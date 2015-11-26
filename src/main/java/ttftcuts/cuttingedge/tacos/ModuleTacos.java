package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;

import static ttftcuts.cuttingedge.tacos.EnumComponentType.*;
import static ttftcuts.cuttingedge.tacos.EnumFlavour.*;

public class ModuleTacos extends Module {

	public static Map<String, TacoContainer> containers = new HashMap<String, TacoContainer>();
	public static Map<String, TacoComponent> components = new HashMap<String, TacoComponent>();
	
	public static Item tacoItem;
	
	public ModuleTacos() {
		super("tacos");
	}

	@Override
	public void configure(Configuration config) {
		
	}

	@Override
	public void handleIMC(List<IMCMessage> messages) {

	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		EnumFlavour.doRelations();
		
		tacoItem = new ItemTaco();
		GameRegistry.registerItem(tacoItem, "cetaco");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new TacoRecipe());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		TacoContainer.register(new TacoContainer("book", new ItemStack(Items.book), 1)
			.setCapacity(Shell, 1.0)
			.setCapacity(Filling, 2.0)
			.setCapacity(Sauce, 1.0)
		);
		TacoComponent.register(new TacoComponent("book", new ItemStack(Items.book), Shell, 1.0, CuttingEdge.MOD_ID+":tacos/taco_basic", 0xFF00FF)
			.addFlavour(Dinner, 1.0)
		);
		
		TacoContainer.register(new TacoContainer("boat", new ItemStack(Items.boat), 50)
			.setCapacity(Shell, 1.0)
			.setCapacity(Filling, 2.0)
			.setCapacity(Sauce, 1.0)
		);
		TacoComponent.register(new TacoComponent("boat", new ItemStack(Items.boat), Shell, 50.0, CuttingEdge.MOD_ID+":tacos/taco_waffle", 0x00FF00)
			.addFlavour(Dinner, 1.0)
		);
		
		TacoComponent.register(new TacoComponent("carrot", "cropCarrot", Filling, 1.0, CuttingEdge.MOD_ID+":tacos/taco_bacon", 0xFFFFFF)
			.addFlavour(Fresh, 1.0)
			.addFlavour(Dinner, 0.5)
		);
		
		TacoComponent.register(new TacoComponent("potato", "cropPotato", Sauce, 1.0, CuttingEdge.MOD_ID+":tacos/taco_chocolate_syrup", 0xFFFFFF)
			.addFlavour(Dinner, 0.5)
			.addFlavour(Salty, 0.1)
		);
	}

	@Override
	public Module makeClient() {
		return new ModuleTacosClient();
	}

	
}
