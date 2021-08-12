package net.eonzenx.needle_ce.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NCEServerInitializer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer() {
        // TODO: This doesn't run on a local hosted game, figure out why
        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_CHANNEL,
                NCEBashServerLogic::execute
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_CHANNEL,
                NCESlamServerLogic::execute
        );

        System.out.println("Needle - Combat Enhancements: Server init complete");
    }
}
