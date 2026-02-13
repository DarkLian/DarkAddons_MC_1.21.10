package com.darkaddons.item;

import com.darkaddons.init.MusicInit;
import com.darkaddons.menu.MusicMenuManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.getItemTypeLore;
import static com.darkaddons.utils.ModUtilities.getRarityColor;

public class MusicStick extends Item {

    public MusicStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Symphonic Staff").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        MusicInit.INSTANCE.trigger(level, player);
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        boolean isPlaying = MusicMenuManager.INSTANCE.getCurrentTrack() != null;

        consumer.accept(Component.literal("Ability: Orchestrate").withStyle(ChatFormatting.GOLD));

        consumer.accept(Component.literal("Access the global music library").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal("to play custom songs.").withStyle(ChatFormatting.GRAY));

        consumer.accept(Component.empty());

        consumer.accept(Component.literal("Now Playing: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(isPlaying ? MusicMenuManager.INSTANCE.getCurrentTrack() : "None")
                        .withStyle(isPlaying ? ChatFormatting.BLUE : ChatFormatting.RED)));

        consumer.accept(Component.literal("Mode: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(MusicMenuManager.INSTANCE.getCurrentPlayMode().getDisplayName())
                        .withStyle(ChatFormatting.GREEN)));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}
