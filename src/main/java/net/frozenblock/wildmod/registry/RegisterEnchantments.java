package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.enchantments.SwiftSneakEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterEnchantments {

    public static final Enchantment SWIFT_SNEAK = register("swift_sneak", new SwiftSneakEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.LEGS));

    public static void init() {
    }

    public static Enchantment register(String string, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(WildMod.MOD_ID, string), enchantment);
    }
}
