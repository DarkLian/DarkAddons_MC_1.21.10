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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.darkaddons.ModSounds.getTotalMusicCount;
import static com.darkaddons.MusicMenuManager.*;

public class MusicStick extends Item {
    private static final AtomicBoolean isCurrentlyLoading = new AtomicBoolean(false);
    @Nullable
    private static String CURRENT_TRACK = null;
    private static boolean looping = false;
    private static boolean initialized = false;

    public MusicStick(Properties properties) {
        super(properties);
    }

    @Nullable
    public static String getCurrentTrack() {
        return CURRENT_TRACK;
    }

    public static void setCurrentTrack(String newTrack) {
        CURRENT_TRACK = newTrack;
    }

    public static boolean isLooping() { return looping; }

    public static void toggleLooping() { looping = !looping; }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        MusicStick.initialized = initialized;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (isInitialized()) {
            callDefaultMusicMenu(player);
            return InteractionResult.PASS;
        }

        // if false, it passes this if and set the atomic boolean to true instantly on hardware level to ensure no race condition
        if (!isCurrentlyLoading.compareAndSet(false, true)) {
            player.displayClientMessage(Component.literal("Music is already loading, please wait...").withStyle(ChatFormatting.RED), false);
            return InteractionResult.FAIL;
        }

        player.displayClientMessage(Component.literal("Loading music...").withStyle(ChatFormatting.GREEN), false);

        CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                initializeMusicCache();
                long dif = System.currentTimeMillis() - startTime;
                Objects.requireNonNull(level.getServer()).execute(() -> {
                    try {
                        player.displayClientMessage(Component.literal("Music Loaded successfully!").withStyle(ChatFormatting.GREEN), false);
                        player.displayClientMessage(Component.literal("Time took: " + dif + "ms").withStyle(ChatFormatting.GREEN), false);
                        callDefaultMusicMenu(player);
                    } finally {
                        isCurrentlyLoading.set(false);
                        setInitialized(true);
                    }
                });
            } catch (Exception e) {
                DarkAddons.LOGGER.error("Failed to load music", e);
                Objects.requireNonNull(level.getServer()).execute(() -> {
                    player.displayClientMessage(Component.literal("Critical error during loading.").withStyle(ChatFormatting.RED), false);
                    isCurrentlyLoading.set(false);
                });
            }
        });
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Right-click to open music menu").withStyle(ChatFormatting.GREEN));
        consumer.accept(Component.literal("Music count: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(getTotalMusicCount())).withStyle(ChatFormatting.BLUE)));
        if (DarkAddons.clientHelper.isShiftPressed()) {
            boolean isPlaying = CURRENT_TRACK != null;
            consumer.accept(Component.literal("Currently Playing: ").withStyle(ChatFormatting.GRAY).append(Component.literal(isPlaying ? CURRENT_TRACK : "None").withStyle(isPlaying ? ChatFormatting.BLUE : ChatFormatting.RED)));
            consumer.accept(Component.literal("Loop: ").withStyle(ChatFormatting.GRAY).append(Component.literal(isLooping() ? "Enabled" : "Disabled").withStyle(isLooping() ? ChatFormatting.GREEN : ChatFormatting.RED)));
        }
    }
}
