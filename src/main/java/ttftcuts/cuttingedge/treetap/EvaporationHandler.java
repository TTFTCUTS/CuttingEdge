package ttftcuts.cuttingedge.treetap;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ttftcuts.cuttingedge.util.GraphicsUtil;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class EvaporationHandler extends TemplateRecipeHandler {

	public class CachedEvaporationRecipe extends CachedRecipe
	{
		public EvaporateType evaptype;
		public PositionedStack output;
		public FluidStack input;
		
		public CachedEvaporationRecipe(EvaporateType evaptype) {
			this.evaptype = evaptype;
			this.output = new PositionedStack(evaptype.output, 111,24); // 116,35 - so offset by -5,-11
			this.input = evaptype.fluid;
		}
		
		@Override
		public PositionedStack getResult() {
			return this.output;
		}
		
		@Override
		public PositionedStack getOtherStack() {
            return FurnaceRecipeHandler.afuels.get((cycleticks / 48) % FurnaceRecipeHandler.afuels.size()).stack;
        }
	}
	
	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(51, 25, 176, 0, 14, 14, 48, 7);
        drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiEvaporator.class;
	}

	@Override
	public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(50, 23, 18, 18), "fuel"));
        transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "ce.evaporator"));
	}
	
	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for(EvaporateType evap : ModuleTreetap.evapables) {
			if (NEIServerUtils.areStacksSameType(result, evap.output)) {
				arecipes.add(new CachedEvaporationRecipe(evap));
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("ce.evaporator")) {
			for (EvaporateType evap : ModuleTreetap.evapables) {
				arecipes.add(new CachedEvaporationRecipe(evap));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (FluidContainerRegistry.isContainer(ingredient)) {
			FluidStack f = FluidContainerRegistry.getFluidForFilledItem(ingredient);
			this.loadUsageRecipes("liquid", new Object[]{f});
			return;
		} else if (ingredient.getItem() instanceof IFluidContainerItem) {
			IFluidContainerItem c = (IFluidContainerItem)ingredient.getItem();
			FluidStack f = c.getFluid(ingredient);
			this.loadUsageRecipes("liquid", new Object[]{f});
			return;
		}
		super.loadUsageRecipes(ingredient);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId.equals("fuel")) {
			this.loadCraftingRecipes("ce.evaporator");
			return;
		} else if(inputId.equals("liquid")) {
			FluidStack f = ingredients.length > 0 ? (FluidStack)ingredients[0] : null;
			if (f != null) { 	
				for (EvaporateType evap : ModuleTreetap.evapables) {
					if (f.isFluidEqual(evap.fluid)) {
						arecipes.add(new CachedEvaporationRecipe(evap));
					}
				}
				return;
			}
		}
		super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public TemplateRecipeHandler newInstance() {
		// THIS IS AN EVIL HACK BECAUSE THE "findFuels" METHOD IS PRIVATE GODDAMNIT
		if (FurnaceRecipeHandler.afuels == null || FurnaceRecipeHandler.afuels.isEmpty()) {
			(new FurnaceRecipeHandler()).newInstance();
		}
		return super.newInstance();
	}
	
	@Override
	public String getRecipeName() {
		return NEIClientUtils.translate("recipe.evaporator");
	}

	@Override
	public String getGuiTexture() {
		return "cuttingedge:textures/gui/treetap/evaporator.png";
	}

	@Override
	public void drawBackground(int recipe) {
		super.drawBackground(recipe);
	}

	@Override
	public void drawForeground(int recipe) {
		CachedEvaporationRecipe r = (CachedEvaporationRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			FluidStack fs = r.input;
			Fluid f = fs.getFluid();
			
			int step = this.cycleticks/48;
			int stages = 4000 / fs.amount;
			int stage = step % stages;
			
			GL11.glColor3d(1, 1, 1);
	    	GL11.glEnable(GL11.GL_BLEND);
	        if (f != null && fs.amount > 0) {
	        	int amount = 4000 - stage * fs.amount;
	        	int capacity = 4000;
	        	
	        	int height = (int)Math.floor((amount / (double)capacity)*13) + 1;
	
	        	GraphicsUtil.drawRepeatedFluidIcon(f, 43, 22-height, 32, height);
	        }
	        
	        GuiDraw.changeTexture(getGuiTexture());
	    	GuiDraw.drawTexturedModalRect(43, 8, 176, 31, 32, 14);
		}
		super.drawForeground(recipe);
	}

	@Override
	public List<String> handleTooltip(GuiRecipe gui, List<String> tooltip, int recipe) {
		CachedEvaporationRecipe r = (CachedEvaporationRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.input != null
				&& p.x >= left + 45
				&& p.x <= left + 82
				&& p.y >= top + 6
				&& p.y <= top + 24
			) {
				tooltip.add(r.input.getLocalizedName());
				tooltip.add(r.input.amount+"mB");
			}
		}
		return super.handleTooltip(gui, tooltip, recipe);
	}

	@Override
	public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
		CachedEvaporationRecipe r = (CachedEvaporationRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.input != null
				&& p.x >= left + 45
				&& p.x <= left + 82
				&& p.y >= top + 6
				&& p.y <= top + 24
			) {
				if(button==0)
				{
					if(GuiCraftingRecipe.openRecipeGui("liquid", new Object[] { r.input }))
						return true;
				}
				else if(button==1)
				{
					if(GuiUsageRecipe.openRecipeGui("liquid", new Object[] { r.input }))
						return true;
				}
			}
		}
		
		return super.mouseClicked(gui, button, recipe);
	}

	@Override
	public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
		CachedEvaporationRecipe r = (CachedEvaporationRecipe)this.arecipes.get(recipe % this.arecipes.size());
		if (r != null) {
			Point p = GuiDraw.getMousePosition();
			int left = (gui.width-176)/2;
			int top = (gui.height-134)/2 + (65 * (recipe % this.recipiesPerPage()));
			if (r.input != null
				&& p.x >= left + 45
				&& p.x <= left + 82
				&& p.y >= top + 6
				&& p.y <= top + 24
			) {
				if(keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
				{
					if(GuiCraftingRecipe.openRecipeGui("liquid", new Object[] { r.input }))
						return true;
				}
				else if(keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
				{
					if(GuiUsageRecipe.openRecipeGui("liquid", new Object[] { r.input }))
						return true;
				}
			}
		}
		
		return super.keyTyped(gui, keyChar, keyCode, recipe);
	}

}
