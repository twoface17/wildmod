package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.*;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
public class SimpleGameEventDispatcherMixin{

    @Shadow private final World world;

    public SimpleGameEventDispatcherMixin(World world) {
        this.world = world;
    }

    @Inject(at = @At("HEAD"), method = "dispatch")
    private void dispatch(GameEvent event, Entity entity, BlockPos pos, CallbackInfo ci) {
        BlockPos eventpos;
        World eventworld;
        LivingEntity evententity;

        if(SculkSensorBlock.FREQUENCIES.containsKey(event)) {
            
            if(entity == null) {
                eventpos = pos;
                eventworld = this.world;
                evententity = null;
            } else {
                if(entity.getType() == EntityType.PLAYER) {
                    if(event == GameEvent.STEP || event == GameEvent.HIT_GROUND || event == GameEvent.PROJECTILE_SHOOT) {
                        if (entity.isSneaking()) {
                            eventpos = null;
                            eventworld = null;
                            evententity = null;
                        } else {
                            PlayerEntity player = (PlayerEntity) entity;
                            Item booties = player.getEquippedStack(EquipmentSlot.FEET).getItem();
                            Identifier identity = Registry.ITEM.getId(booties);
                            if(
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
                                evententity = (LivingEntity) entity;
                            }
                        }
                    } else {
                        eventpos = pos;
                        eventworld = entity.getEntityWorld();
                        evententity = (LivingEntity) entity;
                    }
                } else if(entity.getType() != RegisterEntities.WARDEN) {
                    eventpos = pos;
                    eventworld = entity.getEntityWorld();
                    if(entity.isLiving()) {
                        evententity = (LivingEntity) entity;
                    } else {
                        evententity = null;
                    }
                } else {
                    eventpos = null;
                    eventworld = null;
                    evententity = null;
                }
            }
            boolean bl1 = evententity != null || event==GameEvent.EAT || event==GameEvent.HIT_GROUND;
            if(eventpos != null && eventworld != null && bl1) {
                List<WardenEntity> wardens = this.world.getNonSpectatingEntities(WardenEntity.class, new Box(
                        eventpos.getX() -16, eventpos.getY() -16, eventpos.getZ() -16,
                        eventpos.getX() +16, eventpos.getY() +16, eventpos.getZ() +16)
                );
                Iterator var11 = wardens.iterator();

                WardenEntity wardie;
                while(var11.hasNext()) {
                    wardie = (WardenEntity)var11.next();
                    if(
                            wardie.getEntityWorld() == eventworld &&
                            MathAddon.distance(eventpos.getX(), eventpos.getY(), eventpos.getZ(), wardie.getX(), wardie.getY(), wardie.getZ()) <= 15
                    ) {
                        boolean bl2 = event==GameEvent.HIT_GROUND && evententity==null;
                        if (event!=GameEvent.PROJECTILE_LAND && event!=GameEvent.EAT && !bl2) {
                            wardie.listen(eventpos, eventworld, evententity, wardie.eventSuspicionValue(event, evententity), null);
                        } else {
                            wardie.listen(eventpos, eventworld, null, 0, null);
                        }
                    }
                }
            }

        }
    }
}
