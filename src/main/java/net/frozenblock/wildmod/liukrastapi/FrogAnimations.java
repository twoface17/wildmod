package net.frozenblock.wildmod.liukrastapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.Animation.Builder;
import net.frozenblock.wildmod.liukrastapi.Transformation.Interpolations;
import net.frozenblock.wildmod.liukrastapi.Transformation.Targets;

@Environment(EnvType.CLIENT)
public class FrogAnimations {
    public static final Animation CROAKING = Builder.create(3.0F)
            .addBoneAnimation(
                    "croaking_body",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.375F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(2.9583F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(3.0F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "croaking_body",
                    new Transformation(
                            Targets.SCALE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41822(0.0, 0.0, 0.0), Interpolations.LINEAR),
                                    new Keyframe(0.375F, AnimationHelper.method_41822(0.0, 0.0, 0.0), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(0.4583F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(0.5417F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(0.7083F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(2.25F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(2.3333F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(2.4167F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(2.5F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(2.5833F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(2.6667F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(2.875F, AnimationHelper.method_41822(1.3F, 2.1F, 1.6F), Interpolations.LINEAR),
                                    new Keyframe(2.9583F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(3.0F, AnimationHelper.method_41822(0.0, 0.0, 0.0), Interpolations.LINEAR)
                            }
                    )
            )
            .build();
    public static final Animation WALKING = Builder.create(1.25F)
            .looping()
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.2917F, AnimationHelper.method_41829(7.5F, -2.67F, -7.5F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.7917F, AnimationHelper.method_41829(22.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(1.125F, AnimationHelper.method_41829(-45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, -5.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.1F, -2.0F), Interpolations.LINEAR),
                                    new Keyframe(0.2917F, AnimationHelper.method_41823(-0.5F, -0.25F, -0.13F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41823(-0.5F, 0.1F, 2.0F), Interpolations.LINEAR),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(0.5F, 1.0F, -0.11F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41823(0.0F, 0.1F, -2.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.125F, AnimationHelper.method_41829(22.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4583F, AnimationHelper.method_41829(-45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41829(0.0F, 5.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(7.5F, 2.33F, 7.5F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.5F, 0.1F, 2.0F), Interpolations.LINEAR),
                                    new Keyframe(0.2917F, AnimationHelper.method_41823(-0.5F, 1.0F, 0.12F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41823(0.0F, 0.1F, -2.0F), Interpolations.LINEAR),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(0.5F, -0.25F, -0.13F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41823(0.5F, 0.1F, 2.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.1667F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.2917F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41829(-45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.7917F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.1F, 1.2F), Interpolations.LINEAR),
                                    new Keyframe(0.1667F, AnimationHelper.method_41823(0.0F, 0.1F, 2.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(0.0F, 2.0F, 1.06F), Interpolations.LINEAR),
                                    new Keyframe(0.7917F, AnimationHelper.method_41823(0.0F, 0.1F, -1.0F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41823(0.0F, 0.1F, 1.2F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(-33.75F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.0417F, AnimationHelper.method_41829(-45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.1667F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.7917F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41829(-33.75F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 1.14F, 0.11F), Interpolations.LINEAR),
                                    new Keyframe(0.1667F, AnimationHelper.method_41823(0.0F, 0.1F, -1.0F), Interpolations.LINEAR),
                                    new Keyframe(0.7917F, AnimationHelper.method_41823(0.0F, 0.1F, 2.0F), Interpolations.LINEAR),
                                    new Keyframe(1.125F, AnimationHelper.method_41823(0.0F, 2.0F, 0.95F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41823(0.0F, 1.14F, 0.11F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "body",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 5.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.2917F, AnimationHelper.method_41829(-7.5F, 0.33F, 7.5F), Interpolations.LINEAR),
                                    new Keyframe(0.625F, AnimationHelper.method_41829(0.0F, -5.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(-7.5F, 0.33F, -7.5F), Interpolations.LINEAR),
                                    new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, 5.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .build();
    public static final Animation LONG_JUMPING = Builder.create(0.5F)
            .addBoneAnimation(
                    "body",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(-22.5F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(-22.5F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "body",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(-56.14F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(-56.14F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(-56.14F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(-56.14F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41823(0.0F, 1.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(45.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .build();
    public static final Animation USING_TONGUE = Builder.create(0.5F)
            .addBoneAnimation(
                    "head",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.0833F, AnimationHelper.method_41829(-60.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41829(-60.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "head",
                    new Transformation(
                            Targets.SCALE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(1.0F, 1.0F, 1.0F), Interpolations.LINEAR),
                                    new Keyframe(0.0833F, AnimationHelper.method_41829(0.998F, 1.0F, 1.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41829(0.998F, 1.0F, 1.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(1.0F, 1.0F, 1.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "tongue",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.0833F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41829(-18.0F, 0.0F, 0.0F), Interpolations.LINEAR),
                                    new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)
                            }
                    )
            )
            .addBoneAnimation(
                    "tongue",
                    new Transformation(
                            Targets.SCALE,
                            new Keyframe[]{
                                    new Keyframe(0.0833F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR),
                                    new Keyframe(0.1667F, AnimationHelper.method_41822(0.5, 1.0, 5.0), Interpolations.LINEAR),
                                    new Keyframe(0.4167F, AnimationHelper.method_41822(1.0, 1.0, 1.0), Interpolations.LINEAR)
                            }
                    )
            )
            .build();
    public static final Animation SWIMMING = Builder.create(1.04167F)
            .looping()
            .addBoneAnimation(
                    "body",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.3333F, AnimationHelper.method_41829(10.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.6667F, AnimationHelper.method_41829(-10.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(90.0F, 22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41829(45.0F, 22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.6667F, AnimationHelper.method_41829(-22.5F, -22.5F, -22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(0.875F, AnimationHelper.method_41829(-45.0F, -22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(22.5F, 0.0F, 22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41829(90.0F, 22.5F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, -0.64F, 2.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(0.0F, -0.64F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.6667F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.875F, AnimationHelper.method_41823(0.0F, -0.27F, -1.14F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(0.0F, -1.45F, 0.43F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41823(0.0F, -0.64F, 2.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(90.0F, -22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41829(45.0F, -22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.6667F, AnimationHelper.method_41829(-22.5F, 22.5F, 22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(0.875F, AnimationHelper.method_41829(-45.0F, 22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(22.5F, 0.0F, -22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41829(90.0F, -22.5F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, -0.64F, 2.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(0.0F, -0.64F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.6667F, AnimationHelper.method_41823(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.875F, AnimationHelper.method_41823(0.0F, -0.27F, -1.14F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(0.0F, -1.45F, 0.43F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41823(0.0F, -0.64F, 2.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.25F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41829(67.5F, -45.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.7917F, AnimationHelper.method_41829(90.0F, 45.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(-2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.25F, AnimationHelper.method_41823(-2.0F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(1.0F, -2.0F, -1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.7917F, AnimationHelper.method_41823(0.58F, 0.0F, -2.83F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(-2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41823(-2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.25F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41829(67.5F, 45.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.7917F, AnimationHelper.method_41829(90.0F, -45.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41829(90.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.25F, AnimationHelper.method_41823(2.0F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.4583F, AnimationHelper.method_41823(-1.0F, -2.0F, -1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(0.7917F, AnimationHelper.method_41823(-0.58F, 0.0F, -2.83F), Interpolations.CATMULLROM),
                                    new Keyframe(0.9583F, AnimationHelper.method_41823(2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0417F, AnimationHelper.method_41823(2.5F, 0.0F, 1.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .build();
    public static final Animation IDLING_IN_WATER = Builder.create(3.0F)
            .looping()
            .addBoneAnimation(
                    "body",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.625F, AnimationHelper.method_41829(-10.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, -22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(2.2083F, AnimationHelper.method_41829(0.0F, 0.0F, -45.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41829(0.0F, 0.0F, -22.5F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(-1.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(2.2083F, AnimationHelper.method_41823(-1.0F, -0.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41823(-1.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 22.5F), Interpolations.CATMULLROM),
                                    new Keyframe(2.2083F, AnimationHelper.method_41829(0.0F, 0.0F, 45.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41829(0.0F, 0.0F, 22.5F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_arm",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(1.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(2.2083F, AnimationHelper.method_41823(1.0F, -0.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41823(1.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(22.5F, -22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0F, AnimationHelper.method_41829(22.5F, -22.5F, -45.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41829(22.5F, -22.5F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "left_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0F, AnimationHelper.method_41823(0.0F, -1.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41823(0.0F, 0.0F, 1.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.ROTATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41829(22.5F, 22.5F, 0.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0F, AnimationHelper.method_41829(22.5F, 22.5F, 45.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41829(22.5F, 22.5F, 0.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .addBoneAnimation(
                    "right_leg",
                    new Transformation(
                            Targets.TRANSLATE,
                            new Keyframe[]{
                                    new Keyframe(0.0F, AnimationHelper.method_41823(0.0F, 0.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(1.0F, AnimationHelper.method_41823(0.0F, -1.0F, 1.0F), Interpolations.CATMULLROM),
                                    new Keyframe(3.0F, AnimationHelper.method_41823(0.0F, 0.0F, 1.0F), Interpolations.CATMULLROM)
                            }
                    )
            )
            .build();

    public FrogAnimations() {
    }
}
