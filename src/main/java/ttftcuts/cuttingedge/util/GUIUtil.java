package ttftcuts.cuttingedge.util;

import java.util.ArrayList;
import java.util.List;

import ttftcuts.cuttingedge.CuttingEdge;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIUtil implements IGuiHandler {

	private static List<GuiType> guis = new ArrayList<GuiType>();
	private static int nextID = 0;
	
	public static int registerUI(Class<? extends GuiContainer> uiClass, Class<? extends Container> containerClass, GuiSourceType type) {
		int id = nextID++;
		guis.add(new GuiType(id, uiClass, containerClass, type));
		return id;
	}
	
	public static int registerUI(Class<? extends GuiContainer> uiClass, Class<? extends Container> containerClass) {
		return registerUI(uiClass, containerClass, GuiSourceType.TILEENTITY);
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		GuiType gui = guis.get(ID);
		if (gui != null) {
			return gui.handleServer(player, world, x, y, z);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		GuiType gui = guis.get(ID);
		if (gui != null) {
			return gui.handleClient(player, world, x, y, z);
		}
		return null;
	}

	public static class GuiType {
		public final Class<? extends GuiContainer> uiClass;
		public final Class<? extends Container> containerClass;
		public final int id;
		public final GuiSourceType type;
		
		public GuiType(int id, Class<? extends GuiContainer> uiClass, Class<? extends Container> containerClass, GuiSourceType type) {
			this.id = id;
			this.uiClass = uiClass;
			this.containerClass = containerClass;
			this.type = type;
		}
		
		public GuiContainer handleClient(EntityPlayer player, World world, int x, int y, int z) {
			return this.type.handleClient(this, player, world, x, y, z);
		}
		
		public Container handleServer(EntityPlayer player, World world, int x, int y, int z) {
			return this.type.handleServer(this, player, world, x, y, z);
		}
	}
	
	public static enum GuiSourceType {
		TILEENTITY {
			@Override
			public GuiContainer handleClient(GuiType guitype, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(x, y, z);
				GuiContainer gui = null;
				try {
					gui = guitype.uiClass.getDeclaredConstructor(EntityPlayer.class, TileEntity.class).newInstance(player, te);
				} catch (Exception e) {
					CuttingEdge.logger.warn(e);
				}
				return gui;
			}

			@Override
			public Container handleServer(GuiType guitype, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(x, y, z);
				Container container = null;
				try {
					container = guitype.containerClass.getDeclaredConstructor(EntityPlayer.class, TileEntity.class).newInstance(player, te);
				} catch (Exception e) {
					CuttingEdge.logger.warn(e);
				}
				return container;
			}
		},
		;
		
		public abstract GuiContainer handleClient(GuiType guitype, EntityPlayer player, World world, int x, int y, int z);
		public abstract Container handleServer(GuiType guitype, EntityPlayer player, World world, int x, int y, int z);
	}
}
