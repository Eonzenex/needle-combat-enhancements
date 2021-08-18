package net.eonzenx.needle_ce.entities.effects;

import net.eonzenx.needle_ce.registry_handlers.StatusEffectRegistryHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class NCEStunEffect extends StatusEffect
{
    public NCEStunEffect() {
        super(StatusEffectType.HARMFUL, 0xFFD614);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity.getStatusEffect(StatusEffectRegistryHandler.STUN_IMMUNITY) != null) {
            entity.removeStatusEffect(this);
            return;
        }
        super.onApplied(entity, attributes, amplifier);
    }
}
