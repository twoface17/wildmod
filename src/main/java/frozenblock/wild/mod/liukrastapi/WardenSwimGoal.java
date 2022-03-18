//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WardenSwimGoal extends Goal {
    private final WardenEntity mob;

    public WardenSwimGoal(WardenEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.JUMP));
        mob.getNavigation().setCanSwim(true);
    }

    public boolean canStart() {
        if (this.mob.emergeTicksLeft > 0 || this.mob.emergeTicksLeft == -5) {
            return false;
        }
        return this.mob.isTouchingWater() && this.mob.isSubmergedInWater() || this.mob.isInLava();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().setActive();
        }

    }
}
