package net.eonzenx.needle_ce.entities.enchantments.stamina;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class StaminaRecoveryEnchant extends Enchantment {

    public final EquipmentSlot[] allSlotTypes;

    public StaminaRecoveryEnchant() {
        this(new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.LEGS});
    }

    public StaminaRecoveryEnchant(EquipmentSlot[] allSlotTypes) {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, allSlotTypes);
        this.allSlotTypes = allSlotTypes;
    }

    @Override
    public int getMinLevel() { return 1; }

    @Override
    public int getMaxLevel() { return 5; }

    @Override
    protected boolean canAccept(Enchantment other) {
        var blocked = new ArrayList<>(Arrays.asList(
                EnchantmentRegistryHandler.DASH_PROFICIENCY,
                EnchantmentRegistryHandler.BASH_PROFICIENCY,
                EnchantmentRegistryHandler.SLAM_PROFICIENCY,
                this
        ));

        return !blocked.contains(other);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return true; }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        var item = stack.getItem();
        var acceptable = false;
        for (var slot: allSlotTypes) {
            switch (slot) {
                case HEAD -> acceptable = EnchantmentTarget.ARMOR_HEAD.isAcceptableItem(item);
                case CHEST -> acceptable = EnchantmentTarget.ARMOR_CHEST.isAcceptableItem(item);
                case LEGS -> acceptable = EnchantmentTarget.ARMOR_LEGS.isAcceptableItem(item);
                case FEET -> acceptable = EnchantmentTarget.ARMOR_FEET.isAcceptableItem(item);
                default -> {}
            }

            if (acceptable) break;
        }

        return acceptable;
    }
}
