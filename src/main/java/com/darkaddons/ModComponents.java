package com.darkaddons;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

import static com.darkaddons.DarkAddons.MOD_ID;

public class ModComponents {

    public static final DataComponentType<Integer> charge = register("charge", builder -> builder.persistent(Codec.INT));
    public static final DataComponentType<Integer> durability = register("durability", builder -> builder.persistent(Codec.INT));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Data Components for DarkAddons");
    }
}
