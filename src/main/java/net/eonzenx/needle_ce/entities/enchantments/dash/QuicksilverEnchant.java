package net.eonzenx.needle_ce.entities.enchantments.dash;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class QuicksilverEnchant extends Enchantment {
    public QuicksilverEnchant() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    protected boolean canAccept(Enchantment other) {
        var blocked = new ArrayList<>(Arrays.asList(
                EnchantmentRegistryHandler.VAULTING,
                this
        ));

        return !blocked.contains(other);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return true; }

    @Override
    public boolean isAcceptableItem(ItemStack stack) { return super.isAcceptableItem(stack); }
}
