package ttftcuts.cuttingedge.treetap;

import ttftcuts.cuttingedge.TileBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileTreetap extends TileBasic implements IFluidHandler {

	public FluidTank tank = new FluidTank(4000);
	public ForgeDirection direction = ForgeDirection.UNKNOWN;
	
	public TileTreetap(){}
	
	public TileTreetap(int direction) {
		this.direction = ForgeDirection.getOrientation(direction);
	}
	
	@Override
	public void updateEntity()
	{
		if (this.worldObj.isRemote) {
			return;
		}
		boolean update = false;
		
		if (this.tank.getFluidAmount() < this.tank.getCapacity()) {
			TreeType tree = BlockTreetap.getTappable(worldObj, xCoord - this.direction.offsetX, yCoord, zCoord - this.direction.offsetZ);
			
			if (tree != null) {
				// temp
				this.tank.fill(new FluidStack(tree.fluid,1000), true);
				update = true;
			}
		}
		
		if (this.tank.getFluidAmount() > 0) {
			int outamount = Math.min(200, this.tank.getFluidAmount());
			TileEntity tile = this.worldObj.getTileEntity(this.xCoord, this.yCoord-1, this.zCoord);
			if (tile != null && tile instanceof IFluidHandler) {
				IFluidHandler ifh = (IFluidHandler)tile;
				Fluid f = this.tank.getFluid().getFluid();
				if (ifh.canFill(ForgeDirection.UP, f)) {
					int inamount = ifh.fill(ForgeDirection.UP, new FluidStack(f, outamount), false);
					FluidStack drained = this.tank.drain(inamount, true);
					ifh.fill(ForgeDirection.UP, drained, true);
					update = true;
				}
			}
		}
		
		if (update) {
			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.drain(from, resource != null ? resource.amount : 0, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.canDrain(from, null)) {
			FluidStack f = tank.drain(maxDrain, doDrain);
			if (f != null && f.amount > 0) {
				this.markDirty();
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return f;
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UNKNOWN || from == ForgeDirection.DOWN;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{ this.tank.getInfo() };
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		this.readTank(nbt);	
		this.direction = ForgeDirection.getOrientation(nbt.getInteger("dir"));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		this.writeTank(nbt, false);		
		nbt.setInteger("dir", this.direction.ordinal());
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
}
