package com.oliviathevampire.vainglory;

import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.init.VGItemTags;
import com.oliviathevampire.vainglory.init.VGItems;
import com.oliviathevampire.vainglory.init.VGLoot;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vainglory implements ModInitializer {
	public static final String MOD_ID = "vainglory";

    public static final Logger LOGGER = LoggerFactory.getLogger("Vainglory");

	@Override
	public void onInitialize() {
		LOGGER.info("You're running Vainglory v0.1.0 for 1.21");
		VGItemTags.init();
		VGItems.init();
		VGLoot.init();
		VGEnchantments.init();
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}
}