package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartFallCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

// TODO: Investigate ServerTickingComponent
public class SyncedStaminaComponent implements StaminaComponent, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private final Entity provider;
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

    // Dominoes
    private LivingEntity dominoer;


    public SyncedStaminaComponent(Entity provider) {
        this.provider = provider;
    }


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



    // Slam
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
    public void readFromNbt(NbtCompound tag) {
        stamina = tag.getFloat("stamina");

        regenLocked = tag.getBoolean("regen_locked");
        regenBlocked = tag.getBoolean("regen_blocked");
        regenBlockTime = tag.getFloat("regen_block_time");

        manoeuvreLocked = tag.getBoolean("manoeuvre_locked");
        manoeuvreBlocked = tag.getBoolean("manoeuvre_blocked");
        manoeuvreBlockTime = tag.getFloat("manoeuvre_block_time");

        isAnticipatingSlam = tag.getBoolean("is_anticipating_slam");
        isSlamming = tag.getBoolean("is_slamming");
        anticipateSlamTimer = tag.getFloat("anticipate_slam_timer");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("stamina", stamina);

        tag.putBoolean("regen_locked", regenLocked);
        tag.putBoolean("regen_blocked", regenBlocked);
        tag.putFloat("regen_block_time", regenBlockTime);

        tag.putBoolean("manoeuvre_locked", manoeuvreLocked);
        tag.putBoolean("manoeuvre_blocked", manoeuvreBlocked);
        tag.putFloat("manoeuvre_block_time", manoeuvreBlockTime);

        tag.putBoolean("is_anticipating_slam", isAnticipatingSlam);
        tag.putBoolean("is_slamming", isSlamming);
        tag.putFloat("anticipate_slam_timer", anticipateSlamTimer);
    }
}
