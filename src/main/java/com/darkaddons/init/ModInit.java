package com.darkaddons.init;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ModInit<T> {
    List<T> getCache();

    boolean isInitialized();

    void setInitialized(boolean value);

    void trigger(Level level, Player player);

    boolean isSearching();

    void setSearching(boolean value);

    List<Integer> initializeCache();

}
