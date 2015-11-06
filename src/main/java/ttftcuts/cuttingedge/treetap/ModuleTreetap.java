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
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.Module;
import ttftcuts.cuttingedge.util.GUIUtil;

public class ModuleTreetap extends Module {

	public static List<TreeType> tappables;
	public static List<EvaporateType> evapables;
	public static Block tapblock;
	public static Block evapblock;
	public static Item sapbucket;
	
	public static Fluid rubbersap;
	public static boolean oursap = false;
	
	public static int uiEvaporator;
	
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
		evapables = new ArrayList<EvaporateType>();
		tapblock = GameRegistry.registerBlock(new BlockTreetap(), "tap");
		GameRegistry.registerTileEntity(TileTreetap.class, "cetap");
		evapblock = GameRegistry.registerBlock(new BlockEvaporator(), "evap");
		GameRegistry.registerTileEntity(TileEvaporator.class, "ceevap");
		
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
		uiEvaporator = GUIUtil.registerUI(GuiEvaporator.class, ContainerEvaporator.class);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		boolean ic2 = Loader.isModLoaded("IC2");
		boolean mfr = Loader.isModLoaded("MineFactoryReloaded");
		// trees
		if (ic2) {
			Block wood = GameRegistry.findBlock("IC2", "blockRubWood");
			Block leaves = GameRegistry.findBlock("IC2", "blockRubLeaves");
			tappables.add(new TreeType(wood,-1,leaves,-1,4,2,8,rubbersap,0.0005));
		}
		if (mfr) {
			Block wood = GameRegistry.findBlock("MineFactoryReloaded", "rubberwood.log");
			Block leaves = GameRegistry.findBlock("MineFactoryReloaded", "rubberwood.leaves");
			tappables.add(new TreeType(wood,-1,leaves,0,1,2,8,rubbersap,0.0005));
		}
		// evap stuff
		if (ic2) {
			Item resin = GameRegistry.findItem("IC2", "itemHarz");
			evapables.add(new EvaporateType(new FluidStack(rubbersap,200), new ItemStack(resin)));
		} else if (mfr) {
			Item rawrubber = GameRegistry.findItem("MineFactoryReloaded", "rubber.raw");
			evapables.add(new EvaporateType(new FluidStack(rubbersap,200), new ItemStack(rawrubber)));
		}
	}

}
