package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.fromAccurateSculk.SculkTags;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(SimpleGameEventDispatcher.class)
public class SimpleGameEventDispatcherMixin {

    @Shadow
    private final World world;

    public SimpleGameEventDispatcherMixin(World world) {
        this.world = world;
    }

    @Inject(at = @At("HEAD"), method = "dispatch")
    private void dispatch(GameEvent event, Entity entity, BlockPos pos, CallbackInfo ci) {
        BlockPos eventpos;
        World eventworld;
        Entity evententity;

        if (entity == null) {
            eventpos = pos;
            eventworld = this.world;
            evententity = null;
        } else {
            if (entity.getType() == EntityType.PLAYER) {
                if (event == GameEvent.STEP || event == GameEvent.HIT_GROUND || event == GameEvent.PROJECTILE_SHOOT) {
                    if (entity.isSneaking()) {
                        eventpos = null;
                        eventworld = null;
                        evententity = null;
                    } else {
                        PlayerEntity player = (PlayerEntity) entity;
                        Item booties = player.getEquippedStack(EquipmentSlot.FEET).getItem();
                        Identifier identity = Registry.ITEM.getId(booties);
                        if (
                                new Identifier("wooledboots", "wooled_chainmail_boots").equals(identity) ||
                                        new Identifier("wooledboots", "wooled_gold_boots").equals(identity) ||
                                        new Identifier("wooledboots", "wooled_diamond_boots").equals(identity) ||
                                        new Identifier("wooledboots", "wooled_netherite_boots").equals(identity) ||
                                        new Identifier("wooledboots", "wooled_iron_boots").equals(identity)
                        ) {
                            eventpos = null;
                            eventworld = null;
                            evententity = null;
                        } else {
                            eventpos = pos;
                            eventworld = entity.getEntityWorld();
                            evententity = entity;
                        }
                    }
                } else {
                    eventpos = pos;
                    eventworld = entity.getEntityWorld();
                    evententity = entity;
                }
            } else if (entity.getType() != RegisterEntities.WARDEN) {
                eventpos = pos;
                eventworld = entity.getEntityWorld();
                if (entity.isLiving()) {
                    evententity = entity;
                } else {
                    evententity = null;
                }
            } else {
                eventpos = null;
                eventworld = null;
                evententity = null;
            }
        }
        boolean bl1 = evententity != null || event == GameEvent.EAT || event == GameEvent.HIT_GROUND;
        if (eventpos != null && eventworld != null && bl1) {
            List<WardenEntity> wardens = this.world.getNonSpectatingEntities(WardenEntity.class, new Box(
                    eventpos.getX() - 18, eventpos.getY() - 18, eventpos.getZ() - 18,
                    eventpos.getX() + 18, eventpos.getY() + 18, eventpos.getZ() + 18)
            );
            Iterator<WardenEntity> var11 = wardens.iterator();

            WardenEntity wardie;
            while (var11.hasNext()) {
                wardie = var11.next();
                //System.out.println(Math.floor(Math.sqrt(wardie.getBlockPos().getSquaredDistance(eventpos, false))));
                if (wardie.getEntityWorld() == eventworld && Math.floor(Math.sqrt(wardie.getBlockPos().getSquaredDistance(eventpos))) <= 16) {
                    if (event.isIn(SculkTags.WARDEN_CAN_LISTEN)) {
                        if (evententity instanceof ProjectileEntity projectile) {
                            wardie.listen(eventpos, eventworld, projectile, projectile.getOwner(), 10, null);
                        } else {
                            wardie.listen(eventpos, eventworld, evententity, null, 0, null);
                        }
                    }
                }
            }

        }
    }
}
