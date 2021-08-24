package net.eonzenx.needle_ce.cardinal_components.stamina.specific;

import net.eonzenx.needle_ce.cardinal_components.stamina.SyncedLivingEntityStamina;
import net.minecraft.entity.LivingEntity;

public class LivingEntitySimpleStamina extends SyncedLivingEntityStamina
{
    protected LivingEntity owner;

    public LivingEntitySimpleStamina(LivingEntity owner) {
        super(owner);
        this.owner = owner;
    }
}
