package com.darkaddons.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.getItemTypeLore;
import static com.darkaddons.utils.ModUtilities.getRarityColor;

public class MemeBlockItem extends BlockItem {
    public MemeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Meme Block").withStyle(getRarityColor(stack));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        consumer.accept(getItemTypeLore(stack));
    }
}