package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;
import java.util.function.Predicate;

public class SculkPatchFeature extends Feature<DefaultFeatureConfig> {
    Random random = new Random();
    public SculkPatchFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    /*
    -----READ BEFORE EDITING CODE-----

        The first part of this code will most likely not need to be changed. the
     method "placePatch" is where you should add in some code. This needs to
     generate a patch. Note that: 1. The starting position will usually be in the
     air. 2. There will be multiple of these scattered around. This is ONE patch.
     3. We need variations in the size of it and hopefully try to expand any
     sculk if it overlaps with some previously generated sculk.

     Some useful code tips:

     Place a block:
     context.getWorld().setBlockStates(Position, Block State, flags[this should usually be 0])
     Use the block registry with .getDefaultState attached to the end.
     Add a .with(Specific Blockstate, Value of Specific Blockstate) if you need a specific blockstate from it(can stack multiple times)

     Use BlockPos variables to move around specific parts but go back somewhere previous.
     Use methods with private void in front for code that repeats many times. Make sure they need the correct parameters!

     And with that in mind, happy coding!
     */

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        boolean success = true;
        //Places the catalyst
        context.getWorld().setBlockState(context.getOrigin(), RegisterBlocks.SCULK_CATALYST.getDefaultState(), 0);

        //This code randomly places a few sculk patches around. it repeats a random place selection forever, with a 40% chance of stopping every time and if it reaches 6, it automatically stops it after the 6th patch.

        //TABLE OF PROBABILITY
        //1 Patch: 100.00%
        //2 Patch: 60.00%
        //3 Patch: 36.00%
        //4 Patch: 21.60%
        //5 Patch: 12.96%
        //6 Patch: 7.78%
        //7 Patch: 0.00%
        for (int x = 0; x < 6; x++) {
            BlockPos patchPos = context.getOrigin();
            //Gives a random number of placement movements. It randomly moves one block in any direction each move..
            int patchPlacementMovements = random.nextInt(12);
            for (int i = 0; i < 6; i++) {
                int dir = random.nextInt(6);
                if (dir == 0) {
                    patchPos = patchPos.up();
                } else if (dir == 1) {
                    patchPos = patchPos.down();
                } else if (dir == 2) {
                    patchPos = patchPos.north();
                } else if (dir == 3) {
                    patchPos = patchPos.south();
                } else if (dir == 4) {
                    patchPos = patchPos.east();
                } else if (dir == 5) {
                    patchPos = patchPos.west();
                } else {
                    success = false;
                }
            }
            //Checks if the block at the specified location is air and within 6 blocks of the origin. This only affects the center of the patches.
            if (context.getOrigin().ORIGIN.isWithinDistance(patchPos, 6) && context.getWorld().testBlockState(patchPos, Predicate.isEqual(Blocks.AIR.getDefaultState()))) {
                //Places a patch and gives a chance of stopping.
                placePatch(context, patchPos);
                if (Math.random() < 0.40) {
                    break;
                }
            } else {
                //If it isnt, repeats the for loop 1 more time. Math.random part is a failsafe.
                if (Math.random() < 0.99) {
                    x--;
                }
            }
        }

        return success;
    }

    public void placePatch(FeatureContext<DefaultFeatureConfig> context, BlockPos pos) {
        //Here's where you put the code for placing a patch. For default, ive made it place a sculk block. This will need to change.
        context.getWorld().setBlockState(pos, RegisterBlocks.SCULK.getDefaultState(), 0);
    }
}