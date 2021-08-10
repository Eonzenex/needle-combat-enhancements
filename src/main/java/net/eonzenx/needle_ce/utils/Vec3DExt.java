package net.eonzenx.needle_ce.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Vec3DExt {
    public static Vec3d relativeVectorToWorldSpace(Vec3d direction, float speed, float yawDegrees) {
        double directionLength = direction.lengthSquared();
        if (directionLength < 1.0E-7D) { return Vec3d.ZERO; }

        Vec3d vec3d = (directionLength > 1.0D ? direction.normalize() : direction).multiply(speed);
        float sin = MathHelper.sin(yawDegrees * 0.017453292F);
        float cos = MathHelper.cos(yawDegrees * 0.017453292F);

        double newX = vec3d.x * (double) cos - vec3d.z * (double) sin;
        double newZ = vec3d.z * (double) cos + vec3d.x * (double) sin;
        return new Vec3d(newX, vec3d.y, newZ);
    }

    public static Vec3d relativeVectorToWorldSpace(Vec3d direction, float speed) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return Vec3d.ZERO;

        return relativeVectorToWorldSpace(direction, speed, player.getYaw());
    }
}
