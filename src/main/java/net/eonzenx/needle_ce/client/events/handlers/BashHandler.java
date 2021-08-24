package net.eonzenx.needle_ce.client.events.handlers;

import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.client.events.callbacks.BashCallback;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class BashHandler
{
    public static float CalcBashCost(LivingEntity livingEntity) {
        // Calculate bash stamina cost
        float bashCost = StaminaConfig.Bash.COST;
        int bashProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.BASH_PROFICIENCY, livingEntity);
        if (bashProfEnchantLvl > 0) {
            bashCost = bashCost - (bashProfEnchantLvl * 0.4f);
        }

        return bashCost;
    }

    public static boolean CanPerformBash(LivingEntity livingEntity, IFullStamina stamina) {
        float bashCost = CalcBashCost(livingEntity);
        if (livingEntity instanceof PlayerEntity player) {
            return player.isCreative() || stamina.canExecuteManoeuvre(bashCost);
        }
        return stamina.canExecuteManoeuvre(bashCost);
    }


    public static double CalcBashHeight(LivingEntity livingEntity) {
        return StaminaConfig.Bash.HEIGHT;
    }

    public static float CalcBashForce(LivingEntity livingEntity) {
        return StaminaConfig.Bash.FORCE;
    }


    public static Box CalcBashHitBox(LivingEntity livingEntity) {
        var playerForward = Misc.GetPlayerForward(livingEntity);
        var playerUp = new Vec3d(0, 1, 0);
        var playerRight = playerForward.crossProduct(playerUp);
        var pos = livingEntity.getPos();

        var boxLowerLeft = pos;
        boxLowerLeft = boxLowerLeft.add(playerRight.multiply(-StaminaConfig.Bash.Hitbox.WIDTH));
        var boxUpperRight = pos;
        boxUpperRight = boxUpperRight
                .add(playerForward.multiply(StaminaConfig.Bash.Hitbox.DEPTH))
                .add(playerRight.multiply(StaminaConfig.Bash.Hitbox.WIDTH))
                .add(playerUp.multiply(StaminaConfig.Bash.Hitbox.HEIGHT));

        return new Box(boxLowerLeft, boxUpperRight);
    }

    public static PacketByteBuf CreateBashHitPacket(LivingEntity livingEntity, List<Integer> livingEntityIds) {
        var playerForward = Misc.GetPlayerForward(livingEntity);
        var packet = PacketByteBufs.create();
        packet.writeIntArray(ArraysExt.toIntArray(livingEntityIds));
        packet.writeDouble(playerForward.x);
        packet.writeDouble(playerForward.z);

        return packet;
    }


    public static void PlaySoundHit(LivingEntity livingEntity) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Bash.HIT_SFX);
        var sfxPitch = Misc.randomInRange(0.8f, 1.2f);
        var sfxVolume = Misc.randomInRange(0.4f, 0.7f);

        livingEntity.playSound(soundEvent, sfxVolume, sfxPitch);
    }

    public static void PlaySoundMiss(LivingEntity livingEntity) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Bash.MISS_SFX);
        var sfxPitch = Misc.randomInRange(0.4f, 0.6f);
        var sfxVolume = Misc.randomInRange(0.4f, 0.7f);

        livingEntity.playSound(soundEvent, sfxVolume, sfxPitch);
    }


    private static ActionResult TryPerformBash(LivingEntity livingEntity) {
        // Get the stamina component from the livingEntity.
        IFullStamina stamina = IFullStamina.get(livingEntity);
        if (!CanPerformBash(livingEntity, stamina)) return ActionResult.FAIL;

        ClientPlayNetworking.send(NCENetworkingConstants.BASH_VERIFY_CHANNEL, PacketByteBufs.empty());

        // Commit bash
        if (livingEntity instanceof PlayerEntity player) {
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(CalcBashCost(livingEntity))) return ActionResult.FAIL;
            }
        }

        livingEntity.updateVelocity(CalcBashForce(livingEntity), new Vec3d(0, CalcBashHeight(livingEntity), 1));

        var hitBox = CalcBashHitBox(livingEntity);
        var livingEntityIds = Misc.GetLivingEntityIds(livingEntity, hitBox);

        if (livingEntityIds.size() != 0) {
            PlaySoundHit(livingEntity);
            var packet = CreateBashHitPacket(livingEntity, livingEntityIds);
            ClientPlayNetworking.send(NCENetworkingConstants.BASH_HIT_VERIFY_CHANNEL, packet);
        } else {
            PlaySoundMiss(livingEntity);
        }

        return ActionResult.SUCCESS;
    }



    private static void ReceiveBashVerification(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
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



    public static void init()
    {
        BashCallback.EVENT.register((BashHandler::TryPerformBash));

        ClientPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_VERIFY_CHANNEL,
                BashHandler::ReceiveBashVerification
        );
    }
}
