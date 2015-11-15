package ttftcuts.cuttingedge.treetap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ttftcuts.cuttingedge.util.GraphicsUtil;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class TappingNEIHandler extends TemplateRecipeHandler {

	public class CachedTappingRecipe extends CachedRecipe {

		public TreeType tree;
		public List<PositionedStack> trunk;
		public List<PositionedStack> leaves;
		
		public CachedTappingRecipe(TreeType tree) {
			this.tree = tree;
			
			this.trunk = new ArrayList<PositionedStack>();
			this.leaves = new ArrayList<PositionedStack>();
			
			if (tree.trunkMeta == -1) {
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				Item trunkitem = Item.getItemFromBlock(tree.trunk);
				trunkitem.getSubItems(trunkitem, null, stacks);
				for (ItemStack stack : stacks) {
					this.trunk.add(new PositionedStack(stack, 64,39));
				}
			} else {
				this.trunk.add(new PositionedStack(new ItemStack(tree.trunk, tree.trunkMeta), 64,39));
			}
			
			if (tree.leafMeta == -1) {
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				Item leafitem = Item.getItemFromBlock(tree.leaves);
				leafitem.getSubItems(leafitem, null, stacks);
				for (ItemStack stack : stacks) {
					this.leaves.add(new PositionedStack(stack, 64,10));
				}
			} else {
				this.leaves.add(new PositionedStack(new ItemStack(tree.leaves, tree.leafMeta), 64,10));
			}
		}
		
		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public PositionedStack getIngredient() {
			if (!trunk.isEmpty()) {
				return trunk.get((cycleticks/30) % trunk.size());
			}
			return null;
		}

		@Override
		public PositionedStack getOtherStack() {
			if (!leaves.isEmpty()) {
				return leaves.get((cycleticks/30) % leaves.size());
			}
			return null;
		}

		
	}
	
	@Override
	public String getRecipeName() {
		return NEIClientUtils.translate("recipe.treetap");
	}

	@Override
	public String getGuiTexture() {
		return "cuttingedge:textures/gui/treetap/treetap.png";
	}

	@Override
	public void drawForeground(int recipe) {
		CachedTappingRecipe r = (CachedTappingRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Fluid f = r.tree.fluid;
			
			int step = this.cycleticks/10;
			
			GL11.glColor3d(1, 1, 1);
	    	GL11.glEnable(GL11.GL_BLEND);
	        if (f != null) {
	        	
	        	int height = step % 17 + 1;
	
	        	GraphicsUtil.drawRepeatedFluidIcon(f, 88, 55-height, 16, height);
	        }
	        
	        GuiDraw.changeTexture(getGuiTexture());
	    	GuiDraw.drawTexturedModalRect(43, 8, 176, 31, 32, 14);
		}
		super.drawForeground(recipe);
		super.drawForeground(recipe);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return null;
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (FluidContainerRegistry.isContainer(result)) {
			FluidStack f = FluidContainerRegistry.getFluidForFilledItem(result);
			this.loadCraftingRecipes("liquid", new Object[]{f});
			return;
		} else if (result.getItem() instanceof IFluidContainerItem) {
			IFluidContainerItem c = (IFluidContainerItem)result.getItem();
			FluidStack f = c.getFluid(result);
			this.loadCraftingRecipes("liquid", new Object[]{f});
			return;
		}
		super.loadCraftingRecipes(result);
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("ce.treetap")) {
			for(TreeType tree : ModuleTreetap.tappables) {
				arecipes.add(new CachedTappingRecipe(tree));
			}
			return;
		} 
		else if (outputId.equals("liquid")) {
			FluidStack f = results.length > 0 ? (FluidStack)results[0] : null;
			if (f != null) {
				for(TreeType tree : ModuleTreetap.tappables) {
					if (f.getFluid() == tree.fluid) {
						arecipes.add(new CachedTappingRecipe(tree));
					}
				}
			}
			return;
		}
		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() == Item.getItemFromBlock(ModuleTreetap.tapblock)) {
			this.loadCraftingRecipes("ce.treetap");
			return;
		} else {
			for(TreeType tree : ModuleTreetap.tappables) {
				if (ingredient.getItem() == Item.getItemFromBlock(tree.trunk) && (tree.trunkMeta == -1 || ingredient.getItemDamage() == tree.trunkMeta)) {
					arecipes.add(new CachedTappingRecipe(tree));
				}
				if (ingredient.getItem() == Item.getItemFromBlock(tree.leaves) && (tree.leafMeta == -1 || ingredient.getItemDamage() == tree.leafMeta)) {
					arecipes.add(new CachedTappingRecipe(tree));
				}
			}
			return;
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		// nothing special here I guess :U
		super.loadUsageRecipes(inputId, ingredients);
	}
	
	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(51,  2, 42,  7), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(51,  9, 13,  8), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(80,  9, 13,  8), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(37, 17, 27, 14), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(80, 17, 27, 14), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(64, 29, 16,  2), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(65, 31, 14,  7), "ce.treetap"));
		transferRects.add(new RecipeTransferRect(new Rectangle(65, 56, 14,  7), "ce.treetap"));
		super.loadTransferRects();
	}

	@Override
	public List<String> handleTooltip(GuiRecipe gui, List<String> tooltip, int recipe) {
		CachedTappingRecipe r = (CachedTappingRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.tree.fluid != null
				&& p.x >= left + 93
				&& p.x <= left + 108
				&& p.y >= top + 39
				&& p.y <= top + 54
			) {
				tooltip.add(StatCollector.translateToLocal(r.tree.fluid.getUnlocalizedName()));
			} else if (
				   p.x >= left + 91
				&& p.x <= left + 110
				&& p.y >= top + 38
				&& p.y <= top + 56
			) {
				tooltip.add(StatCollector.translateToLocal(ModuleTreetap.tapblock.getUnlocalizedName()+".name"));
			}
		}
		return super.handleTooltip(gui, tooltip, recipe);
	}
	
	@Override
	public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
		CachedTappingRecipe r = (CachedTappingRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.tree.fluid != null
				&& p.x >= left + 93
				&& p.x <= left + 108
				&& p.y >= top + 39
				&& p.y <= top + 54
			) {
				if(button==0)
				{
					if(GuiCraftingRecipe.openRecipeGui("liquid", new FluidStack(r.tree.fluid,1)))
						return true;
				}
				else if(button==1)
				{
					if(GuiUsageRecipe.openRecipeGui("liquid", new FluidStack(r.tree.fluid,1)))
						return true;
				}
			} else if (
				   p.x >= left + 91
				&& p.x <= left + 110
				&& p.y >= top + 38
				&& p.y <= top + 56
			) {
				if(button==0)
				{
					GuiCraftingRecipe.openRecipeGui("item", new ItemStack(ModuleTreetap.tapblock));
				}
				else if(button==1)
				{
					GuiUsageRecipe.openRecipeGui("item", new ItemStack(ModuleTreetap.tapblock));
				}
			}
		}
		return super.mouseClicked(gui, button, recipe);
	}

	@Override
	public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
		CachedTappingRecipe r = (CachedTappingRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.tree.fluid != null
				&& p.x >= left + 93
				&& p.x <= left + 108
				&& p.y >= top + 39
				&& p.y <= top + 54
			) {
				if(keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
				{
					if(GuiCraftingRecipe.openRecipeGui("liquid", new FluidStack(r.tree.fluid,1)))
						return true;
				}
				else if(keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
				{
					if(GuiUsageRecipe.openRecipeGui("liquid", new FluidStack(r.tree.fluid,1)))
						return true;
				}
			} else if (
				   p.x >= left + 91
				&& p.x <= left + 110
				&& p.y >= top + 38
				&& p.y <= top + 56
			) {
				if(keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
				{
					GuiCraftingRecipe.openRecipeGui("item", new ItemStack(ModuleTreetap.tapblock));
				}
				else if(keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
				{
					GuiUsageRecipe.openRecipeGui("item", new ItemStack(ModuleTreetap.tapblock));
				}
			}
		}
		return super.keyTyped(gui, keyChar, keyCode, recipe);
	}

}
