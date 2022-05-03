package net.frozenblock.wildmod.mixins;

import com.google.common.collect.Maps;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.minecraft.client.model.ModelPartData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelPartData.class)
public abstract class ModelPartDataMixin implements ExpandedModelPart {
    //private final List<ModelCuboidData> cuboidData;
    //private final ModelTransform rotationData;
    private final Map<String, ModelPartData> children = Maps.newHashMap();

    /*@Inject(method = "createPart", at = @At("TAIL"))
    public ModelPart createPart(int textureWidth, int textureHeight, CallbackInfoReturnable<ModelPart> cir) {
        List<ModelCuboidData> cuboidData = Collections.singletonList(ModelCuboidData.class.cast(this));
        Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = this.children
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight),
                                (modelPartx, modelPart2) -> modelPartx,
                                Object2ObjectArrayMap::new
                        )
                );
        List<ModelPart.Cuboid> list = cuboidData
                .stream()
                .map(modelCuboidData -> modelCuboidData.createCuboid(textureWidth, textureHeight))
                .collect(ImmutableList.toImmutableList());
        ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
        modelPart.traverse().forEach(modelPart1 -> ((ExpandedModelPart)modelPart1).resetTransform());
        return modelPart;
    }

    */@Inject(method = "getChild", at = @At("HEAD"))
    public ModelPartData getChild(String name, CallbackInfoReturnable<ModelPartData> cir) {
        return this.children.get(name);
    }
}
