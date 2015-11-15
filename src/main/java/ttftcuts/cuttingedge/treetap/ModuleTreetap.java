package ttftcuts.cuttingedge.treetap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.Module;

public class ModuleTreetap extends Module {

	public static List<TreeType> tappables;
	public static List<EvaporateType> evapables;
	public static Block tapblock;
	public static Block evapblock;
	public static Item sapbucket;
	
	public static Fluid rubbersap;
	public static boolean ourrubbersap = false;
	
	public static Fluid maplesap;
	public static boolean ourmaplesap = false;
	
	public static int uiEvaporator;
	
	public static boolean enableTap = true;
	public static boolean enableEvaporator = true;
	public static double globalTapRate = 1.0;
	
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
	public void configure(Configuration config) {
		enableTap = config.getBoolean("enable tapping kit", this.name, true, "Set to false to disable the tree tapping kit recipe");
		enableEvaporator = config.getBoolean("enable evaporator", this.name, true, "Set to false to disable the evaporator recipe");
		globalTapRate = config.getFloat("global tap rate", this.name, 1.0f, 0.0f, 1000000.0f, "Global multiplier to tap fluid rate. Lower if you think the taps are too good.");
	}

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
			rubbersap.setDensity(920).setViscosity(1200);
			FluidRegistry.registerFluid(rubbersap);
			ourrubbersap = true;
		}
		
		FluidContainerRegistry.registerFluidContainer(rubbersap, new ItemStack(sapbucket, 1, 0), new ItemStack(Items.bucket));
		
		if (Loader.isModLoaded("harvestcraft")) {
			maplesap = FluidRegistry.getFluid("maplesap");
			if (maplesap == null) {
				maplesap = new Fluid("maplesap");
				maplesap.setDensity(1095).setViscosity(1100);
				FluidRegistry.registerFluid(maplesap);
				ourmaplesap = true;
			}
			
			FluidContainerRegistry.registerFluidContainer(maplesap, new ItemStack(sapbucket, 1, 1), new ItemStack(Items.bucket));
		}
		
		
	}

	@Override
	public void init(FMLInitializationEvent event) {		
		if(enableEvaporator) {
			GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(evapblock),
				"CBC",
				"C C",
				"SFS",
				'C', new ItemStack(Blocks.hardened_clay),
				'B', new ItemStack(Items.bucket),
				'S', "cobblestone",
				'F', new ItemStack(Blocks.furnace)
			));
		}
		
		if(enableTap) {
			ItemStack leather = new ItemStack(Items.leather);
			GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(tapblock),
				leather,
				leather,
				leather,
				leather,
				"cobblestone",
				"stickWood",
				new ItemStack(Items.string)
			));
		}
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
		
		if (Loader.isModLoaded("harvestcraft")) {
			Item syrup = GameRegistry.findItem("harvestcraft", "maplesyrupItem");
			evapables.add(new EvaporateType(new FluidStack(maplesap,500), new ItemStack(syrup)));
			
			Block wood = GameRegistry.findBlock("harvestcraft", "pamMaple");
			tappables.add(new TreeType(wood,-1,Blocks.leaves,1,1,2,8,maplesap,0.0015));
			
			if (Loader.isModLoaded("Natura")) {
				Block nwood = GameRegistry.findBlock("Natura", "Rare Tree");
				Block nleaf = GameRegistry.findBlock("Natura", "Rare Leaves");
				tappables.add(new TreeType(nwood,0,nleaf,0,1,2,8,maplesap,0.0015));
			}
		}
	}

	@Override
	public void handleIMC(List<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			if (message.key.equals("treetap.addTree")) {
				if (message.isNBTMessage()) {
					NBTTagCompound tag = message.getNBTValue();
					
					int height = tag.getInteger("maxHeight");
					int canopy = tag.getInteger("canopyHeight");
					int radius = tag.getInteger("radius");
					double rate = tag.getDouble("rate");
					
					ItemStack trunkstack = null;
					if (tag.hasKey("trunk")) {
						trunkstack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("trunk"));
					}
					Block trunk = null;
					int trunkmeta = -1;
					if (trunkstack != null && trunkstack.getItem() instanceof ItemBlock && Block.getBlockFromItem(trunkstack.getItem()) != Blocks.air) {
						trunk = Block.getBlockFromItem(trunkstack.getItem());
						trunkmeta = trunkstack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? -1 : trunkstack.getItemDamage();
					}
					
					ItemStack leafstack = null;
					if (tag.hasKey("leaves")) {
						leafstack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("leaves"));
					}
					Block leaves = null;
					int leafmeta = -1;
					if (leafstack != null && leafstack.getItem() instanceof ItemBlock && Block.getBlockFromItem(leafstack.getItem()) != Blocks.air) {
						leaves = Block.getBlockFromItem(leafstack.getItem());
						leafmeta = leafstack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? -1 : leafstack.getItemDamage();
					}
					
					Fluid fluid = FluidRegistry.getFluid(tag.getString("fluid"));
					
					if (trunk != null && leaves != null && fluid != null) {
						tappables.add(new TreeType(trunk, trunkmeta, leaves, leafmeta, canopy, radius, height, fluid, rate));
						CuttingEdge.logger.info(message.getSender()+" added tappable tree via IMC: "+trunk.getLocalizedName()+":"+trunkmeta+", "+leaves.getLocalizedName()+":"+leafmeta+", "+canopy+","+radius+","+height+", "+fluid.getLocalizedName(null)+", "+rate);
					} else {
						CuttingEdge.logger.warn(String.format("Invalid IMC message sent to Treetap addTree by %s. One or more of trunk, leaves or fluid was null.", message.getSender()));
					}
				} else {
					CuttingEdge.logger.warn(String.format("Invalid IMC message sent to Treetap by %s. treetap.addTree expects an nbt tag.", message.getSender()));
				}
			} else if (message.key.equals("treetap.addEvap")) {
				if (message.isNBTMessage()) {
					NBTTagCompound tag = message.getNBTValue();
					
					FluidStack fluid = null;
					if (tag.hasKey("fluid")) {
						fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
					}
					
					ItemStack output = null;
					if (tag.hasKey("output")) {
						output = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("output"));
					}
					
					if (fluid != null && output != null) {
						evapables.add(new EvaporateType(fluid, output));
						CuttingEdge.logger.info(message.getSender()+" added evaporator recipe via IMC: "+fluid.amount+"mb "+fluid.getLocalizedName()+" -> "+output.stackSize+"x "+output.getDisplayName());
					} else {
						CuttingEdge.logger.warn(String.format("Invalid IMC message sent to Treetap addEvap by %s. Either fluid or output were null.", message.getSender()));
					}
				} else {
					CuttingEdge.logger.warn(String.format("Invalid IMC message sent to Treetap by %s. treetap.addEvap expects an nbt tag.", message.getSender()));
				}
			} else {
				CuttingEdge.logger.warn(String.format("Invalid IMC message sent to Treetap by %s. No matching command %s.", message.getSender(), message.key));
			}
		}
	}

}
