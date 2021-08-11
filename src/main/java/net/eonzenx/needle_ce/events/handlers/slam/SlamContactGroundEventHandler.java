package net.eonzenx.needle_ce.events.handlers.slam;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.utils.Misc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;


public class SlamContactGroundEventHandler
{
    private static Box CalcBashHitBox(PlayerEntity player) {
        var playerForward = Misc.GetPlayerForward(player);
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


    public static void init()
    {
        SlamContactGroundCallback.EVENT.register(((player) -> {

            var hitBox = CalcBashHitBox(player);
            var livingEntityIds = Misc.GetLivingEntityIds(player, hitBox);

            return ActionResult.SUCCESS;
        }));
    }
}
