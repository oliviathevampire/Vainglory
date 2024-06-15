package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

public class VGBlocks {
	public static final Block[] CIRCUS_CLOTH = registerDyeColorBlocks("circus_cloth");

	public static void init() {}

	private static Block register(String name, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, Vainglory.id(name), block);
		Registry.register(BuiltInRegistries.ITEM, Vainglory.id(name), new BlockItem(block, new Item.Properties()));
		return block;
	}

	public static Block[] registerDyeColorBlocks(String baseName) {
		List<Block> blocks = new ArrayList<>();
		for (DyeColor value : DyeColor.values()) {
			blocks.add(register(value.getName() + "_" + baseName, new Block(BlockBehaviour.Properties.ofLegacyCopy(Blocks.WHITE_WOOL))));
		}
		return blocks.toArray(new Block[0]);
	}
}
