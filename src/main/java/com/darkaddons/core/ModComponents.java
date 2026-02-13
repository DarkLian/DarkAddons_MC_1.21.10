package com.darkaddons.core;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.UnaryOperator;

import static com.darkaddons.core.DarkAddons.MOD_ID;

public class ModComponents {

    public static final DataComponentType<Integer> CHARGE = register("charge", builder -> builder.persistent(Codec.INT));
    public static final DataComponentType<Integer> DURABILITY = register("durability", builder -> builder.persistent(Codec.INT));
    public static final DataComponentType<String> SOUND_NAME = register("sound_name", builder -> builder.persistent(Codec.STRING));
    public static final DataComponentType<Rarity> RARITY = register("rarity", builder -> builder.persistent(Rarity.CODEC));
    public static final DataComponentType<ItemType> ITEM_TYPE = register("item_type", builder -> builder.persistent(ItemType.CODEC));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Data Components for DarkAddons");
    }

    public enum Rarity implements StringRepresentable {
        COMMON(Component.literal("COMMON"), ChatFormatting.WHITE),
        UNCOMMON(Component.literal("UNCOMMON"), ChatFormatting.GREEN),
        RARE(Component.literal("RARE"), ChatFormatting.BLUE),
        EPIC(Component.literal("EPIC"), ChatFormatting.DARK_PURPLE),
        LEGENDARY(Component.literal("LEGENDARY"), ChatFormatting.GOLD),
        MYTHIC(Component.literal("MYTHIC"), ChatFormatting.LIGHT_PURPLE),
        DIVINE(Component.literal("DIVINE"), ChatFormatting.AQUA),
        SUPREME(Component.literal("SUPREME"), ChatFormatting.DARK_RED),
        SPECIAL(Component.literal("SPECIAL"), ChatFormatting.RED),
        VERY_SPECIAL(Component.literal("VERY SPECIAL"), ChatFormatting.RED);

        public static final EnumCodec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);
        private final MutableComponent displayName;
        private final ChatFormatting color;

        Rarity(MutableComponent displayName, ChatFormatting color) {
            this.displayName = displayName.withStyle(color);
            this.color = color;
        }

        public MutableComponent getDisplayName() {
            return this.displayName;
        }

        public ChatFormatting getColor() {
            return this.color;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum ItemType implements StringRepresentable {
        WAND(Component.literal("WAND")),
        SWORD(Component.literal("SWORD")),
        BOW(Component.literal("BOW")),
        TOOL(Component.literal("TOOL"));

        public static final EnumCodec<ItemType> CODEC = StringRepresentable.fromEnum(ItemType::values);
        private final MutableComponent displayName;

        ItemType(MutableComponent displayName) {
            this.displayName = displayName;
        }

        public MutableComponent getDisplayName() {
            return this.displayName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
