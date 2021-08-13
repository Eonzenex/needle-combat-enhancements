package net.eonzenx.needle_ce.entities.enchantments.bash;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Arrays;

public class SpringboardEnchant extends Enchantment {
    public SpringboardEnchant() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 2; }

    @Override
    public boolean canAccept(Enchantment other) {
        var blocked = Arrays.asList(
                EnchantmentRegistryHandler.INERTIA,
                this
        );
        return !blocked.contains(other);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) { return stack.getItem() instanceof ShieldItem; }
}
