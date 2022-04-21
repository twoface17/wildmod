package frozenblock.wild.mod;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String entityPose = remapper.mapClassName("intermediary","net.minecraft.class_4050");
        ClassTinkerers.enumBuilder(entityPose).addEnum("CROAKING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("USING_TONGUE").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("ROARING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("SNIFFING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("EMERGING").build();
        ClassTinkerers.enumBuilder(entityPose).addEnum("DIGGING").build();
    }
}
