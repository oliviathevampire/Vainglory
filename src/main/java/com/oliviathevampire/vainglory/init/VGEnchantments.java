package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class VGEnchantments {
	//Non-Treasure
	public static final ResourceKey<Enchantment> SKEWERING  = key("skewering");
	public static final ResourceKey<Enchantment> CAVALIER = key("cavalier");
	public static final ResourceKey<Enchantment> INTREPID = key("intrepid");
	public static final ResourceKey<Enchantment> GALE_FORCE = key("gale_force");
	public static final ResourceKey<Enchantment> BROADSIDE = key("broadside");
	public static final ResourceKey<Enchantment> SUNDERING = key("sundering");
	//Treasure
	public static final ResourceKey<Enchantment> WIND_RIDER = key("wind_rider");
	public static final ResourceKey<Enchantment> EXCAVATOR = key("excavator");
	public static final ResourceKey<Enchantment> VAINGLORY = key("vainglory");
	public static final ResourceKey<Enchantment> RENDING = key("rending");
	public static final ResourceKey<Enchantment> HOT_STREAK = key("hot_streak");

	public static final ResourceKey<Enchantment> BREACH = key("lance_breach");
	public static final ResourceKey<Enchantment> QUICK_CHARGE = key("lance_quick_charge");

	// Curses
	public static final ResourceKey<Enchantment> CURSE_OF_BLAZING = key("curse_of_blazing");

	private static ResourceKey<Enchantment> key(String string) {
		return ResourceKey.create(Registries.ENCHANTMENT, Vainglory.id(string));
	}
}