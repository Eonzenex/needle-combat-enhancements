package net.eonzenx.needle_ce;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.registry_handlers.ItemRegistryHandler;
import net.fabricmc.api.ModInitializer;

public class NCE implements ModInitializer
{
	public static final String MOD_ID = "needle_ce";

	@Override
	public void onInitialize() {
		System.out.println("Needle - Combat Enhancements: Initializing");

		EnchantmentRegistryHandler.init();
		ItemRegistryHandler.init();
	}
}
