package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VGItemTags {
	public static final TagKey<Item> LANCE_ENCHANTABLE = bind("enchantable/lance");

	public static void init() {}

	private static TagKey<Item> bind(String name) {
		return TagKey.create(Registries.ITEM, Vainglory.id(name));
	}
}
