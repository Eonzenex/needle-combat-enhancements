package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.events.callbacks.BashCallback;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.util.sat4j.core.Vec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class BashEventHandler
{
    // TODO: Update CanPerformBash for Bash Proficiency
    private static boolean CanPerformBash(PlayerEntity player, StaminaComponent stamina) {
        if (!player.isCreative() && !stamina.canExecuteManoeuvre(StaminaConfig.BASH_COST)) {
            return false;
        }

        return true;
    }

    private static double CalcBashHeight(PlayerEntity player) {
        return StaminaConfig.BASH_HEIGHT;
    }

    private static float CalcBashForce(PlayerEntity player) {
        return StaminaConfig.BASH_FORCE;
    }

    private static float CalcBashCost(PlayerEntity player) {
        // Calculate dash stamina cost
        return StaminaConfig.BASH_COST;
    }


    private static int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
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

            var playerForward = Vec3d.fromPolar(0, player.getYaw());
            var playerUp = new Vec3d(0, 1, 0);
            var playerRight = playerForward.crossProduct(playerUp);
            var pos = player.getPos();

            var boxLowerLeft = pos;
            boxLowerLeft = boxLowerLeft.add(playerRight.multiply(-StaminaConfig.BASH_HITBOX_WIDTH));
            var boxUpperRight = pos;
            boxUpperRight = boxUpperRight
                    .add(playerForward.multiply(StaminaConfig.BASH_HITBOX_DEPTH))
                    .add(playerRight.multiply(StaminaConfig.BASH_HITBOX_WIDTH))
                    .add(playerUp.multiply(StaminaConfig.BASH_HITBOX_HEIGHT));

            var hitBox = new Box(boxLowerLeft, boxUpperRight);

            var entities = player.getEntityWorld().getOtherEntities(player, hitBox);

            var livingEntityIds = new ArrayList<Integer>();
            for (var entity : entities) {
                if (entity instanceof LivingEntity lEntity)
                {
                    livingEntityIds.add(lEntity.getId());
                }
            }

            var packet = PacketByteBufs.create();
            packet.writeIntArray(toIntArray(livingEntityIds));
            packet.writeDouble(playerForward.x);
            packet.writeDouble(playerForward.z);
            packet.writeFloat(StaminaConfig.BASH_KNOCKBACK);
            packet.writeFloat(StaminaConfig.BASH_KNOCKBACK_HEIGHT);

            ClientPlayNetworking.send(NCENetworkingConstants.BASH_CHANNEL, packet);

            return ActionResult.SUCCESS;
        }));
    }


}
