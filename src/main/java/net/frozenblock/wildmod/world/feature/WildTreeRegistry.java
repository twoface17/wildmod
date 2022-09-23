package net.frozenblock.wildmod.world.feature;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.world.gen.AttachedToLeavesTreeDecorator;
import net.frozenblock.wildmod.world.gen.UpwardsBranchingTrunkPlacer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class WildTreeRegistry {

    public static final TrunkPlacerType<UpwardsBranchingTrunkPlacer> UPWARDS_BRANCHING_TRUNK_PLACER = Registry.register(Registry.TRUNK_PLACER_TYPE, new Identifier(WildMod.MOD_ID, "upwards_branching_trunk_placer"), new TrunkPlacerType<>(UpwardsBranchingTrunkPlacer.CODEC));

    public static final TreeDecoratorType<AttachedToLeavesTreeDecorator> ATTACHED_TO_LEAVES = Registry.register(Registry.TREE_DECORATOR_TYPE, new Identifier(WildMod.MOD_ID, "attached_to_leaves"), new TreeDecoratorType<>(AttachedToLeavesTreeDecorator.CODEC));

    public static void init() {
    }
}
