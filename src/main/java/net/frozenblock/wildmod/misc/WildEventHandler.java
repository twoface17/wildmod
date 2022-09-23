package net.frozenblock.wildmod.misc;

import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public interface WildEventHandler<T extends GameEventListener> {

    void setListener(T listener, @Nullable World world);
}
