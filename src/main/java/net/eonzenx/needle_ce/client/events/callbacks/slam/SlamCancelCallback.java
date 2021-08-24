package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface SlamCancelCallback
{
    Event<SlamCancelCallback> EVENT = EventFactory.createArrayBacked(
            SlamCancelCallback.class,
            (listeners) -> (livingEntity) -> {
                for (SlamCancelCallback listener : listeners) {
                    ActionResult result = listener.cancel(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult cancel(LivingEntity livingEntity);
}
