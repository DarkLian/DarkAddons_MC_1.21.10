package com.darkaddons.core;

import com.darkaddons.util.ModFilter;
import com.darkaddons.util.ModMusicPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModStats {

    public static final AtomicBoolean isCurrentlyLoading = new AtomicBoolean(false);
    @Nullable
    public static String currentTrack = null;
    public static String currentSearchQuery = "";
    public static boolean searching = false;
    public static boolean initialized = false;
    public static ModMusicPlayer.PlayMode currentPlayMode = ModMusicPlayer.PlayMode.DEFAULT;
    private static ModFilter.SortMode currentSortMode = ModFilter.SortMode.DEFAULT;

    @Nullable
    public static String getCurrentTrack() {
        return currentTrack;
    }

    public static void setCurrentTrack(@Nullable String newTrack) {
        currentTrack = newTrack;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        ModStats.initialized = initialized;
    }

    public static ModFilter.SortMode getCurrentSortMode() {
        return currentSortMode;
    }

    public static void setCurrentSortMode(ModFilter.SortMode newMode) {
        currentSortMode = newMode;
    }

    public static String getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    public static void setCurrentSearchQuery(String currentSearchQuery) {
        ModStats.currentSearchQuery = currentSearchQuery;
    }

    public static boolean isSearching() {
        return searching;
    }

    public static void setSearching(boolean searching) {
        ModStats.searching = searching;
    }

    public static ModMusicPlayer.PlayMode getCurrentPlayMode() {
        return currentPlayMode;
    }

    public static void setCurrentPlayMode(ModMusicPlayer.PlayMode currentPlayMode) {
        ModStats.currentPlayMode = currentPlayMode;
    }


}
