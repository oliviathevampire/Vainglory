package com.oliviathevampire.vainglory.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.oliviathevampire.vainglory.init.VGItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 1))
	public Item supportLanceSweepingDamage(Item original) {
		if (original == VGItems.LANCE) return Items.DIAMOND_SWORD;

		return original;
	}

}
