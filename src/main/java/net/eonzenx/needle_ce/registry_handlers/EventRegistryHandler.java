package net.eonzenx.needle_ce.registry_handlers;


import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.handlers.DashHandler;
import net.eonzenx.needle_ce.client.events.handlers.SlamHandler;
import net.eonzenx.needle_ce.client.key_bindings.KeyBindings;
import net.eonzenx.needle_ce.client.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.client.events.callbacks.DashCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartAnticipationCallback;
import net.eonzenx.needle_ce.client.events.handlers.BashHandler;
import net.eonzenx.needle_ce.utils.mixin.IGetTicksPerSec;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class EventRegistryHandler
{
    private static MinecraftClient mcClient;

    private static KeyBinding DASH_KEY;
    private static boolean DashThisKeyPress = false;
    private static boolean BashThisKeyPress = false;
    private static boolean SlamThisKeyPress = false;

    // 1 second / 20 ticks = 0.05f;
    private static float secondsPerTick = 0.05f;


    private static boolean IsTryingToSlam(PlayerEntity player) {
        var pos = player.getPos();
        var cxt = new RaycastContext(pos, pos.add(0, -StaminaConfig.Slam.MIN_HEIGHT, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, player);

        var hitResult = player.getEntityWorld().raycast(cxt);
        if (hitResult.getType() != HitResult.Type.MISS) return false;

        return player.getPitch() > StaminaConfig.Slam.MAX_ANGLE
                && !player.isOnGround()
                && !player.isSwimming()
                && !player.getAbilities().flying;
    }

    private static boolean IsInstanceOfSlamItem(Item item) {
        return item instanceof ShieldItem || item instanceof ToolItem;
    }

    private static boolean IsSlamming(PlayerEntity player) {
        var staminaComponent = IFullStamina.get(player);
        return staminaComponent.isAnticipatingSlam() || staminaComponent.isSlamming();
    }


    private static void StaminaServerTickEvent(MinecraftServer server) {
        if (server instanceof IGetTicksPerSec iServer) {
            secondsPerTick = (float) iServer.GetMillisecondsPerTick() / 1000;
        }

        for (var player: server.getPlayerManager().getPlayerList()) {
            var stamina = IFullStamina.get((PlayerEntity) player);
            stamina.tick(secondsPerTick);
        }
    }

    private static void StaminaClientTickEvent(MinecraftClient client) {
        if (client.isPaused()) return;
        var player = client.player;
        if (player == null) return;

        var stamina = IFullStamina.get((PlayerEntity) player);
        stamina.tick(secondsPerTick);
    }


    private static void DashTriggerEvent(MinecraftClient client) {
        if (client.isPaused()) return;
        var player = client.player;
        if (player == null) return;

        if (IsSlamming(player)) return;

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
        if (client.isPaused()) return;
        var player = client.player;
        if (player == null) return;

        if (IsSlamming(player)) return;

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


    private static void StartSlamTriggerEvent(MinecraftClient client) {
        if (client.isPaused()) return;
        var player = client.player;
        if (player == null) return;

        if (IsSlamming(player)) return;

        var mc_options = mcClient.options;
        var SLAM_KEY_PRESSED = (mc_options.keyAttack.isPressed() && IsInstanceOfSlamItem(player.getMainHandStack().getItem()))
                || (mc_options.keyUse.isPressed() && IsInstanceOfSlamItem(player.getOffHandStack().getItem()));

        if (IsTryingToSlam(player)) {
            if (SLAM_KEY_PRESSED && !SlamThisKeyPress) {
                SlamThisKeyPress = true;
                SlamStartAnticipationCallback.EVENT.invoker().startAnticipation(player);
            }
        }

        // Prevent holding slam
        if (!SLAM_KEY_PRESSED && SlamThisKeyPress) {
            SlamThisKeyPress = false;
        }
    }

    private static void SlamClientTickEvent(MinecraftClient client) {
        if (client.isPaused()) return;
        var player = client.player;
        if (player == null) return;

        var stamina = IFullStamina.get(player);
        if (stamina.isSlamming() && player.isOnGround()) {
            SlamContactGroundCallback.EVENT.invoker().hitGround(player);
        }
    }


    public static void init() {
        mcClient = MinecraftClient.getInstance();
        DASH_KEY = KeyBindingHelper.registerKeyBinding(KeyBindings.DASH);

        DashHandler.init();
        BashHandler.init();
        SlamHandler.init();

        // Stamina tick event
        ServerTickEvents.END_SERVER_TICK.register(EventRegistryHandler::StaminaServerTickEvent);
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::StaminaClientTickEvent);

        // Dash trigger event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::DashTriggerEvent);

        // Bash trigger event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::BashTriggerEvent);

        // Slam trigger event
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::StartSlamTriggerEvent);
        ClientTickEvents.END_CLIENT_TICK.register(EventRegistryHandler::SlamClientTickEvent);
    }
}
