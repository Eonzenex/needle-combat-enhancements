package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.events.callbacks.DashCallback;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

import net.eonzenx.needle_ce.utils.Vec3DExt;


public class DashEventHandler
{
    // TODO: Update CanPerformDash for Dash Proficiency
    private static boolean CanPerformDash(PlayerEntity player, StaminaComponent stamina) {
        if (!player.isCreative() && !stamina.canExecuteManoeuvre(StaminaConfig.DASH_COST)) {
            return false;
        }

        int galeBurstLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.GALE_BURST, player);
        return galeBurstLvl != 0 || player.isOnGround();
    }

    private static Vec3d CalcDashDirection() {
        // Setup dash properties
        GameOptions mc_options = MinecraftClient.getInstance().options;
        Vec3d dashDirection = Vec3d.ZERO;

        // Update velocity adds player yaw, only provide dash angle here
        if (mc_options.keyForward.isPressed()) {
            dashDirection = dashDirection.add(Vec3d.fromPolar(0, 0));
        }
        if (mc_options.keyBack.isPressed()) {
            dashDirection = dashDirection.add(Vec3d.fromPolar(0, -180));
        }
        if (mc_options.keyLeft.isPressed()) {
            dashDirection = dashDirection.add(Vec3d.fromPolar(0, -90));
        }
        if (mc_options.keyRight.isPressed()) {
            dashDirection = dashDirection.add(Vec3d.fromPolar(0, +90));
        }

        return dashDirection;
    }

    private static double CalcDashHeight(PlayerEntity player) {
        double dashHeight = StaminaConfig.DASH_HEIGHT;
        int vaultingEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.VAULTING, player);
        if (vaultingEnchantLvl > 0) {
            dashHeight = dashHeight * (vaultingEnchantLvl + 1);
        }

        return dashHeight;
    }

    private static float CalcDashForce(PlayerEntity player) {
        return StaminaConfig.DASH_FORCE;
    }

    private static float CalcDashCost(PlayerEntity player) {
        // Calculate dash stamina cost
        float dashCost = StaminaConfig.DASH_COST;
        int dashProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.DASH_PROFICIENCY, player);
        if (dashProfEnchantLvl > 0) {
            dashCost = dashCost - ((float) dashProfEnchantLvl / 2);
        }

        return dashCost;
    }


    private static Vec3d CalcFinalDash(Vec3d dashDirection, float dashForce, PlayerEntity player) {
        var dashAbsolute = Vec3DExt.relativeVectorToWorldSpace(dashDirection, dashForce, player.getYaw());

        var playerVelocity = player.getVelocity();
        var dot = dashAbsolute.normalize().dotProduct(playerVelocity.normalize());

        if (dot > 0) {
            var velocityInfluence = playerVelocity.multiply(dot);
            return dashAbsolute.add(velocityInfluence);
        } else {
            return dashAbsolute;
        }
    }


    public static void init()
    {
        DashCallback.EVENT.register(((player) -> {

            // Get the dash component from the player
            StaminaComponent stamina = StaminaComponent.get(player);
            if (!CanPerformDash(player, stamina)) return ActionResult.FAIL;

            Vec3d dashDirection = CalcDashDirection().normalize();

            // Calculate height after normalise otherwise height is normalised and reduced for diagonals
            double dashHeight = CalcDashHeight(player);
            float dashForce = CalcDashForce(player);
            float dashCost = CalcDashCost(player);

            // Perform dash
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(dashCost)) return ActionResult.FAIL;
            }

            // Add height after normalize
            var finalDashDirection = dashDirection.add(new Vec3d(0, dashHeight, 0));

            player.setVelocity(CalcFinalDash(finalDashDirection, dashForce, player));
            return ActionResult.SUCCESS;
        }));
    }
}
