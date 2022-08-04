package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.dynamic.GlobalPos;

import java.util.Optional;

public interface WildPlayerEntity {

    Optional<GlobalPos> getLastDeathPos();

    void setLastDeathPos(Optional<GlobalPos> lastDeathPos);

    SculkShriekerWarningManager getSculkShriekerWarningManager();


}
