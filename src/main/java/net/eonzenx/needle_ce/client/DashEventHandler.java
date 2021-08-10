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
    private static float CalcDashCost(PlayerEntity player) {
        // Calculate dash stamina cost
        float dashCost = StaminaConfig.Dash.COST;
        int dashProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.DASH_PROFICIENCY, player);
        if (dashProfEnchantLvl > 0) {
            dashCost = dashCost - (dashProfEnchantLvl * 0.25f);
        }

        return dashCost;
    }

    private static boolean CanPerformDash(PlayerEntity player, StaminaComponent stamina) {
        float dashCost = CalcDashCost(player);
        if (!player.isCreative() && !stamina.canExecuteManoeuvre(dashCost)) {
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
        double dashHeight = StaminaConfig.Dash.HEIGHT;
        int vaultingEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.VAULTING, player);
        if (vaultingEnchantLvl > 0) {
            dashHeight = dashHeight + (vaultingEnchantLvl * 0.11f);
        }

        return dashHeight;
    }

    private static float CalcDashForce(PlayerEntity player) {
        // Calculate dash force
        float dashForce = StaminaConfig.Dash.FORCE;
        int quicksilverEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.QUICKSILVER, player);
        if (quicksilverEnchantLvl > 0) {
            dashForce = dashForce + (quicksilverEnchantLvl * 0.15f);
        }

        return dashForce;
    }


    private static Vec3d CalcFinalDash(Vec3d dashDirection, float dashForce, PlayerEntity player) {
        var dashAbsolute = Vec3DExt.relativeVectorToWorldSpace(dashDirection, dashForce, player.getYaw());

        var playerVelocity = player.getVelocity();
        var dot = dashAbsolute.normalize().dotProduct(playerVelocity.normalize());
        var clampDot = dot < 0 ? 0 : dot;

        var velocityInfluence = playerVelocity.multiply(clampDot);
        return dashAbsolute.add(velocityInfluence);
    }


    public static void init()
    {
        DashCallback.EVENT.register(((player) -> {
            // Get the dash component from the player
            StaminaComponent stamina = StaminaComponent.get(player);
            if (!CanPerformDash(player, stamina)) return ActionResult.FAIL;

            Vec3d dashDirection = CalcDashDirection().normalize();

            // Calculate height after normalise otherwise height is normalised and reduced for diagonals
            float dashForce = CalcDashForce(player);
            double dashHeight = CalcDashHeight(player);
            float dashCost = CalcDashCost(player);

            // Perform dash
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(dashCost)) return ActionResult.FAIL;
            }

            var finalDashVelocity = CalcFinalDash(dashDirection, dashForce, player);
            finalDashVelocity = finalDashVelocity.add(0, dashHeight, 0);

            // Add height after normalize
            player.setVelocity(finalDashVelocity);
            return ActionResult.SUCCESS;
        }));
    }
}
