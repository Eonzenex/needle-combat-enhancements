package net.eonzenx.needle_ce.client.events.handlers.slam;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.cardinal_components.stamina.StaminaComponent;
import net.eonzenx.needle_ce.client.events.callbacks.slam.SlamStartAnticipationCallback;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;


public class SlamStartEventHandler
{
    public static float CalcSlamCost(PlayerEntity player) {
        // Calculate bash stamina cost
        float slamCost = StaminaConfig.Slam.COST;
        int slamProfEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.SLAM_PROFICIENCY, player);
        if (slamProfEnchantLvl > 0) {
            slamCost = slamCost - (slamProfEnchantLvl * 0.4f);
        }

        return slamCost;
    }

    public static boolean CanPerformSlam(PlayerEntity player, StaminaComponent stamina) {
        float bashCost = CalcSlamCost(player);
        return player.isCreative() || stamina.canExecuteManoeuvre(bashCost);
    }


    public static void init()
    {
        SlamStartAnticipationCallback.EVENT.register(((player) -> {
            // Get the stamina component from the player.
            StaminaComponent stamina = StaminaComponent.get(player);
            if (!CanPerformSlam(player, stamina)) return ActionResult.FAIL;

            // Commit slam
            if (!player.isCreative()) {
                if (!stamina.commitManoeuvre(CalcSlamCost(player))) return ActionResult.FAIL;
            }

            var staminaComponent = StaminaComponent.get(player);
            staminaComponent.startAnticipatingSlam(StaminaConfig.Slam.ANTICIPATION_TIME);

            var yCap = 0.1f;
            var playerVelocity = player.getVelocity();
            if (playerVelocity.y > yCap) player.setVelocity(playerVelocity.x, yCap, playerVelocity.z);
            else if (playerVelocity.y < -yCap) player.setVelocity(playerVelocity.x, -yCap, playerVelocity.z);

            player.setNoGravity(true);

            return ActionResult.SUCCESS;
        }));
    }
}
