package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;

import static ttftcuts.cuttingedge.tacos.EnumComponentType.*;
import static ttftcuts.cuttingedge.tacos.TacoFlavour.*;

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
		TacoFlavour.flavourInit();
		
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
			.addFlavour(dinner, 1.0)
		);
		
		TacoContainer.register(new TacoContainer("boat", new ItemStack(Items.boat), 50)
			.setCapacity(Shell, 1.0)
			.setCapacity(Filling, 2.0)
			.setCapacity(Sauce, 1.0)
		);
		TacoComponent.register(new TacoComponent("boat", new ItemStack(Items.boat), Shell, 50.0, CuttingEdge.MOD_ID+":tacos/taco_waffle", 0x00FF00)
			.addFlavour(dinner, 1.0)
		);
		
		TacoComponent.register(new TacoComponent("carrot", "cropCarrot", Filling, 1.0, CuttingEdge.MOD_ID+":tacos/taco_bacon", 0xFFFFFF)
			.addFlavour(fresh, 1.0)
			.addFlavour(dinner, 0.5)
		);
		
		TacoComponent.register(new TacoComponent("potato", "cropPotato", Sauce, 1.0, CuttingEdge.MOD_ID+":tacos/taco_chocolate_syrup", 0xFFFFFF)
			.addFlavour(dinner, 0.5)
			.addFlavour(salty, 0.1)
		);
		
		// POTIONS ###############################
		Set<Potion> potions = new HashSet<Potion>();
		List<ItemStack> potionitems = new ArrayList<ItemStack>();
		Items.potionitem.getSubItems(Items.potionitem, null, potionitems);
		
		for (ItemStack pstack : potionitems) {
			@SuppressWarnings("unchecked")
			List<PotionEffect> e = Items.potionitem.getEffects(pstack);
			if (e == null || e.isEmpty()) { continue; }
			Potion p = Potion.potionTypes[e.get(0).getPotionID()];
			if (p == null || potions.contains(p)) { continue; }
			potions.add(p);
			
			TacoComponent.register(new TacoComponent("pot_"+p.getName(), new TacoComponent.PotionMatcher(p), pstack.copy(), Sauce, 1.0, CuttingEdge.MOD_ID+":tacos/taco_chocolate_syrup", p.getLiquidColor())
				.addFlavour(potionFlavours.get(p), 1.0)
				.setSizer(TacoComponent.ComponentSizer.potionSizer)
			);
		}
		// POTIONS ###############################
	}

	@Override
	public Module makeClient() {
		return new ModuleTacosClient();
	}

	
}
