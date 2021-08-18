package net.eonzenx.needle_ce.mixin;

import net.eonzenx.needle_ce.registry_handlers.StatusEffectRegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class NCELivingEntityMixin extends Entity
{
    public NCELivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    @Shadow public abstract void setOnGround(boolean onGround);

    @Shadow public float flyingSpeed;
    @Shadow private float movementSpeed;

    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    private float originalGetMovementSpeed(float slipperiness, boolean onGround, float movementSpeed, float flyingSpeed) {
        return onGround ? movementSpeed * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : flyingSpeed;
    }


    @Inject(method = "getMovementSpeed(F)F", at = @At("INVOKE"), cancellable = true)
    private void getMovementSpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
        var correctMovementSpeed = originalGetMovementSpeed(slipperiness, this.onGround, this.movementSpeed, this.flyingSpeed);

        if (this.getStatusEffect(StatusEffectRegistryHandler.STUN) != null) {
            if (this.getStatusEffect(StatusEffectRegistryHandler.STUN_IMMUNITY) != null) {
                this.removeStatusEffect(StatusEffectRegistryHandler.STUN);
                cir.setReturnValue(correctMovementSpeed);
                return;
            }

            var stunResistance = this.getStatusEffect(StatusEffectRegistryHandler.STUN_RESISTANCE);
            if (stunResistance == null) {
                cir.setReturnValue(0f);
                return;
            }

            var safeAmp = stunResistance.getAmplifier() + 1;
            var correctAmp = safeAmp * 0.15f;
            var movementMultiplier = Math.min(correctAmp, 1f);
            cir.setReturnValue(correctMovementSpeed * movementMultiplier);
            return;
        }

        cir.setReturnValue(correctMovementSpeed);
    }
}
