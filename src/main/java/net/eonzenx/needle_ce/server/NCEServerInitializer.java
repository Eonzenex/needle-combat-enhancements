package net.eonzenx.needle_ce.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NCEServerInitializer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer() {
        // TODO: This doesn't run on a local hosted game
        // <editor-fold desc="Verify maneuvers">
        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.DASH_VERIFY_CHANNEL,
                NCEStaminaServerLogic::verifyDash
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_VERIFY_CHANNEL,
                NCEStaminaServerLogic::verifyBash
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_VERIFY_CHANNEL,
                NCEStaminaServerLogic::verifySlam
        );
        // </editor-fold>

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_HIT_VERIFY_CHANNEL,
                NCEBashServerLogic::execute
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_HIT_VERIFY_CHANNEL,
                NCESlamServerLogic::execute
        );

        System.out.println("Needle - Combat Enhancements: Server init complete");
    }
}
