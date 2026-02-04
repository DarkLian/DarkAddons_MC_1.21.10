package com.darkaddons;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

import static com.darkaddons.ModFilter.*;
import static com.darkaddons.ModSounds.*;
import static com.darkaddons.MusicLoopHandler.startTracking;
import static com.darkaddons.MusicLoopHandler.stopTracking;
import static com.darkaddons.item.MusicStick.*;
import static com.darkaddons.ModMusicPlayer.*;

public class MusicMenuManager {
    // This is for singleplayer and one person only, hence static variable is used here
    public static final int STATUS_INDEX = 4;
    public static final int EMPTY_INDEX = 10;
    public static final int LOOP_INDEX = 45;
    public static final int SORT_INDEX = 46;
    public static final int FILTER_INDEX = 47;
    public static final int PREVIOUS_PAGE_INDEX = 48;
    public static final int CLOSE_INDEX = 49;
    public static final int NEXT_PAGE_INDEX = 50;
    public static final int RESET_INDEX = 53;
    public static final int DEFAULT_PAGE = 1;
    private static final ItemStack STATUS_DISPLAY = createStaticItem(Items.ENCHANTED_BOOK, "", ChatFormatting.WHITE);
    private static final ItemStack GLASS_PANE = createStaticItem(Items.GRAY_STAINED_GLASS_PANE, "", ChatFormatting.WHITE);
    private static final ItemStack EMPTY_FEATHER = createStaticItem(Items.FEATHER, "No music found", ChatFormatting.RED);
    private static final ItemStack MODE_BUTTON = createStaticItem(Items.GOLD_INGOT, "Play Mode", ChatFormatting.GREEN);
    private static final ItemStack SORT_HOPPER = createStaticItem(Items.HOPPER, "Sort", ChatFormatting.GREEN);
    private static final ItemStack FILTER_SIGN = createStaticItem(Items.OAK_SIGN, "Search", ChatFormatting.GREEN);
    private static final ItemStack NEXT_ARROW = createStaticItem(Items.ARROW, "Next Page", ChatFormatting.GREEN);
    private static final ItemStack PREV_ARROW = createStaticItem(Items.ARROW, "Previous Page", ChatFormatting.GREEN);
    private static final ItemStack CLOSE_BARRIER = createStaticItem(Items.BARRIER, "Close", ChatFormatting.RED);
    private static final ItemStack RESET_REDSTONE_BLOCK = createStaticItem(Items.REDSTONE_BLOCK, "Reset Music", ChatFormatting.RED);
    private static final MutableComponent SELECTED_LORE = literal("Selected!", ChatFormatting.RED);
    private static final MutableComponent UNSELECTED_LORE = literal("Click to select!", ChatFormatting.GREEN);
    private static final MutableComponent EMPTY_LINE = literal("", ChatFormatting.WHITE);
    private static final List<ItemStack> MUSIC_ITEM_CACHE = new ArrayList<>();
    private static final List<ItemStack> FILTERED_MUSIC_ITEM = new ArrayList<>();

    public static List<ItemStack> getFilteredList() {
        return FILTERED_MUSIC_ITEM;
    }

    public static List<ItemStack> getMusicCache() {
        return new ArrayList<>(MUSIC_ITEM_CACHE);
    }

    public static int getPageMusicCount(int page) {
        // 0 ~ 28
        // if 0 it indicates that there is no content in the page
        int remaining = FILTERED_MUSIC_ITEM.size() - 28 * (page - 1);
        return Math.max(0, Math.min(remaining, 28));
    }

    public static Integer getLastMusicSlotIndex(int page) {
        int a = getPageMusicCount(page);
        if (a == 0) return null;
        return (a - 1) + 10 + 2 * ((a - 1) / 7);
    }

    // Return non italic colored component literal
    private static MutableComponent literal(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(s -> s.withColor(color).withItalic(false));
    }

    private static MutableComponent literal(String text, ChatFormatting color1, ChatFormatting color2, boolean isFirst) {
        return Component.literal(text).withStyle(s -> s.withColor(isFirst ? color1 : color2).withItalic(false));
    }

    private static ItemStack createStaticItem(Item item, String name, ChatFormatting color) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.CUSTOM_NAME, literal(name, color));
        itemStack.set(DataComponents.LORE, ItemLore.EMPTY);
        return itemStack;
    }

    public static List<Integer> initializeMusicCache() {
        List<Integer> failedIndices = new ArrayList<>();
        MUSIC_ITEM_CACHE.clear();

        for (int i = 0; i < getTotalMusicCount(); i++) {
            Item icon = getItem(i);
            Integer duration = getSoundDuration(i);
            String soundName = getSoundName(i);
            SoundEvent soundEvent = getSound(i);

            // If the data of a music is missing, it won't be stored to MUSIC_ITEM_CACHE, and will not be used at all
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

            MUSIC_ITEM_CACHE.add(item);
        }
        return failedIndices;
    }

    private static void createMusicMenu(SimpleContainer container, int page, MusicMenu musicMenu) {
        for (int i = 0; i < 9; i++) {
            container.setItem(i, GLASS_PANE);
            container.setItem(45 + i, GLASS_PANE);
        }
        for (int i = 1; i < 5; i++) {
            container.setItem(i * 9, GLASS_PANE);
            container.setItem(i * 9 + 8, GLASS_PANE);
        }
        loadGuiItems(container, page, musicMenu);
        loadMusic(container, page);
    }

    private static void loadGuiItems(SimpleContainer container, int page, MusicMenu musicMenu) {
        int totalPages = musicMenu.getPageCount();
        boolean isPlaying = getCurrentTrack() != null;
        SortMode currentSortMode = getCurrentSortMode();
        PlayMode currentPlayMode = getCurrentPlayMode();
        String currentSearchQuery = getCurrentSearchQuery();

        MutableComponent status = literal("Currently Playing: ", ChatFormatting.GREEN)
                .append(literal(isPlaying ? getCurrentTrack() : "None", ChatFormatting.BLUE, ChatFormatting.RED, isPlaying));

        List<Component> pLines = new ArrayList<>();
        pLines.add(EMPTY_LINE);
        for (ModMusicPlayer.PlayMode mode : PlayMode.values()) {
            boolean selected = (mode == currentPlayMode);
            String prefix = selected ? "▶ " : "";
            ChatFormatting color = selected ? ChatFormatting.WHITE : ChatFormatting.GRAY;
            pLines.add(literal(prefix + mode.getDisplayName(), color));
        }
        pLines.add(EMPTY_LINE);
        pLines.add(literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        pLines.add(literal("Click to switch mode!", ChatFormatting.GREEN));

        MutableComponent pageInfo = literal("(" + page + "/" + totalPages + ")", ChatFormatting.GRAY);
        ItemLore navigationLore = new ItemLore(List.of(
                pageInfo,
                EMPTY_LINE,
                literal("Right-Click to skip!", ChatFormatting.WHITE),
                literal("Click to turn page!", ChatFormatting.YELLOW)
        ));

        List<Component> sLines = new ArrayList<>();
        sLines.add(EMPTY_LINE);
        for (SortMode mode : SortMode.values()) {
            boolean selected = (mode == currentSortMode);
            String prefix = selected ? "▶ " : "";
            ChatFormatting color = selected ? ChatFormatting.WHITE : ChatFormatting.GRAY;
            sLines.add(literal(prefix + mode.getDisplayName(), color));
        }
        sLines.add(EMPTY_LINE);
        sLines.add(literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        sLines.add(literal("Click to switch filter!", ChatFormatting.GREEN));

        List<Component> searchLore = new ArrayList<>();
        searchLore.add(EMPTY_LINE);
        if (!currentSearchQuery.isEmpty()) {
            searchLore.add(literal("Filtered: ", ChatFormatting.GRAY).append(literal(currentSearchQuery, ChatFormatting.GREEN)));
            searchLore.add(EMPTY_LINE);
        }
        searchLore.add(literal("Right-Click to clear!", ChatFormatting.WHITE));
        searchLore.add(literal("Click to edit filter!", ChatFormatting.YELLOW));

        MODE_BUTTON.set(DataComponents.LORE, new ItemLore(pLines));
        SORT_HOPPER.set(DataComponents.LORE, new ItemLore(sLines));
        FILTER_SIGN.set(DataComponents.LORE, new ItemLore(searchLore));
        NEXT_ARROW.set(DataComponents.LORE, navigationLore);
        PREV_ARROW.set(DataComponents.LORE, navigationLore);
        STATUS_DISPLAY.set(DataComponents.CUSTOM_NAME, status);
        container.setItem(STATUS_INDEX, STATUS_DISPLAY);
        container.setItem(LOOP_INDEX, MODE_BUTTON);
        container.setItem(SORT_INDEX, SORT_HOPPER);
        container.setItem(FILTER_INDEX, FILTER_SIGN);
        container.setItem(CLOSE_INDEX, CLOSE_BARRIER);
        container.setItem(RESET_INDEX, RESET_REDSTONE_BLOCK);
        container.setItem(PREVIOUS_PAGE_INDEX, (page > 1) ? PREV_ARROW : GLASS_PANE);
        container.setItem(NEXT_PAGE_INDEX, (page < totalPages) ? NEXT_ARROW : GLASS_PANE);
    }

    private static void loadMusic(SimpleContainer container, int page) {
        int pageMusicCount = getPageMusicCount(page);
        if (pageMusicCount == 0) {
            container.setItem(EMPTY_INDEX, EMPTY_FEATHER);
        } else {
            for (int i = 0; i < pageMusicCount; i++) {
                int musicIndex = i + 28 * (page - 1);
                int slotIndex = 10 + i + 2 * (i / 7);
                ItemStack menuItem = FILTERED_MUSIC_ITEM.get(musicIndex).copy();
                boolean isSelected = menuItem.getOrDefault(ModComponents.SOUND_NAME, "").equals(getCurrentTrack());
                ItemLore itemLore = menuItem.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(isSelected ? SELECTED_LORE : UNSELECTED_LORE);
                menuItem.set(DataComponents.LORE, itemLore);
                menuItem.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, isSelected);
                container.setItem(slotIndex, menuItem);
            }
        }
    }

    private static void buildPage(SimpleContainer container, int page, MusicMenu musicMenu) {
        container.clearContent();
        applyFilterAndSort();
        musicMenu.setPageCount(Math.max((FILTERED_MUSIC_ITEM.size() + 27) / 28, 1));
        musicMenu.setPage(page);
        createMusicMenu(container, page, musicMenu);
    }

    public static void handleResetClick(Player player, Container container, int page, MusicMenu musicMenu) {
        stopTracking();
        DarkAddons.clientHelper.stopMusic();
        player.displayClientMessage(Component.literal("Reset Music!").withStyle(ChatFormatting.RED), false);
        setCurrentTrack(null);
        refreshMusicMenu(player, page, container, musicMenu);
    }

    public static void handlePlayModeClick(Player player, Container container, int page, MusicMenu musicMenu, int button) {
        if (button == 0) setCurrentPlayMode(getCurrentPlayMode().next());
        else if (button == 1) setCurrentPlayMode(getCurrentPlayMode().prev());
        refreshMusicMenu(player, page, container, musicMenu);
    }

    public static void handleSortClick(Player player, Container container, MusicMenu musicMenu, int button) {
        if (button == 0) setCurrentSortMode(getCurrentSortMode().next());
        else if (button == 1) setCurrentSortMode(getCurrentSortMode().prev());
        refreshMusicMenu(player, DEFAULT_PAGE, container, musicMenu);
    }

    public static void handleFilterClick(Player player, Container container, MusicMenu musicMenu, int button) {
        if (button == 0) {
            setSearching(true);
            closeContainer(player);
            player.displayClientMessage(Component.literal("Type the name of the music you want to search in chat and press Enter!").withStyle(ChatFormatting.YELLOW), false);
            DarkAddons.clientHelper.openChatBox();
        } else if (button == 1) {
            setCurrentSearchQuery("");
            refreshMusicMenu(player, DEFAULT_PAGE, container, musicMenu);
        }
    }

    private static void swapMusic(String soundName, Player player) {
        DarkAddons.clientHelper.stopMusic();
        startTracking(player, soundName);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), getSound(soundName), SoundSource.RECORDS, 1.0f, 1.0f);
    }

    public static void handleMusicSwap(Player player, Container container, int slotIndex, int page, MusicMenu musicMenu) {
        String newSoundName = container.getItem(slotIndex).getOrDefault(ModComponents.SOUND_NAME, "");
        boolean isSelected = newSoundName.equals(getCurrentTrack());
        if (isSelected) {
            player.displayClientMessage(Component.literal("Music is already selected!").withStyle(ChatFormatting.RED), false);
        } else {
            setCurrentTrack(newSoundName);
            swapMusic(newSoundName, player);
            player.displayClientMessage(Component.literal("Playing: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(newSoundName).withStyle(ChatFormatting.BLUE)), false);
        }
        refreshMusicMenu(player, page, container, musicMenu);
    }

    public static void closeContainer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }

    public static void shiftPage(Player player, Container container, MusicMenu musicMenu, int page, int delta, int button) {
        int newPage;
        if (button == 0) {
            newPage = page + delta;
        } else if (button == 1) {
            newPage = (delta == 1) ? musicMenu.getPageCount() : DEFAULT_PAGE;
        } else return;
        refreshMusicMenu(player, newPage, container, musicMenu);
    }

    public static void callMusicMenu(Player player) {
        SimpleContainer container = new SimpleContainer(54);
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> {
            MusicMenu musicMenu = new MusicMenu(containerId, playerInventory, container, DEFAULT_PAGE);
            buildPage(container, DEFAULT_PAGE, musicMenu);
            return musicMenu;
        }, Component.literal("Music Menu")));
    }

    private static void refreshMusicMenu(Player player, int page, Container container, MusicMenu musicMenu) {
        if (container instanceof SimpleContainer simple && player.containerMenu instanceof MusicMenu menu) {
            buildPage(simple, page, musicMenu);
            menu.broadcastChanges();
        }
    }

    public static boolean isFunctional(int slotIndex, MusicMenu musicMenu) {
        return switch (slotIndex) {
            case CLOSE_INDEX, RESET_INDEX, LOOP_INDEX, SORT_INDEX, FILTER_INDEX -> true;
            case PREVIOUS_PAGE_INDEX -> musicMenu.getPage() > 1;
            case NEXT_PAGE_INDEX -> musicMenu.getPage() < musicMenu.getPageCount();
            default -> false;
        };
    }

    public static boolean isMusic(int slotIndex, MusicMenu musicMenu) {
        Integer lastMusicIndex = getLastMusicSlotIndex(musicMenu.getPage());
        if (lastMusicIndex != null && slotIndex >= 10 && slotIndex <= lastMusicIndex) {
            return slotIndex % 9 != 0 && slotIndex % 9 != 8;
        }
        return false;
    }
}
