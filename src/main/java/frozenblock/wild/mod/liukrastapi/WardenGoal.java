package frozenblock.wild.mod.liukrastapi;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

public class WardenGoal extends Goal {

    private final MobEntity mob;

    public WardenGoal(MobEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    public void tick() {
        this.mob.getLookControl().lookAt(100, 100, 100);

        this.mob.getNavigation().startMovingTo(100, 100, 100, 1);
    }
}
