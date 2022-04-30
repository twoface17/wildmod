package net.frozenblock.wildmod.block;

import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.frozenblock.wildmod.fromAccurateSculk.WildProperties;
import net.frozenblock.wildmod.fromAccurateSculk.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.fromAccurateSculk.SculkCatalystPhase;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkCatalystBlock extends BlockWithEntity {
    public static final int BLOOM_DURATION = 8;
    public static final BooleanProperty BLOOM = WildProperties.BLOOM;
    private final IntProvider experience = ConstantIntProvider.create(20);

    public SculkCatalystBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(BLOOM, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BLOOM);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(BLOOM)) {
            world.setBlockState(pos, state.with(BLOOM, false), 3);
        }
    }

    public static void bloom(ServerWorld world, BlockPos pos, BlockState state, AbstractRandom random) {
        world.setBlockState(pos, state.with(BLOOM, true), 3);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 8);
        world.spawnParticles(RegisterParticles.SCULK_SOUL, (double)pos.getX() + 0.5, (double)pos.getY() + 1.15, (double)pos.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
        world.playSound(null, pos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkCatalystBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return blockEntity instanceof SculkCatalystBlockEntity ? (SculkCatalystBlockEntity)blockEntity : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, WildBlockEntityType.SCULK_CATALYST, SculkCatalystBlockEntity::tick);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        this.dropExperienceWhenMined(world, pos, stack, this.experience);
    }

    public static final BlockSoundGroup CATALYSTSOUNDS = new BlockSoundGroup(1.0F, 1.0F,
            RegisterSounds.BLOCK_SCULK_CATALYST_BREAK,
            RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
            RegisterSounds.BLOCK_SCULK_CATALYST_PLACE,
            RegisterSounds.BLOCK_SCULK_CATALYST_HIT,
            RegisterSounds.BLOCK_SCULK_CATALYST_FALL
    );

    public static final AbstractBlock.Settings SCULK_CATALYST_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .sounds(CATALYSTSOUNDS)
            .luminance(state -> 6);
    public static final Block SCULK_CATALYST_BLOCK = new SculkCatalystBlock(SCULK_CATALYST_PROPERTIES
            .strength(3.0F, 3.0F));

    protected void dropExperienceWhenMined(ServerWorld world, BlockPos pos, ItemStack tool, IntProvider experience) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            int i = experience.get(world.random);
            if (i > 0) {
                this.dropExperience(world, pos, i);
            }
        }

    }
}
