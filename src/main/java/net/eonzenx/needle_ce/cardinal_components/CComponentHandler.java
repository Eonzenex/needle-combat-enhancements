package net.eonzenx.needle_ce.cardinal_components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.cardinal_components.stamina.SimpleStaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.stamina.specific.LivingEntitySimpleStamina;
import net.eonzenx.needle_ce.cardinal_components.stamina.specific.PlayerStamina;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class CComponentHandler implements EntityComponentInitializer
{
    // retrieving a type for my component or for a required dependency
    public static final ComponentKey<SimpleStaminaComponent> SIMPLE_STAMINA;
    public static final ComponentKey<StaminaComponent> STAMINA;


    private void RegisterForEntities(EntityComponentFactoryRegistry registry) {
        // Register entity components
        registry.registerFor(LivingEntity.class, SIMPLE_STAMINA, LivingEntitySimpleStamina::new);
    }

    private void RegisterForPlayers(EntityComponentFactoryRegistry registry) {
        // Register player components
        registry.registerForPlayers(STAMINA, PlayerStamina::new, RespawnCopyStrategy.ALWAYS_COPY);
    }


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
    {
        RegisterForEntities(registry);
        RegisterForPlayers(registry);
    }


    static {
        SIMPLE_STAMINA = ComponentRegistry.getOrCreate(new Identifier(NCE.MOD_ID, "simple_stamina"), SimpleStaminaComponent.class);
        STAMINA = ComponentRegistry.getOrCreate(new Identifier(NCE.MOD_ID, "stamina"), StaminaComponent.class);
    }
}
