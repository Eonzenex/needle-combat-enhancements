package net.eonzenx.needle_ce.client;

import net.eonzenx.needle_ce.registry_handlers.EventRegistryHandler;
import net.eonzenx.needle_ce.server.NCEBashServerLogic;
import net.eonzenx.needle_ce.server.NCENetworkingConstants;
import net.eonzenx.needle_ce.server.NCESlamServerLogic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NCEClientInitializer implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        EventRegistryHandler.init();

        // Server stuff
        // TODO: Should probably NOT be on the ClientInit
        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.BASH_CHANNEL,
                NCEBashServerLogic::execute
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NCENetworkingConstants.SLAM_CHANNEL,
                NCESlamServerLogic::execute
        );

        System.out.println("Needle - Combat Enhancements: Client init complete");
    }

}

