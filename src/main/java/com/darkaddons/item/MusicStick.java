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

import java.util.Objects;
import java.util.function.Consumer;

import static com.darkaddons.ModSounds.getMusicCount;
import static com.darkaddons.MusicMenuManager.*;

public class MusicStick extends Item {
    private static String CURRENT_TRACK = "None";

    public MusicStick(Properties properties) {
        super(properties);
    }

    public static String getCurrentTrack() {
        return CURRENT_TRACK;
    }

    public static void setCurrentTrack(String newTrack) {
        CURRENT_TRACK = newTrack;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;

        player.displayClientMessage(Component.literal("Refreshing music cache...").withStyle(ChatFormatting.YELLOW), false);

        new Thread(() -> {
            try {
                initializeMusicCache();
                Thread.sleep(1500);
                Objects.requireNonNull(level.getServer()).execute(() -> {
                    if (getPageMusicCount(DEFAULT_PAGE) == 0) {
                        player.displayClientMessage(Component.literal("Error: No music found!").withStyle(ChatFormatting.RED), false);
                    } else {
                        player.displayClientMessage(Component.literal("Cache Updated!").withStyle(ChatFormatting.GREEN), false);
                        callDefaultMusicMenu(player);
                    }
                });

            } catch (Exception e) {
                DarkAddons.LOGGER.error("Failed to load music cache", e);
                player.displayClientMessage(Component.literal("Critical error during loading.").withStyle(ChatFormatting.DARK_RED), false);
            }
        }).start();
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Right-click to open music menu").withStyle(ChatFormatting.GREEN));
        consumer.accept(Component.literal("Music count: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(getMusicCount())).withStyle(ChatFormatting.BLUE)));
        if (DarkAddons.clientHelper.isShiftPressed())
            consumer.accept(Component.literal("Currently Playing: ").withStyle(ChatFormatting.GRAY).append(Component.literal(CURRENT_TRACK).withStyle((CURRENT_TRACK.equals("None")) ? ChatFormatting.RED : ChatFormatting.BLUE)));
    }
}
