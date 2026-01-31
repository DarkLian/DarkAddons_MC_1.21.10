package com.darkaddons;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
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

import static com.darkaddons.ModSounds.*;
import static com.darkaddons.MusicLoopHandler.startTracking;
import static com.darkaddons.MusicLoopHandler.stopTracking;
import static com.darkaddons.item.MusicStick.*;

public class MusicMenuManager {
    public static final int STATUS_INDEX = 4;
    public static final int EMPTY_INDEX = 10;
    public static final int LOOP_INDEX = 45;
    public static final int PREVIOUS_PAGE_INDEX = 48;
    public static final int CLOSE_INDEX = 49;
    public static final int NEXT_PAGE_INDEX = 50;
    public static final int RESET_INDEX = 53;
    public static final int DEFAULT_PAGE = 1;
    private static final ItemStack STATUS_DISPLAY = createStaticItem(Items.ENCHANTED_BOOK, "", ChatFormatting.WHITE);
    private static final ItemStack GLASS_PANE = createStaticItem(Items.GRAY_STAINED_GLASS_PANE, "", ChatFormatting.WHITE);
    private static final ItemStack EMPTY_FEATHER = createStaticItem(Items.FEATHER, "No music found", ChatFormatting.RED);
    private static final ItemStack LOOP_BUTTON = createStaticItem(Items.GOLD_INGOT, "", ChatFormatting.WHITE);
    private static final ItemStack NEXT_ARROW = createStaticItem(Items.ARROW, "", ChatFormatting.WHITE);
    private static final ItemStack PREV_ARROW = createStaticItem(Items.ARROW, "", ChatFormatting.WHITE);
    private static final ItemStack CLOSE_BARRIER = createStaticItem(Items.BARRIER, "Close", ChatFormatting.RED);
    private static final ItemStack RESET_REDSTONE_BLOCK = createStaticItem(Items.REDSTONE_BLOCK, "Reset Music", ChatFormatting.RED);
    private static final MutableComponent SELECTED_LORE = literal("Selected", ChatFormatting.GREEN);
    private static final MutableComponent UNSELECTED_LORE = literal("Left-click to select", ChatFormatting.GREEN);
    private static int pageCount;
    private static final ArrayList<ItemStack> MUSIC_ITEM_CACHE = new ArrayList<>();
    private static final ArrayList<ItemStack> FILTERED_MUSIC_ITEM = new ArrayList<>();

    public static void setPageCount(int count) {
        pageCount = count;
    }

    public static int getPageCount() {
        return pageCount;
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
        return (a - 1) + 10 + 2 * ((a - 1)/ 7);
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

    public static void initializeMusicCache() {
        for (int i = 0; i < getTotalMusicCount(); i++) {
            Item icon = getItem(i);
            Integer duration = getSoundDuration(i);
            String soundName = getSoundName(i);
            if (icon == null || duration == null || soundName == null) continue;
            ItemStack item = new ItemStack(icon);
            int minutes = duration / 60;
            int seconds = duration % 60;
            String length = String.format("%02dm %02ds", minutes, seconds);
            ItemLore itemLore = new ItemLore(List.of(literal(length, ChatFormatting.GREEN)));
            item.set(DataComponents.CUSTOM_NAME, literal(soundName, ChatFormatting.BLUE));
            item.set(DataComponents.LORE, itemLore);
            item.set(ModComponents.SOUND_NAME, soundName);
            MUSIC_ITEM_CACHE.add(item);
        }

        //set to default
        FILTERED_MUSIC_ITEM.clear();
        FILTERED_MUSIC_ITEM.addAll(MUSIC_ITEM_CACHE);
        setPageCount(Math.max((FILTERED_MUSIC_ITEM.size() + 27) / 28, 1));
    }

    public static void setDefaultMusicMenu(SimpleContainer container, int page) {
        for (int i = 0; i < 9; i++) {
            container.setItem(i, GLASS_PANE);
            container.setItem(45 + i, GLASS_PANE);
        }
        for (int i = 1; i < 5; i++) {
            container.setItem(i * 9, GLASS_PANE);
            container.setItem(i * 9 + 8, GLASS_PANE);
        }
        loadGuiItems(page, container);
    }

    private static void loadGuiItems(int page, SimpleContainer container) {
        boolean isPlaying = getCurrentTrack() != null;
        MutableComponent status = literal("Currently Playing: ", ChatFormatting.GREEN)
                .append(literal(isPlaying ? getCurrentTrack() : "None", ChatFormatting.BLUE, ChatFormatting.RED, isPlaying));
        MutableComponent loopStatus = literal("Loop: ", ChatFormatting.GREEN)
                .append(literal(isLooping() ? "Enabled" : "Disabled", ChatFormatting.YELLOW, ChatFormatting.RED, isLooping()));
        MutableComponent nextPage = literal("Next Page (" + (page + 1) + "/" + getPageCount() + ") ->", ChatFormatting.GREEN);
        MutableComponent prevPage = literal("<- Previous Page (" + (page - 1) + "/" + getPageCount() + ")", ChatFormatting.GREEN);
        LOOP_BUTTON.set(DataComponents.CUSTOM_NAME, loopStatus);
        STATUS_DISPLAY.set(DataComponents.CUSTOM_NAME, status);
        NEXT_ARROW.set(DataComponents.CUSTOM_NAME, nextPage);
        PREV_ARROW.set(DataComponents.CUSTOM_NAME, prevPage);
        container.setItem(STATUS_INDEX, STATUS_DISPLAY);
        container.setItem(LOOP_INDEX, LOOP_BUTTON);
        container.setItem(CLOSE_INDEX, CLOSE_BARRIER);
        container.setItem(RESET_INDEX, RESET_REDSTONE_BLOCK);
        container.setItem(PREVIOUS_PAGE_INDEX, (page > 1) ? PREV_ARROW : GLASS_PANE);
        container.setItem(NEXT_PAGE_INDEX, (page < getPageCount()) ? NEXT_ARROW : GLASS_PANE);
    }

    public static void loadMusic(SimpleContainer container, int page) {
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

    private static void buildPage(SimpleContainer container, int page) {
        container.clearContent();
        setDefaultMusicMenu(container, page);
        loadMusic(container, page);
    }

    public static void handleResetClick(Player player, Container container, int page) {
        stopTracking();
        DarkAddons.clientHelper.stopMusic();
        player.displayClientMessage(Component.literal("Reset Music").withStyle(ChatFormatting.RED), false);
        if (!(getCurrentTrack() == null)) {
            setCurrentTrack(null);
            refreshMusicMenu(player, page, container);
        }
    }

    public static void handleLoopClick(Player player, Container container, int page) {
        toggleLooping();
        refreshMusicMenu(player, page, container);
    }

    public static void swapMusic(String soundName, Player player) {
        DarkAddons.clientHelper.stopMusic();
        startTracking(player, soundName);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), getSound(soundName), SoundSource.RECORDS, 1.0f, 1.0f);
    }

    public static void handleMusicSwap(Player player, Container container, int slotIndex, int page) {
        String newSoundName = container.getItem(slotIndex).getOrDefault(ModComponents.SOUND_NAME, "");
        boolean isSelected = newSoundName.equals(getCurrentTrack());
        if (isSelected) {
            player.displayClientMessage(Component.literal("Music is already selected").withStyle(ChatFormatting.RED), false);
        } else {
            setCurrentTrack(newSoundName);
            swapMusic(newSoundName, player);
            refreshMusicMenu(player, page, container);
            assert getCurrentTrack() != null;
            player.displayClientMessage(Component.literal("Playing: ").withStyle(ChatFormatting.GREEN).append(Component.literal(getCurrentTrack()).withStyle(ChatFormatting.BLUE)), false);
        }
    }

    public static void closeContainer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }

    public static void shiftPage(Player player, Container container, MusicMenu menu, int page, int delta) {
        int newPage = page + delta;
        menu.setPage(newPage);
        menu.setLastMusicIndex(newPage);
        refreshMusicMenu(player, newPage, container);
    }

    public static void callDefaultMusicMenu(Player player) {
        SimpleContainer container = new SimpleContainer(54);
        buildPage(container, DEFAULT_PAGE);
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> new MusicMenu(containerId, playerInventory, container, DEFAULT_PAGE), Component.literal("Music Menu")));
    }

    public static void refreshMusicMenu(Player player, int page, Container container) {
        if (container instanceof SimpleContainer simple && player.containerMenu instanceof MusicMenu musicMenu) {
            buildPage(simple, page);
            musicMenu.broadcastChanges();
        }
    }
}
