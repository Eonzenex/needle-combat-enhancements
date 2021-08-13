package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SlamStartAnticipationCallback
{
    Event<SlamStartAnticipationCallback> EVENT = EventFactory.createArrayBacked(
            SlamStartAnticipationCallback.class,
            (listeners) -> (player) -> {
                for (SlamStartAnticipationCallback listener : listeners) {
                    ActionResult result = listener.startAnticipation(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult startAnticipation(PlayerEntity player);
}
