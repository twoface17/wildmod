package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.registry.RegisterParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class FireflyEntity extends MobEntity {
    public FireflyEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createFireflyAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1);
    }

    @Override
    public boolean collides() {
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    public void tick() {
        this.world.addParticle(RegisterParticles.FIREFLY, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
    }
}
