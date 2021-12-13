package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterParticles;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {

    @Inject(at = @At("TAIL"), method = "setActive")
    private static void setActive(World world, BlockPos pos, BlockState state, int power, CallbackInfo ci) {
        ArrayList<BlockPos> shriekers = Sphere.checkSpherePos(
                RegisterBlocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.POWERED, false),
                world,
                pos,
                10,
                true
        );
        for (BlockPos pos1 : shriekers) {
            world.setBlockState(pos1, RegisterBlocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.POWERED, true));
            if (!world.isClient) {
                world.playSound(null, pos1, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 1f, 1f);
                world.addParticle(RegisterParticles.SHRIEK, pos1.getX(), pos1.getY(), pos1.getZ(), 0, 0.3, 0);

                double d = 10;
                Box box = (new Box(pos1)).expand(d).stretch(0.0D, world.getHeight(), 0.0D);
                List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
                Iterator var11 = list.iterator();

                PlayerEntity playerEntity;
                while (var11.hasNext()) {
                    playerEntity = (PlayerEntity) var11.next();
                    playerEntity.addStatusEffect(new StatusEffectInstance(RegisterStatusEffects.DARKNESS, 240, 0, false, true));
                }
            }
            world.createAndScheduleBlockTick(pos1, world.getBlockState(pos1).getBlock(), 40);
        }
    }
}
