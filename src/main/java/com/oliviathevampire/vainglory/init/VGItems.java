package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.items.LanceItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class VGItems {
	public static final Item LANCE = register("lance", new LanceItem(new Item.Properties().rarity(Rarity.EPIC)));

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
			entries.addAfter(Items.MACE, LANCE);
		});
	}

	private static Item register(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, Vainglory.id(name), item);
	}
}
