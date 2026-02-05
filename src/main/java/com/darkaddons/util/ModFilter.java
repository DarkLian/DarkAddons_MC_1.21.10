package com.darkaddons.util;

import com.darkaddons.core.ModComponents;
import com.darkaddons.core.ModStats;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.darkaddons.core.ModSounds.getSoundDuration;
import static com.darkaddons.util.MusicMenuManager.getFilteredList;
import static com.darkaddons.util.MusicMenuManager.getMusicCache;

public class ModFilter {

    public static void applyFilterAndSort() {
        String searchQuery = ModStats.getCurrentSearchQuery();
        SortMode mode = ModStats.getCurrentSortMode();
        Comparator<ItemStack> rule = mode.getSortRule();
        List<ItemStack> filteredList = getFilteredList();
        List<ItemStack> items = getMusicCache();

        if (!searchQuery.isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase();
            items.removeIf(stack -> {
                String displayName = stack.getOrDefault(ModComponents.SOUND_NAME, "").toLowerCase();
                return !displayName.contains(lowerQuery);
            });
        }

        if (rule != null) {
            items.sort(rule);
        }

        filteredList.clear();
        filteredList.addAll(items);
    }

    public enum SortMode {
        DEFAULT("Default", null),
        A_Z("A-Z", Comparator.comparing(s -> s.getOrDefault(ModComponents.SOUND_NAME, ""))),
        Z_A("Z-A", Comparator.comparing((ItemStack s) -> s.getOrDefault(ModComponents.SOUND_NAME, "")).reversed()),
        DURATION("Shortest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, ""))))),
        DURATION_INVERSE("Longest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, "")))).reversed());

        private static final SortMode[] MODES = values();
        private final String displayName;
        private final Comparator<ItemStack> sortRule;

        SortMode(String displayName, Comparator<ItemStack> sortRule) {
            this.displayName = displayName;
            this.sortRule = sortRule;
        }

        public SortMode next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        public SortMode prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public Comparator<ItemStack> getSortRule() {
            return this.sortRule;
        }
    }
}
