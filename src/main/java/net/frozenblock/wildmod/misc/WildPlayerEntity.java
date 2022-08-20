package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.minecraft.util.dynamic.GlobalPos;

import java.util.Optional;

public interface WildPlayerEntity {

    Optional<GlobalPos> getLastDeathPos();

    void setLastDeathPos(Optional<GlobalPos> lastDeathPos);

    SculkShriekerWarningManager getSculkShriekerWarningManager();


}
