package net.eonzenx.needle_ce.registry_handlers;

import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.entities.items.NCEShield;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistryHandler
{
    public static final NCEShield NCE_SHIELD = new NCEShield(new FabricItemSettings()
            .group(ItemGroup.COMBAT));

    public static void init()
    {
        Registry.register(Registry.ITEM, new Identifier(NCE.MOD_ID, "nce_shield"), NCE_SHIELD);
    }
}
