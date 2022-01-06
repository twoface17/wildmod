package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.FireflyEntity;
import frozenblock.wild.mod.entity.FrogEntity;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FrogGoal extends Goal {

    private final FrogEntity mob;

    public FrogGoal(FrogEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    public void tick() {
        World world = this.mob.getEntityWorld();

        if(this.mob.isSubmergedInWater() && this.mob.isTouchingWater()) {
            if(world.getBlockState(this.mob.getBlockPos().up()) != Blocks.AIR.getDefaultState() && world.getBlockState(this.mob.getBlockPos().up()) != Blocks.WATER.getDefaultState()) {
                double angle = Math.random() * 360;
                double radius = Math.random() * 0.3;
                this.mob.setYaw((float) angle);
                this.mob.updateVelocity(2F, new Vec3d(-Math.sin(angle) * radius, 0, -Math.cos(angle) * radius));
                this.mob.getLookControl().lookAt(new Vec3d(-Math.sin(angle) * radius, 0, -Math.cos(angle) * radius));
            }

            if(Math.random() < 0.5) {
                double result;
                int radius = 3;

                BlockPos targetPos = null;
                ArrayList<BlockPos> targetList = checkforSafePlaceToGo(world, this.mob.getBlockPos(), radius);

                if (targetList.size() > 0) {
                    result = Math.round(Math.random() * (targetList.size() - 1));
                    targetPos = targetList.get((int) result);
                }
                if (!(targetPos == null)) {

                    double dx = targetPos.getX() - this.mob.getBlockPos().getX();
                    double dy = targetPos.getY() - this.mob.getBlockPos().getY();
                    double dz = targetPos.getZ() - this.mob.getBlockPos().getZ();

                    if (dy < 0) {
                        dy = 0;
                    }

                    if (Math.sqrt(dx * dx) > 0 || Math.sqrt(dz * dz) > 0) {
                        this.mob.updateVelocity(2F, new Vec3d(dx / 15, 0.1 + dy / 10, dz / 15));
                        this.mob.getLookControl().lookAt(new Vec3d(dx / 15, 0.1 + dy / 10, dz / 15));
                    }
                }
            }

        }

        if(this.mob.isSubmergedInWater()) {

            if(Math.random() < 0.05) {
                double jumpamount = Math.random() / 4;
                double angle = Math.random() * 360;
                double radius = Math.random() / 10;
                this.mob.setYaw((float) angle);
                this.mob.updateVelocity(2F, new Vec3d(-Math.sin(angle) * radius, jumpamount, -Math.cos(angle) * radius));
                this.mob.getLookControl().lookAt(new Vec3d(-Math.sin(angle) * radius, jumpamount, -Math.cos(angle) * radius));
            }
        }
        if(this.mob.isOnGround()) {
            this.mob.setBodyYaw((float)Math.random());
            if(this.mob.getTongue() < 1) {
                double d = 3;
                Box box = (new Box(this.mob.getBlockPos())).expand(d).stretch(0.0D, world.getHeight(), 0.0D);

                List<FireflyEntity> list = world.getNonSpectatingEntities(FireflyEntity.class, box);
                if (list.size() > 0) {
                    if (world.getTime()-mob.eatTimer>=10) {
                        FireflyEntity target = list.get(0);
                        world.sendEntityStatus(this.mob, (byte) 4);
                        target.teleport(this.mob.getX(), this.mob.getY(), this.mob.getZ());
                        target.kill();
                        target.deathTime=10;
                        mob.eatTimer = world.getTime();
                    }
                }

                List<SlimeEntity> slimelist = world.getNonSpectatingEntities(SlimeEntity.class, box);
                if (slimelist.size() > 0) {
                    for (SlimeEntity target : slimelist) {
                        if (target.getSize() == 1 && target.getType()!=EntityType.MAGMA_CUBE && target.isAlive()) {
                            if (world.getTime()-mob.eatTimer>=10) {
                                world.sendEntityStatus(this.mob, (byte) 4);
                                target.teleport(this.mob.getX(), this.mob.getY(), this.mob.getZ());
                                target.kill();
                                target.deathTime=10;
                                mob.eatTimer = world.getTime();
                            }
                        }
                    }
                }
                List<MagmaCubeEntity> magmalist = world.getNonSpectatingEntities(MagmaCubeEntity.class, box);
                if (magmalist.size() > 0) {
                    for (MagmaCubeEntity target : magmalist) {
                        if (target.getSize() == 1 && target.isAlive()) {
                            if (world.getTime()-this.mob.eatTimer>=10) {
                                world.sendEntityStatus(this.mob, (byte) 4);
                                target.teleport(this.mob.getX(), this.mob.getY(), this.mob.getZ());
                                target.kill();
                                target.deathTime=10;
                                if (mob.getVariant()==FrogEntity.Variant.SWAMP) {
                                    mob.dropItem(RegisterBlocks.OCHRE_FROGLIGHT.asItem(),0);
                                } else if (mob.getVariant()==FrogEntity.Variant.COLD) {
                                    mob.dropItem(RegisterBlocks.VERDANT_FROGLIGHT.asItem(),0);
                                } else if (mob.getVariant()==FrogEntity.Variant.TROPICAL) {
                                    mob.dropItem(RegisterBlocks.PEARLESCENT_FROGLIGHT.asItem(),0);
                                }
                                mob.eatTimer = world.getTime();
                            }
                        }
                    }
                }
            }
        }

            if (Math.random() < 0.025) {

                double jumpamount = Math.random() / 2;

                if(jumpamount < 0.25) {
                    jumpamount = 0.25;
                }

                if(Math.random() < 0.25) {

                    // RANDOM JUMP
                    double angle = Math.random() * 360;
                    double radius = Math.random() * 0.3;
                    this.mob.updateVelocity(2F, new Vec3d(-Math.sin(angle) * radius, jumpamount, -Math.cos(angle) * radius));
                    this.mob.getLookControl().lookAt(new Vec3d(-Math.sin(angle) * radius, jumpamount, -Math.cos(angle) * radius));
                    this.mob.playSound(RegisterSounds.ENTITY_FROG_LONG_JUMP, 1.0f, 1.0f);
                } else {
                    // LILYPAD/DRIPLEAF JUMP

                    double result;
                    int radius = 5;
                    BlockPos targetPos = null;
                    ArrayList<BlockPos> targetList = Sphere.checkSpherePos(Blocks.LILY_PAD.getDefaultState(), this.mob.getEntityWorld(), this.mob.getBlockPos(), radius, true);
                    ArrayList<BlockPos> northDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.NORTH), this.mob.getEntityWorld(), this.mob.getBlockPos(), radius, true);
                    ArrayList<BlockPos> southDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.SOUTH), this.mob.getEntityWorld(), this.mob.getBlockPos(), radius, true);
                    ArrayList<BlockPos> eastDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.EAST), this.mob.getEntityWorld(), this.mob.getBlockPos(), radius, true);
                    ArrayList<BlockPos> westDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.WEST), this.mob.getEntityWorld(), this.mob.getBlockPos(), radius, true);

                    targetList.addAll(northDripleafList);
                    targetList.addAll(southDripleafList);
                    targetList.addAll(eastDripleafList);
                    targetList.addAll(westDripleafList);

                    if(targetList.size() > 0) {
                        result = Math.round(Math.random() * (targetList.size() - 1));
                        targetPos = targetList.get((int)result);
                    }
                    if(!(targetPos == null)) {

                        double dx = targetPos.getX() - this.mob.getBlockPos().getX();
                        double dy = targetPos.getY() - this.mob.getBlockPos().getY();
                        double dz = targetPos.getZ() - this.mob.getBlockPos().getZ();

                        if(dy < 0) {
                            dy = 0;
                        }

                        if(Math.sqrt(dx*dx) > 0 || Math.sqrt(dz*dz) > 0) {
                            this.mob.updateVelocity(2F, new Vec3d(dx / 10, jumpamount + dy/10, dz / 10));
                            this.mob.playSound(RegisterSounds.ENTITY_FROG_LONG_JUMP, 1.0f, 1.0f);
                        }
                    }

                }
            }
        }

    private static ArrayList<BlockPos> checkforSafePlaceToGo(World world, BlockPos pos, Integer radius) {

        int fixedradius = radius - 1;

        ArrayList<BlockPos> exitList = new ArrayList<>();

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sx = fixedradius * -1;
        double sy;
        double sz;

        for (int index0 = 0; index0 < ((radius * 2) - 1); index0++) {
            sy = fixedradius * -1;
            for (int index1 = 0; index1 < ((radius * 2) - 1); index1++) {
                sz = fixedradius * -1;
                for (int index2 = 0; index2 < ((radius * 2) - 1); index2++) {
                    if (Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2)) <= radius) {
                        if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).isSolidBlock(world, new BlockPos(x + sx, y + sy, z + sz))) {
                            if (world.getBlockState(new BlockPos(x + sx, y + sy+1, z + sz)).getMaterial() == Material.AIR) {
                                exitList.add(new BlockPos(x + sx, y + sy, z + sz));
                            }
                        }
                    }
                    sz = sz + 1;
                }
                sy = sy + 1;
            }
            sx = sx + 1;
        }
        return exitList;
    }
}

