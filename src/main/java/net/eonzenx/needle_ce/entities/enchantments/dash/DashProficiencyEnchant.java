package net.eonzenx.needle_ce.entities.enchantments.dash;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class DashProficiencyEnchant extends Enchantment {
    public DashProficiencyEnchant() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 5; }

    @Override
    protected boolean canAccept(Enchantment other) {
        var blocked = new ArrayList<>(Arrays.asList(
                EnchantmentRegistryHandler.STAMINA_RECOVERY,
                EnchantmentRegistryHandler.BASH_PROFICIENCY,
                EnchantmentRegistryHandler.SLAM_PROFICIENCY,
                this
        ));

        return !blocked.contains(other);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return true; }

    @Override
    public boolean isAcceptableItem(ItemStack stack) { return super.isAcceptableItem(stack); }
}
