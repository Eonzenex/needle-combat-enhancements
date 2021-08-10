package net.eonzenx.needle_ce.cardinal_components;

public class StaminaConfig
{
    public static final float MIN_LEVEL = 10f;
    public static float MAX(int level) {return MIN_LEVEL + level * 5f;}
    public static final float BASE_REGEN_RATE = 2.5f;
    public static final float MANOEUVRE_BLOCK_REGEN_TIME = 3f;

    // Dash
    public static final float DASH_COST = 5f;
    public static final float DASH_FORCE = 0.6f;
    public static final double DASH_HEIGHT = 0.78d;

    // Bash
    public static final float BASH_COST = 10f;
    public static final float BASH_FORCE = 0.75f;
    public static final double BASH_HEIGHT = 0.1d;

    public static final float BASH_HITBOX_WIDTH = 1.25f;
    public static final float BASH_HITBOX_DEPTH = 2.5f;
    public static final float BASH_HITBOX_HEIGHT = 2f;

    public static final float BASH_KNOCKBACK = 1.5f;
    public static final float BASH_KNOCKBACK_HEIGHT = 0.25f;
}
