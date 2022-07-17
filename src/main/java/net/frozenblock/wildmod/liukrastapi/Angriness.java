package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;

import java.util.Arrays;

public enum Angriness {
    CALM(0, RegisterSounds.ENTITY_WARDEN_AMBIENT, RegisterSounds.ENTITY_WARDEN_LISTENING),
    AGITATED(40, RegisterSounds.ENTITY_WARDEN_AGITATED, RegisterSounds.ENTITY_WARDEN_LISTENING_ANGRY),
    ANGRY(80, RegisterSounds.ENTITY_WARDEN_ANGRY, RegisterSounds.ENTITY_WARDEN_LISTENING_ANGRY);

    private static final Angriness[] VALUES = Util.make(values(), values -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    private final int threshold;
    private final SoundEvent sound;
    private final SoundEvent listeningSound;

    Angriness(int threshold, SoundEvent sound, SoundEvent listeningSound) {
        this.threshold = threshold;
        this.sound = sound;
        this.listeningSound = listeningSound;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static Angriness getForAnger(int anger) {
        for (Angriness angriness : VALUES) {
            if (anger >= angriness.threshold) {
                return angriness;
            }
        }

        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }
}
