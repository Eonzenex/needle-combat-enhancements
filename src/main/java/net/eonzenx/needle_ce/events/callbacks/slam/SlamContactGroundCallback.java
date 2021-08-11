package net.eonzenx.needle_ce.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SlamContactGroundCallback
{
    Event<SlamContactGroundCallback> EVENT = EventFactory.createArrayBacked(
            SlamContactGroundCallback.class,
            (listeners) -> (player) -> {
                for (SlamContactGroundCallback listener : listeners) {
                    ActionResult result = listener.hitGround(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult hitGround(PlayerEntity player);
}
