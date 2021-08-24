package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface SlamStartFastFallCallback
{
    Event<SlamStartFastFallCallback> EVENT = EventFactory.createArrayBacked(
            SlamStartFastFallCallback.class,
            (listeners) -> (livingEntity) -> {
                for (SlamStartFastFallCallback listener : listeners) {
                    ActionResult result = listener.startFastFall(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult startFastFall(LivingEntity livingEntity);
}
