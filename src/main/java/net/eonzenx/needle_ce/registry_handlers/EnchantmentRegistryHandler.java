package net.eonzenx.needle_ce.registry_handlers;

import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.entities.enchantments.bash.BashProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.bash.HeavyWeightEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.DashProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.GaleBurstEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.QuicksilverEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.VaultingEnchant;
import net.eonzenx.needle_ce.entities.enchantments.slam.SlamProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.stamina.StaminaRecoveryEnchant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentRegistryHandler
{
    // Stamina enchants
    public static final StaminaRecoveryEnchant STAMINA_RECOVERY = new StaminaRecoveryEnchant();

    // Dash enchants
    public static final QuicksilverEnchant QUICKSILVER = new QuicksilverEnchant();
    public static final VaultingEnchant VAULTING = new VaultingEnchant();
    public static final GaleBurstEnchant GALE_BURST = new GaleBurstEnchant();
    public static final DashProficiencyEnchant DASH_PROFICIENCY = new DashProficiencyEnchant();

    // Bash enchants
    public static final HeavyWeightEnchant HEAVY_WEIGHT = new HeavyWeightEnchant();
    public static final BashProficiencyEnchant BASH_PROFICIENCY = new BashProficiencyEnchant();

    // Slam enchants
    public static final SlamProficiencyEnchant SLAM_PROFICIENCY = new SlamProficiencyEnchant();


    public static void init() {
        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "stamina_recovery"), STAMINA_RECOVERY);

        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "quicksilver"), QUICKSILVER);
        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "vaulting"), VAULTING);
        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "gale_burst"), GALE_BURST);
        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "dash_proficiency"), DASH_PROFICIENCY);

        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "heavy_weight"), HEAVY_WEIGHT);
        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "bash_proficiency"), BASH_PROFICIENCY);

        Registry.register(Registry.ENCHANTMENT, new Identifier(NCE.MOD_ID, "slam_proficiency"), SLAM_PROFICIENCY);
    }
}
