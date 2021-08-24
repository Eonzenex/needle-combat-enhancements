package net.eonzenx.needle_ce.mixin.entities;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class NCEPlayerMixin
{
    @Shadow public abstract boolean isInvulnerableTo(DamageSource source);

    @Inject(method = "applyDamage", at = @At("HEAD"))
    public void onHit(DamageSource source, float amount, CallbackInfo ci) {
        if (!this.isInvulnerableTo(source)) {
            var stamina = IFullStamina.get(this);
            stamina.blockRegen(StaminaConfig.HIT_BLOCK_REGEN_TIME, true);
        }
    }
}
