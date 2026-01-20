package com.darkaddons;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.darkaddons.ModSounds.*;
import static com.darkaddons.item.MusicStick.currentTrack;

public class MusicMenuManager {
    public static final int resetIndex = 49;
    public static final int nextPageIndex = 50;
    public static final int previousPageIndex = 48;
    public static final int defaultPage = 1;
    private static final ItemStack GLASS_PANE = createStaticItem(Items.GRAY_STAINED_GLASS_PANE, "", ChatFormatting.WHITE);
    private static final ItemStack NEXT_ARROW = createStaticItem(Items.ARROW, "Next page", ChatFormatting.GREEN);
    private static final ItemStack PREV_ARROW = createStaticItem(Items.ARROW, "Previous page", ChatFormatting.GREEN);
    private static final ItemStack RESET_BARRIER = createStaticItem(Items.BARRIER, "Reset", ChatFormatting.RED);
    private static final ItemLore SELECTED_LORE = new ItemLore(List.of(literal("Selected", ChatFormatting.GREEN)));
    private static final ItemLore UNSELECTED_LORE = new ItemLore(List.of(literal("Left-click to select", ChatFormatting.GREEN)));
    public static int pageCount;
    private static ItemStack[] MUSIC_ITEM_CACHE;

    public static int getMusicIndex(int slotIndex, int page) {
        int a = 28 * (page - 1) + slotIndex - 10 - 2 * ((slotIndex - 10) / 9);
        if (a < 0 || a >= getMusicCount()) throw new IllegalArgumentException("Music index not found");
        return a;
    }

    public static int getSlotIndex(int i) {
        if (i < 0 || i > 27) throw new IllegalArgumentException("Index out of bound");
        return 10 + i + 2 * (i / 7);
    }

    public static int getPageMusicCount(int page) {
        // 0 ~ 28
        // if 0 it indicates that there is no content in the page
        int remaining = getMusicCount() - 28 * (page - 1);
        return Math.max(0, Math.min(remaining, 28));
    }

    public static int getLastMusicSlotIndex(int page) {
        if (getPageMusicCount(page) == 0) throw new IllegalStateException("The page has no music");
        return getSlotIndex(getPageMusicCount(page) - 1);
    }

    // Return non italic colored component literal
    private static Component literal(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(s -> s.withColor(color).withItalic(false));
    }

    private static ItemStack createStaticItem(Item item, String name, ChatFormatting color) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.CUSTOM_NAME, literal(name, color));
        itemStack.set(DataComponents.LORE, ItemLore.EMPTY);
        return itemStack;
    }

    public static void initializeMusicCache() {
        int count = getMusicCount();
        pageCount = Math.max((count + 27) / 28, 1);
        MUSIC_ITEM_CACHE = new ItemStack[count];
        for (int i = 0; i < count; i++) {
            ItemStack item = new ItemStack(getItem(i));
            item.set(DataComponents.CUSTOM_NAME, literal(getSoundName(i), ChatFormatting.BLUE));
            MUSIC_ITEM_CACHE[i] = item;
        }
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
        container.setItem(resetIndex, RESET_BARRIER);
        container.setItem(previousPageIndex, (page > 1) ? PREV_ARROW : GLASS_PANE);
        container.setItem(nextPageIndex, (page < pageCount) ? NEXT_ARROW : GLASS_PANE);
    }

    public static void loadMusic(SimpleContainer container, int page, int pageMusicCount) {
        if (pageMusicCount == 0) return;
        for (int i = 0; i < pageMusicCount; i++) {
            int musicIndex = i + 28 * (page - 1);
            boolean isSelected = currentTrack.equals(getSoundName(musicIndex));
            ItemStack menuItem = MUSIC_ITEM_CACHE[musicIndex].copy();
            menuItem.set(DataComponents.LORE, isSelected ? SELECTED_LORE : UNSELECTED_LORE);
            menuItem.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, isSelected);
            container.setItem(getSlotIndex(i), menuItem);
        }
    }

    private static void buildPage(SimpleContainer container, int page) {
        container.clearContent();
        setDefaultMusicMenu(container, page);
        loadMusic(container, page, getPageMusicCount(page));
    }

    public static void handleResetClick(Player player, Container container, int page) {
        DarkAddons.clientHelper.stopMusic();
        player.displayClientMessage(Component.literal("Reset Music").withStyle(ChatFormatting.RED), false);
        if (!currentTrack.equals("None")) {
            currentTrack = "None";
            refreshMusicMenu(player, page, container);
        }
    }

    public static void swapMusic(int index, Player player) {
        Level level = player.level();
        DarkAddons.clientHelper.stopMusic();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), getSound(index), net.minecraft.sounds.SoundSource.RECORDS, 1.0f, 1.0f);
    }

    public static void handleMusicSwap(Player player, Container container, int musicIndex, int page) {
        if (currentTrack.equals(getSoundName(musicIndex))) {
            player.displayClientMessage(Component.literal("Music is already selected").withStyle(ChatFormatting.RED), false);
        } else {
            currentTrack = getSoundName(musicIndex);
            swapMusic(musicIndex, player);
            refreshMusicMenu(player, page, container);
            player.displayClientMessage(Component.literal("Playing: ").withStyle(ChatFormatting.GREEN).append(Component.literal(currentTrack).withStyle(ChatFormatting.BLUE)), false);
        }
    }

    public static void shiftPage(Player player, Container container, MusicMenu menu, int delta) {
        int newPage = menu.getPage() + delta;
        menu.setPage(newPage);
        menu.setLastMusicIndex(newPage);
        refreshMusicMenu(player, newPage, container);
    }

    public static void callDefaultMusicMenu(Player player) {
        SimpleContainer container = new SimpleContainer(54);
        buildPage(container, defaultPage);
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> new MusicMenu(containerId, playerInventory, container, defaultPage), Component.literal("Music Menu")));
    }

    public static void refreshMusicMenu(Player player, int page, Container container) {
        if (container instanceof SimpleContainer simple) {
            buildPage(simple, page);
        }
        if (player.containerMenu instanceof MusicMenu musicMenu) {
            musicMenu.broadcastChanges();
        }
    }
}
