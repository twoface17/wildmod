package frozenblock.wild.mod.mixins;

import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Activity.class)
public interface ActivityInvoker {
    @Invoker
    static Activity callRegister(String id) {
        throw new IllegalStateException();
    }
}
