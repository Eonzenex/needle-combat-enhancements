package net.eonzenx.needle_ce.server;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Vec3d;

public class NCEBashServer
{
    private static float CalcKnockbackForce(PlayerEntity player) {
        // Calculate bash knockback force
        float bashForce = StaminaConfig.Bash.Knockback.FORCE;
        int bashKnockForceEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.HEAVY_WEIGHT, player);
        if (bashKnockForceEnchantLvl > 0) {
            bashForce = bashForce + (bashKnockForceEnchantLvl * 0.4f);
        }

        return bashForce;
    }

    private static float CalcKnockbackHeight(PlayerEntity player) {
        return StaminaConfig.Bash.Knockback.HEIGHT;
    }

    public static void execute(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var livingEntityIds = buf.readIntArray();
        var xDir = buf.readDouble();
        var zDir = buf.readDouble();

        var bashKnockbackForce = CalcKnockbackForce(player);
        var bashKnockbackHeight = CalcKnockbackHeight(player);

        for (var livingEntityId: livingEntityIds) {
            if (server.getOverworld().getEntityById(livingEntityId) instanceof LivingEntity livingEntity) {
                var distance = player.getPos().distanceTo(livingEntity.getPos());
                if (distance > StaminaConfig.Bash.Hitbox.DEPTH) continue;

                var finalDash = new Vec3d(xDir, 0, zDir).multiply(bashKnockbackForce);
                finalDash = finalDash.add(0, bashKnockbackHeight, 0);

                livingEntity.setVelocity(finalDash);
            }
        }
    }
}
