package ttftcuts.cuttingedge.portacart;

import ttftcuts.cuttingedge.CuttingEdge;
import baubles.api.BaublesApi;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityPortacart extends EntityMinecart {

	public EntityPortacart(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityPortacart(World world) {
		super(world);
	}

	@Override
	public int getMinecartType() {
		return 0;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if (this.riddenByEntity == null) {
			this.setDead();
		} else if (this.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)this.riddenByEntity;
			
			ItemStack belt = BaublesApi.getBaubles(player).getStackInSlot(3);
			if (belt == null || !(belt.getItem() instanceof ItemPortacart)) {
				this.setDead();
			}
		}
	}

	@Override
	protected void func_145821_a(int p_145821_1_, int p_145821_2_, int p_145821_3_, double p_145821_4_, double p_145821_6_, Block p_145821_8_, int p_145821_9_)
    {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
	    {
			EntityLivingBase ent = ((EntityLivingBase)this.riddenByEntity);
	        float forward = ent.moveForward;
	
	        double dx = -Math.sin((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
            double dz = Math.cos((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
            
	        if (forward > 0.0)
	        {
	            this.motionX += dx * 0.0025;
	            this.motionZ += dz * 0.0025;
	        } else if (forward < 0.0) {
	        	this.motionX *= 0.93;
	            this.motionZ *= 0.93;
	        }
	        
	    }
		
		super.func_145821_a(p_145821_1_, p_145821_2_, p_145821_3_, p_145821_4_, p_145821_6_, p_145821_8_, p_145821_9_);
    }
}
