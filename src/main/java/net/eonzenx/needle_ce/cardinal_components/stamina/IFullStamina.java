package net.eonzenx.needle_ce.cardinal_components.stamina;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;

public interface IFullStamina extends Component, BaseStamina
{
    static <T> IFullStamina get(T provider) {
        return CComponentHandler.STAMINA.get(provider);
    }

    float getStamina();

    boolean canExecuteManoeuvre(float cost);
    boolean commitManoeuvre(float cost);

    void reduceStamina(float amount, boolean sync);

    void regenerate(float deltaTime, boolean sync);
    void tryRegenerate(float deltaTime);

    void tickBlockRegen(float deltaTime, boolean sync);
    void tickManoeuvreRegen(float deltaTime, boolean sync);
    void tickAnticipateSlam(float deltaTime, boolean sync);
    void tick(float deltaTime);

    // Block
    void lockRegen();
    void unlockRegen();
    void blockRegen(float time, boolean sync);

    void lockManoeuvre();
    void unlockManoeuvre();
    void blockManoeuvre(float time, boolean sync);


    // Slam
    boolean isAnticipatingSlam();
    boolean isSlamming();
    void startAnticipatingSlam(float newTimer, boolean sync);
    void startFastFallSlam(boolean sync);
    void completeSlam();
    void cancelSlam();
}
