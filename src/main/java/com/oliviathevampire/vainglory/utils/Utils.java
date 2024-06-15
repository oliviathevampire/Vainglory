package com.oliviathevampire.vainglory.utils;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Iterator;
import java.util.Set;

public class Utils {
	public static boolean hasEnchantment(ResourceKey<Enchantment> enchantment, ItemStack stack)
	{
		ItemEnchantments component = EnchantmentHelper.getEnchantmentsForCrafting(stack);
		Set<Holder<Enchantment>> set = component.keySet();

		for (Holder<Enchantment> test : set) {
			ResourceKey<Enchantment> key = test.unwrapKey().get();

			if (enchantment == key) {
				return true;
			}
		}

		return false;
	}
}
