package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.Arrays;

public enum Angriness {
    CALM(0, RegisterSounds.ENTITY_WARDEN_AMBIENT),
    AGITATED(40, RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY),
    ANGRY(80, RegisterSounds.ENTITY_WARDEN_ANGRY);

    private static final Angriness[] field_38123;
    private final int field_38124;
    private final SoundEvent field_38125;

    Angriness(int j, SoundEvent soundEvent) {
        this.field_38124 = j;
        this.field_38125 = soundEvent;
    }

    public int method_42170() {
        return this.field_38124;
    }

    public SoundEvent method_42174() {
        return this.field_38125;
    }

    public static Angriness method_42171(int i) {
        for (Angriness angriness : field_38123) {
            if (i < angriness.field_38124) continue;
            return angriness;
        }
        return CALM;
    }

    static {
        field_38123 = Util.make(Angriness.values(), angrinesss -> Arrays.sort(angrinesss, (angriness, angriness2) -> Integer.compare(angriness2.field_38124, angriness.field_38124)));
    }
}
