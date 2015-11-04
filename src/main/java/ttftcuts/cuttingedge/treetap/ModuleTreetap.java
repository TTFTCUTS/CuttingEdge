package ttftcuts.cuttingedge.treetap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;

public class ModuleTreetap extends Module {

	public static List<TreeType> tappables;
	public static Block tapblock;
	public static Item sapbucket;
	
	public static Fluid rubbersap;
	public static boolean oursap = false;
	
	public ModuleTreetap() {
		super("treetap");
	}

	@Override
	public boolean shouldLoad() {
		return Loader.isModLoaded("IC2") || Loader.isModLoaded("MineFactoryReloaded");
	}
	
	@Override
	public Module makeClient() {
		return new ModuleTreetapClient();
	}
	
	@Override
	public void configure(Configuration config) {}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		tappables = new ArrayList<TreeType>();
		tapblock = GameRegistry.registerBlock(new BlockTreetap(), "tap");
		GameRegistry.registerTileEntity(TileTreetap.class, "cetap");
		
		sapbucket = new ItemSapBucket();
		GameRegistry.registerItem(sapbucket, "sapbucket");
		
		rubbersap = FluidRegistry.getFluid("rubbersap");
		if (rubbersap == null) {
			rubbersap = new Fluid("rubbersap");
			FluidRegistry.registerFluid(rubbersap);
			oursap = true;
		}
		
		FluidContainerRegistry.registerFluidContainer(rubbersap, new ItemStack(sapbucket, 1, 0), new ItemStack(Items.bucket));
	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded("IC2")) {
			Block wood = GameRegistry.findBlock("IC2", "blockRubWood");
			Block leaves = GameRegistry.findBlock("IC2", "blockRubLeaves");
			tappables.add(new TreeType(wood,-1,leaves,-1,4,2,8,rubbersap,1.0));
		}
		if (Loader.isModLoaded("MineFactoryReloaded")) {
			Block wood = GameRegistry.findBlock("MineFactoryReloaded", "rubberwood.log");
			Block leaves = GameRegistry.findBlock("MineFactoryReloaded", "rubberwood.leaves");
			tappables.add(new TreeType(wood,-1,leaves,0,1,2,8,rubbersap,1.0));
		}
	}

}
