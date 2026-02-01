package com.darkaddons;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.darkaddons.ModSounds.getSoundDuration;
import static com.darkaddons.MusicMenuManager.*;

public class ModFilter {

    public static void applyFilter(FilterMode filterMode) {
        List<ItemStack> filteredList = getFilteredList();
        filteredList.clear();
        List<ItemStack> items = getMusicCache();
        Comparator<ItemStack> rule = filterMode.getSortRule();
        if (rule != null) {
            items.sort(rule);
        }
        filteredList.addAll(items);
        setPageCount(Math.max((filteredList.size() + 27) / 28, 1));
    }

    public enum FilterMode {
        DEFAULT("Default", null),
        A_Z("A-Z", Comparator.comparing(s -> s.getOrDefault(ModComponents.SOUND_NAME, ""))),
        Z_A("Z-A", Comparator.comparing((ItemStack s) -> s.getOrDefault(ModComponents.SOUND_NAME, "")).reversed()),
        DURATION("Shortest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, ""))))),
        DURATION_INVERSE("Longest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, "")))).reversed());

        private static final FilterMode[] MODES = values();
        private final String displayName;
        private final Comparator<ItemStack> sortRule;

        FilterMode(String displayName, Comparator<ItemStack> sortRule) {
            this.displayName = displayName;
            this.sortRule = sortRule;
        }

        public FilterMode next() { return MODES[(ordinal() + 1) % MODES.length]; }
        public String getDisplayName() { return this.displayName; }
        public Comparator<ItemStack> getSortRule() { return this.sortRule; }
    }
}
