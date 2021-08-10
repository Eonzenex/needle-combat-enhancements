package net.eonzenx.needle_ce.cardinal_components.stamina;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;
import net.minecraft.entity.player.PlayerEntity;

public interface StaminaComponent extends Component {

    static <T> StaminaComponent get(T provider) { return CComponentHandler.STAMINA.get(provider); }

    float get();

    boolean canExecuteManoeuvre(float cost);
    boolean commitManoeuvre(float cost);
    void reduceStamina(float amount);

    void tick(PlayerEntity player, float deltaTime);
    void tryRegenerate(PlayerEntity player, float deltaTime);

    void lockRegen();
    void unlockRegen();
    void blockRegen(float time);

    void lockManoeuvre();
    void unlockManoeuvre();
    void blockManoeuvre(float time);
}
