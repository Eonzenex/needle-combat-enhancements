package net.eonzenx.needle_ce.entities.enchantments.slam;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import java.util.Arrays;

public class HeavyWeightEnchant extends Enchantment {
    public HeavyWeightEnchant() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public boolean canAccept(Enchantment other) {
        var blocked = Arrays.asList(
                this
        );
        return !blocked.contains(other);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) { return stack.getItem() instanceof ShieldItem; }
}
