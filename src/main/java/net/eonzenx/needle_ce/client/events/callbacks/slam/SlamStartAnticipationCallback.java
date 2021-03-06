package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface SlamStartAnticipationCallback
{
    Event<SlamStartAnticipationCallback> EVENT = EventFactory.createArrayBacked(
            SlamStartAnticipationCallback.class,
            (listeners) -> (livingEntity) -> {
                for (SlamStartAnticipationCallback listener : listeners) {
                    ActionResult result = listener.startAnticipation(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult startAnticipation(LivingEntity livingEntity);
}
