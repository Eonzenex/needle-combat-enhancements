package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class SyncedLivingEntityStamina implements BaseStamina, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private final Entity provider;

    // Dominoes
    private LivingEntity dominoer;


    public SyncedLivingEntityStamina(Entity provider) {
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
