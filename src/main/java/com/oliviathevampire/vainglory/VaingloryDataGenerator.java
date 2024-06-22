package com.oliviathevampire.vainglory;

import com.oliviathevampire.vainglory.init.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class VaingloryDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(VGBlockStateDefinitionProvider::new);
		pack.addProvider(VGTranslationProvider::new);
		pack.addProvider(VGLootTableProvider::new);
		pack.addProvider(VGRecipeProvider::new);
		pack.addProvider(VGBlockTagsProvider::new);
		pack.addProvider(VGItemTagsProvider::new);
	}

	private static class VGBlockStateDefinitionProvider extends FabricModelProvider {

		public VGBlockStateDefinitionProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
			ModelTemplate modelTemplate = create("orientable", TextureSlot.TOP, TextureSlot.FRONT, TextureSlot.SIDE);
			for (Block block : VGBlocks.CIRCUS_CLOTH) {
				blockStateModelGenerator.createTrivialCube(block);
			}
		}

		private static ModelTemplate create(String blockModelLocation, TextureSlot... requiredSlots) {
			return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + blockModelLocation)), Optional.empty(), requiredSlots);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		}
	}

	private static class VGTranslationProvider extends FabricLanguageProvider {

		protected VGTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
			for (Block block : VGBlocks.CIRCUS_CLOTH) {
				translationBuilder.add(block, WordUtils.capitalizeFully(BuiltInRegistries.BLOCK.getKey(block).getPath().replace("_", " ")));
			}
			translationBuilder.add(VGItems.LANCE, "Lance");
			translationBuilder.addEnchantment(VGEnchantments.SKEWERING, "Skewering");
			translationBuilder.addEnchantment(VGEnchantments.CAVALIER, "Cavalier");
			translationBuilder.addEnchantment(VGEnchantments.INTREPID, "Intrepid");
			translationBuilder.addEnchantment(VGEnchantments.WIND_RIDER, "Wind Rider");
			translationBuilder.addEnchantment(VGEnchantments.EXCAVATOR, "Excavator");
			translationBuilder.addEnchantment(VGEnchantments.VAINGLORY, "Vainglory");
			translationBuilder.addEnchantment(VGEnchantments.ORTHOGONAL_KNOCKBACK, "Orthogonal Knockback");
			translationBuilder.addEnchantment(VGEnchantments.GALE_FORCE, "Gale Force");
			translationBuilder.addEnchantment(VGEnchantments.BROADSIDE, "Broadside");
		}
	}

	private static class VGLootTableProvider extends FabricBlockLootTableProvider {
		public VGLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		public void generate() {
			for (Block block : VGBlocks.CIRCUS_CLOTH) {
				this.dropSelf(block);
			}
		}
	}

	private static class VGRecipeProvider extends FabricRecipeProvider {
		public VGRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		public void buildRecipes(RecipeOutput exporter) {
			for (Block block : VGBlocks.CIRCUS_CLOTH) {
				String dyeColor = BuiltInRegistries.BLOCK.getKey(block).getPath().replace("_circus_cloth", "");
				ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
						.define('W', Items.WHITE_WOOL)
						.define(dyeColor.charAt(0), BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(dyeColor + "_wool")))
						.pattern(dyeColor.charAt(0) + "W")
						.pattern(dyeColor.charAt(0) + "W")
						.unlockedBy("has_white_wool", has(Items.WHITE_WOOL))
						.unlockedBy("has_" + dyeColor + "_wool", has(BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(dyeColor + "_wool"))))
						.save(exporter, getSimpleRecipeName(block));
			}
		}
	}

	private static class VGBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
		public VGBlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			for (Block block : VGBlocks.CIRCUS_CLOTH) {
				this.tag(BlockTags.WOOL).add(reverseLookup(block));
			}
		}
	}

	private static class VGItemTagsProvider extends FabricTagProvider.ItemTagProvider {
		public VGItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, registryLookup);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			this.tag(ItemTags.SWORD_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(ItemTags.MACE_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
			this.tag(VGItemTags.LANCE_ENCHANTABLE).add(reverseLookup(VGItems.LANCE));
		}
	}
}
