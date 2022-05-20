package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.util.dynamic.GlobalPos;

import java.util.Optional;

public interface WildPlayerEntity {
    Optional<GlobalPos> getLastDeathPos();

    void setLastDeathPos(Optional<GlobalPos> lastDeathPos);
}
