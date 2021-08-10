package net.eonzenx.needle_ce.cardinal_components.stamina.specific;

import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedStaminaComponent;
import net.minecraft.entity.LivingEntity;

public class EntityStamina extends SyncedStaminaComponent
{
    protected LivingEntity owner;

    public EntityStamina(LivingEntity owner) { this.owner = owner; }
}
