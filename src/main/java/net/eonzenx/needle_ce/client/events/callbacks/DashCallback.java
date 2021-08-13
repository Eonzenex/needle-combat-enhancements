package net.eonzenx.needle_ce.client.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface DashCallback
{
    Event<DashCallback> EVENT = EventFactory.createArrayBacked(
            DashCallback.class,
            (listeners) -> (player) -> {
                for (DashCallback listener : listeners) {
                    ActionResult result = listener.dash(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult dash(PlayerEntity player);
}
