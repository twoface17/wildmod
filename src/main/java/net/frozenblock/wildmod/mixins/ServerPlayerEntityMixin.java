package net.frozenblock.wildmod.mixins;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin /*implements WildServerPlayerEntity */{
    //public SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    //@Override
    //public SculkShriekerWarningManager getSculkShriekerWarningManager() {
        //return this.sculkShriekerWarningManager;
    //}
}
