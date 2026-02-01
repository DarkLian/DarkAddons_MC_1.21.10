package com.darkaddons.item;

import com.darkaddons.DarkAddons;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.darkaddons.ModFilter.FilterMode;
import static com.darkaddons.MusicMenuManager.*;

public class MusicStick extends Item {
    private static final AtomicBoolean isCurrentlyLoading = new AtomicBoolean(false);
    @Nullable
    private static String currentTrack = null;
    private static boolean looping = false;
    private static boolean initialized = false;
    private static FilterMode currentMode = FilterMode.DEFAULT;

    public MusicStick(Properties properties) {
        super(properties);
    }

    @Nullable
    public static String getCurrentTrack() {
        return currentTrack;
    }

    public static void setCurrentTrack(@Nullable String newTrack) {
        currentTrack = newTrack;
    }

    public static boolean isLooping() {
        return looping;
    }

    public static void toggleLooping() {
        looping = !looping;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        MusicStick.initialized = initialized;
    }

    public static FilterMode getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(FilterMode newMode) {
        currentMode = newMode;
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
                List<Integer> failedIndices = initializeMusicCache();
                long dif = System.currentTimeMillis() - startTime;
                Objects.requireNonNull(level.getServer()).execute(() -> {
                    if (failedIndices.isEmpty()) {
                        player.displayClientMessage(Component.literal("Music Loaded successfully!").withStyle(ChatFormatting.GREEN), false);
                    } else {
                        MutableComponent message = Component.literal("Failed to load music numbers: ").withStyle(ChatFormatting.RED);
                        for (int i = 0; i < failedIndices.size(); i++) {
                            message.append(Component.literal(String.valueOf(failedIndices.get(i))).withStyle(ChatFormatting.BLUE));
                            if (i < failedIndices.size() - 1) {
                                message.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                            }
                        }
                        player.displayClientMessage(message, false);
                    }
                    player.displayClientMessage(Component.literal("Time took: " + dif + "ms").withStyle(ChatFormatting.GREEN), false);
                    setInitialized(true);
                    isCurrentlyLoading.set(false);
                    callDefaultMusicMenu(player);
                });
            } catch (Exception e) {
                DarkAddons.LOGGER.error("Failed to load music", e);
                isCurrentlyLoading.set(false);
            }
        });
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Right-click to open music menu!").withStyle(ChatFormatting.GREEN));
        //it will show 0 before initialization (and the music count shows the successfully loaded ones, failed ones will be skipped)
        consumer.accept(Component.literal("Music count: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(getFilteredList().size())).withStyle(ChatFormatting.BLUE)));
        if (DarkAddons.clientHelper.isShiftPressed()) {
            boolean isPlaying = currentTrack != null;
            consumer.accept(Component.literal("Currently Playing: ").withStyle(ChatFormatting.GRAY).append(Component.literal(isPlaying ? currentTrack : "None").withStyle(isPlaying ? ChatFormatting.BLUE : ChatFormatting.RED)));
            consumer.accept(Component.literal("Loop: ").withStyle(ChatFormatting.GRAY).append(Component.literal(isLooping() ? "Enabled" : "Disabled").withStyle(isLooping() ? ChatFormatting.GREEN : ChatFormatting.RED)));
        }
    }
}
