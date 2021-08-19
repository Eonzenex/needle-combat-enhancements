package net.eonzenx.needle_ce.cardinal_components.stamina;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface SimpleStaminaComponent extends Component {

    static <T> SimpleStaminaComponent get(T provider) { return CComponentHandler.SIMPLE_STAMINA.get(provider); }

    // Dominoes
    boolean hasBeenDominoed();
    LivingEntity dominoer();
    void dominoer(LivingEntity entity);
}
