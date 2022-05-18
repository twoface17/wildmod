package net.frozenblock.wildmod.entity.render.allay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.AllayEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class AllayHeldItemFeatureRenderer extends FeatureRenderer<AllayEntity, AllayEntityModel> {
    public AllayHeldItemFeatureRenderer(FeatureRendererContext<AllayEntity, AllayEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AllayEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrices.translate(0.0, 0.0, -1.0);
        
        matrices.push();

        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, itemStack, ModelTransformation.Mode.GROUND, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
