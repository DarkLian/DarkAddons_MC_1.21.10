package com.darkaddons.item;

import com.darkaddons.DarkAddons;
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

import static com.darkaddons.MusicMenuManager.*;

public class MusicStick extends Item {
    public static String currentTrack;

    public MusicStick(Properties properties) {
        super(properties);
        currentTrack = "None";
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (getPageMusicCount(defaultPage) == 0) {
            player.displayClientMessage(Component.literal("Music stick is temporarily unavailable, please try again later.").withStyle(ChatFormatting.RED), false);
            return InteractionResult.FAIL;
        }
        callDefaultMusicMenu(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Right-click to open music menu").withStyle(ChatFormatting.GREEN));
        if (DarkAddons.clientHelper.isShiftPressed())
            consumer.accept(Component.literal("Currently Playing: ").withStyle(ChatFormatting.GRAY).append(Component.literal(currentTrack).withStyle((currentTrack.equals("None")) ? ChatFormatting.RED : ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD)));
    }
}
