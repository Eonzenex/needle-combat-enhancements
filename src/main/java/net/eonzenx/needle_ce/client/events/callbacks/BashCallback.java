package net.eonzenx.needle_ce.client.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface BashCallback
{
    Event<BashCallback> EVENT = EventFactory.createArrayBacked(
            BashCallback.class,
            (listeners) -> (player) -> {
                for (BashCallback listener : listeners) {
                    ActionResult result = listener.bash(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult bash(PlayerEntity player);
}
