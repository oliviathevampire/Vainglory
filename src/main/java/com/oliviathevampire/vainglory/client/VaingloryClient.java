package com.oliviathevampire.vainglory.client;

import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.init.VGItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class VaingloryClient implements ClientModInitializer {

	public static final Map<Item, ModelResourceLocation> BIG_MODELS = Map.of(
			VGItems.LANCE, ModelResourceLocation.inventory(Vainglory.id("lance_big"))
	);

	@Override
	public void onInitializeClient() {}
}