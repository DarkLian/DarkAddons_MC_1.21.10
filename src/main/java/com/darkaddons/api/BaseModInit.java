package com.darkaddons.api;

import com.darkaddons.core.DarkAddons;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseModInit<T, M extends BaseModMenuManager<?, ?, ?>> implements ModInit<T> {

    protected final AtomicBoolean isCurrentlyLoading = new AtomicBoolean(false);
    protected final List<T> cache = new ArrayList<>();
    protected boolean initialized = false;
    protected boolean searching = false;

    protected abstract M getManager();

    protected abstract String getDataName();

    @Override
    public List<T> getCache() {
        return new ArrayList<>(cache);
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
    public boolean isSearching() {
        return this.searching;
    }

    @Override
    public void setSearching(boolean value) {
        this.searching = value;
    }

    @Override
    public void handleSearchInput(Player player, String input) {
        getManager().setCurrentSearchQuery(input);
        setSearching(false);
    }

    @Override
    public void trigger(Level level, Player player) {
        boolean clear = true;
        for (ModInit<?> init : ModInit.REGISTRY) {
            if (init.isSearching()) {
                clear = false;
                init.setSearching(false);
            }
        }
        if (!clear) {
            player.displayClientMessage(Component.literal("Search Canceled!").withStyle(ChatFormatting.RED), false);
        }
        if (isInitialized()) {
            getManager().callMenu(player);
            return;
        }
        if (!isCurrentlyLoading.compareAndSet(false, true)) {
            player.displayClientMessage(Component.literal(getDataName() + " is already loading, please wait...").withStyle(ChatFormatting.RED), false);
            return;
        }

        player.displayClientMessage(Component.literal("Loading " + getDataName() + "...").withStyle(ChatFormatting.GREEN), false);

        CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                List<Integer> failedIndices = initializeCache();
                long dif = System.currentTimeMillis() - startTime;

                Objects.requireNonNull(level.getServer()).execute(() -> {
                    if (failedIndices.isEmpty()) {
                        player.displayClientMessage(Component.literal(getDataName() + " Loaded successfully!").withStyle(ChatFormatting.GREEN), false);
                    } else {
                        MutableComponent message = Component.literal("Failed to load " + getDataName() + ": ").withStyle(ChatFormatting.RED);
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
                    getManager().callMenu(player);
                });
            } catch (Exception e) {
                DarkAddons.LOGGER.error("Failed to load {}", getDataName(), e);
                isCurrentlyLoading.set(false);
            }
        });
    }
}