package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.fromAccurateSculk.*;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "setHealth", at = @At("HEAD"))
	private void setHealth(float f, CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		if (entity.getType()==EntityType.ENDER_DRAGON && f==0.0F) {
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(20));
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(14));
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(7));
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos());
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(7));
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(14));
			entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(20));
			}
		}


	@Inject(method = "updatePostDeath", at = @At("HEAD"))
	private void updatePostDeath(CallbackInfo info) throws InterruptedException {
		LivingEntity entity = LivingEntity.class.cast(this);
		++entity.deathTime;
		if (entity.deathTime == 19 && !entity.world.isClient()) {
			BlockPos pos = new BlockPos(entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ());
			if (SculkTags.DROPSXP.contains(entity.getType()) && entity.world.getGameRules().getBoolean(WildMod.DO_CATALYST_POLLUTION)) {
				if (Sphere.sphereBlock(RegisterBlocks.SCULK_CATALYST, entity.world, pos, 8)) {
					entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, pos);
					int numCatalysts=Sphere.generateSphere(pos, 8, false, entity.world);
					if (numCatalysts>0) {
					if (entity.world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {
						CatalystThreader.main(entity.world, entity, pos, numCatalysts);
					} else if (!(entity.world.getGameRules().getBoolean(WildMod.SCULK_THREADING))) {
						SculkGrower.sculk(pos, entity.world, entity, numCatalysts);
						int rVal2 = getHighestRadius(entity.world, pos);
						int activatorLoop = (int) ((48) * Math.sin((rVal2 / 40.75)));
						new ActivatorGrower().placeActiveOmptim(activatorLoop, rVal2, pos, entity.world);
					}
					}
				}
			}
		}
	}

	public int getHighestRadius(World world, BlockPos pos) {
		int current = 3;
		for (BlockPos blockPos : Sphere.checkSpherePos(SculkCatalystBlock.SCULK_CATALYST_BLOCK.getDefaultState(), world, pos, 8, false)) {
			BlockEntity catalyst = world.getBlockEntity(blockPos);
			if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
				current=Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
			}
		}
		return current;
	}

}