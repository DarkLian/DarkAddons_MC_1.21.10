package com.darkaddons.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.getItemTypeLore;
import static com.darkaddons.utils.ModUtilities.getRarityColor;

public class ShowcaseBlockItem extends BlockItem {
    public ShowcaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Showcase Glass").withStyle(getRarityColor(stack));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        consumer.accept(Component.literal("Ability: Display").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.literal("Right-click to place an item inside.").withStyle(ChatFormatting.GRAY));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(stack));
    }
}