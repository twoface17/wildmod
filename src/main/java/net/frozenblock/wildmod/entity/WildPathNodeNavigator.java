package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.SampleType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WildPathNodeNavigator extends PathNodeNavigator {
    public WildPathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
        super(pathNodeMaker, range);
    }

    @Override
    @Nullable
    public Path findPathToAny(Profiler profiler, PathNode startNode, Map<TargetPathNode, BlockPos> positions, float followRange, int distance, float rangeMultiplier) {
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
        Set<TargetPathNode> set3 = Sets.newHashSetWithExpectedSize(set.size());
        int j = (int) ((float) this.range * rangeMultiplier);

        while (!this.minHeap.isEmpty()) {
            ++i;
            if (i >= j) {
                break;
            }

            PathNode pathNode = this.minHeap.pop();
            pathNode.visited = true;
            Iterator var13 = set.iterator();

            while (var13.hasNext()) {
                TargetPathNode targetPathNode = (TargetPathNode) var13.next();
                if (pathNode.getManhattanDistance(targetPathNode) <= (float) distance) {
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

        Optional<Path> optional = !set3.isEmpty() ? set3.stream().map((targetPathNodex) -> {
            return this.createPath(targetPathNodex.getNearestNode(), positions.get(targetPathNodex), true);
        }).min(Comparator.comparingInt(Path::getLength)) : set.stream().map((targetPathNodex) -> {
            return this.createPath(targetPathNodex.getNearestNode(), positions.get(targetPathNodex), false);
        }).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
        profiler.pop();
        if (!optional.isPresent()) {
            return null;
        } else {
            Path path = optional.get();
            return path;
        }
    }

    protected float getDistance(WardenEntity.WildPathNode a, WardenEntity.WildPathNode b) {
        return a.getDistance(b);
    }
}
