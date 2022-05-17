package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.client.model.ModelPart;

import java.util.Optional;

public interface ExpandedSinglePartEntityModel {
	Optional<ModelPart> getChild(String string);

	public void updateAnimation(AnimationState animationState, Animation animation, float f);

	public void updateAnimation(AnimationState animationState, Animation animation, float f, float g);
}
