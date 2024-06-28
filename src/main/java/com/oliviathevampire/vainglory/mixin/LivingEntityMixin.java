package com.oliviathevampire.vainglory.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.oliviathevampire.vainglory.init.VGItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@ModifyExpressionValue(method = "canDisableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 0))
	public Item supportLanceSweepingDamage(Item original) {
		if (original == VGItems.AXE_OF_CLEAVING) return Items.DIAMOND_AXE;

		return original;
	}

}
