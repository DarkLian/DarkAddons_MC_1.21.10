package com.darkaddons.core;


import com.darkaddons.item.*;
import com.darkaddons.item.Terminator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.darkaddons.core.DarkAddons.MOD_ID;

public final class ModItems {
    private static final List<ItemEntry> ITEM_ENTRIES = new ArrayList<>();

    static {
        register("lightning_stick", LightningStick::new, new Item.Properties().stacksTo(1).component(ModComponents.CHARGE, 20).component(ModComponents.RARITY, ModComponents.Rarity.RARE).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.WAND));
        register("teleport_stick", TeleportStick::new, new Item.Properties().stacksTo(1).component(ModComponents.RARITY, ModComponents.Rarity.RARE).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.WAND));
        register("hyperion", Hyperion::new, new Item.Properties().stacksTo(1).component(ModComponents.DURABILITY, 200).component(ModComponents.RARITY, ModComponents.Rarity.LEGENDARY).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.SWORD));
        register("music_stick", MusicStick::new, new Item.Properties().stacksTo(1).component(ModComponents.RARITY, ModComponents.Rarity.UNCOMMON).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.TOOL));
        register("gravity_stick", GravityStick::new, new Item.Properties().stacksTo(1).component(ModComponents.RARITY, ModComponents.Rarity.EPIC).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.WAND));
        register("terminator", Terminator::new, new Item.Properties().stacksTo(1).component(ModComponents.RARITY, ModComponents.Rarity.LEGENDARY).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.BOW));
        register("bonzo_staff", BonzoStaff::new, new Item.Properties().stacksTo(1).component(ModComponents.RARITY, ModComponents.Rarity.MYTHIC).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.WAND));
    }

    private static <T extends Item> void register(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        properties.setId(key);
        T item = itemFactory.apply(properties);
        Item modItem = Registry.register(BuiltInRegistries.ITEM, key, item);
        ITEM_ENTRIES.add(new ItemEntry(modItem));
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Items for DarkAddons");
    }

    public static int getTotalItemCount() {
        return ITEM_ENTRIES.size();
    }

    public static Item getItem(int index) {
        if (isInvalidIndex(index)) return null;
        return ITEM_ENTRIES.get(index).item();
    }

    private static boolean isInvalidIndex(int index) {
        return index < 0 || index >= ITEM_ENTRIES.size();
    }

    public record ItemEntry(Item item) {
    }
}
