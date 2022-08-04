package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WildPathNodeNavigator extends PathNodeNavigator {
    public WildPathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        super(pathNodeMaker, range);
    }

    @Nullable
    public Path findPathToAny(ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, mob);
        PathNode pathNode = this.pathNodeMaker.getStart();
        if (pathNode == null) {
            return null;
        } else {
            Map<TargetPathNode, BlockPos> map = positions.stream()
                    .collect(Collectors.toMap(pos -> this.pathNodeMaker.getNode(pos.getX(), pos.getY(), pos.getZ()), Function.identity()));
            Path path = this.findPathToAny(world.getProfiler(), pathNode, map, followRange, distance, rangeMultiplier);
            this.pathNodeMaker.clear();
            return path;
        }
    }

    @Override
    @Nullable
    public Path findPathToAny(
            Profiler profiler, PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier
    ) {
        profiler.push("find_path");
        profiler.markSampleType(SampleType.PATH_FINDING);
        Set<TargetPathNode> set = positions.keySet();
        startNode.penalizedPathLength = 0.0F;
        startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
        startNode.heapWeight = startNode.distanceToNearestTarget;
        this.minHeap.clear();
        this.minHeap.push(startNode);
        Set<PathNode> set2 = ImmutableSet.of();
        int i = 0;
        Set<TargetPathNode> set3 = Sets.<TargetPathNode>newHashSetWithExpectedSize(set.size());
        int j = (int)((float)this.range * rangeMultiplier);

        while(!this.minHeap.isEmpty()) {
            if (++i >= j) {
                break;
            }

            PathNode pathNode = this.minHeap.pop();
            pathNode.visited = true;

            for(TargetPathNode targetPathNode : set) {
                if (pathNode.getManhattanDistance(targetPathNode) <= (float)distance) {
                    targetPathNode.markReached();
                    set3.add(targetPathNode);
                }
            }

            if (!set3.isEmpty()) {
                break;
            }

            if (!(pathNode.getDistance(startNode) >= followRange)) {
                int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);

                for (int l = 0; l < k; ++l) {
                    if (pathNode instanceof WardenEntity.WildPathNode wildPathNode) {
                        PathNode pathNode2 = this.successors[l];
                        if (pathNode2 instanceof WardenEntity.WildPathNode wildPathNode2) {
                            float f = this.getDistance(wildPathNode, wildPathNode2);
                            wildPathNode2.pathLength = wildPathNode.pathLength + f;
                            float g = wildPathNode.penalizedPathLength + f + wildPathNode2.penalty;
                            if (wildPathNode2.pathLength < followRange && (!wildPathNode2.isInHeap() || g < wildPathNode2.penalizedPathLength)) {
                                wildPathNode2.previous = wildPathNode;
                                wildPathNode2.penalizedPathLength = g;
                                wildPathNode2.distanceToNearestTarget = this.calculateDistances(wildPathNode2, set) * 1.5F;
                                if (wildPathNode2.isInHeap()) {
                                    this.minHeap.setNodeWeight(wildPathNode2, wildPathNode2.penalizedPathLength + wildPathNode2.distanceToNearestTarget);
                                } else {
                                    wildPathNode2.heapWeight = wildPathNode2.penalizedPathLength + wildPathNode2.distanceToNearestTarget;
                                    this.minHeap.push(wildPathNode2);
                                }
                            }
                        }
                    }
                }
            }
        }

        Optional<Path> optional = !set3.isEmpty()
                ? set3.stream().map(node -> this.createPath(node.getNearestNode(), positions.get(node), true)).min(Comparator.comparingInt(Path::getLength))
                : set.stream()
                .map(targetPathNode -> this.createPath(targetPathNode.getNearestNode(), positions.get(targetPathNode), false))
                .min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        profiler.pop();
        return !optional.isPresent() ? null : optional.get();
    }

    protected float getDistance(WardenEntity.WildPathNode a, WardenEntity.WildPathNode b) {
        return a.getDistance(b);
    }

}
