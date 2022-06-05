package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.minecraft.util.dynamic.GlobalPos;

import java.util.Optional;

public interface WildPlayerEntity {
    Optional<GlobalPos> getLastDeathPos();

    void setLastDeathPos(Optional<GlobalPos> lastDeathPos);

    SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    default SculkShriekerWarningManager getSculkShriekerWarningManager() {
        return this.sculkShriekerWarningManager;
    };
}
