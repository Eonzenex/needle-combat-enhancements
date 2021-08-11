package net.eonzenx.needle_ce.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Misc {
    public static float randomInRange(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }

    public static Vec3d GetPlayerForward(PlayerEntity player) {
        return Vec3d.fromPolar(0, player.getYaw());
    }

    public static List<Integer> GetLivingEntityIds(PlayerEntity player, Box hitbox) {
        var entities = player.getEntityWorld().getOtherEntities(player, hitbox);
        var livingEntityIds = new ArrayList<Integer>();
        for (var entity: entities) {
            if (entity instanceof LivingEntity lEntity)
            {
                livingEntityIds.add(lEntity.getId());
            }
        }

        return livingEntityIds;
    }
}
