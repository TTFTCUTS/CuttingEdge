package ttftcuts.cuttingedge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class GraphicsUtil {
	public static Tessellator tes() {
		return Tessellator.instance;
	}
	
	public static void drawTexturedRect(float x, float y, float w, float h, double... uv)
	{
		tes().startDrawingQuads();
		tes().addVertexWithUV(x, y+h, 0, uv[0], uv[3]);
		tes().addVertexWithUV(x+w, y+h, 0, uv[1], uv[3]);
		tes().addVertexWithUV(x+w, y, 0, uv[1], uv[2]);
		tes().addVertexWithUV(x, y, 0, uv[0], uv[2]);
		tes().draw();
	}
	public static void drawTexturedRect(int x, int y, int w, int h, float picSize, int... uv)
	{
		double[] d_uv = new double[]{uv[0]/picSize,uv[1]/picSize, uv[2]/picSize,uv[3]/picSize};
		drawTexturedRect(x,y,w,h, d_uv);
	}
	
	public static void drawRepeatedFluidIcon(Fluid fluid, float x, float y, float w, float h)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		IIcon icon = fluid.getIcon();
		if(icon != null)
		{
			int iW = icon.getIconWidth();
			int iH = icon.getIconHeight();
			if(iW>0 && iH>0)
				drawRepeatedIcon(x,y,w,h, iW, iH, icon.getMinU(),icon.getMaxU(), icon.getMinV(),icon.getMaxV());
		}
	}
	
	public static void drawRepeatedIcon(float x, float y, float w, float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax)
	{
		int iterMaxW = (int)(w/iconWidth);
		int iterMaxH = (int)(h/iconHeight);
		float leftoverW = w%iconWidth;
		float leftoverH = h%iconHeight;
		float leftoverWf = leftoverW/(float)iconWidth;
		float leftoverHf = leftoverH/(float)iconHeight;
		float iconUDif = uMax-uMin;
		float iconVDif = vMax-vMin;
		for(int ww=0; ww<iterMaxW; ww++)
		{
			for(int hh=0; hh<iterMaxH; hh++)
				drawTexturedRect(x+ww*iconWidth, y+hh*iconHeight, iconWidth,iconHeight, uMin,uMax,vMin,vMax);
			drawTexturedRect(x+ww*iconWidth, y+iterMaxH*iconHeight, iconWidth,leftoverH, uMin,uMax,vMin,(vMin+iconVDif*leftoverHf));
		}
		if(leftoverW>0)
		{
			for(int hh=0; hh<iterMaxH; hh++)
				drawTexturedRect(x+iterMaxW*iconWidth, y+hh*iconHeight, leftoverW,iconHeight, uMin,(uMin+iconUDif*leftoverWf),vMin,vMax);
			drawTexturedRect(x+iterMaxW*iconWidth, y+iterMaxH*iconHeight, leftoverW,leftoverH, uMin,(uMin+iconUDif*leftoverWf),vMin,(vMin+iconVDif*leftoverHf));
		}
	}
}
