package net.eonzenx.needle_ce.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoeItem.class)
public abstract class NCEHoeItemMixin extends MiningToolItem
{

	protected NCEHoeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, Tag<Block> effectiveBlocks, Settings settings) {
		super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		var result = super.postHit(stack, target, attacker);

		var userToEntity = target.getPos().add(attacker.getPos().multiply(-1));
		target.takeKnockback(1, userToEntity.x, userToEntity.z);

		return result;
	}
}
