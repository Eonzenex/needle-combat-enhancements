package net.eonzenx.needle_ce.entities.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public abstract class MultiTargetEnchant extends Enchantment {

    public final EquipmentSlot[] allSlotTypes;

    public MultiTargetEnchant() {
        this(Rarity.RARE, new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.LEGS});
    }

    public MultiTargetEnchant(Rarity weight, EquipmentSlot[] allSlotTypes) {
        super(weight, EnchantmentTarget.ARMOR_CHEST, allSlotTypes);
        this.allSlotTypes = allSlotTypes;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }
}
