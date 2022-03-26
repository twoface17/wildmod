package frozenblock.wild.mod.mixins;

import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(MemoryModuleType.class)
public interface MemoryModuleTypeInvoker {
    @Invoker
    static <U> MemoryModuleType<U> callRegister(String id, Codec<U> codec) {
        throw new IllegalStateException();
    }
}
