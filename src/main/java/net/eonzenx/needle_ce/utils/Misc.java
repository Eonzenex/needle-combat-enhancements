package net.eonzenx.needle_ce.utils;

import net.minecraft.sound.SoundEvent;

public class Misc {
    public static float randomInRange(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }
}
