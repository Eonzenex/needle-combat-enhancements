package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SyncedStaminaComponent implements StaminaComponent, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private float stamina;

    private boolean regenLocked;
    private boolean regenBlocked;
    private float regenBlockTime;

    private boolean manoeuvreLocked;
    private boolean manoeuvreBlocked;
    private float manoeuvreBlockTime;


    @Override
    public float get() { return stamina; }

    @Override
    public boolean canExecuteManoeuvre(float cost) {
        return !manoeuvreLocked && cost <= stamina;
    }

    @Override
    public boolean commitManoeuvre(float cost) {
        if (canExecuteManoeuvre(cost)) {
            reduceStamina(cost);
            blockRegen(StaminaConfig.MANOEUVRE_BLOCK_REGEN_TIME);
            return true;
        }

        return false;
    }

    @Override
    public void reduceStamina(float amount) {
        if (stamina - amount == 0) stamina = 0;
        else stamina -= amount;
    }

    private void regenerate(PlayerEntity player, float deltaTime) {
        var regenAmount = StaminaConfig.BASE_REGEN_RATE;

        stamina += regenAmount * deltaTime;
    }

    @Override
    public void tryRegenerate(PlayerEntity player, float deltaTime) {
        if (regenLocked) return;
        if (regenBlocked) return;

        if (regenBlockTime == 0f) {
            regenerate(player, deltaTime);
        }
    }

    private void blockRegenTick(float deltaTime) {
        if (!regenBlocked) return;

        if (regenBlockTime - deltaTime < 0f) {
            regenBlockTime = 0f;
            regenBlocked = false;
        }
        else regenBlockTime -= deltaTime;
    }

    private void blockManoeuvreTick(float deltaTime) {
        if (!manoeuvreBlocked) return;

        if (manoeuvreBlockTime - deltaTime < 0f) {
            manoeuvreBlockTime = 0f;
            manoeuvreBlocked = false;
        }
        else manoeuvreBlockTime -= deltaTime;
    }

    @Override
    public void tick(PlayerEntity player, float deltaTime) {
        blockRegenTick(deltaTime);
        blockManoeuvreTick(deltaTime);

        tryRegenerate(player, deltaTime);
    }


    @Override
    public void lockRegen() { regenLocked = true; }

    @Override
    public void unlockRegen() { regenLocked = false; }

    @Override
    public void blockRegen(float time) {
        regenBlockTime = time;
        regenBlocked = true;
    }


    @Override
    public void lockManoeuvre() { manoeuvreLocked = true; }

    @Override
    public void unlockManoeuvre() { manoeuvreLocked = false; }

    @Override
    public void blockManoeuvre(float time) {
        manoeuvreBlockTime = time;
        manoeuvreBlocked = true;
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        stamina = tag.getFloat("stamina");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("stamina", stamina);
    }
}
