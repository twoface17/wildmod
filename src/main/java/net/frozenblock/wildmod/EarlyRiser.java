package net.frozenblock.wildmod;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        // EntityPose
        String entityPose = remapper.mapClassName("intermediary", "net.minecraft.class_4050");
        ClassTinkerers.enumBuilder(entityPose).addEnum("CROAKING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("USING_TONGUE").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("ROARING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("SNIFFING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("EMERGING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("DIGGING").build();

        // UseAction
        String useAction = remapper.mapClassName("intermediary", "net.minecraft.class_1839");
        ClassTinkerers.enumBuilder(useAction).addEnum("TOOT_HORN").build();

        // BipedEntityModel.ArmPose
        String armPose = remapper.mapClassName("intermediary", "net.minecraft.class_572$class_573");
        ClassTinkerers.enumBuilder(armPose, boolean.class).addEnum("TOOT_HORN", false).build();
    }
}
