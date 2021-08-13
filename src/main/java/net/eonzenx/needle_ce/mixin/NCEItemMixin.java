package net.eonzenx.needle_ce.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class NCEItemMixin {
	@Inject(at = @At("HEAD"), method = "useOnEntity")
	private void useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (!(stack.getItem() instanceof HoeItem)) return;

		var userToEntity = entity.getPos().add(user.getPos().multiply(-1));

		entity.takeKnockback(1, userToEntity.x, userToEntity.z);
	}
}
