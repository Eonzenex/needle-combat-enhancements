package net.eonzenx.needle_ce.client.events.handlers;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamCancelCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartAnticipationCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartFastFallCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.utils.ArraysExt;
import net.eonzenx.needle_ce.utils.Misc;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SlamHandler
{
    public static float CalcSlamCost(LivingEntity livingEntity) {
        // Calculate bash stamina cost
        float slamCost = StaminaConfig.Slam.COST;
        int slamProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.SLAM_PROFICIENCY, livingEntity);
        if (slamProfEnchantLvl > 0) {
            slamCost = slamCost - (slamProfEnchantLvl * 0.4f);
        }

        return slamCost;
    }

    public static boolean CanPerformSlam(LivingEntity livingEntity, IFullStamina stamina) {
        float bashCost = CalcSlamCost(livingEntity);
        if (livingEntity instanceof PlayerEntity player) {
            return player.isCreative() || stamina.canExecuteManoeuvre(bashCost);
        }
        return stamina.canExecuteManoeuvre(bashCost);
    }

    public static ActionResult TryStartSlam(LivingEntity livingEntity) {
        // Get the stamina component from the player.
        IFullStamina stamina = IFullStamina.get(livingEntity);
        if (!CanPerformSlam(livingEntity, stamina)) return ActionResult.FAIL;

        // Commit slam
        if (livingEntity instanceof PlayerEntity player){
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(CalcSlamCost(player))) return ActionResult.FAIL;
            }
        }

        var staminaComponent = IFullStamina.get(livingEntity);
        staminaComponent.startAnticipatingSlam(StaminaConfig.Slam.ANTICIPATION_TIME, false);

        var yCap = 0.1f;
        var playerVelocity = livingEntity.getVelocity();
        if (playerVelocity.y > yCap) livingEntity.setVelocity(playerVelocity.x, yCap, playerVelocity.z);
        else if (playerVelocity.y < -yCap) livingEntity.setVelocity(playerVelocity.x, -yCap, playerVelocity.z);

        livingEntity.setNoGravity(true);

        ClientPlayNetworking.send(NCENetworkingConstants.SLAM_VERIFY_CHANNEL, PacketByteBufs.empty());

        return ActionResult.SUCCESS;
    }



    public static ActionResult SlamFastFall(LivingEntity livingEntity) {
        livingEntity.setVelocity(0, -StaminaConfig.Slam.Fall.FORCE, 0);
        livingEntity.setNoGravity(false);

        ClientPlayNetworking.send(NCENetworkingConstants.SLAM_FAST_FALL_VERIFY_CHANNEL, PacketByteBufs.empty());

        return ActionResult.SUCCESS;
    }



    public static Box CalcHitbox(LivingEntity livingEntity) {
        var playerForward = Misc.GetPlayerForward(livingEntity);
        var playerUp = new Vec3d(0, 1, 0);
        var playerRight = playerForward.crossProduct(playerUp);
        var pos = livingEntity.getPos();

        var boxLowerLeft = pos;
        boxLowerLeft = boxLowerLeft
                .add(StaminaConfig.Slam.Hitbox.WIDTH, StaminaConfig.Slam.Hitbox.HEIGHT, StaminaConfig.Slam.Hitbox.WIDTH);

        var boxUpperRight = pos;
        boxUpperRight = boxUpperRight
                .add(-StaminaConfig.Slam.Hitbox.WIDTH, -StaminaConfig.Slam.Hitbox.HEIGHT, -StaminaConfig.Slam.Hitbox.WIDTH);

        return new Box(boxLowerLeft, boxUpperRight);
    }


    public static PacketByteBuf MakeHitPacket(List<Integer> livingEntityIds) {
        var packet = PacketByteBufs.create();

        packet.writeBoolean(true);
        packet.writeIntArray(ArraysExt.toIntArray(livingEntityIds));

        return packet;
    }

    public static PacketByteBuf MakeNoHitPacket() {
        var packet = PacketByteBufs.create();

        packet.writeBoolean(false);

        return packet;
    }


    public static void PlaySoundHit(LivingEntity livingEntity) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Slam.HIT_SFX);
        var sfxPitch = Misc.randomInRange(0.8f, 1.2f);
        var sfxVolume = Misc.randomInRange(0.4f, 0.7f);

        livingEntity.playSound(soundEvent, sfxVolume, sfxPitch);
    }

    public static void PlaySoundMiss(LivingEntity livingEntity) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Slam.MISS_SFX);
        var sfxPitch = Misc.randomInRange(0.4f, 0.6f);
        var sfxVolume = Misc.randomInRange(0.7f, 1f);

        livingEntity.playSound(soundEvent, sfxVolume, sfxPitch);
    }


    public static double RandBetween() {
        return (Math.random() - 0.5) * 3;
    }

    public static void SpawnImpactParticle(LivingEntity livingEntity, ParticleEffect type) {
        var pos = livingEntity.getPos();
        livingEntity.getEntityWorld().addParticle(
                type,
                pos.x, pos.y, pos.z,
                RandBetween(), (Math.random() / 2) + 0.1, RandBetween());
    }

    public static ActionResult SlamHitGround(LivingEntity livingEntity) {
        var stamina = IFullStamina.get(livingEntity);
        stamina.completeSlam();

        var hitBox = CalcHitbox(livingEntity);
        var livingEntityIds = Misc.GetLivingEntityIds(livingEntity, hitBox);

        PacketByteBuf packet;
        if (livingEntityIds.size() != 0) {
            PlaySoundHit(livingEntity);
            packet = MakeHitPacket(livingEntityIds);
        } else {
            PlaySoundMiss(livingEntity);
            packet = MakeNoHitPacket();
        }

        ClientPlayNetworking.send(NCENetworkingConstants.SLAM_HIT_VERIFY_CHANNEL, packet);

//        for (var i = 0; i < 100; i++) {
//            SpawnImpactParticle(livingEntity, ParticleTypes.CRIT);
//        }

        return ActionResult.SUCCESS;
    }


    public static ActionResult CancelSlam(LivingEntity livingEntity) {
        if (livingEntity != null) {
            livingEntity.setNoGravity(false);
        }

        return ActionResult.SUCCESS;
    }



    private static void ReceiveSlamVerification(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var client = MinecraftClient.getInstance();
        if (client == null) { return; }
        var player = client.player;
        if (player == null) { return; }

        var pos = player.getPos();

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

    private static void ReceiveSlamFastFallVerification(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        var client = MinecraftClient.getInstance();
        if (client == null) { return; }
        var player = client.player;
        if (player == null) { return; }

        // Failed dash
        if (!packetByteBuf.readBoolean()) {
            var stamina = IFullStamina.get(player);
            stamina.cancelSlam();
        }
    }



    public static void init() {
        SlamStartAnticipationCallback.EVENT.register(SlamHandler::TryStartSlam);
        SlamStartFastFallCallback.EVENT.register(SlamHandler::SlamFastFall);
        SlamContactGroundCallback.EVENT.register(SlamHandler::SlamHitGround);
        SlamCancelCallback.EVENT.register(SlamHandler::CancelSlam);

        ClientPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_VERIFY_CHANNEL,
                SlamHandler::ReceiveSlamVerification
        );

        ClientPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_FAST_FALL_VERIFY_CHANNEL,
                SlamHandler::ReceiveSlamFastFallVerification
        );
    }
}
