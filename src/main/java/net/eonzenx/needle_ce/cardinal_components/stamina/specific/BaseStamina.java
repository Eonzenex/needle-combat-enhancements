package net.eonzenx.needle_ce.cardinal_components.stamina.specific;

import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedFullStamina;
import net.minecraft.entity.LivingEntity;

public class BaseStamina extends SyncedFullStamina
{
    protected LivingEntity owner;

    public BaseStamina(LivingEntity owner) {
        super(owner);
        this.owner = owner;
    }
}
