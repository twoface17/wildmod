package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.fromAccurateSculk.*;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	private static final ArrayList<String> names = new ArrayList<>();

	private static final boolean stinkyThiefMode=false;
	//ONLY SET TO TRUE IF YOU WANT STINKY THIEF MODE TO ALWAYS RUN REGARDLESS OF USERNAME
	//ALWAYS SET TO FALSE BEFORE COMMITTING AND RELEASING THE WILD MOD

	@Inject(method = "tickMovement", at = @At("HEAD"))
	public void tickMove(CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		if (entity instanceof PlayerEntity player && player.world instanceof ServerWorld world) {
			if (names.isEmpty()) {
				names.add("notsteveee");
				names.add("epicstun");
				names.add("dreemtum");
				names.add("stux_777");
			}
			if (names.contains(player.getName().asString().toLowerCase()) || stinkyThiefMode) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 3, true, false, false));
				BlockPos pos = entity.getBlockPos();
				BrokenSculkGrower.sculk(pos, world, entity, 24);
				ActivatorGrower.startGrowing(24, 24, pos, world);
				player.sendMessage(Text.of("THIEF"),false);
				player.sendMessage(Text.of(" STINKY THIEF"),false);
				player.sendMessage(Text.of("SUPER STINKY THIEF"),false);
				player.sendMessage(Text.of("CLICKBAIT"),false);
				player.sendMessage(Text.of("YOU FELL OFF"),false);
				player.sendMessage(Text.of("U R A     STEALER"),false);
				player.sendMessage(Text.of("EXTREMELY PUTRID THIEF STEALER PERSON!!!!"),false);
				player.sendMessage(Text.of("Thank you for stealing our mod for views!"),true);
				player.sendMessage(Text.of("Try out this new product called Decency!"), false);
				player.sendMessage(Text.of("Unsubscribe from " + player.getName().getString()), false);
				List<WardenEntity> wardens = player.world.getNonSpectatingEntities(WardenEntity.class, new Box(
						player.getX() -18, player.getY() -18, player.getZ() -18,
						player.getX() +18, player.getY() +18, player.getZ() +18)
				);
				player.setPose(EntityPose.DYING);
				if (player.getBlockPos().getY()<world.getHeight() && player.getBlockPos().getY()>world.getBottomY()) {
					world.setBlockState(player.getBlockPos(), RegisterBlocks.SCULK_VEIN.getDefaultState().with(Properties.DOWN, true));
				}
				Iterator<WardenEntity> var11 = wardens.iterator();
				WardenEntity wardie;
				while(var11.hasNext()) {
					wardie = var11.next();
					wardie.listen(player.getBlockPos(), world, player, 48, null);
				}
				if (wardens.isEmpty()) {
					player.emitGameEvent(GameEvent.BLOCK_DESTROY, player, pos);
					WardenEntity warden = RegisterEntities.WARDEN.create(world);
					assert warden != null;
					warden.refreshPositionAndAngles(player.getX() + 1D, player.getY()+1.5, player.getZ() + 1D, 0.0F, 0.0F);
					world.spawnEntity(warden);
					warden.handleStatus((byte) 5);
					warden.leaveTime = world.getTime() + 1200;
					warden.setPersistent();
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 0.2F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 0.2F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 0.2F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 0.2F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 0.2F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
					world.playSound(player, pos, RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
						WardenEntity warden2 = RegisterEntities.WARDEN.create(world);
						assert warden2 != null;
						warden2.refreshPositionAndAngles(player.getX() + 1D, player.getY()+1.5, player.getZ() + 1D, 0.0F, 0.0F);
						world.spawnEntity(warden2);
						warden2.handleStatus((byte) 5);
						warden2.leaveTime = world.getTime() + 1200;
						warden2.setPersistent();
						WitherEntity warden3 = EntityType.WITHER.create(world);
						assert warden3 != null;
						warden3.refreshPositionAndAngles(player.getX() + 1D, player.getY()+1.5, player.getZ() + 1D, 0.0F, 0.0F);
						world.spawnEntity(warden3);
						world.playSound(player, player.getBlockPos(), RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.MASTER, 1F, 1.0F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.ENTITY_WARDEN_AMBIENT_UNDERGROUND, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.ENTITY_WARDEN_DIG, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_AMBIENT, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 0.5F, 1F);
						world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1F, 3F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.BLOCK_SCULK_SENSOR_RECEIVE_RF, SoundCategory.MASTER, 1F, 3F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.MASTER, 1F, 1F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.MASTER, 1F, 0.2F);
						world.playSound(player, player.getBlockPos(), RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.MASTER, 1F, 2F);
				}
			}
		}
	}
}
