package net.eonzenx.needle_ce.registry_handlers;

import net.eonzenx.needle_ce.NCE;
import net.eonzenx.needle_ce.entities.enchantments.bash.BashProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.bash.InertiaEnchant;
import net.eonzenx.needle_ce.entities.enchantments.bash.SpringboardEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.DashProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.GaleBurstEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.QuicksilverEnchant;
import net.eonzenx.needle_ce.entities.enchantments.dash.VaultingEnchant;
import net.eonzenx.needle_ce.entities.enchantments.slam.SlamProficiencyEnchant;
import net.eonzenx.needle_ce.entities.enchantments.stamina.StaminaRecoveryEnchant;
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
    public static final InertiaEnchant INERTIA = new InertiaEnchant();
    public static final SpringboardEnchant SPRING_BOARD = new SpringboardEnchant();
    public static final BashProficiencyEnchant BASH_PROFICIENCY = new BashProficiencyEnchant();

    // Slam enchants
    public static final SlamProficiencyEnchant SLAM_PROFICIENCY = new SlamProficiencyEnchant();


    public static void init() {
        Registry.register(Registry.ENCHANTMENT, NCE.newId("stamina_recovery"), STAMINA_RECOVERY);

        Registry.register(Registry.ENCHANTMENT, NCE.newId("quicksilver"), QUICKSILVER);
        Registry.register(Registry.ENCHANTMENT, NCE.newId("vaulting"), VAULTING);
        Registry.register(Registry.ENCHANTMENT, NCE.newId("gale_burst"), GALE_BURST);
        Registry.register(Registry.ENCHANTMENT, NCE.newId("dash_proficiency"), DASH_PROFICIENCY);

        Registry.register(Registry.ENCHANTMENT, NCE.newId("inertia"), INERTIA);
        Registry.register(Registry.ENCHANTMENT, NCE.newId("spring_board"), SPRING_BOARD);
        Registry.register(Registry.ENCHANTMENT, NCE.newId("bash_proficiency"), BASH_PROFICIENCY);

        Registry.register(Registry.ENCHANTMENT, NCE.newId("slam_proficiency"), SLAM_PROFICIENCY);
    }
}
