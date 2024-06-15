package com.oliviathevampire.vainglory.client;

import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.init.VGItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class VaingloryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerItemPredicates();
	}

	private void registerItemPredicates() {
		registerPredicate(VGItems.LANCE, Vainglory.id("charging"), (stack, world, entity, seed) -> {
			return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
		});

		registerPredicate(VGItems.LANCE, Vainglory.id("dashing"), (stack, world, entity, seed) -> {
			return entity != null && entity.isSprinting() && entity.getUseItem() == stack ? 1.0F : 0.0F;
		});

		registerPredicate(VGItems.LANCE, Vainglory.id("in_hand"), (stack, world, entity, seed) -> {
			return entity != null && (entity.getMainHandItem() == stack || entity.getOffhandItem() == stack) ? 1.0F : 0.0F;
		});
	}

	private void registerPredicate(Item item, ResourceLocation id, ClampedItemPropertyFunction property) {
		ItemProperties.register(item, id, property);
	}
}
