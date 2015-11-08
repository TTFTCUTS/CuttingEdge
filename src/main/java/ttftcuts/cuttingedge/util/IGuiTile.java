package ttftcuts.cuttingedge.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public interface IGuiTile {
	
	public Container getContainer(EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player);
}
