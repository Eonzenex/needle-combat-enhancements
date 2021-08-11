package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.utils.ArraysExt;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class BashEventHandler
{
    private static float CalcBashCost(PlayerEntity player) {
        // Calculate bash stamina cost
        float bashCost = StaminaConfig.Bash.COST;
        int bashProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.BASH_PROFICIENCY, player);
        if (bashProfEnchantLvl > 0) {
            bashCost = bashCost - (bashProfEnchantLvl * 0.4f);
        }

        return bashCost;
    }

    private static boolean CanPerformBash(PlayerEntity player, StaminaComponent stamina) {
        float bashCost = CalcBashCost(player);
        return player.isCreative() || stamina.canExecuteManoeuvre(bashCost);
    }

    private static double CalcBashHeight(PlayerEntity player) {
        return StaminaConfig.Bash.HEIGHT;
    }

    private static float CalcBashForce(PlayerEntity player) {
        return StaminaConfig.Bash.FORCE;
    }

    private static float CalcBashKnockbackForce(PlayerEntity player) {
        // Calculate bash knockback force
        float bashForce = StaminaConfig.Bash.Knockback.FORCE;
        int bashKnockForceEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.HEAVY_WEIGHT, player);
        if (bashKnockForceEnchantLvl > 0) {
            bashForce = bashForce + (bashKnockForceEnchantLvl * 0.4f);
        }

        return bashForce;
    }


    private static Vec3d GetPlayerForward(PlayerEntity player) {
        return Vec3d.fromPolar(0, player.getYaw());
    }

    private static Box CalcBashHitBox(PlayerEntity player) {
        var playerForward = GetPlayerForward(player);
        var playerUp = new Vec3d(0, 1, 0);
        var playerRight = playerForward.crossProduct(playerUp);
        var pos = player.getPos();

        var boxLowerLeft = pos;
        boxLowerLeft = boxLowerLeft.add(playerRight.multiply(-StaminaConfig.Bash.Hitbox.WIDTH));
        var boxUpperRight = pos;
        boxUpperRight = boxUpperRight
                .add(playerForward.multiply(StaminaConfig.Bash.Hitbox.DEPTH))
                .add(playerRight.multiply(StaminaConfig.Bash.Hitbox.WIDTH))
                .add(playerUp.multiply(StaminaConfig.Bash.Hitbox.HEIGHT));

        return new Box(boxLowerLeft, boxUpperRight);
    }

    private static List<Integer> GetLivingEntityIds(PlayerEntity player, Box hitbox) {
        var entities = player.getEntityWorld().getOtherEntities(player, hitbox);
        var livingEntityIds = new ArrayList<Integer>();
        for (var entity: entities) {
            if (entity instanceof LivingEntity lEntity)
            {
                livingEntityIds.add(lEntity.getId());
            }
        }

        return livingEntityIds;
    }

    private static PacketByteBuf CreateBashPacket(PlayerEntity player, List<Integer> livingEntityIds) {
        var playerForward = GetPlayerForward(player);
        var packet = PacketByteBufs.create();
        packet.writeIntArray(ArraysExt.toIntArray(livingEntityIds));
        packet.writeDouble(playerForward.x);
        packet.writeDouble(playerForward.z);
        packet.writeFloat(StaminaConfig.Bash.Knockback.FORCE);
        packet.writeFloat(StaminaConfig.Bash.Knockback.HEIGHT);

        return packet;
    }

    private static void PlaySound(PlayerEntity player) {
        var soundEvent = ArraysExt.getRandom(StaminaConfig.Bash.SFX);

        var minPitch = 0.8f;
        var maxPitch = 1.2f;
        var sfxPitch = minPitch + (float) Math.random() * (maxPitch - minPitch);

        var minVolume = 0.4f;
        var maxVolume = 0.7f;
        var sfxVolume = minVolume + (float) Math.random() * (maxVolume - minVolume);

        player.playSound(soundEvent, sfxVolume, sfxPitch);
    }


    public static void init()
    {
        BashCallback.EVENT.register(((player) -> {
            // Get the bash component from the player.
            StaminaComponent stamina = StaminaComponent.get(player);
            if (!CanPerformBash(player, stamina)) return ActionResult.FAIL;

            var bashDirection = new Vec3d(0, 0, 1);
            var bashForce = CalcBashForce(player);
            var bashHeight = CalcBashHeight(player);
            var bashCost = CalcBashCost(player);

            // Perform bash
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(bashCost)) return ActionResult.FAIL;
            }

            var finalBashDirection = bashDirection.add(new Vec3d(0, bashHeight, 0));
            player.updateVelocity(bashForce, finalBashDirection);

            var hitBox = CalcBashHitBox(player);
            var livingEntityIds = GetLivingEntityIds(player, hitBox);

            if (livingEntityIds.size() != 0) {
                PlaySound(player);
                var packet = CreateBashPacket(player, livingEntityIds);
                ClientPlayNetworking.send(NCENetworkingConstants.BASH_CHANNEL, packet);
            }

            return ActionResult.SUCCESS;
        }));
    }


}
