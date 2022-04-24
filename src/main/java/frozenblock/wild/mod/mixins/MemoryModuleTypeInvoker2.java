package frozenblock.wild.mod.mixins;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MemoryModuleType.class)
public interface MemoryModuleTypeInvoker2 {
    @Invoker
    static <U> MemoryModuleType<U> callRegister(String id) {
        throw new IllegalStateException();
    }
}
