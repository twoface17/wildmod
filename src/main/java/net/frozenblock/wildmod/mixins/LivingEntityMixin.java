package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.block.entity.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.fromAccurateSculk.ActivatorGrower;
import net.frozenblock.wildmod.fromAccurateSculk.SculkGrower;
import net.frozenblock.wildmod.fromAccurateSculk.SculkTags;
import net.frozenblock.wildmod.liukrastapi.Sphere;
import net.frozenblock.wildmod.registry.RegisterAccurateSculk;
import net.frozenblock.wildmod.registry.RegisterBlocks;
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
	private void updatePostDeath(CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		++entity.deathTime;
		if (entity.deathTime == 19 && !entity.world.isClient()) {
			BlockPos pos = entity.getBlockPos();
			World world = entity.world;
			if (SculkTags.entityTagContains(entity.getType(), SculkTags.DROPSXP)) {
				//SculkSpreadManager sculkSpreadManager = SculkSpreadManager.create();
				int numCatalysts = Sphere.blocksInSphere(pos, 9, RegisterBlocks.SCULK_CATALYST, world);
				if (numCatalysts>0) {
					entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, pos);
					//sculkSpreadManager.spread(new BlockPos((Vec3d.ofCenter(pos).withBias(Direction.UP, 0.5))), 5);
					SculkGrower.sculk(pos, world, entity, numCatalysts);
					int rVal2 = getHighestRadius(world, pos);
					ActivatorGrower.startGrowing(rVal2, rVal2, pos, world);
				}
			}
		}
	}

	public int getHighestRadius(World world, BlockPos pos) {
		int current = 3;
		for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, RegisterBlocks.SCULK_CATALYST, world)) {
			BlockEntity catalyst = world.getBlockEntity(blockPos);
			if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
				current=Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
			}
		}
		return current;
	}

}
