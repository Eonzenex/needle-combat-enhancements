package net.eonzenx.needle_ce.cardinal_components.slam.specific;

import net.eonzenx.needle_ce.cardinal_components.slam.SyncedSlam;
import net.minecraft.entity.LivingEntity;

public class EntitySlam extends SyncedSlam
{
    protected LivingEntity owner;

    public EntitySlam(LivingEntity owner) { this.owner = owner; }
}
