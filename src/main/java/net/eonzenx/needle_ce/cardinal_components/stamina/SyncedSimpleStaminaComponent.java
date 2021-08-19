package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartFallCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class SyncedSimpleStaminaComponent implements SimpleStaminaComponent, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private final Entity provider;

    // Dominoes
    private LivingEntity dominoer;


    public SyncedSimpleStaminaComponent(Entity provider) {
        this.provider = provider;
    }


    // Dominoes
    @Override
    public boolean hasBeenDominoed() { return dominoer != null; }

    @Override
    @Nullable
    public LivingEntity dominoer() { return dominoer; }

    @Override
    public void dominoer(LivingEntity newDominoer) {
        dominoer = newDominoer;
        if (provider instanceof LivingEntity lEntity) {
            var velocity = newDominoer.getVelocity();
            lEntity.takeKnockback(velocity.length(), velocity.x, velocity.z);
        }
    }


    // Required
    @Override
    public void readFromNbt(NbtCompound tag) { }

    @Override
    public void writeToNbt(NbtCompound tag) { }
}
