package ttftcuts.cuttingedge.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidUtil {
	// <3 Blu
	public static boolean fillPlayerItemFromFluidHandler(World world, IFluidHandler handler, EntityPlayer player, FluidStack tankFluid)
	{
		ItemStack equipped = player.getCurrentEquippedItem();
		if(equipped==null)
			return false;
		if(FluidContainerRegistry.isEmptyContainer(equipped))
		{
			ItemStack filledStack = FluidContainerRegistry.fillFluidContainer(tankFluid, equipped);
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(filledStack);
			if(fluid==null || filledStack==null)
				return false;
			if(world.isRemote)
				return true;

			if(!player.capabilities.isCreativeMode)
				if(equipped.stackSize == 1)
				{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, filledStack);
					equipped.stackSize -= 1;
					if (equipped.stackSize <= 0)
						equipped = null;
				}
				else 
				{
					equipped.stackSize -= 1;
					if(!player.inventory.addItemStackToInventory(filledStack))
						player.func_146097_a(filledStack, false, true);
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
				}
			handler.drain(ForgeDirection.UNKNOWN, fluid.amount, true);
			return true;
		}
		else if(equipped.getItem() instanceof IFluidContainerItem)
		{
			IFluidContainerItem container = (IFluidContainerItem)equipped.getItem();
			if(container.fill(equipped, tankFluid, false)>0)
			{
				if(world.isRemote)
					return true;

				int fill = container.fill(equipped, tankFluid, true);
				handler.drain(ForgeDirection.UNKNOWN, fill, true);
				player.openContainer.detectAndSendChanges();
				((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
				return true;
			}
		}
		return false;
	}

	public static boolean fillFluidHandlerWithPlayerItem(World world, IFluidHandler handler, EntityPlayer player)
	{
		ItemStack equipped = player.getCurrentEquippedItem();
		if(equipped==null)
			return false;
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(equipped);
		if(fluid != null)
		{
			if(handler.fill(ForgeDirection.UNKNOWN, fluid, false) == fluid.amount || player.capabilities.isCreativeMode)
			{
				if(world.isRemote)
					return true;

				ItemStack filledStack = FluidContainerRegistry.drainFluidContainer(equipped);
				if (!player.capabilities.isCreativeMode)
				{
					if(equipped.stackSize==1)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						player.inventory.addItemStackToInventory(filledStack);
					}
					else
					{
						equipped.stackSize -= 1;
						if(filledStack!=null && !player.inventory.addItemStackToInventory(filledStack))
							player.func_146097_a(filledStack, false, true);
					}
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
				}
				handler.fill(ForgeDirection.UNKNOWN, fluid, true);
				return true;
			}
		}
		else if(equipped.getItem() instanceof IFluidContainerItem)
		{
			IFluidContainerItem container = (IFluidContainerItem)equipped.getItem();
			fluid = container.getFluid(equipped);
			if(handler.fill(ForgeDirection.UNKNOWN, fluid, false)>0)
			{
				if(world.isRemote)
					return true;

				int fill = handler.fill(ForgeDirection.UNKNOWN, fluid, true);
				container.drain(equipped, fill, true);
				player.openContainer.detectAndSendChanges();
				((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
				return true;
			}
		}
		return false;
	}
}
