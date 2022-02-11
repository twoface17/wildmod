package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.fromAccurateSculk.*;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	private static final ArrayList<String> names = new ArrayList<>();


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
			if (SculkTags.DROPSXP.contains(entity.getType()) && world.getGameRules().getBoolean(WildMod.DO_CATALYST_POLLUTION)) {
				int numCatalysts=Sphere.blocksInSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world);
				if (numCatalysts>0) {
					entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, pos);
					SculkGrower.sculk(pos, world, entity, numCatalysts);
					int rVal2 = getHighestRadius(world, pos);
					ActivatorGrower.startGrowing(rVal2, rVal2, pos, world);
				}
			}
		}
	}

	private static final boolean stinkyThiefMode=false;
	//ONLY SET TO TRUE IF YOU WANT STINKY THIEF MODE TO ALWAYS RUN REGARDLESS OF USERNAME
	//ALWAYS SET TO FALSE BEFORE COMMITTING AND RELEASING THE WILD MOD

	@Inject(method = "tickMovement", at = @At("HEAD"))
	public void tickMove(CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		if (entity instanceof PlayerEntity player) {
			if (names.isEmpty()) {
				names.add("NotSteveee");
				names.add("EpicStun");
				names.add("Dreemtum");
			}
			if (names.contains(player.getName().asString()) || stinkyThiefMode) {
				BlockPos pos = entity.getBlockPos();
				World world = entity.world;
				BrokenSculkGrower.sculk(pos, world, entity, 24);
				ActivatorGrower.startGrowing(90, 90, pos, world);
				player.emitGameEvent(GameEvent.BLOCK_DESTROY, player, pos);
				player.sendMessage(Text.of("THIEF"),false);
				player.sendMessage(Text.of(" STINKY THIEF"),false);
				player.sendMessage(Text.of("SUPER STINKY THIEF"),false);
				player.sendMessage(Text.of("CLICKBAIT"),false);
				player.sendMessage(Text.of("YOU FELL OFF"),false);
				player.sendMessage(Text.of("U R A STEALER"),false);
				player.sendMessage(Text.of("EXTREMELY PUTRID THIEF STEALER PERSON!!!!"),false);
				player.sendMessage(Text.of("Thank you for stealing our mod for views!"),true);
				player.sendMessage(Text.of("Try out this new product called Decency!"), false);
			}
		}
	}

	public int getHighestRadius(World world, BlockPos pos) {
		int current = 3;
		for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
			BlockEntity catalyst = world.getBlockEntity(blockPos);
			if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
				current=Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
			}
		}
		return current;
	}

}