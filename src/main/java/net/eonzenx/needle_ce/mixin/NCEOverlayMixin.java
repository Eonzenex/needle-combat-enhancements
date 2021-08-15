package net.eonzenx.needle_ce.mixin;

import net.eonzenx.needle_ce.client.gui.NCEStaminaOverlay;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class NCEOverlayMixin
{
    private NCEStaminaOverlay staminaOverlay;


    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    public void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        staminaOverlay = staminaOverlay == null
                ? new NCEStaminaOverlay()
                : staminaOverlay;

        staminaOverlay.render(matrices);
    }
}
