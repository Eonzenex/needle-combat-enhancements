package net.eonzenx.needle_ce.client.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface BashCallback
{
    Event<BashCallback> EVENT = EventFactory.createArrayBacked(
            BashCallback.class,
            (listeners) -> (livingEntity) -> {
                for (BashCallback listener : listeners) {
                    ActionResult result = listener.bash(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult bash(LivingEntity livingEntity);
}
