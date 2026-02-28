package com.darkaddons.item;


import com.darkaddons.core.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.*;

public class Statue extends Item {
    private static final int TEXTURE_COUNT;
    private static final Map<Integer, String> id = new HashMap<>();

    static {
        id.put(0, "Escoffier");
        id.put(1, "Furina");
        id.put(2, "Skirk");

        TEXTURE_COUNT = id.size();
    }

    public Statue(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Statue").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            int next = (stack.getOrDefault(ModComponents.VARIANT, 0) + 1) % TEXTURE_COUNT;
            stack.set(ModComponents.VARIANT, next);
            MutableComponent message = literal("Selected: ", ChatFormatting.GREEN).append(literal(id.get(next), ChatFormatting.BLUE));
            player.displayClientMessage(message, false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Ability: Magic!").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.literal("Right-click to select your favorite character!").withStyle(ChatFormatting.GRAY));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}
