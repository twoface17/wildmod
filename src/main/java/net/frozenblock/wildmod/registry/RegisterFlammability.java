package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class RegisterFlammability {

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_LOG,5,5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PLANKS,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_STAIRS,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_DOOR,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.STRIPPED_MANGROVE_LOG,5,5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.STRIPPED_MANGROVE_WOOD,5,5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_WOOD,5,5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_FENCE,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_SLAB,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_FENCE_GATE,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PRESSURE_PLATE,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_TRAPDOOR,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_LEAVES,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_ROOTS,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PROPAGULE,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_BUTTON,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_SIGN,100,60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_WALL_SIGN,100,60);
    }
}