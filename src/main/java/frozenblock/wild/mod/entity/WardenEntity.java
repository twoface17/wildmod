package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.liukrastapi.WardenGoal;
import frozenblock.wild.mod.liukrastapi.WardenSensorListener;
import frozenblock.wild.mod.mixins.SimpleGameEventDispatcherMixin;
import frozenblock.wild.mod.registry.RegisterSounds;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.util.EventListener;
import java.util.Optional;

public class WardenEntity extends HostileEntity {

    private boolean attacking = false;

    private static final double speed = 0.5D;

    public BlockPos lasteventpos;
    public World lasteventworld;
    public LivingEntity lastevententity;


    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createWardenAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 84.0D);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WardenGoal(this, speed));
    }

    public boolean isAttackingAnimation() {
        return this.attacking;
    }

    public void listen(BlockPos eventPos, World eventWorld, LivingEntity eventEntity) {
        this.lasteventpos = eventPos;
        this.lasteventworld = eventWorld;
        this.lastevententity = eventEntity;
    }
}