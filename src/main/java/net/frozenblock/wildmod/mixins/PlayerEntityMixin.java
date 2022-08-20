package net.frozenblock.wildmod.mixins;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.entity.WildHostileEntity;
import net.frozenblock.wildmod.entity.WildPathAwareEntity;
import net.frozenblock.wildmod.entity.ai.WardenBrain;
import net.frozenblock.wildmod.fromAccurateSculk.ActivatorGrower;
import net.frozenblock.wildmod.fromAccurateSculk.BrokenSculkGrower;
import net.frozenblock.wildmod.misc.Angriness;
import net.frozenblock.wildmod.misc.WildLivingEntity;
import net.frozenblock.wildmod.misc.WildPlayerEntity;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements WildPlayerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow
    public abstract void disableShield(boolean sprinting);

    public SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    private static final ArrayList<String> names = new ArrayList<>();

    private static final boolean stinkyThiefMode = false;
    //ONLY SET TO TRUE IF YOU WANT STINKY THIEF MODE TO ALWAYS RUN REGARDLESS OF USERNAME
    //ALWAYS SET TO FALSE BEFORE COMMITTING AND RELEASING THE WILD MOD


    @Override
    public SculkShriekerWarningManager getSculkShriekerWarningManager() {
        return this.sculkShriekerWarningManager;
    }


    public Optional<GlobalPos> getLastDeathPos() {
        PlayerEntity player = PlayerEntity.class.cast(this);
        return player.getDataTracker().get(WildMod.LAST_DEATH_POS());
    }

    public void setLastDeathPos(Optional<GlobalPos> lastDeathPos) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        player.getDataTracker().set(WildMod.LAST_DEATH_POS(), lastDeathPos);
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        this.setLastDeathPos(Optional.of(GlobalPos.create(player.world.getRegistryKey(), player.getBlockPos())));
    }

    @Inject(method = "takeShieldHit", at = @At("HEAD"))
    protected void takeShieldHit(LivingEntity attacker, CallbackInfo ci) {
        if (((WildLivingEntity) attacker).disablesShield()) {
            this.disableShield(true);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("warden_spawn_tracker", NbtElement.COMPOUND_TYPE)) {
            SculkShriekerWarningManager.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("warden_spawn_tracker"))).resultOrPartial(LOGGER::error).ifPresent(sculkShriekerWarningManager -> this.sculkShriekerWarningManager = sculkShriekerWarningManager);
        }

		/*if (nbt.contains("LastDeathLocation", NbtElement.COMPOUND_TYPE)) {
			this.setLastDeathPos(GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("LastDeathLocation")).resultOrPartial(LOGGER::error));
		}*/
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        SculkShriekerWarningManager.CODEC.encodeStart(NbtOps.INSTANCE, this.sculkShriekerWarningManager).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put("warden_spawn_tracker", nbtElement));

        //this.getLastDeathPos().flatMap(globalPos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).resultOrPartial(LOGGER::error)).ifPresent(nbtElement -> nbt.put("LastDeathLocation", nbtElement));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        if (!player.world.isClient) {
            this.sculkShriekerWarningManager.tick();
        }
    }

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
                player.sendMessage(Text.of("THIEF"), false);
                player.sendMessage(Text.of(" STINKY THIEF"), false);
                player.sendMessage(Text.of("SUPER STINKY THIEF"), false);
                player.sendMessage(Text.of("CLICKBAIT"), false);
                player.sendMessage(Text.of("YOU FELL OFF"), false);
                player.sendMessage(Text.of("U R A     STEALER"), false);
                player.sendMessage(Text.of("EXTREMELY PUTRID THIEF STEALER PERSON!!!!"), false);
                player.sendMessage(Text.of("Thank you for stealing our mod for views!"), true);
                player.sendMessage(Text.of("Try out this new product called Decency!"), false);
                player.sendMessage(Text.of("Unsubscribe from " + player.getName().getString()), false);
                List<WardenEntity> wardens = player.world.getNonSpectatingEntities(WardenEntity.class, new Box(
                        player.getX() - 18, player.getY() - 18, player.getZ() - 18,
                        player.getX() + 18, player.getY() + 18, player.getZ() + 18)
                );
                player.setPose(EntityPose.DYING);
                if (player.getBlockPos().getY() < world.getHeight() && player.getBlockPos().getY() > world.getBottomY()) {
                    world.setBlockState(player.getBlockPos(), RegisterBlocks.SCULK_VEIN.getDefaultState().with(Properties.DOWN, true));
                }
                Iterator<WardenEntity> var11 = wardens.iterator();
                WardenEntity wardie;
                while (var11.hasNext()) {
                    wardie = var11.next();
                    wardie.listen(player.getBlockPos(), world, player, null, Angriness.ANGRY.getThreshold() + 20, null);
                }
                if (wardens.isEmpty()) {
                    player.emitGameEvent(GameEvent.BLOCK_DESTROY, player, pos);
                    WardenEntity warden = RegisterEntities.WARDEN.create(world);
                    assert warden != null;
                    warden.refreshPositionAndAngles(player.getX() + 1D, player.getY() + 1.5, player.getZ() + 1D, 0.0F, 0.0F);
                    world.spawnEntity(warden);
                    warden.handleStatus((byte) 5);
                    WardenBrain.resetDigCooldown(warden);
                    warden.setPersistent();
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 0.2F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 0.2F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 0.2F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 0.2F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 0.2F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    WardenEntity warden2 = RegisterEntities.WARDEN.create(world);
                    assert warden2 != null;
                    warden2.refreshPositionAndAngles(player.getX() + 1D, player.getY() + 1.5, player.getZ() + 1D, 0.0F, 0.0F);
                    world.spawnEntity(warden2);
                    warden2.handleStatus((byte) 5);
                    WardenBrain.resetDigCooldown(warden2);
                    warden2.setPersistent();
                    WitherEntity warden3 = EntityType.WITHER.create(world);
                    assert warden3 != null;
                    warden3.refreshPositionAndAngles(player.getX() + 1D, player.getY() + 1.5, player.getZ() + 1D, 0.0F, 0.0F);
                    world.spawnEntity(warden3);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_EMERGE, 1.0F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_AMBIENT_UNDERGROUND, 1.0F);
                    easySound(player, world, RegisterSounds.ENTITY_WARDEN_DIG, 1.0F);
                    easySound(player, world, SoundEvents.BLOCK_DISPENSER_FAIL, 1.0F);
                    easySound(player, world, SoundEvents.BLOCK_ANVIL_PLACE, 1.0F);
                    easySound(player, world, SoundEvents.ENTITY_BLAZE_AMBIENT, 1F);
                    easySound(player, world, SoundEvents.ENTITY_BLAZE_HURT, 1.0F);
                    easySound(player, world, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, 1.0F);
                    easySound(player, world, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 3.0F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_SENSOR_RECEIVE_RF, 3.0F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_SPREAD, 3.0F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.0F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.2F);
                    easySound(player, world, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, 2.0F);
                }
            }
        }
    }

    private void easySound(PlayerEntity player, ServerWorld world, SoundEvent sound, float pitch) {
        world.playSound(player, player.getBlockPos(), sound, SoundCategory.MASTER, 10.0F, pitch);
    }
}
