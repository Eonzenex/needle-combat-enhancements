package net.eonzenx.needle_ce.server;

import net.eonzenx.needle_ce.cardinal_components.StaminaConfig;
import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.registry_handlers.StatusEffectRegistryHandler;
import net.eonzenx.needle_ce.utils.mixin.IGetTicksPerSec;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class NCESlamServerLogic
{
    private static float CalcKnockbackForce(PlayerEntity player) {
        // Calculate slam knockback force
        float slamForce = StaminaConfig.Slam.Impact.FORCE;
        int slamForceEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.HEAVY_WEIGHT, player);
        if (slamForceEnchantLvl > 0) {
            slamForce += (slamForceEnchantLvl * 0.4f);
        }

        return slamForce;
    }

    private static float CalcKnockbackHeight(PlayerEntity player) {
        return StaminaConfig.Slam.Impact.HEIGHT;
    }

    private static float CalcDamage(PlayerEntity player) {
        return StaminaConfig.Slam.Impact.DAMAGE;
    }

    private static float CalcStunTime(PlayerEntity player, LivingEntity lEntity) {
        // Calculate stun time
        var stunImmunity = lEntity.getStatusEffect(StatusEffectRegistryHandler.STUN_IMMUNITY);
        if (stunImmunity != null) return 0f;

        var stunTime = StaminaConfig.Slam.Stun.BASE_TIME;
        int impactEnchantLvl = EnchantmentHelper.getEquipmentLevel(EnchantmentRegistryHandler.IMPACT, player);
        if (impactEnchantLvl > 0) {
            stunTime += (impactEnchantLvl * 0.33f);
        }

        var stunResistance = lEntity.getStatusEffect(StatusEffectRegistryHandler.STUN_RESISTANCE);
        if (stunResistance != null) {
            stunTime -= (stunResistance.getAmplifier() * 0.33f);
        }

        return stunTime;
    }


    private static double RandBetween() {
        return (Math.random() - 0.5) * 3;
    }

    private static void SpawnImpactParticles(PlayerEntity player, ParticleEffect type, int count) {
        var pos = player.getPos();

        ((ServerWorld) player.getEntityWorld())
                .spawnParticles(
                        type,
                        pos.x, pos.y, pos.z,
                        count,
                        RandBetween(), (Math.random() / 2) + 0.1, RandBetween(),
                        5f);
    }


    public static void execute(MinecraftServer server, PlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var livingEntityIds = buf.readIntArray();
        var posX = buf.readDouble();
        var posY = buf.readDouble();
        var posZ = buf.readDouble();

        var clientPlayerPos = new Vec3d(posX, posY, posZ);
        var playerPos = player.getPos();
        if (playerPos.distanceTo(clientPlayerPos) < StaminaConfig.Slam.Hitbox.MAX_DISTANCE) {
            playerPos = clientPlayerPos;
        }

        var bashKnockbackForce = CalcKnockbackForce(player);
        var bashKnockbackHeight = CalcKnockbackHeight(player);

        for (var livingEntityId: livingEntityIds) {
            if (server.getOverworld().getEntityById(livingEntityId) instanceof LivingEntity livingEntity) {
                var entityPos = livingEntity.getPos();

                var distance = playerPos.distanceTo(entityPos);
                if (distance > StaminaConfig.Slam.Hitbox.MAX_DISTANCE) continue;

                var knockbackDirection = entityPos
                        .add(playerPos.multiply(-1));
                var finalKnockback = knockbackDirection
                        .multiply(bashKnockbackForce);
                finalKnockback = finalKnockback
                        .add(0, bashKnockbackHeight, 0);

                livingEntity.setVelocity(finalKnockback);
                if (server instanceof IGetTicksPerSec serverTick) {
                    int stunTime = (int) (CalcStunTime(player, livingEntity) * serverTick.GetTicksPerSec());
                    var statusEffectInstance = new StatusEffectInstance(
                            StatusEffectRegistryHandler.STUN, stunTime, 0, false, true);
                    livingEntity.addStatusEffect(statusEffectInstance);
                }

                livingEntity.damage(DamageSource.player(player), CalcDamage(player));
            }
        }

        SpawnImpactParticles(player, new BlockStateParticleEffect(ParticleTypes.BLOCK, player.getBlockStateAtPos()), 200);
    }
}
