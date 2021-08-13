package net.eonzenx.needle_ce.events.handlers.slam;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.utils.ArraysExt;
import net.eonzenx.needle_ce.utils.Misc;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class SlamContactGroundEventHandler
{
    private static Box CalcHitbox(PlayerEntity player) {
        var playerForward = Misc.GetPlayerForward(player);
        var playerUp = new Vec3d(0, 1, 0);
        var playerRight = playerForward.crossProduct(playerUp);
        var pos = player.getPos();

        var boxLowerLeft = pos;
        boxLowerLeft = boxLowerLeft
                .add(StaminaConfig.Slam.Hitbox.WIDTH, StaminaConfig.Slam.Hitbox.HEIGHT, StaminaConfig.Slam.Hitbox.WIDTH);

        var boxUpperRight = pos;
        boxUpperRight = boxUpperRight
                .add(-StaminaConfig.Slam.Hitbox.WIDTH, -StaminaConfig.Slam.Hitbox.HEIGHT, -StaminaConfig.Slam.Hitbox.WIDTH);

        return new Box(boxLowerLeft, boxUpperRight);
    }

    private static PacketByteBuf CreatePacket(PlayerEntity player, List<Integer> livingEntityIds) {
        var packet = PacketByteBufs.create();
        var pos = player.getPos();

        packet.writeIntArray(ArraysExt.toIntArray(livingEntityIds));
        packet.writeDouble(pos.x);
        packet.writeDouble(pos.y);
        packet.writeDouble(pos.z);

        return packet;
    }



    private static void PlaySoundHit(PlayerEntity player) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Slam.HIT_SFX);
        var sfxPitch = Misc.randomInRange(0.8f, 1.2f);
        var sfxVolume = Misc.randomInRange(0.4f, 0.7f);

        player.playSound(soundEvent, sfxVolume, sfxPitch);
    }

    private static void PlaySoundMiss(PlayerEntity player) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Slam.MISS_SFX);
        var sfxPitch = Misc.randomInRange(0.4f, 0.6f);
        var sfxVolume = Misc.randomInRange(0.7f, 1f);

        player.playSound(soundEvent, sfxVolume, sfxPitch);
    }


    private static double RandBetween() {
        return (Math.random() - 0.5) * 3;
    }

    private static void SpawnImpactParticle(PlayerEntity player, ParticleEffect type) {
        var pos = player.getPos();
        player.getEntityWorld().addParticle(
                type,
                pos.x, pos.y, pos.z,
                RandBetween(), (Math.random() / 2) + 0.1, RandBetween());
    }


    public static void init()
    {
        SlamContactGroundCallback.EVENT.register(((player) -> {
            var hitBox = CalcHitbox(player);
            var livingEntityIds = Misc.GetLivingEntityIds(player, hitBox);

            if (livingEntityIds.size() != 0) {
                PlaySoundHit(player);
                var packet = CreatePacket(player, livingEntityIds);
                ClientPlayNetworking.send(NCENetworkingConstants.SLAM_CHANNEL, packet);
            } else {
                PlaySoundMiss(player);
            }

            for (var i = 0; i < 100; i++) {
                SpawnImpactParticle(player, ParticleTypes.CRIT);
            }

            return ActionResult.SUCCESS;
        }));
    }
}
