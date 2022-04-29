package net.frozenblock.wildmod.entity.render;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelTransform;

@Environment(EnvType.CLIENT)
public class ModelData {
    private ModelPartData data = new ModelPartData(ImmutableList.of(), ModelTransform.NONE);

    public ModelData() {
    }

    public ModelPartData getRoot() {
        return this.data;
    }
}
