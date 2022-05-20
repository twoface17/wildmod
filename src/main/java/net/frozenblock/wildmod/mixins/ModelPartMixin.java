package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ExpandedModelPart {
	@Shadow public float pivotX;
	@Shadow public float pivotY;
	@Shadow public float pivotZ;
	@Shadow public float pitch;
	@Shadow public float yaw;
	@Shadow public float roll;
	@Shadow @Final private Map<String, ModelPart> children;

	@Shadow public abstract void setTransform(ModelTransform rotationData);

	@Shadow public boolean visible;
	@Shadow @Final public List<ModelPart.Cuboid> cuboids;

	@Shadow protected abstract void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);

	@Shadow public abstract void rotate(MatrixStack matrices);

	private boolean hidden;

	public float xScale = 1.0F;
	public float yScale = 1.0F;
	public float zScale = 1.0F;
	private ModelTransform defaultTransform = ModelTransform.NONE;

	public boolean getHidden() {
		return this.hidden;
	}

	public void setHidden(boolean value) {
		this.hidden=value;
	}

	/**
	 * @author FrozenBlock
	 * @reason New Warden Rendering
	 */
	@Overwrite
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrices.push();
				this.rotate(matrices);
				if (!this.hidden) {
					this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
				}

				for(ModelPart modelPart : this.children.values()) {
					modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
				}

				matrices.pop();
			}
		}
	}

	@Override
	public ModelTransform getDefaultTransform() {
		return defaultTransform;
	}

	@Override
	public void setDefaultTransform(ModelTransform modelTransform) {
		this.defaultTransform = modelTransform;
	}

	@Override
	public void resetTransform() {
		this.setTransform(defaultTransform);
	}

	@Override
	public boolean hasChild(String child) {
		return this.children.containsKey(child);
	}

	@Override
	public float getXScale() {
		return xScale;
	}

	@Override
	public float getYScale() {
		return yScale;
	}

	@Override
	public float getZScale() {
		return zScale;
	}

	@Override
	public void translate(Vec3f vector3f) {
		this.pivotX += vector3f.getX();
		this.pivotY += vector3f.getY();
		this.pivotZ += vector3f.getZ();
	}

	@Override
	public void rotate(Vec3f vector3f) {
		this.pitch += vector3f.getX();
		this.yaw += vector3f.getY();
		this.roll += vector3f.getZ();
	}

	@Override
	public void scale(Vec3f vector3f) {
		this.xScale += vector3f.getX();
		this.yScale += vector3f.getY();
		this.zScale += vector3f.getZ();
	}

	@Inject(method = "setTransform", at=@At("RETURN"))
	public void expandedSetTransform(ModelTransform rotationData, CallbackInfo ci) {
		this.xScale = 1.0F;
		this.yScale = 1.0F;
		this.zScale = 1.0F;
	}

	@Inject(method = "copyTransform", at=@At("RETURN"))
	public void expandedCopyTransform(ModelPart part, CallbackInfo ci) {
		this.xScale = ((ExpandedModelPart)part).getXScale();
		this.yScale = ((ExpandedModelPart)part).getYScale();
		this.zScale = ((ExpandedModelPart)part).getZScale();
	}

	@Inject(method = "rotate", at=@At("RETURN"))
	public void expandedRotate(MatrixStack matrices, CallbackInfo ci) {
		if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
			matrices.scale(this.xScale, this.yScale, this.zScale);
		}
	}
}
