package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterItems;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TadpoleEntity extends SchoolingFishEntity {
    public int life;

    private static final int lifeBeforeFrog = 100; // Time in seconds before the tadpole becomes a frog

    public TadpoleEntity(EntityType<? extends SchoolingFishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ItemStack getBucketItem() {
        return RegisterItems.TADPOLE_BUCKET.getDefaultStack();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SALMON_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SALMON_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SALMON_HURT;
    }


    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_SALMON_FLOP;
    }

    public void mobTick() {
        this.life = this.life + 1;
        if(this.life > lifeBeforeFrog*20) {
            if(Math.random() < 0.3) {
                this.convertTo(RegisterEntities.FROG, true);
            }
        }
    }
}
