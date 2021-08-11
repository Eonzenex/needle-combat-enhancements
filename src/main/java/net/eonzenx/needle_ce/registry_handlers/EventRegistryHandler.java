package net.eonzenx.needle_ce.registry_handlers;


import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.client.key_bindings.KeyBindings;
import net.eonzenx.needle_ce.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.events.callbacks.DashCallback;
import net.eonzenx.needle_ce.events.handlers.BashEventHandler;
import net.eonzenx.needle_ce.events.handlers.DashEventHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;

public class EventRegistryHandler
{
    private static MinecraftClient mcClient;

    private static KeyBinding DASH_KEY;
    private static boolean DashThisKeyPress = false;

    private static boolean BashThisKeyPress = false;


    private static void StaminaTickEvent(MinecraftClient client) {
        var player = client.player;
        if (player == null) { return; }

        StaminaComponent stamina = StaminaComponent.get(player);
        stamina.tick(player, mcClient.getTickDelta());
    }

    private static void DashTriggerEvent(MinecraftClient client) {
        var player = client.player;
        if (player == null) { return; }

        if (DASH_KEY.isPressed() && !DashThisKeyPress) {
            DashThisKeyPress = true;
            DashCallback.EVENT.invoker().dash(player);
        }

        // Prevent holding dash
        if (!DASH_KEY.isPressed() && DashThisKeyPress) {
            DashThisKeyPress = false;
        }
    }

    private static void BashTriggerEvent(MinecraftClient client) {
        var player = client.player;
        if (player == null) { return; }

        var mc_options = mcClient.options;

        var itemHold = player.getActiveHand() == Hand.MAIN_HAND
                ? player.getMainHandStack().getItem()
                : player.getOffHandStack().getItem();

        var BASH_KEY_PRESSED = itemHold instanceof ShieldItem
                && player.isUsingItem()
                && mc_options.keyAttack.isPressed();

        if (BASH_KEY_PRESSED && !BashThisKeyPress) {
            BashThisKeyPress = true;
            BashCallback.EVENT.invoker().bash(player);
        }

        // Prevent holding bash
        if (!BASH_KEY_PRESSED && BashThisKeyPress) {
            BashThisKeyPress = false;
        }
    }


    public static void init() {
        mcClient = MinecraftClient.getInstance();
        DASH_KEY = KeyBindingHelper.registerKeyBinding(KeyBindings.DASH);

        DashEventHandler.init();
        BashEventHandler.init();

        // Stamina tick event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::StaminaTickEvent);

        // Dash trigger event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::DashTriggerEvent);

        // Bash trigger event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::BashTriggerEvent);
    }
}
