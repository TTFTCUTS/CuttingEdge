package ttftcuts.cuttingedge.treetap;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ttftcuts.cuttingedge.CuttingEdge;
import ttftcuts.cuttingedge.util.GraphicsUtil;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiEvaporator extends GuiContainer {

	private static final ResourceLocation texture = new ResourceLocation(CuttingEdge.MOD_ID, "textures/gui/treetap/evaporator.png");
	private TileEvaporator evap;
	private EntityPlayer player;
	
	public GuiEvaporator(EntityPlayer player, TileEntity te) {
		super (new ContainerEvaporator(player, te));
		this.evap = (TileEvaporator)te;
		this.player = player;
	}
	
	public GuiEvaporator(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my)
    {
        String s = I18n.format(this.evap.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        GL11.glColor3d(1, 1, 1);
    	GL11.glEnable(GL11.GL_BLEND);
        if (this.evap.tank.getFluid() != null && this.evap.tank.getFluidAmount() > 0) {
        	int amount = this.evap.tank.getFluidAmount();
        	int capacity = Math.max(1,this.evap.tank.getCapacity());
        	
        	int height = (int)Math.floor((amount / (double)capacity)*13) + 1;

        	GraphicsUtil.drawRepeatedFluidIcon(this.evap.tank.getFluid().getFluid(), 48, 33-height, 32, height);
        }
        
        this.mc.getTextureManager().bindTexture(texture);
    	this.drawTexturedModalRect(48, 19, 176, 31, 32, 14);
    	
    	if (mx >= this.guiLeft + 45 && mx <= this.guiLeft + 82 && my >= this.guiTop + 17 && my <= this.guiTop + 36) {
    		List<String> tooltip = new ArrayList<String>();
    		if (this.evap.tank.getFluid() != null) {
    			tooltip.add(this.evap.tank.getFluid().getLocalizedName());
    			tooltip.add(this.evap.tank.getFluidAmount()+"/"+this.evap.tank.getCapacity()+"mB");
    		} else {
    			tooltip.add(StatCollector.translateToLocal("gui.treetap.empty"));
    		}
    		
    		if (this.player.inventory.getItemStack() == null) {
    			this.drawHoveringText(tooltip, mx - this.guiLeft, my - this.guiTop, fontRendererObj);
    			RenderHelper.enableGUIStandardItemLighting();
    		}
    	}
    }

	@Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.evap.burnTime > 0)
        {
            int i1 = this.evap.getBurnTimeRemainingScaled(13);
            this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = this.evap.getCookProgressScaled(24);
            this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
        }
    }

}
