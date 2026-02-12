package com.darkaddons.init;

import com.darkaddons.core.DarkAddons;
import com.darkaddons.core.ModComponents;
import com.darkaddons.utils.MusicMenuManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.darkaddons.core.ModSounds.*;
import static com.darkaddons.utils.ModUtilities.literal;

public class MusicInit implements ModInit<ItemStack> {

    public static final MusicInit INSTANCE = new MusicInit();
    public final AtomicBoolean isCurrentlyLoading = new AtomicBoolean(false);
    private final List<ItemStack> MUSIC_CACHE = new ArrayList<>();
    private boolean initialized = false;
    private boolean searching = false;

    @Override
    public List<Integer> initializeCache() {
        List<Integer> failedIndices = new ArrayList<>();
        MUSIC_CACHE.clear();

        for (int i = 0; i < getTotalMusicCount(); i++) {
            Item icon = getItem(i);
            Integer duration = getSoundDuration(i);
            String soundName = getSoundName(i);
            SoundEvent soundEvent = getSound(i);

            // If the data of a music is missing, it won't be stored to cache, and will not be used at all
            // Just basic null checking, if the .ogg file is missing, it will still be added but will play silence
            if (icon == null || duration == null || soundName == null || soundEvent == null) {
                failedIndices.add(i);
                continue;
            }

            ItemStack item = new ItemStack(icon);
            String length = String.format("%02dm %02ds", duration / 60, duration % 60);
            ItemLore itemLore = new ItemLore(List.of(literal("Duration: ", ChatFormatting.GRAY).append(literal(length, ChatFormatting.GREEN))));
            item.set(DataComponents.CUSTOM_NAME, literal(soundName, ChatFormatting.BLUE));
            item.set(DataComponents.LORE, itemLore);
            item.set(ModComponents.SOUND_NAME, soundName);

            MUSIC_CACHE.add(item);
        }
        return failedIndices;
    }


    @Override
    public List<ItemStack> getCache() {
        return new ArrayList<>(MUSIC_CACHE);
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean value) {
        this.initialized = value;
    }

    @Override
    public void trigger(Level level, Player player) {
        if (isSearching()) {
            player.displayClientMessage(Component.literal("Search Canceled!").withStyle(ChatFormatting.RED), false);
            setSearching(false);
        }
        if (isInitialized()) {
            MusicMenuManager.INSTANCE.callMenu(player);
            return;
        }

        // if false, it passes this if and set the atomic boolean to true instantly on hardware level to ensure no race condition
        if (!isCurrentlyLoading.compareAndSet(false, true)) {
            player.displayClientMessage(Component.literal("Music is already loading, please wait...").withStyle(ChatFormatting.RED), false);
            return;
        }

        player.displayClientMessage(Component.literal("Loading music...").withStyle(ChatFormatting.GREEN), false);

        CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                List<Integer> failedIndices = initializeCache();
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
                    MusicMenuManager.INSTANCE.callMenu(player);
                });
            } catch (Exception e) {
                DarkAddons.LOGGER.error("Failed to load music", e);
                isCurrentlyLoading.set(false);
            }
        });
    }

    @Override
    public boolean isSearching() {
        return this.searching;
    }

    @Override
    public void setSearching(boolean value) {
        this.searching = value;
    }


}
