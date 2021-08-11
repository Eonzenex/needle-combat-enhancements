package net.eonzenx.needle_ce.events.handlers.slam;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.events.callbacks.slam.SlamStartFallCallback;
import net.minecraft.util.ActionResult;


public class SlamFallEventHandler
{
    public static void init()
    {
        SlamStartFallCallback.EVENT.register(((player) -> {
            player.setVelocity(0, -StaminaConfig.Slam.Fall.FORCE, 0);

            player.setNoGravity(false);

            return ActionResult.SUCCESS;
        }));
    }
}
