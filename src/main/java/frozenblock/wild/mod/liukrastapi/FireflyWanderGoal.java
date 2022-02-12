package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.FireflyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
public class FireflyWanderGoal extends Goal {
    protected final FireflyEntity mob;

    public FireflyWanderGoal(FireflyEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        return !mob.isInvulnerable();
    }

    public boolean shouldContinue() {
        return false;
    }

    public void start() {
        BlockPos pos = this.getRandomLocation();
        if (pos != null) {
            mob.startMovingTo(pos);
        }

    }

    @Nullable
    private BlockPos getRandomLocation() {
        BlockPos pos;
        if (!mob.getBlockPos().isWithinDistance(mob.getSpawnPos(), 6)) {
            pos = mob.getSpawnPos().add(getRandom(3, mob), getRandom(3, mob), getRandom(3, mob));
        } else {
            pos = mob.getBlockPos().add(getRandom(1, mob), getRandom(1, mob), getRandom(1, mob));
        }
        return pos;
    }
    private int getRandom(int max, Entity entity) {
        return UniformIntProvider.create(-max, max).get(entity.getWorld().getRandom());
    }
}
