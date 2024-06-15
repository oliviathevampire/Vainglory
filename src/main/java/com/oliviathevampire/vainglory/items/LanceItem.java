package com.oliviathevampire.vainglory.items;

import com.oliviathevampire.vainglory.init.VGEnchantments;
import com.oliviathevampire.vainglory.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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

	private float attackDamageBonus = 0.0f;

	public LanceItem(Properties properties) {
		super(properties.attributes(createAttributes(8,  0.6f)).durability(400));
	}

	public static ItemAttributeModifiers createAttributes(int attackDamage, float attackSpeed) {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND
				).build();
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
			int i = this.getUseDuration(stack, livingEntity) - timeCharged;
			float power = getChargeForTime(i);
			if (power >= 0.1) {
				dash(player, level, power);
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
		return MAX_USE_DURATION; // Max use duration
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public float getAttackDamageBonus(Entity entity, float f, DamageSource damageSource) {
		return attackDamageBonus;
	}

	private void dash(Player player, Level world, float chargeDuration) {
		Vec3 look = player.getViewVector(1.0f);
		double baseSpeed = BASE_SPEED * (chargeDuration * 5);
		double maxSpeed = MAX_SPEED * (chargeDuration * 5);
		double acceleration = ACCELERATION;
		double dashDuration = DASH_DURATION;

		ItemStack lance = player.getMainHandItem();
		EnchantmentLevels enchantmentLevels = getEnchantmentLevels(lance, world);

		if (enchantmentLevels.intrepidLevel > 0) {
			acceleration += ACCELERATION_INCREMENT * enchantmentLevels.intrepidLevel;
			maxSpeed += MAX_SPEED_INCREMENT * enchantmentLevels.intrepidLevel;
		}

		if (enchantmentLevels.windRiderLevel > 0) {
			dashDuration = 10;
			player.setNoGravity(true);
		}

		for (int i = 0; i < dashDuration; i++) {
			Vec3 dashVec = look.scale(baseSpeed);
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

		player.causeFallDamage(0, 0, player.damageSources().fall());

		player.getCooldowns().addCooldown(this, (int) ((30 * (1 / maxSpeed * baseSpeed)) + chargeDuration) * 7);
	}

	private void handleCollisions(Player player, Level world, double baseSpeed, Vec3 dashVec, EnchantmentLevels enchantmentLevels) {
		BlockPos pos = player.blockPosition();
		BlockState state = world.getBlockState(pos);

		if (enchantmentLevels.excavatorLevel > 0 && !world.isEmptyBlock(pos) && state.getDestroySpeed(world, pos) <= (1.5 * enchantmentLevels.excavatorLevel)) {
			world.destroyBlock(pos, true);
		}

		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.0), entity -> entity != player);
		int skeweredCount = 0;

		for (LivingEntity entity : entities) {
			if (entity.hasExactlyOnePlayerPassenger() && player.isPassenger()) {
				continue;
			}

			if (skeweredCount < (3 + enchantmentLevels.skeweringLevel)) {
				int fireAspectLevel = Utils.hasEnchantment(Enchantments.FIRE_ASPECT, player.getUseItem()) ? EnchantmentHelper.getItemEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), player.getUseItem()) : 0;
				attackDamageBonus = (float) baseSpeed + enchantmentLevels.sharpnessLevel;
				player.attack(entity);
				Vec3 knockback = new Vec3(entity.getX() - player.getX(), 0, entity.getZ() - player.getZ()).normalize().scale(baseSpeed * 0.5);
				entity.setDeltaMovement(knockback.x, knockback.y, knockback.z);
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
			if (enchantmentLevels.orthogonalKnockbackLevel > 0) {
				Vec3 orthogonalKnockback = dashVec.cross(new Vec3(0, 1, 0)).normalize().scale(baseSpeed * 0.5 * enchantmentLevels.orthogonalKnockbackLevel);
				entity.setDeltaMovement(orthogonalKnockback.x, orthogonalKnockback.y, orthogonalKnockback.z);
			}

			if (enchantmentLevels.cavalierLevel > 0 && (entity.hasExactlyOnePlayerPassenger() || player.isPassenger())) {
				baseSpeed *= 1.5;
			}
		}

		// Trigger explosion/particle effect on wall collision
		if (world.getBlockState(player.blockPosition().below()).isSolid()) {
			world.explode(player, null, new ExplosionDamageCalculator() {
					@Override
					public float getEntityDamageAmount(Explosion explosion, Entity entity) {
						return 0.0F;
					}

					@Override
					public float getKnockbackMultiplier(Entity entity) {
						return 0f;
					}
			}, player.getX() + dashVec.x(), player.getY() + dashVec.y(), player.getZ() + dashVec.z(),
				1.0F, false, Level.ExplosionInteraction.NONE,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.WIND_CHARGE_BURST
			);
			world.gameEvent(player, GameEvent.EXPLODE, player.blockPosition());
		}
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
		int orthogonalKnockbackLevel = getEnchantmentLevel(VGEnchantments.ORTHOGONAL_KNOCKBACK, lance, enchantmentRegistryLookup);

		return new EnchantmentLevels(skeweringLevel, cavalierLevel, intrepidLevel, sharpnessLevel, windRiderLevel, excavatorLevel, vaingloryLevel, orthogonalKnockbackLevel);
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

	private static record EnchantmentLevels(int skeweringLevel, int cavalierLevel, int intrepidLevel, int sharpnessLevel, int windRiderLevel, int excavatorLevel, int vaingloryLevel, int orthogonalKnockbackLevel) { }

}
