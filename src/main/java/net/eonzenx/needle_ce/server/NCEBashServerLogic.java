package net.eonzenx.needle_ce.server;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Vec3d;

public class NCEBashServerLogic
{
    private static float CalcKnockbackForce(PlayerEntity player) {
        // Calculate bash knockback force
        float bashForce = StaminaConfig.Bash.Knockback.FORCE;
        int bashKnockForceEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.INERTIA, player);
        if (bashKnockForceEnchantLvl > 0) {
            bashForce = bashForce + (bashKnockForceEnchantLvl * 0.125f);
        }

        return bashForce;
    }

    private static float CalcKnockbackDamage(PlayerEntity player) {
        // Calculate bash damage, if any
        float bashDamage = StaminaConfig.Bash.Knockback.DAMAGE;
        int bashDamageEnchantLvl = EnchantmentHelper.getEquipmentLevel(Enchantments.THORNS, player);
        if (bashDamageEnchantLvl > 0) {
            bashDamage = bashDamage + (bashDamageEnchantLvl * 0.5f);
        }

        return bashDamage;
    }

    private static float CalcKnockbackHeight(PlayerEntity player) {
        // Calculate bash height
        float bashHeight = StaminaConfig.Bash.Knockback.HEIGHT;
        int bashHeightEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.SPRING_BOARD, player);
        if (bashHeightEnchantLvl > 0) {
            bashHeight = bashHeight + (bashHeightEnchantLvl * 0.2f);
        }

        return bashHeight;
    }


    // TODO: Send the request just before performing it, then rollback the player if they do not meet the requirements
    public static void execute(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var livingEntityIds = buf.readIntArray();
        var xDir = buf.readDouble();
        var zDir = buf.readDouble();

        var bashKnockbackForce = CalcKnockbackForce(player);
        var bashKnockbackHeight = CalcKnockbackHeight(player);
        var bashDamage = CalcKnockbackDamage(player);

        for (var livingEntityId: livingEntityIds) {
            if (server.getOverworld().getEntityById(livingEntityId) instanceof LivingEntity livingEntity) {
                var distance = player.getPos().distanceTo(livingEntity.getPos());
                if (distance > StaminaConfig.Bash.Hitbox.DEPTH) continue;

                var finalBash = new Vec3d(xDir, 0, zDir).multiply(bashKnockbackForce);
                finalBash = finalBash.add(0, bashKnockbackHeight, 0);

                livingEntity.setVelocity(finalBash);
                if (bashDamage > 0f) livingEntity.damage(DamageSource.player(player), bashDamage);
            }
        }
    }
}
