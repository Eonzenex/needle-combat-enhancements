package net.eonzenx.needle_ce.cardinal_components.slam;


import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.eonzenx.needle_ce.events.callbacks.slam.SlamContactGroundCallback;
import net.eonzenx.needle_ce.events.callbacks.slam.SlamStartFallCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SyncedSlam implements SlamComponent, AutoSyncedComponent
{
//    Auto sync
//    https://github.com/OnyxStudios/Cardinal-Components-API/wiki/Synchronizing-components

    private boolean isAnticipatingSlam;
    private float timer;
    private boolean isSlamming;


    @Override
    public boolean getIsAnticipatingSlam() { return isAnticipatingSlam; }

    @Override
    public boolean getIsSlamming() { return isSlamming; }

    @Override
    public void set(float newTimer) {
        timer = newTimer;
        isAnticipatingSlam = true;
    }

    @Override
    public void tick(PlayerEntity player, float deltaTime) {
        if (!isAnticipatingSlam) return;

        if (timer - deltaTime < 0f) {
            timer = 0f;
            isAnticipatingSlam = false;
            isSlamming = true;

            SlamStartFallCallback.EVENT.invoker().startFall(player);

            return;
        }

        timer -= deltaTime;
    }

    @Override
    public void completeSlam(PlayerEntity player) {
        isSlamming = false;
        SlamContactGroundCallback.EVENT.invoker().hitGround(player);
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        timer = tag.getFloat("timer");
        isAnticipatingSlam = tag.getBoolean("isAnticipatingSlam");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("timer", timer);
        tag.putBoolean("isAnticipatingSlam", isAnticipatingSlam);
    }
}
