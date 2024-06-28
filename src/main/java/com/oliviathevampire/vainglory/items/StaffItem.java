package com.oliviathevampire.vainglory.items;

import com.google.common.collect.ImmutableMap;
import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.utils.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StaffItem extends Item {
	public StaffItem(Properties properties) {
		super(properties.attributes(createAttributes()).durability(1250));
	}

	static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return repairCandidate.is(Items.STICK);
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

	/*@Override
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
	}*/

	private record EnchantmentLevels(int sunderingLevel, int rendingLevel, int hotStreakLevel, int curseOfBlazingLevel) { }


}
