package com.oliviathevampire.vainglory.init;

import com.oliviathevampire.vainglory.Vainglory;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

import java.util.function.UnaryOperator;

public class VGComponents {

	public static final DataComponentType<Float> ANIMATION_STATE = register("animation_state", DataComponentType.<Float>builder()
			.persistent(ExtraCodecs.POSITIVE_FLOAT)
			.networkSynchronized(ByteBufCodecs.FLOAT)
			.build()
	);

	public static DataComponentType<?> bootstrap(Registry<DataComponentType<?>> registry) {
		return ANIMATION_STATE;
	}

	private static <T> DataComponentType<T> register(String name, DataComponentType<T> builder) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Vainglory.id(name), builder);
	}
}
