package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;

public interface WildServerPlayerEntity {

    SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    default SculkShriekerWarningManager getSculkShriekerWarningManager() {
        return this.sculkShriekerWarningManager;
    }

}
