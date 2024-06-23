package com.oliviathevampire.vainglory.init;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class VGLoot {
    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin() && BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.equals(key)) {
                tableBuilder.modifyPools(poolBuilder ->  {
                    poolBuilder.add(LootItem.lootTableItem(VGItems.LANCE));
                });
            }
        });
    }
}
