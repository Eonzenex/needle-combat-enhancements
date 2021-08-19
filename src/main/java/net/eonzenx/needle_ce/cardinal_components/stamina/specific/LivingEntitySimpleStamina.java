package net.eonzenx.needle_ce.cardinal_components.stamina.specific;

import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedSimpleStaminaComponent;
import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedStaminaComponent;
import net.minecraft.entity.LivingEntity;

public class LivingEntitySimpleStamina extends SyncedSimpleStaminaComponent
{
    protected LivingEntity owner;

    public LivingEntitySimpleStamina(LivingEntity owner) {
        super(owner);
        this.owner = owner;
    }
}
