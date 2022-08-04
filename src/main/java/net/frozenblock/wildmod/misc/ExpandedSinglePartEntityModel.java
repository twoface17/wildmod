package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.misc.animation.Animation;
import net.frozenblock.wildmod.misc.animation.AnimationState;
import net.minecraft.client.model.ModelPart;

import java.util.Optional;

public interface ExpandedSinglePartEntityModel {
    Optional<ModelPart> getChild(String string);

    void updateAnimation(AnimationState animationState, Animation animation, float f);

    void updateAnimation(AnimationState animationState, Animation animation, float f, float g);
}
