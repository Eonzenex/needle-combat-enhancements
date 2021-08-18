package net.eonzenx.needle_ce.mixin;

import net.eonzenx.needle_ce.utils.mixin.IGetTicksPerSec;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class NCEMinecraftServerMixin implements IGetTicksPerSec
{
    @Shadow @Final private static long MILLISECONDS_PER_TICK;

    @Override
    public float GetTicksPerSec() {
        return (float) (1000 / MILLISECONDS_PER_TICK);
    }
}
