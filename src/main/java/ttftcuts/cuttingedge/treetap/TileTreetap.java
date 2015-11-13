package ttftcuts.cuttingedge.treetap;

import ttftcuts.cuttingedge.TileBasic;
import net.minecraft.block.Block;
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
	
	public double fill = 0.0;	
	public double rate = 0.0;
	public int checktimer = 0;
	public static final int checkinterval = 300;
	
	private static final ForgeDirection[] cardinals = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
	
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
		
		if (this.checktimer == 0) {
			this.calculateRate();
		} else {
			this.checktimer--;
		}
		
		if (this.tank.getFluidAmount() < this.tank.getCapacity()) {
			TreeType tree = BlockTreetap.getTappable(worldObj, xCoord - this.direction.offsetX, yCoord, zCoord - this.direction.offsetZ);
			
			if (tree != null) {
				this.fill += this.rate;
				
				if (this.fill >= 1.0) {
					int amount = (int)Math.floor(this.fill);
					this.fill -= amount;
					this.tank.fill(new FluidStack(tree.fluid, amount), true);
					update = true;
				}
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
		this.fill = nbt.getDouble("fill");
		this.rate = nbt.getDouble("rate");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		this.writeTank(nbt, false);		
		nbt.setInteger("dir", this.direction.ordinal());
		nbt.setDouble("fill", this.fill);
		nbt.setDouble("rate", this.rate);
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
	
	protected void calculateRate() {
		this.rate = 0.0;
		
		int x = this.xCoord - this.direction.offsetX;
		int y = this.yCoord;
		int z = this.zCoord - this.direction.offsetZ;
		
		TreeType tree = BlockTreetap.getTappable(worldObj, x,y,z);

		if (tree != null) {
			int taps = 0;
			int leaves = 0;
			
			// tap check
			for (int i=0; i<=tree.maxHeight; i++) {
				if (tree.isTrunk(worldObj, x, y+i, z)) {
					for (ForgeDirection dir : cardinals) {
						Block b = this.worldObj.getBlock(x + dir.offsetX, y+i, z + dir.offsetZ);
						if (b instanceof BlockTreetap) {
							taps++;
						}
					}
				} else {
					break;
				}
			}
			
			for (int i=0; i<=tree.maxHeight; i++) {
				if (tree.isTrunk(worldObj, x, y-(i+1), z)) {
					for (ForgeDirection dir : cardinals) {
						Block b = this.worldObj.getBlock(x + dir.offsetX, y-(i+1), z + dir.offsetZ);
						if (b instanceof BlockTreetap) {
							taps++;
						}
					}
				} else {
					break;
				}
			}
			
			// leaves
			y++;
			
			for (int i=0; i<tree.maxHeight; i++) {
				if (tree.isTrunk(worldObj, x, y, z)) {
					for (int ox = -tree.radius; ox<= tree.radius; ox++) {
						for (int oz = -tree.radius; oz<= tree.radius; oz++) {
							if (tree.isLeaves(worldObj, x+ox, y, z+oz)) {
								leaves++;
							}
						}
					}
				} else {
					break;
				}
				y++;
			}
			
			for (int i=0; i<tree.canopyHeight; i++) {
				for (int ox = -tree.radius; ox<= tree.radius; ox++) {
					for (int oz = -tree.radius; oz<= tree.radius; oz++) {
						if (tree.isLeaves(worldObj, x+ox, y, z+oz)) {
							leaves++;
						}
					}
				}
				y++;
			}
			
			this.rate = ((leaves * tree.rate) / Math.max(1,taps)) * ModuleTreetap.globalTapRate;
		}
		
		this.checktimer = checkinterval;
		
		//CuttingEdge.logger.info("Rate update: "+taps+" taps, "+leaves+" leaves -> "+this.rate);
	}
}
