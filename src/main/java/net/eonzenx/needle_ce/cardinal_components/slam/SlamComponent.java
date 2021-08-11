package net.eonzenx.needle_ce.cardinal_components.slam;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;
import net.minecraft.entity.player.PlayerEntity;

public interface SlamComponent extends Component {

    static <T> SlamComponent getIsAnticipatingSlam(T provider) { return CComponentHandler.ANTICIPATING_SLAM.get(provider); }

    boolean getIsAnticipatingSlam();
    boolean getIsSlamming();
    void set(float newTimer);

    void tick(PlayerEntity player, float deltaTime);
    void completeSlam(PlayerEntity player);
}
