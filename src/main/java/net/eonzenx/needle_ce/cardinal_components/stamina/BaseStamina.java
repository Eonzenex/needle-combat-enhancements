package net.eonzenx.needle_ce.cardinal_components.stamina;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;
import net.minecraft.entity.LivingEntity;

public interface BaseStamina extends Component {

    static <T> BaseStamina get(T provider) { return CComponentHandler.BASE_STAMINA.get(provider); }

    // Dominoes
    boolean hasBeenDominoed();
    LivingEntity dominoer();
    void dominoer(LivingEntity entity);
}
