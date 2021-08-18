package net.eonzenx.needle_ce.cardinal_components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.cardinal_components.slam.SlamComponent;
import net.eonzenx.needle_ce.cardinal_components.slam.specific.PlayerSlam;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.stamina.specific.PlayerStamina;
import net.minecraft.util.Identifier;

public class CComponentHandler implements EntityComponentInitializer
{
    // retrieving a type for my component or for a required dependency
    public static final ComponentKey<StaminaComponent> STAMINA = ComponentRegistry.getOrCreate(new Identifier(NCE.MOD_ID, "stamina"), StaminaComponent.class);
    public static final ComponentKey<SlamComponent> SLAM = ComponentRegistry.getOrCreate(new Identifier(NCE.MOD_ID, "slam"), SlamComponent.class);


    private void RegisterForEntities(EntityComponentFactoryRegistry registry) {
        // Register entity components
    }

    private void RegisterForPlayers(EntityComponentFactoryRegistry registry) {
        // Register player components
        registry.registerForPlayers(STAMINA, PlayerStamina::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(SLAM, PlayerSlam::new, RespawnCopyStrategy.ALWAYS_COPY);
    }


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        RegisterForEntities(registry);
        RegisterForPlayers(registry);
    }
}
