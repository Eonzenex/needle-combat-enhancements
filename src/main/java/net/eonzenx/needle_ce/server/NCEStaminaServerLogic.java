package net.eonzenx.needle_ce.server;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.eonzenx.needle_ce.client.events.handlers.BashHandler;
import net.eonzenx.needle_ce.client.events.handlers.DashHandler;
import net.eonzenx.needle_ce.client.events.handlers.SlamHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class NCEStaminaServerLogic
{
    public static PacketByteBuf MakeFailPacket(PlayerEntity player) {
        var packet = PacketByteBufs.create();

        packet.writeBoolean(false);
        packet.writeDouble(player.getX());
        packet.writeDouble(player.getY());
        packet.writeDouble(player.getZ());

        return packet;
    }

    public static PacketByteBuf MakeSuccessPacket() {
        var packet = PacketByteBufs.create();
        packet.writeBoolean(true);

        return packet;
    }


    public static void verifyDash(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stamina = IFullStamina.get(player);
        if (!DashHandler.CanPerformDash(player, stamina)) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.DASH_VERIFY_CHANNEL, MakeFailPacket(player));
            return;
        }

        float dashCost = DashHandler.CalcDashCost(player);

        // Perform dash
        if (!player.isCreative()) {
            if (!stamina.commitManoeuvre(dashCost)) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.DASH_VERIFY_CHANNEL, MakeFailPacket(player));
                return;
            }
        }

        ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.DASH_VERIFY_CHANNEL, MakeSuccessPacket());
    }

    public static void verifyBash(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stamina = IFullStamina.get(player);
        if (!BashHandler.CanPerformBash(player, stamina)) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.BASH_VERIFY_CHANNEL, MakeFailPacket(player));
            return;
        }

        float bashCost = BashHandler.CalcBashCost(player);

        // Perform dash
        if (!player.isCreative()) {
            if (!stamina.commitManoeuvre(bashCost)) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.BASH_VERIFY_CHANNEL, MakeFailPacket(player));
                return;
            }
        }

        ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.BASH_VERIFY_CHANNEL, MakeSuccessPacket());
    }

    public static void verifySlam(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stamina = IFullStamina.get(player);
        var slamCost = SlamHandler.CalcSlamCost(player);

        // Perform dash
        if (!player.isCreative()) {
            if (!stamina.commitManoeuvre(slamCost)) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.SLAM_VERIFY_CHANNEL, MakeFailPacket(player));
                return;
            }
        }

        stamina.startAnticipatingSlam(StaminaConfig.Slam.ANTICIPATION_TIME, true);
        ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.SLAM_VERIFY_CHANNEL, MakeSuccessPacket());
    }

    public static void verifySlamFastFall(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var stamina = IFullStamina.get(player);
        var packet = PacketByteBufs.create();
        if (stamina.isAnticipatingSlam() || stamina.isSlamming()) {
            packet.writeBoolean(true);
            ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.SLAM_FAST_FALL_VERIFY_CHANNEL, packet);

            return;
        }

        stamina.cancelSlam();

        packet.writeBoolean(false);
        ServerPlayNetworking.send((ServerPlayerEntity) player, NCENetworkingConstants.SLAM_FAST_FALL_VERIFY_CHANNEL, packet);
    }
}
