package net.eonzenx.needle_ce.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;

public class NCEServerInitializer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer() {
        // TODO: This doesn't run on a local hosted game, figure out why
        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_CHANNEL,
                (server, player, handler, buf, responseSender) -> {
                    var livingEntityIds = buf.readIntArray();
                    var xDir = buf.readDouble();
                    var zDir = buf.readDouble();
                    var bashForce = buf.readFloat();

                    for (var livingEntityId: livingEntityIds) {
                        if (server.getOverworld().getEntityById(livingEntityId) instanceof LivingEntity livingEntity) {
                            var distance = (player.getPos().distanceTo(livingEntity.getPos()));
                            if (distance > 3) continue;

                            livingEntity.takeKnockback(bashForce, xDir, zDir);
                        }
                    }

                    System.out.println("Bashed!");
                }
        );

        System.out.println("Needle - Combat Enhancements: Server init complete");
    }
}
