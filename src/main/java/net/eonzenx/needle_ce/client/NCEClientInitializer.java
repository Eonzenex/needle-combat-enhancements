package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.events.callbacks.DashCallback;
import net.eonzenx.needle_ce.client.key_bindings.KeyBindings;
import net.eonzenx.needle_ce.server.NCEBashServer;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;

public class NCEClientInitializer implements ClientModInitializer
{
    private MinecraftClient mcClient;

    private boolean DashThisKeyPress = false;
    private boolean BashThisKeyPress = false;


    @Override
    public void onInitializeClient() {
        mcClient = MinecraftClient.getInstance();

        DashEventHandler.init();
        BashEventHandler.init();

        // Stamina tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var player = client.player;
            if (player == null) { return; }

            StaminaComponent stamina = StaminaComponent.get(player);
            stamina.tick(player, mcClient.getTickDelta());
        });

        // Dash trigger event
        KeyBinding DASH_KEY = KeyBindingHelper.registerKeyBinding(KeyBindings.DASH);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
        });

        // Bash trigger event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
        });

        // Server stuff
        // TODO: Should probably NOT be on the ClientInit
        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_CHANNEL,
                NCEBashServer::execute
        );

        System.out.println("Needle - Combat Enhancements: Client init complete");
    }

}

