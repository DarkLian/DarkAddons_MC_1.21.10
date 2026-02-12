package com.darkaddons.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public interface ModInit<T> {
    List<ModInit<?>> REGISTRY = new ArrayList<>();

    static void register(ModInit<?> init) {
        REGISTRY.add(init);
    }

    List<T> getCache();

    boolean isInitialized();

    void setInitialized(boolean value);

    void trigger(Level level, Player player);

    boolean isSearching();

    void setSearching(boolean value);

    void handleSearchInput(Player player, String input);

    List<Integer> initializeCache();
}
