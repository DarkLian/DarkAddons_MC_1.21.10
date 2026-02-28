package com.darkaddons.core;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
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
    public static final DataComponentType<Integer> VARIANT = register("variant", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, name), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Data Components for DarkAddons");
    }

    public enum Rarity implements StringRepresentable {
        COMMON("Common", ChatFormatting.WHITE),
        UNCOMMON("Uncommon", ChatFormatting.GREEN),
        RARE("Rare", ChatFormatting.BLUE),
        EPIC("Epic", ChatFormatting.DARK_PURPLE),
        LEGENDARY("Legendary", ChatFormatting.GOLD),
        MYTHIC("Mythic", ChatFormatting.LIGHT_PURPLE),
        DIVINE("Divine", ChatFormatting.AQUA),
        SUPREME("Supreme", ChatFormatting.DARK_RED),
        SPECIAL("Special", ChatFormatting.RED),
        VERY_SPECIAL("Very Special", ChatFormatting.RED);

        public static final EnumCodec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);
        private static final Rarity[] MODES = values();
        private final String displayName;
        private final ChatFormatting color;

        Rarity(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public ChatFormatting getColor() {
            return this.color;
        }

        public Rarity next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        public Rarity prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum ItemType implements StringRepresentable {
        WAND("Wand"),
        SWORD("Sword"),
        BOW("Bow"),
        TOOL("Tool"),
        BLOCK("Block"),
        COSMETIC("Cosmetic");

        public static final EnumCodec<ItemType> CODEC = StringRepresentable.fromEnum(ItemType::values);
        private static final ItemType[] MODES = values();
        private final String displayName;

        ItemType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public ItemType next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        public ItemType prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
