package com.oliviathevampire.vainglory.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.oliviathevampire.vainglory.client.VaingloryClient;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @WrapOperation(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel useBigItemModels(ItemRenderer instance, ItemStack stack, Level level, LivingEntity entity, int seed, Operation<BakedModel> original, @Local(argsOnly = true) LocalRef<ItemDisplayContext> localContext) {
        ItemDisplayContext context = localContext.get();
        BakedModel originalModel = original.call(instance, stack, level, entity, seed);

        if (context.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                || context.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                || context.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                || context.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            if (!VaingloryClient.BIG_MODELS.containsKey(stack.getItem())) return originalModel;

            AtomicReference<ModelResourceLocation> modelResourceLocation = new AtomicReference<>();
            VaingloryClient.BIG_MODELS.forEach((item, modelResourceLocation1) -> {
                if (stack.is(item)) modelResourceLocation.set(modelResourceLocation1);
            });

            return ((ItemRendererAccessor) this).getItemModelShaper().getModelManager().getModel(modelResourceLocation.get());
        }

        return originalModel;
    }
}