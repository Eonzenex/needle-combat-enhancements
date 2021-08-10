package net.eonzenx.needle_ce.entities.enchantments.dash;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class DashProficiencyEnchant extends Enchantment {
    public DashProficiencyEnchant() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 5; }

    @Override
    protected boolean canAccept(Enchantment other) { return super.canAccept(other); }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return true; }

    @Override
    public boolean isAcceptableItem(ItemStack stack) { return super.isAcceptableItem(stack); }
}