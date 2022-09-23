package net.frozenblock.wildmod.fromAccurateSculk;

import net.frozenblock.wildmod.block.entity.SculkCatalystPhase;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class WildProperties extends Properties {
    public static final IntProperty AGE_4 = IntProperty.of("age", 0, 4);
    public static final BooleanProperty BLOOM = BooleanProperty.of("bloom");

    public static final EnumProperty<SculkCatalystPhase> SCULK_CATALYST_PHASE = EnumProperty.of("sculk_catalyst_phase", SculkCatalystPhase.class);
    public static final EnumProperty<SculkShriekerPhase> SCULK_SHRIEKER_PHASE = EnumProperty.of("sculk_shrieker_phase", SculkShriekerPhase.class);
    public static final BooleanProperty ORIGINAL_RF = BooleanProperty.of("original_rf");
    public static final BooleanProperty CAN_SUMMON = BooleanProperty.of("can_summon");
    public static final BooleanProperty SHRIEKING = BooleanProperty.of("shrieking");
}
