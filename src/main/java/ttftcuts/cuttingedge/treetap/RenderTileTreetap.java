package ttftcuts.cuttingedge.treetap;

import org.lwjgl.opengl.GL11;

import ttftcuts.cuttingedge.CuttingEdge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RenderTileTreetap extends TileEntitySpecialRenderer {

	public static boolean inventory = false;
	
	private static final ResourceLocation texture = new ResourceLocation(CuttingEdge.MOD_ID, "textures/blocks/treetap/treetap.png");
	private static final ModelTreetap model = new ModelTreetap();
	
	private static final int[] rot = {-1,-1,2,0,1,3};
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		TileTreetap tap = (TileTreetap)tile;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glTranslated(x, y, z);
		
		//GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 1.5f, 0.5f);
		GL11.glScalef(1F, -1F, -1F);
		
		if (tap.direction != null && tap.direction != ForgeDirection.UNKNOWN) {
			GL11.glRotatef(90F * rot[tap.direction.ordinal()], 0F, 1F, 0F);
		}
		
		mc.getTextureManager().bindTexture(texture);
		
		model.render();
		
		//GL11.glPopMatrix();
		
		if (!inventory) {
			
			int tx = tap.xCoord - tap.direction.offsetX;
			int tz = tap.zCoord - tap.direction.offsetZ;
			
			TreeType tree = BlockTreetap.getTappable(tap.getWorldObj(), tx, tap.yCoord, tz);
			
			if (tree != null) {				
				Fluid fluid = tree.fluid;
				
				IIcon still = fluid.getStillIcon();
				IIcon flowing = fluid.getFlowingIcon();
				
				mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				
				Tessellator tes = Tessellator.instance;
				
				double p = 1/16.0;
				double uf = flowing.getMaxU() - flowing.getMinU();
				double vf = flowing.getMaxV() - flowing.getMinV();
				double us = still.getMaxU() - still.getMinU();
				double vs = still.getMaxV() - still.getMinV();
				
				double u1 = flowing.getMinU() + p*7*uf;
				double u2 = flowing.getMinU() + p*8*uf;
				
				if (tap.rate > 0) {
					tes.startDrawingQuads();
					tes.setNormal(tap.direction.offsetX, 0, tap.direction.offsetZ);
					tes.addVertexWithUV(-p*0.5, 0.5+p*3, 0.5-p*1.1, u1, flowing.getMinV());
					tes.addVertexWithUV(-p*0.5, 0.5+p*15, 0.5-p*1.1, u1, flowing.getMaxV());
					tes.addVertexWithUV(p*0.5, 0.5+p*15, 0.5-p*1.1, u2, flowing.getMaxV());
					tes.addVertexWithUV(p*0.5, 0.5+p*3, 0.5-p*1.1, u2, flowing.getMinV());
					tes.draw();
				}
				
				if (tap.tank.getFluid() != null) {
					double fill = tap.tank.getFluidAmount() / (double)tap.tank.getCapacity();
					
					double height = (11*(1-fill) + 4) * p;
					double len = ((1-fill) + 4) * p;
					
					tes.startDrawingQuads();
					tes.setNormal(0, -1, 0);
					tes.addVertexWithUV(-0.5 + 3*p, 0.5 + height, 0.5 - 1*p, still.getMinU() + 3*p*us, still.getMinV() + 15*p*vs);
					tes.addVertexWithUV(-0.5 + 3*p, 0.5 + height, -0.5 + len, still.getMinU() + 3*p*us, still.getMinV() + len*vs);
					tes.addVertexWithUV(0.5 - 3*p, 0.5 + height, -0.5 + len, still.getMinU() + 13*p*us, still.getMinV() + len*vs);
					tes.addVertexWithUV(0.5 - 3*p, 0.5 + height, 0.5 - 1*p, still.getMinU() + 13*p*us, still.getMinV() + 15*p*vs);
					tes.draw();
				}
			}
		}
		GL11.glPopMatrix();
	}

}
