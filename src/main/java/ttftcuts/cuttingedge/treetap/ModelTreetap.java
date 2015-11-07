package ttftcuts.cuttingedge.treetap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * Treetap - TTFTCUTS
 * Created using Tabula 4.1.1
 */
public class ModelTreetap extends ModelBase {
    public ModelRenderer Trickle;
    public ModelRenderer BagSideRight;
    public ModelRenderer BagBack;
    public ModelRenderer BagFront;
    public ModelRenderer BagSideLeft;
    public ModelRenderer BagBottom;
    public ModelRenderer HookRight;
    public ModelRenderer HookLeft;
    public ModelRenderer Tap;

    public ModelTreetap() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.BagFront = new ModelRenderer(this, 28, 0);
        this.BagFront.setRotationPoint(-6.0F, 10.3F, 6.0F);
        this.BagFront.addBox(0.0F, 0.0F, -11.0F, 12, 12, 1, 0.0F);
        this.setRotateAngle(BagFront, 0.153588974175501F, 0.0F, 0.0F);
        this.HookLeft = new ModelRenderer(this, 0, 14);
        this.HookLeft.setRotationPoint(-4.7F, 10.0F, 6.5F);
        this.HookLeft.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.HookRight = new ModelRenderer(this, 0, 14);
        this.HookRight.setRotationPoint(3.7F, 10.0F, 6.5F);
        this.HookRight.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.BagSideLeft = new ModelRenderer(this, 15, 2);
        this.BagSideLeft.setRotationPoint(-6.05F, 11.0F, 7.0F);
        this.BagSideLeft.addBox(0.0F, 0.0F, -11.0F, 1, 12, 11, 0.0F);
        this.setRotateAngle(BagSideLeft, 0.07853981633974483F, 0.0F, 0.0F);
        this.Trickle = new ModelRenderer(this, 30, 39);
        this.Trickle.setRotationPoint(0.0F, 11.0F, 6.8F);
        this.Trickle.addBox(-0.5F, 0.0F, 0.0F, 1, 12, 1, 0.0F);
        this.Tap = new ModelRenderer(this, 6, 14);
        this.Tap.setRotationPoint(0.0F, 9.7F, 8.4F);
        this.Tap.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(Tap, 0.4363323129985824F, 0.0F, 0.0F);
        this.BagBack = new ModelRenderer(this, 0, 0);
        this.BagBack.setRotationPoint(-6.0F, 11.0F, 7.0F);
        this.BagBack.addBox(0.0F, 0.0F, 0.0F, 12, 12, 1, 0.0F);
        this.BagSideRight = new ModelRenderer(this, 15, 2);
        this.BagSideRight.mirror = true;
        this.BagSideRight.setRotationPoint(5.05F, 11.0F, 7.0F);
        this.BagSideRight.addBox(0.0F, 0.0F, -11.0F, 1, 12, 11, 0.0F);
        this.setRotateAngle(BagSideRight, 0.07853981633974483F, 0.0F, 0.0F);
        this.BagBottom = new ModelRenderer(this, 0, 25);
        this.BagBottom.setRotationPoint(-5.5F, 23.0F, -3.0F);
        this.BagBottom.addBox(0.0F, 0.0F, 0.0F, 11, 1, 11, 0.0F);
    }

    public void render() {
    	//(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
    	float f5 = 1/16f;
        this.BagFront.render(f5);
        this.HookLeft.render(f5);
        this.HookRight.render(f5);
        this.BagSideLeft.render(f5);
        //this.Trickle.render(f5);
        this.Tap.render(f5);
        this.BagBack.render(f5);
        this.BagSideRight.render(f5);
        this.BagBottom.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
