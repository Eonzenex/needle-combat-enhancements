package net.eonzenx.needle_ce.registry_handlers;


import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.slam.SlamComponent;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.client.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.client.events.callbacks.DashCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartAnticipationCallback;
import net.eonzenx.needle_ce.client.events.handlers.BashEventHandler;
import net.eonzenx.needle_ce.client.events.handlers.DashEventHandler;
import net.eonzenx.needle_ce.client.events.handlers.slam.SlamContactGroundEventHandler;
import net.eonzenx.needle_ce.client.events.handlers.slam.SlamFallEventHandler;
import net.eonzenx.needle_ce.client.events.handlers.slam.SlamStartEventHandler;
import net.eonzenx.needle_ce.client.key_bindings.KeyBindings;
import net.eonzenx.needle_ce.entities.effects.NCEStunEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunImmunityEffect;
import net.eonzenx.needle_ce.entities.effects.NCEStunResistanceEffect;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

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
