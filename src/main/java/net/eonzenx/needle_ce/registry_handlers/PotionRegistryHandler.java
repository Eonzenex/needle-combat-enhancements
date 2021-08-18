package net.eonzenx.needle_ce.registry_handlers;


import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.entities.effects.NCEStunEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunImmunityEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunResistanceEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

public class PotionRegistryHandler
{
    public static final Potion STUN;
    public static final Potion LONG_STUN;
    public static final Potion LONGER_STUN;

    public static final Potion STUN_RESISTANCE;
    public static final Potion LONG_STUN_RESISTANCE;
    public static final Potion LONGER_STUN_RESISTANCE;

    public static final Potion STUN_IMMUNITY;
    public static final Potion LONG_STUN_IMMUNITY;
    public static final Potion LONGER_STUN_IMMUNITY;


    public static void init() {
        Registry.register(Registry.POTION, NCE.newId("stun"), STUN);
        Registry.register(Registry.POTION, NCE.newId("long_stun"), LONG_STUN);
        Registry.register(Registry.POTION, NCE.newId("longer_stun"), LONGER_STUN);

        Registry.register(Registry.POTION, NCE.newId("stun_resistance"), STUN_RESISTANCE);
        Registry.register(Registry.POTION, NCE.newId("long_stun_resistance"), LONG_STUN_RESISTANCE);
        Registry.register(Registry.POTION, NCE.newId("longer_stun_resistance"), LONGER_STUN_RESISTANCE);

        Registry.register(Registry.POTION, NCE.newId("stun_immunity"), STUN_IMMUNITY);
        Registry.register(Registry.POTION, NCE.newId("long_stun_immunity"), LONG_STUN_IMMUNITY);
        Registry.register(Registry.POTION, NCE.newId("longer_stun_immunity"), LONGER_STUN_IMMUNITY);
    }


    static {
        STUN = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN, 40));
        LONG_STUN = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN, 60));
        LONGER_STUN = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN, 80));

        STUN_RESISTANCE = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_RESISTANCE, 2400));
        LONG_STUN_RESISTANCE = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_RESISTANCE, 4800));
        LONGER_STUN_RESISTANCE = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_RESISTANCE, 7200));

        STUN_IMMUNITY = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_IMMUNITY, 1200));
        LONG_STUN_IMMUNITY = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_IMMUNITY, 2400));
        LONGER_STUN_IMMUNITY = new Potion(new StatusEffectInstance(StatusEffectRegistryHandler.STUN_RESISTANCE, 3600));
    }
}
