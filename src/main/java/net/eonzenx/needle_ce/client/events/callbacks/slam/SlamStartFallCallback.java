package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SlamStartFallCallback
{
    Event<SlamStartFallCallback> EVENT = EventFactory.createArrayBacked(
            SlamStartFallCallback.class,
            (listeners) -> (player) -> {
                for (SlamStartFallCallback listener : listeners) {
                    ActionResult result = listener.startFall(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult startFall(PlayerEntity player);
}
