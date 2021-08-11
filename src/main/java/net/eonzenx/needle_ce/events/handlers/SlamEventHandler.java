package net.eonzenx.needle_ce.events.handlers;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.events.callbacks.DashCallback;
import net.eonzenx.needle_ce.events.callbacks.SlamCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.utils.ArraysExt;
import net.eonzenx.needle_ce.utils.Misc;
import net.eonzenx.needle_ce.utils.Vec3DExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;


public class SlamEventHandler
{
    public static void init()
    {
        SlamCallback.EVENT.register(((player) -> {
            // Get the stamina component from the player
            var stamina = StaminaComponent.get(player);

            System.out.println("Slammed!");

            return ActionResult.SUCCESS;
        }));
    }
}
