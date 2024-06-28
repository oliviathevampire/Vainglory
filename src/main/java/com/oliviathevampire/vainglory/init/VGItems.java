package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.items.AxeOfCleavingItem;
import com.oliviathevampire.vainglory.items.LanceItem;
import com.oliviathevampire.vainglory.items.StaffItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class VGItems {
	public static final Item LANCE = register("lance", new LanceItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final Item AXE_OF_CLEAVING = register("axe_of_cleaving", new AxeOfCleavingItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final Item STAFF = register("staff", new StaffItem(new Item.Properties().rarity(Rarity.EPIC)));

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
			entries.addAfter(Items.MACE, LANCE);
			entries.addAfter(LANCE, AXE_OF_CLEAVING);
			entries.addAfter(AXE_OF_CLEAVING, STAFF);
		});
	}

	private static Item register(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, Vainglory.id(name), item);
	}
}
