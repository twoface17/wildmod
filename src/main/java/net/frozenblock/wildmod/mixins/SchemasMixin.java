package net.frozenblock.wildmod.mixins;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.frozenblock.wildmod.liukrastapi.GoatHornIdFix;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(Schemas.class)
public class SchemasMixin {
    @Shadow @Final private static BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE;

    @Inject(method = "build", at = @At("TAIL"))
    private static void build(DataFixerBuilder builder, CallbackInfo ci) {
        Schema schema168 = builder.addSchema(3094, EMPTY_IDENTIFIER_NORMALIZE);
        builder.addFixer(new GoatHornIdFix(schema168)) ;
    }
}
