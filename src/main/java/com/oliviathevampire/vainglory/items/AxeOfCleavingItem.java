package com.oliviathevampire.vainglory.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.utils.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AxeOfCleavingItem extends Item {
	protected static final Map<Block, Block> STRIPPABLES = new ImmutableMap.Builder<Block, Block>()
			.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
			.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
			.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
			.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
			.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
			.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
			.put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD)
			.put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG)
			.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
			.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
			.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
			.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
			.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
			.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
			.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
			.put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
			.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
			.put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
			.put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD)
			.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG)
			.put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK)
			.build();

	public AxeOfCleavingItem(Properties properties) {
		super(properties.attributes(createAttributes())
				.component(DataComponents.TOOL, createToolProperties())
				.durability(1250)
		);
	}

	static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Vainglory.id("aoc_entity_interaction"), 0.5f, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				).build();
	}

	static Tool createToolProperties() {
		return new Tool(List.of(Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_IRON_TOOL), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, 5.0F)), 3.0F, 2);
	}

	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		Player player = context.getPlayer();
		if (playerHasShieldUseIntent(context)) {
			return InteractionResult.PASS;
		} else {
			Optional<BlockState> optional = this.evaluateNewBlockState(level, blockPos, player, level.getBlockState(blockPos));
			if (optional.isEmpty()) {
				return InteractionResult.PASS;
			} else {
				ItemStack itemStack = context.getItemInHand();
				if (player instanceof ServerPlayer) {
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
				}

				level.setBlock(blockPos, optional.get(), 11);
				level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, optional.get()));
				if (player != null) {
					itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
	}

	private static boolean playerHasShieldUseIntent(UseOnContext useOnContext) {
		Player player = useOnContext.getPlayer();
		return useOnContext.getHand().equals(InteractionHand.MAIN_HAND) && player.getOffhandItem().is(Items.SHIELD) && !player.isSecondaryUseActive();
	}

	private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state) {
		Optional<BlockState> optional = this.getStripped(state);
		if (optional.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			return optional;
		} else {
			Optional<BlockState> optional2 = WeatheringCopper.getPrevious(state);
			if (optional2.isPresent()) {
				level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.levelEvent(player, 3005, pos, 0);
				return optional2;
			} else {
				Optional<BlockState> optional3 = Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock()))
						.map(block -> block.withPropertiesOf(state));
				if (optional3.isPresent()) {
					level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.levelEvent(player, 3004, pos, 0);
					return optional3;
				} else {
					return Optional.empty();
				}
			}
		}
	}

	private Optional<BlockState> getStripped(BlockState unstrippedState) {
		return Optional.ofNullable(STRIPPABLES.get(unstrippedState.getBlock()))
				.map(block -> block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, unstrippedState.getValue(RotatedPillarBlock.AXIS)));
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return repairCandidate.is(Items.BLAZE_ROD);
	}

	private int getFireAspectLevel(Player player, Level world) {
		if (Utils.hasEnchantment(Enchantments.FIRE_ASPECT, player.getUseItem())) {
			return EnchantmentHelper.getItemEnchantmentLevel(
					world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT),
					player.getUseItem()
			);
		}
		return 0;
	}

	private EnchantmentLevels getEnchantmentLevels(ItemStack lance, Level world) {
		HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

		int sunderingLevel = getEnchantmentLevel(VGEnchantments.SUNDERING, lance, enchantmentRegistryLookup);
		int rendingLevel = getEnchantmentLevel(VGEnchantments.RENDING, lance, enchantmentRegistryLookup);
		int hotStreakLevel = getEnchantmentLevel(VGEnchantments.HOT_STREAK, lance, enchantmentRegistryLookup);
		int curseOfBlazingLevel = getEnchantmentLevel(VGEnchantments.CURSE_OF_BLAZING, lance, enchantmentRegistryLookup);

		return new EnchantmentLevels(sunderingLevel, rendingLevel, hotStreakLevel, curseOfBlazingLevel);
	}

	private int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, ItemStack itemStack, HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup) {
		return Utils.hasEnchantment(enchantment, itemStack) ? EnchantmentHelper.getItemEnchantmentLevel(enchantmentRegistryLookup.getOrThrow(enchantment), itemStack) : 0;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!(attacker instanceof Player player)) return super.hurtEnemy(stack, target, attacker);

		if (getFireAspectLevel(player, attacker.level()) > 0) {
			target.igniteForSeconds(4);
		}
//		if (EnchantmentHelper.getItemEnchantmentLevel(VGEnchantments.SUNDERING, stack) > 0) {
//			target.getAttributes().getValue(Attributes.SHIELD_BLOCKING_DELAY).addModifier(new AttributeModifier("Sundering Enchantment", 2, AttributeModifier.Operation.ADDITION));
//		}
		if (target instanceof Player && target.isBlocking()) {
			((Player) target).disableShield();
		}
		return true;
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!(attacker instanceof Player player)) return;

		EnchantmentLevels enchantmentLevels = getEnchantmentLevels(stack, player.level());

		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
		if (enchantmentLevels.rendingLevel > 0) {
			Vec3 targetPos = target.position();
			target.level().explode(target, targetPos.x, targetPos.y, targetPos.z, 2.0F, Level.ExplosionInteraction.TNT);
		}
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
		EnchantmentLevels enchantmentLevels = getEnchantmentLevels(stack, world);

		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			stack.hurtAndBreak(1, miner, EquipmentSlot.MAINHAND);
			if (enchantmentLevels.rendingLevel > 0) {
				// Implement vein-mining logic here
			}
		}
		return true;
	}

	private record EnchantmentLevels(int sunderingLevel, int rendingLevel, int hotStreakLevel, int curseOfBlazingLevel) { }


}
