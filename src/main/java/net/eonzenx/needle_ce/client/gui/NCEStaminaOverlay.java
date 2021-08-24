package net.eonzenx.needle_ce.client.gui;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.IFullStamina;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class NCEStaminaOverlay extends DrawableHelper
{
    public void render(MatrixStack mat) {
        var mcInstance = MinecraftClient.getInstance();

        var player = mcInstance.player;
        if (player == null) return;
        var staminaComponent = IFullStamina.get(player);

        var mainWindow = mcInstance.getWindow();
        int scaledWidth = mainWindow.getScaledWidth();
        int scaledHeight = mainWindow.getScaledHeight();

        // Draw max stamina GUI
        int maxStaminaWidthOffset = scaledWidth / 2 - 91;
        int maxStaminaHeightOffset = scaledHeight - 56;

        drawTexture(mat,
                maxStaminaWidthOffset, maxStaminaHeightOffset,
                0, 64,
                182, 5
        );

        // Draw current stamina GUI
        var widthOfBar = 183f;
        var currentStamina = staminaComponent.getStamina();
        var maxStamina = StaminaConfig.MAX(player.experienceLevel);
        var scaledCurrentStamina = (currentStamina / maxStamina) * widthOfBar;

        drawTexture(mat,
                maxStaminaWidthOffset, maxStaminaHeightOffset,
                0, 69,
                (int) scaledCurrentStamina, 5
        );
    }
}
