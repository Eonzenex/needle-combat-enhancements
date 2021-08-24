package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.registry_handlers.EventRegistryHandler;
import net.eonzenx.needle_ce.server.NCEBashServerLogic;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.server.NCESlamServerLogic;
import net.eonzenx.needle_ce.server.NCEStaminaServerLogic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NCEClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        EventRegistryHandler.init();

        // Server stuff
        // TODO: Should probably NOT be on the ClientInit
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

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_FAST_FALL_VERIFY_CHANNEL,
                NCEStaminaServerLogic::verifySlamFastFall
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

        System.out.println("Needle - Combat Enhancements: Client init complete");
    }

}

