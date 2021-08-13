package net.eonzenx.needle_ce.cardinal_components;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StaminaConfig
{
    public static final float MIN_LEVEL = 10f;
    public static float MAX(int level) {return MIN_LEVEL + level * 5f;}
    public static final float BASE_REGEN_RATE = 2.5f;
    public static final float MANOEUVRE_BLOCK_REGEN_TIME = 3f;

    // Dash
    public static final class Dash {
        public static final float COST = 5f;
        public static final float FORCE = 0.6f;
        public static final double HEIGHT = 0.33d;

        public static final SoundEvent[] SFX = {
                SoundEvents.ENTITY_SPLASH_POTION_THROW
        };
    }

    // Bash
    public static final class Bash {
        public static final float COST = 10f;
        public static final float FORCE = 1f;
        public static final double HEIGHT = 0.1d;

        public static final class Hitbox {
            public static final float WIDTH = 1.25f;
            public static final float DEPTH = 3f;
            public static final float HEIGHT = 2f;
        }

        public static final class Knockback {
            public static final float FORCE = 1.5f;
            public static final float HEIGHT = 0.45f;
        }

        public static final SoundEvent[] HIT_SFX = {
                SoundEvents.ITEM_SHIELD_BLOCK
        };

        public static final SoundEvent[] MISS_SFX = {
                SoundEvents.ENTITY_SPLASH_POTION_THROW
        };
    }

    // Slam
    public static final class Slam {
        public static final float MIN_HEIGHT = 2.2f;
        public static final float MAX_ANGLE = 55f;
        public static final float ANTICIPATION_TIME = 0.4f;
        public static final float COST = 10f;

        public static final class Fall {
            public static final float FORCE = 2f;
        }

        public static final class Hitbox {
            public static final float WIDTH = 2f;
            public static final float HEIGHT = 1.5f;
            public static final float MAX_DISTANCE = 2.82f;
        }

        public static final class Impact {
            public static final float FORCE = 2f;
            public static final float HEIGHT = 0.45f;
            public static final float DAMAGE = 0.5f;
        }

        public static final SoundEvent[] HIT_SFX = {
                SoundEvents.ITEM_SHIELD_BLOCK
        };

        public static final SoundEvent[] MISS_SFX = {
                SoundEvents.BLOCK_CALCITE_HIT
        };
    }
}
