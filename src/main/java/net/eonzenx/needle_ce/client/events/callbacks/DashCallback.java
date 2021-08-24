package net.eonzenx.needle_ce.client.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface DashCallback
{
    Event<DashCallback> EVENT = EventFactory.createArrayBacked(
            DashCallback.class,
            (listeners) -> (livingEntity) -> {
                for (DashCallback listener : listeners) {
                    ActionResult result = listener.dash(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult dash(LivingEntity livingEntity);
}
