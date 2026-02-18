package com.darkaddons.core;

import com.darkaddons.block.DisplayBase;
import com.darkaddons.block.MemeBlock;
import com.darkaddons.block.ShowcaseBlock;
import com.darkaddons.item.DisplayBaseBlockItem;
import com.darkaddons.item.MemeBlockItem;
import com.darkaddons.item.ShowcaseBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.darkaddons.core.ModItems.ITEM_ENTRIES;

public class ModBlocks {

    public static final Block SHOWCASE_BLOCK = register("showcase_block", ShowcaseBlock::new, ShowcaseBlockItem::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), new Item.Properties().stacksTo(10).component(ModComponents.RARITY, ModComponents.Rarity.RARE).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.BLOCK));
    public static final Block MEME_BLOCK = register("meme_block", MemeBlock::new, MemeBlockItem::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), new Item.Properties().stacksTo(64).component(ModComponents.RARITY, ModComponents.Rarity.DIVINE).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.BLOCK));
    public static final Block DISPLAY_BASE = register("display_base", DisplayBase::new, DisplayBaseBlockItem::new, BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK).noOcclusion(), new Item.Properties().stacksTo(64).component(ModComponents.RARITY, ModComponents.Rarity.RARE).component(ModComponents.ITEM_TYPE, ModComponents.ItemType.TOOL));


    private static <B extends Block, I extends Item> B register(String name, Function<BlockBehaviour.Properties, B> blockFactory, BiFunction<B, Item.Properties, I> itemFactory, BlockBehaviour.Properties blockSettings, Item.Properties itemSettings) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DarkAddons.MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

        B block = blockFactory.apply(blockSettings.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        I blockItem = itemFactory.apply(block, itemSettings.setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        ITEM_ENTRIES.add(new ModItems.ItemEntry(blockItem));

        return block;
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Blocks for DarkAddons");
    }
}