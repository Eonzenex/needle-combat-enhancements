package net.eonzenx.needle_ce;

import net.eonzenx.needle_ce.registry_handlers.EnchantmentRegistryHandler;
import net.eonzenx.needle_ce.registry_handlers.ItemRegistryHandler;
import net.eonzenx.needle_ce.registry_handlers.PotionRegistryHandler;
import net.eonzenx.needle_ce.registry_handlers.StatusEffectRegistryHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class NCE implements ModInitializer
{
	public static final String MOD_ID = "needle_ce";


	@Override
	public void onInitialize() {
		System.out.println("Needle - Combat Enhancements: Initializing");

		EnchantmentRegistryHandler.init();
		ItemRegistryHandler.init();
		StatusEffectRegistryHandler.init();
		PotionRegistryHandler.init();
	}

	public static Identifier newId(String id) {
		return new Identifier(MOD_ID, id);
	}
}
