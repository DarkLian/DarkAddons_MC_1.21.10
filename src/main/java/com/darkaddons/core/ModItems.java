package com.darkaddons.core;


import com.darkaddons.item.Hyperion;
import com.darkaddons.item.LightningStick;
import com.darkaddons.item.MusicStick;
import com.darkaddons.item.TeleportStick;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

import static com.darkaddons.core.DarkAddons.MOD_ID;

@SuppressWarnings("unused")
public final class ModItems {

    public static final Item LIGHTNING_STICK = register("lightning_stick", LightningStick::new, new Item.Properties().stacksTo(1).component(ModComponents.CHARGE, 20));
    public static final Item TELEPORT_STICK = register("teleport_stick", TeleportStick::new, new Item.Properties().stacksTo(1));
    public static final Item HYPERION = register("hyperion", Hyperion::new, new Item.Properties().stacksTo(1).component(ModComponents.DURABILITY, 200));
    public static final Item MUSIC_STICK = register("music_stick", MusicStick::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        properties.setId(key);
        T item = itemFactory.apply(properties);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Items for DarkAddons");
    }

}
