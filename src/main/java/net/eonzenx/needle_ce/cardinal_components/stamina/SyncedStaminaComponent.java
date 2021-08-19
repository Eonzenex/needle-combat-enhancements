package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartFallCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SyncedStaminaComponent implements StaminaComponent, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private float stamina;

    // Block
    private boolean regenLocked;
    private boolean regenBlocked;
    private float regenBlockTime;

    private boolean manoeuvreLocked;
    private boolean manoeuvreBlocked;
    private float manoeuvreBlockTime;

    // Slam
    private boolean isAnticipatingSlam;
    private boolean isSlamming;
    private float anticipateSlamTimer;


    @Override
    public float getStamina() { return stamina; }

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
        var staminaRegenEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.STAMINA_RECOVERY, player);
        if (staminaRegenEnchantLvl > 0) {
            regenAmount += staminaRegenEnchantLvl * 0.25f;
        }

        stamina += regenAmount * deltaTime;
    }

    @Override
    public void tryRegenerate(PlayerEntity player, float deltaTime) {
        // Pause blocking is done in the event trigger

        if (regenLocked) return;
        if (regenBlocked) return;

        if (regenBlockTime == 0f && getStamina() < StaminaConfig.MAX(player.experienceLevel)) {
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
        anticipateSlamTick(player, deltaTime);

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
    public boolean isAnticipatingSlam()  { return isAnticipatingSlam; }

    @Override
    public boolean isSlamming() { return isSlamming; }

    @Override
    public void startAnticipatingSlam(float newTimer) {
        anticipateSlamTimer = newTimer;
        isAnticipatingSlam = true;
    }

    @Override
    public void completeSlam(PlayerEntity player) {
        isSlamming = false;
        SlamContactGroundCallback.EVENT.invoker().hitGround(player);
    }

    @Override
    public void anticipateSlamTick(PlayerEntity player, float deltaTime) {
        if (!isAnticipatingSlam) return;

        if (anticipateSlamTimer - deltaTime < 0f) {
            anticipateSlamTimer = 0f;
            isAnticipatingSlam = false;
            isSlamming = true;

            SlamStartFallCallback.EVENT.invoker().startFall(player);

            return;
        }

        anticipateSlamTimer -= deltaTime;
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
