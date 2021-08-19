package net.eonzenx.needle_ce.registry_handlers;


import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.entities.effects.NCEStunEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunImmunityEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunResistanceEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class StatusEffectRegistryHandler
{
    public static final StatusEffect STUN = new NCEStunEffect();
    public static final StatusEffect STUN_RESISTANCE = new NCEStunResistanceEffect();
    public static final StatusEffect STUN_IMMUNITY = new NCEStunImmunityEffect();

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, NCE.newId("stun"), STUN);
        Registry.register(Registry.STATUS_EFFECT, NCE.newId("stun_resistance"), STUN_RESISTANCE);
        Registry.register(Registry.STATUS_EFFECT, NCE.newId("stun_immunity"), STUN_IMMUNITY);
    }
}
