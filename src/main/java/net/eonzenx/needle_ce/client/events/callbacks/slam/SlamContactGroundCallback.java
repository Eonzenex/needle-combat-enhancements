package net.eonzenx.needle_ce.client.events.callbacks.slam;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface SlamContactGroundCallback
{
    Event<SlamContactGroundCallback> EVENT = EventFactory.createArrayBacked(
            SlamContactGroundCallback.class,
            (listeners) -> (livingEntity) -> {
                for (SlamContactGroundCallback listener : listeners) {
                    ActionResult result = listener.hitGround(livingEntity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult hitGround(LivingEntity livingEntity);
}
