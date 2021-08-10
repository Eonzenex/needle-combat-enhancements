package net.eonzenx.needle_ce.client.key_bindings;

import net.eonzenx.needle_ce.NCE;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings
{
    public static final KeyBinding DASH = new KeyBinding(
        "key." + NCE.MOD_ID + ".dash",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_ALT,
        "category." + NCE.MOD_ID + ".key_bindings"
    );
}
