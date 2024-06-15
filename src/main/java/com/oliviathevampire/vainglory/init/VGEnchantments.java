package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class VGEnchantments {
	public static final ResourceKey<Enchantment> SKEWERING  = key("skewering");
	public static final ResourceKey<Enchantment> CAVALIER = key("cavalier");
	public static final ResourceKey<Enchantment> INTREPID = key("intrepid");
	public static final ResourceKey<Enchantment> WIND_RIDER = key("wind_rider");
	public static final ResourceKey<Enchantment> EXCAVATOR = key("excavator");
	public static final ResourceKey<Enchantment> VAINGLORY = key("vainglory");
	public static final ResourceKey<Enchantment> ORTHOGONAL_KNOCKBACK = key("orthogonal_knockback");
	public static final ResourceKey<Enchantment> EXPLOSIVE_DASH = key("explosive_dash");
	public static final ResourceKey<Enchantment> EXTENDED_REACH = key("extended_reach");

	private static ResourceKey<Enchantment> key(String string) {
		return ResourceKey.create(Registries.ENCHANTMENT, Vainglory.id(string));
	}
}
