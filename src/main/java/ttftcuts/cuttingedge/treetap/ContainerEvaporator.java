package ttftcuts.cuttingedge.treetap;

import ttftcuts.cuttingedge.CuttingEdge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ContainerEvaporator extends Container {
	private TileEvaporator evap;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;
    private int lastFluidLevel;
    private int lastFluidId;
	
	public ContainerEvaporator(EntityPlayer player, TileEntity te) {
		this.evap = (TileEvaporator)te;
		
        this.addSlotToContainer(new Slot(this.evap, 0, 56, 53));
        this.addSlotToContainer(new SlotFurnace(player, this.evap, 1, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return evap.isUseableByPlayer(player);
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting)
    {
        super.addCraftingToCrafters(icrafting);
        icrafting.sendProgressBarUpdate(this, 0, this.evap.cookTime);
        icrafting.sendProgressBarUpdate(this, 1, this.evap.burnTime);
        icrafting.sendProgressBarUpdate(this, 2, this.evap.currentItemBurnTime);
        icrafting.sendProgressBarUpdate(this, 3, this.evap.tank.getFluid() != null ? this.evap.tank.getFluid().getFluidID() : -1);
        icrafting.sendProgressBarUpdate(this, 4, this.evap.tank.getFluidAmount());
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        int fluidId = this.evap.tank.getFluid() != null ? this.evap.tank.getFluid().getFluidID() : -1;
        
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastCookTime != this.evap.cookTime)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.evap.cookTime);
            }

            if (this.lastBurnTime != this.evap.burnTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.evap.burnTime);
            }

            if (this.lastItemBurnTime != this.evap.currentItemBurnTime)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.evap.currentItemBurnTime);
            }
            
            if (this.lastFluidId != fluidId) {
            	icrafting.sendProgressBarUpdate(this, 3, fluidId);
            }
            
            if (this.lastFluidLevel != this.evap.tank.getFluidAmount()) {
            	icrafting.sendProgressBarUpdate(this, 4, this.evap.tank.getFluidAmount());
            }
        }

        this.lastCookTime = this.evap.cookTime;
        this.lastBurnTime = this.evap.burnTime;
        this.lastItemBurnTime = this.evap.currentItemBurnTime;
        this.lastFluidLevel = this.evap.tank.getFluidAmount();
        this.lastFluidId = fluidId;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int progress)
    {
        if (id == 0)
        {
            this.evap.cookTime = progress;
        }

        if (id == 1)
        {
            this.evap.burnTime = progress;
            CuttingEdge.logger.info(progress);
        }

        if (id == 2)
        {
            this.evap.currentItemBurnTime = progress;
        }
        
        if (id == 3) {
        	if (this.evap.tank.getFluid() == null) {
        		if (progress != -1) {
        			FluidStack fluid = new FluidStack(FluidRegistry.getFluid(progress), this.evap.tank.getFluidAmount());
            		this.evap.tank.setFluid(fluid);
        		}
        	} else {
        		if (progress == -1) {
        			this.evap.tank.setFluid(null);
        		} else if (progress != this.evap.tank.getFluid().getFluid().getID()) {
        			FluidStack fluid = new FluidStack(FluidRegistry.getFluid(progress), this.evap.tank.getFluidAmount());
        			this.evap.tank.setFluid(fluid);
        		}
        	}
        }
        
        if (id == 4) {
        	if (this.evap.tank.getFluid() != null) {
        		this.evap.tank.getFluid().amount = progress;
        	}
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotid)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotid);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotid == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotid != 1 && slotid != 0)
            {
                if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (slotid >= 3 && slotid < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (slotid >= 30 && slotid < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
