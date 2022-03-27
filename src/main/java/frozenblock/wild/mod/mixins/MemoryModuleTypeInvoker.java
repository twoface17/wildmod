package frozenblock.wild.mod.mixins;

import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MemoryModuleType.class)
public interface MemoryModuleTypeInvoker {
    @Invoker
    static <U> MemoryModuleType<U> callRegister(String id, Codec<U> codec) {
        throw new IllegalStateException();
    }
}
