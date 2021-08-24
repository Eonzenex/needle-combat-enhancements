package net.eonzenx.needle_ce.cardinal_components.stamina;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.cardinal_components.CComponentHandler;
import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamCancelCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartFastFallCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

// TODO: Investigate ServerTickingComponent
public class SyncedFullStamina implements IFullStamina, AutoSyncedComponent
{
    // Auto sync
    // https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components
    // AutoSync only supports Server-to-Client not Client-to-Server
    // Must use packets and such to allow Client-to-Server sync

    private final LivingEntity provider;
    @Nullable private final PlayerEntity player;
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


    public SyncedFullStamina(LivingEntity provider) {
        this.provider = provider;
        if (provider instanceof PlayerEntity player) {
            this.player = player;
        } else {
            this.player = null;
        }
    }


    @Override
    public float getStamina() { return stamina; }

    @Override
    public boolean canExecuteManoeuvre(float cost) {
        return !manoeuvreLocked && cost <= stamina;
    }

    @Override
    public boolean commitManoeuvre(float cost) {
        if (!canExecuteManoeuvre(cost)) return false;

        reduceStamina(cost, false);
        blockRegen(StaminaConfig.MANOEUVRE_BLOCK_REGEN_TIME, false);

        CComponentHandler.STAMINA.sync(provider);

        return true;
    }


    @Override
    public void reduceStamina(float amount, boolean sync) {
        if (stamina - amount == 0) stamina = 0;
        else stamina -= amount;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }


    // <editor-fold desc="Regenerate Stamina functions">
    @Override
    public void regenerate(float deltaTime, boolean sync) {
        if (regenBlockTime != 0f) return;

        var regenAmount = StaminaConfig.BASE_REGEN_RATE;
        var maxStamina = StaminaConfig.BASE_STAMINA;
        if (player != null) {
            regenAmount = StaminaConfig.REGEN_RATE(player.experienceLevel);
            maxStamina = StaminaConfig.MAX(player.experienceLevel);
        }

        var staminaRegenEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.STAMINA_RECOVERY, player);
        if (staminaRegenEnchantLvl > 0) {
            regenAmount += staminaRegenEnchantLvl * 0.25f;
        }

        var deltaRegen = regenAmount * deltaTime;
        if (stamina + deltaRegen > maxStamina) {
            stamina = maxStamina;
        } else stamina += deltaRegen;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void tryRegenerate(float deltaTime) {
        // Pause blocking is done in the event trigger

        if (regenLocked) return;
        if (regenBlocked) return;

        regenerate(deltaTime, false);
    }
    // </editor-fold>


    // <editor-fold desc="Tick functions">
    @Override
    public void tickBlockRegen(float deltaTime, boolean sync) {
        if (!regenBlocked) return;

         if (regenBlockTime - deltaTime < 0f) {
            regenBlockTime = 0f;
            regenBlocked = false;
            return;
        }

        regenBlockTime -= deltaTime;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void tickManoeuvreRegen(float deltaTime, boolean sync) {
        if (!manoeuvreBlocked) return;

        if (manoeuvreBlockTime - deltaTime < 0f) {
            manoeuvreBlockTime = 0f;
            manoeuvreBlocked = false;
        }
        else manoeuvreBlockTime -= deltaTime;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void tickAnticipateSlam(float deltaTime, boolean sync) {
        if (!isAnticipatingSlam) return;

        if (anticipateSlamTimer - deltaTime < 0f) {
            anticipateSlamTimer = 0f;
            startFastFallSlam(false);

            return;
        } else {
            anticipateSlamTimer -= deltaTime;
        }

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void tick(float deltaTime) {
        tickBlockRegen(deltaTime, false);
        tickManoeuvreRegen(deltaTime, false);
        tickAnticipateSlam(deltaTime, false);

        tryRegenerate(deltaTime);

        CComponentHandler.STAMINA.sync(provider);
    }
    // </editor-fold>


    @Override
    public void lockRegen() {
        regenLocked = true;
        CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void unlockRegen() {
        regenLocked = false;
        CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void blockRegen(float time, boolean sync) {
        regenBlockTime = time;
        regenBlocked = true;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }


    @Override
    public void lockManoeuvre() {
        manoeuvreLocked = true;
        CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void unlockManoeuvre() {
        manoeuvreLocked = false;
        CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void blockManoeuvre(float time, boolean sync) {
        manoeuvreBlockTime = time;
        manoeuvreBlocked = true;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }



    // <editor-fold desc="Slam functions">
    @Override
    public boolean isAnticipatingSlam()  { return isAnticipatingSlam; }

    @Override
    public boolean isSlamming() { return isSlamming; }

    @Override
    public void startAnticipatingSlam(float newTimer, boolean sync) {
        anticipateSlamTimer = newTimer;
        isAnticipatingSlam = true;

        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void startFastFallSlam(boolean sync) {
        isAnticipatingSlam = false;
        isSlamming = true;

        SlamStartFastFallCallback.EVENT.invoker().startFastFall(player);
        if (sync) CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void completeSlam() {
        isSlamming = false;

        CComponentHandler.STAMINA.sync(provider);
    }

    @Override
    public void cancelSlam() {
        isAnticipatingSlam = false;
        isSlamming = false;
        anticipateSlamTimer = 0f;

        SlamCancelCallback.EVENT.invoker().cancel(provider);
        CComponentHandler.STAMINA.sync(provider);
    }
    // </editor-fold>


    // Dominoes
    @Override
    public boolean hasBeenDominoed() { return dominoer != null; }

    @Override
    @Nullable
    public LivingEntity dominoer() { return dominoer; }

    @Override
    public void dominoer(LivingEntity newDominoer) {
        dominoer = newDominoer;
        var velocity = newDominoer.getVelocity();
        provider.takeKnockback(velocity.length(), velocity.x, velocity.z);

        CComponentHandler.STAMINA.sync(provider);
    }


    // <editor-fold desc="Super Overrides">
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

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return provider == player;
    }
    // </editor-fold>
}
