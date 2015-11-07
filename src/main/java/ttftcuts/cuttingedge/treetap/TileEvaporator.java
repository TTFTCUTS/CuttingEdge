package ttftcuts.cuttingedge.treetap;

import ttftcuts.cuttingedge.TileBasic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEvaporator extends TileBasic implements ISidedInventory, IFluidHandler {
	protected static int[] outstacks = new int[] {1};
	protected static int[] fuelstacks = new int[] {0};
	protected ItemStack[] stacks = new ItemStack[2];
	protected FluidTank tank = new FluidTank(4000);
	
	public int burnTime;
    public int currentItemBurnTime;
    public int cookTime;
    
    public boolean burning;
	
    @Override
	public void updateEntity() {
    	boolean burningNow = this.burnTime > 0;
    	boolean update = false;
    	boolean updateBlock = false;
    	
    	if (this.burnTime > 0) {
    		this.burnTime--;
    	}
    	
    	if (!this.worldObj.isRemote) {
			if (this.burnTime == 0 && this.canEvaporate()) {
				this.currentItemBurnTime = this.burnTime = TileEntityFurnace.getItemBurnTime(this.stacks[0]);
				
				if (this.burnTime > 0) {
					update = true;
					
					if (this.stacks[0] != null) {
						this.stacks[0].stackSize--;
						
						if (this.stacks[0].stackSize == 0) {
							this.stacks[0] = this.stacks[0].getItem().getContainerItem(this.stacks[0]);
						}
					}
				}
			}
			
			if (this.burnTime > 0 && this.canEvaporate()) {
				this.cookTime++;
				
				if (this.cookTime == 200) {
					this.cookTime = 0;
					this.evaporate();
					update = true;
				}
			} else {
				this.cookTime = 0;
			}
    		
    		if (burningNow != this.burnTime > 0) {
    			update = true;
    			this.burning = this.burnTime > 0;
    			updateBlock = true;
    		}
    	}
    	
    	if (update) {
    		this.markDirty();
    		if (updateBlock) {
    			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    		}
    	}
    }
    
    protected boolean canEvaporate() {
    	if (this.tank.getFluidAmount() == 0) {
    		return false;
    	}
    	
    	EvaporateType evap = null;
    	for (EvaporateType e : ModuleTreetap.evapables) {
    		if (e.fluidMatches(this.tank.getFluid().getFluid())) {
    			evap = e;
    			break;
    		}
    	}
    	if (evap == null) { 
    		return false; 
    	}
    	
    	ItemStack result = evap.output;
    	if (this.tank.getFluidAmount() < evap.fluid.amount) { 
    		return false; 
    	}
    	if (this.stacks[1] == null) { return true; }
    	if (!this.stacks[1].isItemEqual(result)) {
    		return false; 
    	}
    	
    	int outcount = stacks[1].stackSize + result.stackSize;
    	return outcount <= this.getInventoryStackLimit() && outcount <= this.stacks[1].getMaxStackSize();
    }
    
	@Override
	public int getSizeInventory() {
		return this.stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.stacks[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			ItemStack outstack;
			if (stack.stackSize <= amount) {
				outstack = stack;
				this.stacks[slot] = null;
				return outstack;
			} else {
				outstack = stack.splitStack(amount);
				if (stack.stackSize == 0) {
					this.stacks[slot] = null;
				}
				return outstack;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.stacks[slot] != null)
        {
            ItemStack itemstack = this.stacks[slot];
            this.stacks[slot] = null;
            return itemstack;
        }
        return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.stacks[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
	}

	@Override
	public String getInventoryName() {
		return "container.treetap.evaporator";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return slot == 1 ? false : TileEntityFurnace.isItemFuel(stack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side == 1 ? outstacks : fuelstacks;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return side != 0 || slot != 0 || stack.getItem() == Items.bucket;
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		this.stacks[0] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("fuel"));
		this.stacks[1] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("out"));
		
		this.burnTime = nbt.getInteger("burn");
		this.cookTime = nbt.getInteger("cook");
		this.currentItemBurnTime = nbt.getInteger("cibt");
		
		boolean wasburning = this.burning;
		this.burning = nbt.getBoolean("burning");
		if (wasburning != this.burning) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		this.readTank(nbt);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		if (stacks[0] != null) {
			nbt.setTag("fuel", stacks[0].writeToNBT(new NBTTagCompound()));
		}
		if (stacks[1] != null) {
			nbt.setTag("out", stacks[1].writeToNBT(new NBTTagCompound()));
		}
		
		nbt.setInteger("burn", this.burnTime);
		nbt.setInteger("cook", this.cookTime);
		nbt.setInteger("cibt", this.currentItemBurnTime);
		
		nbt.setBoolean("burning", this.burning);
		
		this.writeTank(nbt, false);
	}

	public void readTank(NBTTagCompound nbt)
	{
		tank.readFromNBT(nbt.getCompoundTag("tank"));
	}
	
	public void writeTank(NBTTagCompound nbt, boolean toItem)
	{
		boolean write = tank.getFluidAmount()>0;
		NBTTagCompound tankTag = tank.writeToNBT(new NBTTagCompound());
		if(!toItem || write) {
			nbt.setTag("tank", tankTag);
		}
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource != null && this.canFill(from, resource.getFluid())) {
			int l = this.tank.fill(resource, doFill);
			if (l > 0) {
				this.markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return l;
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		boolean allowed = false;
		
		for (EvaporateType evap : ModuleTreetap.evapables) {
			if (evap.fluidMatches(fluid)) {
				allowed = true;
				break;
			}
		}
		
		return allowed && (from == ForgeDirection.UNKNOWN || from == ForgeDirection.UP);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {this.tank.getInfo()};
	}
	
    public int getCookProgressScaled(int pixels)
    {
        return this.cookTime * pixels / 200;
    }

    public int getBurnTimeRemainingScaled(int pixels)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.burnTime * pixels / this.currentItemBurnTime;
    }
    
    public void evaporate() {
    	if (this.canEvaporate()) {
    		EvaporateType evap = null;
        	for (EvaporateType e : ModuleTreetap.evapables) {
        		if (e.fluidMatches(this.tank.getFluid().getFluid())) {
        			evap = e;
        			break;
        		}
        	}
        	if (evap == null) { 
        		return; 
        	}
        	
        	ItemStack stack = evap.output;
        	
        	if (this.stacks[1] == null) {
        		this.stacks[1] = stack.copy();
        	} else if (this.stacks[1].getItem() == stack.getItem()) {
        		this.stacks[1].stackSize += stack.stackSize;
        	}
        	
        	this.tank.drain(evap.fluid.amount, true);
    	}
    }
}
