package net.frozenblock.wildmod.entity.render;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.MangroveBoatEntityModel;
import net.frozenblock.wildmod.entity.render.allay.AllayEntityModel;
import net.frozenblock.wildmod.entity.render.frog.FrogEntityModel;
import net.frozenblock.wildmod.entity.render.tadpole.TadpoleEntityModel;
import net.frozenblock.wildmod.entity.render.warden.WardenEntityModel;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.SignType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class EntityModels {
    private static final Dilation FISH_PATTERN_DILATION = new Dilation(0.008F);
    private static final Dilation ARMOR_DILATION = new Dilation(1.0F);
    private static final Dilation HAT_DILATION = new Dilation(0.5F);

    public EntityModels() {
    }

    public static Map<EntityModelLayer, TexturedModelData> getModels() {
        ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder = ImmutableMap.builder();

        builder.put(EntityModelLayers.WARDEN, WardenEntityModel.getTexturedModelData());

        TexturedModelData texturedModelData19 = MangroveBoatEntityModel.getTexturedModelData(false);
        TexturedModelData texturedModelData20 = MangroveBoatEntityModel.getTexturedModelData(true);

        for(BoatEntity.Type type : BoatEntity.Type.values()) {
            builder.put(EntityModelLayers.createBoat(type), texturedModelData19);
            builder.put(EntityModelLayers.createChestBoat(type), texturedModelData20);
        }

        ImmutableMap<EntityModelLayer, TexturedModelData> immutableMap = builder.build();
        List<EntityModelLayer> list = EntityModelLayers.getLayers().filter(layer -> !immutableMap.containsKey(layer)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing layer definitions: " + list);
        } else {
            return immutableMap;
        }
    }
}
