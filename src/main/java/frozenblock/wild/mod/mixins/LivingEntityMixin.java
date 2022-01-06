package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.fromAccurateSculk.CatalystThreader;
import frozenblock.wild.mod.fromAccurateSculk.ClickGameEvent;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.*;


@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	public BlockPos solidsculkCheck(BlockPos blockPos, World world) {
		if (checkPt1(blockPos, world).getY()!=-64) {
			return checkPt1(blockPos, world);
		} else if (checkPt2(blockPos, world).getY()!=-64) {
			return checkPt2(blockPos, world);
		} else { return new BlockPos(0,-64,0); }
	}
	public BlockPos checkPt1(BlockPos blockPos, World world) {
		int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
		int MAX = world.getHeight();
		if (blockPos.getY() + upward >= MAX) {
			upward = (MAX - blockPos.getY())-1;
		}
		for (int h = 0; h < upward; h++) {
			if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up(h+1)).getBlock()) &&
					world.getBlockState(blockPos.add(0,(h),0))== RegisterBlocks.SCULK.getDefaultState()) {
				return blockPos.up(h);
			}
		}
		return new BlockPos(0,-64,0);
	}
	public BlockPos checkPt2(BlockPos blockPos, World world) {
		int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
		int MIN = world.getBottomY();
		if (blockPos.getY() - downward <= MIN) {
			downward = (MIN + blockPos.getY())+1;
		}
		for (int h = 0; h < downward; h++) {
			if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.down(h)).getBlock()) &&
					world.getBlockState(blockPos.down(h+1))==RegisterBlocks.SCULK.getDefaultState()) {
				return blockPos.down(h+1);
			}
		}
		return new BlockPos(0,-64,0);
	}

	public void placeActivator(BlockPos blockPos, World world, int chance) {
		BlockState sculk = RegisterBlocks.SCULK.getDefaultState();
		BlockState sensor = Blocks.SCULK_SENSOR.getDefaultState();
		BlockState shrieker = SculkShriekerBlock.SCULK_SHRIEKER_BLOCK.getDefaultState();
		BlockPos NewSculk;
		int chanceCheck = (chance - 1);
		if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(solidsculkCheck(blockPos, world).up()).getBlock()) && world.getBlockState(solidsculkCheck(blockPos, world)) == sculk) {
			NewSculk = solidsculkCheck(blockPos, world);
			if (NewSculk.getY() != 64) {
				int uniInt = UniformIntProvider.create(0, 1).get(world.getRandom());
				if ((UniformIntProvider.create(0, chance).get(world.getRandom()) > chanceCheck)) {
					if (uniInt > 0.5) {
						if (world.getBlockState(NewSculk.up()) == Blocks.WATER.getDefaultState()) {
							world.setBlockState(NewSculk.up(), sensor.with(Properties.WATERLOGGED, true));
						} else if (world.getBlockState(NewSculk.up()).getBlock() != Blocks.WATER) {
							if (world.getBlockState(NewSculk.up()) == SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.WATERLOGGED, true)) {
								world.setBlockState(NewSculk.up(), sensor.with(Properties.WATERLOGGED, true));
							} else
								world.removeBlock(NewSculk.up(), true);
							world.setBlockState(NewSculk.up(), sensor);
						}
					} else if (uniInt < 0.5) {
							if (world.getBlockState(NewSculk.up()) == Blocks.WATER.getDefaultState()) {
								world.setBlockState(NewSculk.up(), shrieker.with(Properties.WATERLOGGED, true));
							} else if (world.getBlockState(NewSculk.up()).getBlock() != Blocks.WATER) {
								if (world.getBlockState(NewSculk.up()) == SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.WATERLOGGED, true)) {
									world.setBlockState(NewSculk.up(), shrieker.with(Properties.WATERLOGGED, true));
								} else
									world.removeBlock(NewSculk.up(), true);
								world.setBlockState(NewSculk.up(), shrieker);
							}
						}
					}
				}
			}
		}

	@Inject(method = "setHealth", at = @At("HEAD"))
	private void setHealth(float f, CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		if (entity.getType()==EntityType.ENDER_DRAGON && f==0.0F) {
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().down(20));
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().down(14));
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().down(7));
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos());
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().up(7));
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().up(14));
			entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos().up(20));
			}
		}

		public void placeActiveOmptim(int loop, int rVal, int CHANCE, BlockPos pos, LivingEntity entity) {
			if (!entity.world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {
				placeActivator(pos, entity.world, CHANCE);
				for (int l = 0; l < (loop * entity.world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER)); ++l) {
					double a = random() * 2 * PI;
					double r = sqrt(rVal * entity.world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER)) * sqrt(random());
					int x = (int) (r * cos(a));
					int y = (int) (r * sin(a));
					placeActivator(pos.add(x, 0, y), entity.world, CHANCE);
				}
			}
		}

	@Inject(method = "updatePostDeath", at = @At("HEAD"))
	private void updatePostDeath(CallbackInfo info) throws InterruptedException {
		LivingEntity entity = LivingEntity.class.cast(this);
		++entity.deathTime;
		if (entity.deathTime == 19 && !entity.world.isClient()) {
			BlockPos pos = new BlockPos(entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ());
			if (SculkTags.DROPSXP.contains(entity.getType()) && entity.world.getGameRules().getBoolean(WildMod.DO_CATALYST_POLLUTION)) {
			if (Sphere.sphereBlock(RegisterBlocks.SCULK_CATALYST, entity.world, pos, 8)) {
				if (!entity.world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {
					if (SculkTags.THREE.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
						placeActiveOmptim(3, 4, 7, pos, entity);
					}
					if (SculkTags.FIVE.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
						placeActiveOmptim(4, 5, 7, pos, entity);
					}
					if (SculkTags.TEN.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
						placeActiveOmptim(9, 10, 6, pos, entity);
					}
					if (SculkTags.TWENTY.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
						placeActiveOmptim(19, 20, 9, pos, entity);
					}
					if (SculkTags.FIFTY.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
						placeActiveOmptim(59, 50, 14, pos, entity);
					}
					if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
						entity.emitGameEvent(ClickGameEvent.DEATH, entity, entity.getBlockPos());
						placeActiveOmptim(1000, 33, 20, pos, entity);
					}
				} else if (entity.world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {
						int numCatalysts = Sphere.generateSphere(pos, 8, false, entity.world);
						if (SculkTags.THREE.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 3, 4, numCatalysts, 7);
						} else if (SculkTags.FIVE.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 4, 5, numCatalysts, 7);
						} else if (SculkTags.TEN.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 9, 10, numCatalysts, 6);
						} else if (SculkTags.TWENTY.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 19, 20, numCatalysts, 9);
						} else if (SculkTags.FIFTY.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 59, 50, numCatalysts, 14);
						} else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, 1599, 33, numCatalysts, 20);
						} else if (entity.world.getGameRules().getBoolean(WildMod.CATALYST_DETECTS_ALL)) {
							entity.emitGameEvent(ClickGameEvent.DEATH, entity, pos);
							CatalystThreader.main(entity.world, pos, (UniformIntProvider.create(1, 7).get(entity.world.getRandom())) * numCatalysts, (UniformIntProvider.create(1, 7).get(entity.world.getRandom())), numCatalysts, 5);
						}
					}
				}
			}
		}
		}
	}

