package com.oliviathevampire.vainglory;

import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.init.VGItemTags;
import com.oliviathevampire.vainglory.init.VGItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class VaingloryDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(VGTranslationProvider::new);
		pack.addProvider(VGItemTagsProvider::new);
	}

	private static class VGTranslationProvider extends FabricLanguageProvider {

		protected VGTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add(VGItems.LANCE, "Lance");
			translationBuilder.add(VGItems.AXE_OF_CLEAVING, "Axe of Cleaving");
			translationBuilder.add(VGItems.STAFF, "Staff");
			translationBuilder.addEnchantment(VGEnchantments.SKEWERING, "Skewering");
			translationBuilder.addEnchantment(VGEnchantments.CAVALIER, "Cavalier");
			translationBuilder.addEnchantment(VGEnchantments.INTREPID, "Intrepid");
			translationBuilder.addEnchantment(VGEnchantments.WIND_RIDER, "Wind Rider");
			translationBuilder.addEnchantment(VGEnchantments.EXCAVATOR, "Excavator");
			translationBuilder.addEnchantment(VGEnchantments.VAINGLORY, "Vainglory");
			translationBuilder.addEnchantment(VGEnchantments.GALE_FORCE, "Gale Force");
			translationBuilder.addEnchantment(VGEnchantments.BROADSIDE, "Broadside");
		}
	}

	private static class VGItemTagsProvider extends FabricTagProvider.ItemTagProvider {
		public VGItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			this.tag(ItemTags.SWORD_ENCHANTABLE).add(reverseLookup(VGItems.LANCE), reverseLookup(VGItems.AXE_OF_CLEAVING));
			this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(reverseLookup(VGItems.LANCE), reverseLookup(VGItems.AXE_OF_CLEAVING), reverseLookup(VGItems.STAFF));
			this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(reverseLookup(VGItems.LANCE), reverseLookup(VGItems.AXE_OF_CLEAVING), reverseLookup(VGItems.STAFF));
			this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(reverseLookup(VGItems.LANCE), reverseLookup(VGItems.AXE_OF_CLEAVING));
			this.tag(VGItemTags.LANCE_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(VGItemTags.AXE_OF_CLEAVING_ENCHANTABLE).add(reverseLookup(VGItems.AXE_OF_CLEAVING));
		}
	}
}
