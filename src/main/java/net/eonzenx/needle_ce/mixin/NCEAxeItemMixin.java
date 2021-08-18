package net.eonzenx.needle_ce.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public abstract class NCEAxeItemMixin extends MiningToolItem
{
	protected NCEAxeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, Tag<Block> effectiveBlocks, Settings settings) {
		super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		var result = super.postHit(stack, target, attacker);

		if (target.isBlocking()) {
			ItemStack itemStack;
			var wasOffhand = true;
			if (target.getOffHandStack().getItem() instanceof ShieldItem) {
				itemStack = target.getOffHandStack();
			} else {
				itemStack = target.getMainHandStack();
				wasOffhand = false;
			}

			boolean finalWasOffhand = wasOffhand;
			itemStack.damage(2, attacker, (e) -> e.sendEquipmentBreakStatus(finalWasOffhand ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND));
		}

		return result;
	}
}
