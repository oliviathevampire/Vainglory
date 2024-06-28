package com.oliviathevampire.vainglory.items;

import com.oliviathevampire.vainglory.Vainglory;
import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LanceItem extends Item {

	private static final double ACCELERATION_INCREMENT = 0.05;
	private static final double MAX_SPEED_INCREMENT = 0.5;
	private static final double BASE_SPEED = 0.2;
	private static final double MAX_SPEED = 0.5;
	private static final double ACCELERATION = 0.02;
	private static final int DASH_DURATION = 5;
	private static final int MAX_USE_DURATION = 72000;

	private float attackDamageBonus = 9.0f;

	public LanceItem(Properties properties) {
		super(properties.attributes(createAttributes())
				.component(DataComponents.TOOL, createToolProperties())
				.durability(400)
		);
	}

	static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Vainglory.id("lance_entity_interaction"), 1f, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				).build();
	}

	static Tool createToolProperties() {
		return new Tool(List.of(), 1.0F, 2);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		player.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F);
		return InteractionResultHolder.success(itemStack);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
		if (livingEntity instanceof Player player) {
			int chargeTime = this.getUseDuration(stack, livingEntity) - timeCharged;
			EnchantmentLevels enchantmentLevels = getEnchantmentLevels(stack, level);
			float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, livingEntity, 1.25F);
//			chargeTime += Mth.floor(f * 20.0F);
			chargeTime = adjustChargeTimeForQuickCharge(chargeTime, enchantmentLevels.quickChargeLevel);
			float power = getChargeForTime(chargeTime);
			if (power >= 0.1) {
				dash(player, level, power, enchantmentLevels);
				player.playSound(SoundEvents.TRIDENT_RETURN, 1.0F, 1.0F);
			}
		}
	}

	protected float getChargeForTime(int charge) {
		float f = (float) charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		return Math.min(f, 10.0F);
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
		EnchantmentLevels enchantmentLevels = getEnchantmentLevels(itemStack, livingEntity.level());
		return adjustChargeTimeForQuickCharge(MAX_USE_DURATION, enchantmentLevels.quickChargeLevel);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.SPEAR;
	}

	@Override
	public float getAttackDamageBonus(Entity entity, float f, DamageSource damageSource) {
		return attackDamageBonus;
	}

	private void dash(Player player, Level world, float chargeDuration, EnchantmentLevels enchantmentLevels) {
		Vec3 look = player.getViewVector(1.0f);
		double baseSpeed = BASE_SPEED * (chargeDuration * 5);
		double maxSpeed = MAX_SPEED * (chargeDuration * 5);
		double acceleration = ACCELERATION;
		double dashDuration = DASH_DURATION;

		ItemStack lance = player.getMainHandItem();

		if (enchantmentLevels.intrepidLevel > 0) {
			acceleration += ACCELERATION_INCREMENT * enchantmentLevels.intrepidLevel;
			maxSpeed += MAX_SPEED_INCREMENT * enchantmentLevels.intrepidLevel;
		}

		if (enchantmentLevels.windRiderLevel > 0) {
			dashDuration = 10;
			player.setNoGravity(true);
		}

		Vec3 dashVec = new Vec3(look.x * baseSpeed, Math.min(look.y * 0.2, 0.4), look.z * baseSpeed); // Limit vertical movement
		for (int i = 0; i < dashDuration; i++) {
			if (player.isPassenger() && player.getControlledVehicle().hasExactlyOnePlayerPassenger()) {
				player.getControlledVehicle().addDeltaMovement(dashVec);
			} else {
				player.addDeltaMovement(dashVec);
			}
			player.hurtMarked = true;
			baseSpeed = Math.min(baseSpeed + acceleration, maxSpeed);

			handleCollisions(player, world, baseSpeed, dashVec, enchantmentLevels);
		}

		if (enchantmentLevels.windRiderLevel > 0) {
			player.setNoGravity(false);
		}

		// Trigger explosion/particle effect on wall collision
		if (world.getBlockState(player.blockPosition().below()).isSolid() && enchantmentLevels.galeForceLevel > 0) {
			world.explode(player, null, new ExplosionDamageCalculator() {
						@Override
						public float getEntityDamageAmount(Explosion explosion, Entity entity) {
							return 0.0F;
						}
					}, player.getX(), player.getY(), player.getZ(),
					1.0f + enchantmentLevels.galeForceLevel, false, Level.ExplosionInteraction.NONE,
					ParticleTypes.GUST_EMITTER_SMALL,
					ParticleTypes.GUST_EMITTER_LARGE,
					SoundEvents.WIND_CHARGE_BURST
			);
			world.gameEvent(player, GameEvent.EXPLODE, player.blockPosition());
		}

		lance.hurtAndBreak((int) chargeDuration, player, EquipmentSlot.MAINHAND);

		player.getCooldowns().addCooldown(this, (int) ((30 * (1 / maxSpeed * baseSpeed)) + chargeDuration) * 7);
	}

	private int adjustChargeTimeForQuickCharge(int chargeTime, int quickChargeLevel) {
		if (quickChargeLevel > 0) {
			chargeTime -= quickChargeLevel * 5; // Reduce charge time by 5 ticks per level of Quick Charge
		}
		return Math.max(chargeTime, 0); // Ensure charge time doesn't go below 0
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return repairCandidate.is(Items.BREEZE_ROD);
	}

	private static double getKnockbackPower(Player player, LivingEntity entity, Vec3 entityPos, double speed) {
		return (3.5 - entityPos.length()) * 0.7F * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
	}

	private void handleCollisions(Player player, Level world, double baseSpeed, Vec3 dashVec, EnchantmentLevels enchantmentLevels) {
		BlockPos pos = player.blockPosition();
		BlockState state = world.getBlockState(pos);

		if (enchantmentLevels.excavatorLevel > 0 && !world.isEmptyBlock(pos) && state.getDestroySpeed(world, pos) <= (1.5 * enchantmentLevels.excavatorLevel)) {
			world.destroyBlock(pos, true);
		}

		// Check for collisions with entities in front of the player
		double reach = 1.0 + (enchantmentLevels.broadsideLevel * 0.5);
		Vec3 forward = player.getViewVector(1.0F).normalize().scale(reach);
		AABB hitbox = new AABB(player.position().subtract(0.5, 0.5, 0.5), player.position().scale(reach));

		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, hitbox, entity -> entity != player);
		int skeweredCount = 0;

		for (LivingEntity entity : entities) {
			if (entity.hasExactlyOnePlayerPassenger() && player.isPassenger()) {
				continue;
			}

			if (skeweredCount < (3 + enchantmentLevels.skeweringLevel)) {
				int fireAspectLevel = getFireAspectLevel(player, world);

				float attackDamageBonus = (float) baseSpeed + enchantmentLevels.sharpnessLevel;

				AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
				double originalAttackDamage = attackDamageAttribute.getBaseValue();
				attackDamageAttribute.setBaseValue(originalAttackDamage + attackDamageBonus);

				player.attack(entity);

				attackDamageAttribute.setBaseValue(originalAttackDamage);

				Vec3 vec3 = entity.position().subtract(player.position());
				double d = getKnockbackPower(player, entity, vec3, baseSpeed * 0.5);
				Vec3 vec32 = vec3.normalize().scale(d);
				entity.push(vec32.x, vec32.y, vec32.z);
				if (fireAspectLevel > 0) entity.igniteForSeconds(fireAspectLevel + 3);
				skeweredCount++;
			} else {
				break;
			}

			// Vainglory healing
			if (enchantmentLevels.vaingloryLevel > 0) {
				player.heal(1.0F * enchantmentLevels.vaingloryLevel);
			}

			// Orthogonal knockback
			if (enchantmentLevels.galeForceLevel > 0) {
				Vec3 orthogonalKnockback = dashVec.cross(new Vec3(0, 1, 0)).normalize().scale(baseSpeed * 0.5 * enchantmentLevels.galeForceLevel);
				entity.setDeltaMovement(orthogonalKnockback.x, orthogonalKnockback.y, orthogonalKnockback.z);
			}

			if (enchantmentLevels.cavalierLevel > 0 && (entity.hasExactlyOnePlayerPassenger() || player.isPassenger())) {
				baseSpeed *= 1.5;
			}
		}
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

		int skeweringLevel = getEnchantmentLevel(VGEnchantments.SKEWERING, lance, enchantmentRegistryLookup);
		int cavalierLevel = getEnchantmentLevel(VGEnchantments.CAVALIER, lance, enchantmentRegistryLookup);
		int intrepidLevel = getEnchantmentLevel(VGEnchantments.INTREPID, lance, enchantmentRegistryLookup);
		int sharpnessLevel = getEnchantmentLevel(Enchantments.SHARPNESS, lance, enchantmentRegistryLookup);
		int windRiderLevel = getEnchantmentLevel(VGEnchantments.WIND_RIDER, lance, enchantmentRegistryLookup);
		int excavatorLevel = getEnchantmentLevel(VGEnchantments.EXCAVATOR, lance, enchantmentRegistryLookup);
		int vaingloryLevel = getEnchantmentLevel(VGEnchantments.VAINGLORY, lance, enchantmentRegistryLookup);
		int galeForceLevel = getEnchantmentLevel(VGEnchantments.GALE_FORCE, lance, enchantmentRegistryLookup);
		int broadsideLevel = getEnchantmentLevel(VGEnchantments.BROADSIDE, lance, enchantmentRegistryLookup);
		int quickChargeLevel = getEnchantmentLevel(Enchantments.QUICK_CHARGE, lance, enchantmentRegistryLookup);
		int breachLevel = getEnchantmentLevel(Enchantments.BREACH, lance, enchantmentRegistryLookup);

		return new EnchantmentLevels(skeweringLevel, cavalierLevel, intrepidLevel, sharpnessLevel, windRiderLevel, excavatorLevel, vaingloryLevel, galeForceLevel, broadsideLevel, quickChargeLevel, breachLevel);
	}


	private int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, ItemStack itemStack, HolderLookup.RegistryLookup<Enchantment> enchantmentRegistryLookup) {
		return Utils.hasEnchantment(enchantment, itemStack) ? EnchantmentHelper.getItemEnchantmentLevel(enchantmentRegistryLookup.getOrThrow(enchantment), itemStack) : 0;
	}

	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	public void postHurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.hurtAndBreak(1, livingEntity2, EquipmentSlot.MAINHAND);
	}

	private static record EnchantmentLevels(int skeweringLevel, int cavalierLevel, int intrepidLevel, int sharpnessLevel, int windRiderLevel,
											int excavatorLevel, int vaingloryLevel, int galeForceLevel, int broadsideLevel, int quickChargeLevel,
											int breachLevel) { }


}
