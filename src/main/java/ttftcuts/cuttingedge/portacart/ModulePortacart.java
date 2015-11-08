package ttftcuts.cuttingedge.portacart;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;
import ttftcuts.cuttingedge.util.EntityUtil;
import ttftcuts.cuttingedge.util.NetworkUtil;

public class ModulePortacart extends Module {
	public static Item portacart;
	
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
		portacart = new ItemPortacart();
		GameRegistry.registerItem(portacart, "portacart");
		
		EntityRegistry.registerModEntity(EntityPortacart.class, "portacart", EntityUtil.getNextEntityID(), CuttingEdge.instance, 64, 1, true);
		
		NetworkUtil.INSTANCE.registerMessage(PortacartMessage.PortacartMessageHandler.class, PortacartMessage.class, NetworkUtil.getNextPacketId(), Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ItemStack pcart = new ItemStack(portacart);
		if (Loader.isModLoaded("gregtech")) {
			CuttingEdge.logger.info("Gregtech portacart recipe enabled");
			Block machine = GameRegistry.findBlock("gregtech", "gt.blockmachines");
			GameRegistry.addRecipe(new ShapedOreRecipe(pcart, 
				"Wwb",
				"SPS",
				"gCp",
				'b', new ItemStack(machine, 1, 100),// solar boiler
				'W', "craftingToolWrench",
				'w', "bucketWater",
				'S', "plateSteel",
				'g', "stickLongBlaze",
				'C', new ItemStack(Items.furnace_minecart),
				'p', "pipeSmallBronze",
				'P', new ItemStack(Items.ender_eye)
			));
		} else {
			CuttingEdge.logger.info("Vanilla portacart recipe enabled");
			GameRegistry.addRecipe(new ShapedOreRecipe(pcart, 
				"IwI",
				"IPI",
				"BCB",
				'C', new ItemStack(Items.furnace_minecart),
				'B', new ItemStack(Items.blaze_rod),
				'P', new ItemStack(Items.ender_pearl),
				'I', "ingotIron",
				'w', new ItemStack(Items.water_bucket)
			));
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

	@Override
	public void handleIMC(List<IMCMessage> messages) {}
}
