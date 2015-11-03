package ttftcuts.cuttingedge.portacart;

import ttftcuts.cuttingedge.CuttingEdge;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;

public class RenderPortacart extends RenderMinecart {

	private static ResourceLocation minecartTextures = new ResourceLocation(CuttingEdge.MOD_ID, "textures/entity/portacart/portacart.png");
	
	@Override
	protected ResourceLocation getEntityTexture(EntityMinecart cart)
    {
        return minecartTextures;
    }
}
