package ttftcuts.cuttingedge.util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIUtil implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IGuiTile) {
			return ((IGuiTile)tile).getContainer(player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IGuiTile) {
			return ((IGuiTile)tile).getGui(player);
		}
		return null;
	}
	
	public static boolean isKeyDown(int keyCode)
	{
		try
		{
			if (keyCode < 0) // Keycodes less than zero are probably mouse buttons
			{
				return Mouse.isButtonDown(keyCode + 100);
			}
			
			return Keyboard.isKeyDown(keyCode);
		}
		catch (Exception ex)
		{
			// Can happen if an invalid index is used
			return false;
		}
	}
}