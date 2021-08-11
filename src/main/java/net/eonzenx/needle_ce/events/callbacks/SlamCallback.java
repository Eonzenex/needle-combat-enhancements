package net.eonzenx.needle_ce.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SlamCallback
{
    Event<SlamCallback> EVENT = EventFactory.createArrayBacked(
            SlamCallback.class,
            (listeners) -> (player) -> {
                for (SlamCallback listener : listeners) {
                    ActionResult result = listener.slam(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult slam(PlayerEntity player);
}
