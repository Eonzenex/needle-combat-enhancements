package net.eonzenx.needle_ce.entities.enchantments.slam;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class SlamProficiencyEnchant extends Enchantment {

    public final EquipmentSlot[] allSlotTypes;

    public SlamProficiencyEnchant() {
        this(new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.LEGS});
    }

    public SlamProficiencyEnchant(EquipmentSlot[] allSlotTypes) {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, allSlotTypes);
        this.allSlotTypes = allSlotTypes;
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
