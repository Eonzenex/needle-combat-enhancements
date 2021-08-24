package net.eonzenx.needle_ce.client.events.handlers;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.eonzenx.needle_ce.client.events.callbacks.DashCallback;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.utils.ArraysExt;
import net.eonzenx.needle_ce.utils.Misc;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

import net.eonzenx.needle_ce.utils.Vec3DExt;


public class DashHandler
{
    public static float CalcDashCost(LivingEntity livingEntity) {
        // Calculate dash stamina cost
        float dashCost = StaminaConfig.Dash.COST;
        int dashProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.DASH_PROFICIENCY, livingEntity);
        if (dashProfEnchantLvl > 0) {
            dashCost = dashCost - (dashProfEnchantLvl * 0.25f);
        }

        return dashCost;
    }

    public static boolean CanPerformDash(LivingEntity livingEntity, IFullStamina stamina) {
        float dashCost = CalcDashCost(livingEntity);

        if (livingEntity instanceof PlayerEntity player) {
            if (!player.isCreative() && !stamina.canExecuteManoeuvre(dashCost)) {
                return false;
            }
        }

        int galeBurstLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.GALE_BURST, livingEntity);
        return galeBurstLvl != 0 || livingEntity.isOnGround();
    }

    public static Vec3d CalcDashDirection() {
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

    public static double CalcDashHeight(LivingEntity livingEntity) {
        double dashHeight = StaminaConfig.Dash.HEIGHT;
        int vaultingEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.VAULTING, livingEntity);
        if (vaultingEnchantLvl > 0) {
            dashHeight = dashHeight + (vaultingEnchantLvl * 0.11f);
        }

        return dashHeight;
    }

    public static float CalcDashForce(LivingEntity livingEntity) {
        // Calculate dash force
        float dashForce = StaminaConfig.Dash.FORCE;
        int quicksilverEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.QUICKSILVER, livingEntity);
        if (quicksilverEnchantLvl > 0) {
            dashForce = dashForce + (quicksilverEnchantLvl * 0.15f);
        }

        return dashForce;
    }


    public static Vec3d CalcFinalDash(Vec3d dashDirection, float dashForce, LivingEntity livingEntity) {
        var dashAbsolute = Vec3DExt.relativeVectorToWorldSpace(dashDirection, dashForce, livingEntity.getYaw());

        var playerVelocity = livingEntity.getVelocity();
        var dot = dashAbsolute.normalize().dotProduct(playerVelocity.normalize());
        var clampDot = dot < 0 ? 0 : dot;

        var velocityInfluence = playerVelocity.multiply(clampDot);
        return dashAbsolute.add(velocityInfluence);
    }

    public static void PlaySound(LivingEntity livingEntity) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Dash.SFX);
        var sfxPitch = Misc.randomInRange(0.4f, 0.6f);
        var sfxVolume = Misc.randomInRange(0.4f, 0.7f);

        livingEntity.playSound(soundEvent, sfxVolume, sfxPitch);
    }

    public static void SpawnImpactParticle(LivingEntity livingEntity, ParticleEffect type) {
        var pos = livingEntity.getPos();
        var particlePos = pos.add(Math.random(), Math.random(), Math.random());

        var vel = livingEntity.getVelocity();
        var particleVel = vel.multiply(-0.5f);

        livingEntity.getEntityWorld().addParticle(
                type,
                particlePos.x, particlePos.y, particlePos.z,
                particleVel.x, particleVel.y, particleVel.z);
    }


    private static ActionResult TryPerformDash(LivingEntity livingEntity) {
        // Get the dash component from the player
        IFullStamina stamina = IFullStamina.get(livingEntity);
        if (!CanPerformDash(livingEntity, stamina)) return ActionResult.FAIL;

        ClientPlayNetworking.send(NCENetworkingConstants.DASH_VERIFY_CHANNEL, PacketByteBufs.empty());

        Vec3d dashDirection = CalcDashDirection().normalize();

        // Calculate height after normalise otherwise height is normalised and reduced for diagonals
        float dashForce = CalcDashForce(livingEntity);
        double dashHeight = CalcDashHeight(livingEntity);
        float dashCost = CalcDashCost(livingEntity);

        // Perform dash
        if (livingEntity instanceof PlayerEntity player) {
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(dashCost)) return ActionResult.FAIL;
            }
        }

        var finalDashVelocity = CalcFinalDash(dashDirection, dashForce, livingEntity);
        finalDashVelocity = new Vec3d(finalDashVelocity.x, dashHeight, finalDashVelocity.z);

        // Add height after normalize
        livingEntity.setVelocity(finalDashVelocity);
        PlaySound(livingEntity);

        for (var i = 0; i < 5; i++) {
            SpawnImpactParticle(livingEntity, ParticleTypes.POOF);
        }

        return ActionResult.SUCCESS;
    }



    private static void ReceiveDashVerification(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var client = MinecraftClient.getInstance();
        if (client == null) { return; }
        var player = MinecraftClient.getInstance().player;
        if (player == null) { return; }

        var pos = MinecraftClient.getInstance().player.getPos();

        // Failed dash
        if (!packetByteBuf.readBoolean()) {
            var serverPastPos = new Vec3d(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble());
            if (serverPastPos.distanceTo(pos) > 5f) {
                // Bad vectors, do something to fix it here
                return;
            }

            player.teleport(serverPastPos.x, serverPastPos.y, serverPastPos.z);
        }
    }



    public static void init() {
        DashCallback.EVENT.register((DashHandler::TryPerformDash));

        ClientPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.DASH_VERIFY_CHANNEL,
                DashHandler::ReceiveDashVerification
        );
    }
}
