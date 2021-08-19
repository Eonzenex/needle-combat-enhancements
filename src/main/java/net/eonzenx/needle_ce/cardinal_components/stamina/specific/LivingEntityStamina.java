package net.eonzenx.needle_ce.cardinal_components.stamina.specific;

import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedStaminaComponent;
import net.minecraft.entity.LivingEntity;

public class LivingEntityStamina extends SyncedStaminaComponent
{
    protected LivingEntity owner;

    public LivingEntityStamina(LivingEntity owner) {
        super(owner);
        this.owner = owner;
    }
}
