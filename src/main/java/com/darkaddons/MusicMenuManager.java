package com.darkaddons;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.darkaddons.ModSounds.*;
import static com.darkaddons.item.MusicStick.currentTrack;

public class MusicMenuManager {
    public static final int resetIndex = getIndex(5, 4, 9);
    public static final int nextPageIndex = getIndex(5, 5, 9);
    public static final int previousPageIndex = getIndex(5, 3, 9);
    public static final int defaultPage = 1;
    public static final int defaultPageMusicCount = getPageMusicCount(defaultPage);
    public static int pageCount = (getMusicCount() % 28 == 0 && getMusicCount() != 0) ? getMusicCount() / 28 : (getMusicCount() / 28) + 1;

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
        // return value might be 0
        int a = Math.min(getMusicCount() - 28 * (page - 1), 28);
        if (a < 0) throw new IllegalArgumentException("Invalid page input, pageMusicCount is out of boundary");
        return a;
    }

    public static int getLastMusicSlotIndex(int page) {
        if (getPageMusicCount(page) == 0) throw new IllegalStateException("The page has no music");
        return getIndex(1 + getPageMusicCount(page) / 8, 1 + (getPageMusicCount(page) - 1) % 7, 9);
    }

    public static int getIndex(int i, int j, int width) {
        if (i < 0 || i > 5 || j < 0 || j > 8 || width < 1 || width > 9)
            throw new IllegalArgumentException("Index out of gui boundary");
        return i * width + j;
    }

    public static void setDefaultMusicMenu(SimpleContainer container, int page) {
        ItemStack glass = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        ItemStack nextPageArrow = new ItemStack(Items.ARROW);
        ItemStack previousPageArrow = new ItemStack(Items.ARROW);
        glass.set(DataComponents.CUSTOM_NAME, Component.empty());
        glass.set(DataComponents.LORE, ItemLore.EMPTY);
        nextPageArrow.set(DataComponents.CUSTOM_NAME, Component.literal("Next page").withStyle(ChatFormatting.GREEN).withStyle(style -> style.withItalic(false)));
        previousPageArrow.set(DataComponents.CUSTOM_NAME, Component.literal("Previous page").withStyle(ChatFormatting.GREEN).withStyle(style -> style.withItalic(false)));

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                for (int j = 0; j < 9; j++) {
                    container.setItem(getIndex(i, j, 9), glass);
                }
            } else if (i == 5) {
                for (int j = 0; j < 9; j++) {
                    switch (j) {
                        case 3:
                            if (page > 1) container.setItem(previousPageIndex, previousPageArrow);
                            else container.setItem(previousPageIndex, glass);
                            break;
                        case 4:
                            break;
                        case 5:
                            if (page < pageCount) container.setItem(nextPageIndex, nextPageArrow);
                            else container.setItem(nextPageIndex, glass);
                            break;
                        default:
                            container.setItem(getIndex(i, j, 9), glass);
                    }
                }
            } else {
                container.setItem(getIndex(i, 0, 9), glass);
                container.setItem(getIndex(i, 8, 9), glass);
            }
        }
    }

    public static void setResetButton(SimpleContainer container) {
        ItemStack barrier = new ItemStack(Items.BARRIER);
        barrier.set(DataComponents.CUSTOM_NAME, Component.literal("Reset").withStyle(ChatFormatting.RED).withStyle(style -> style.withItalic(false)));
        container.setItem(getIndex(5, 4, 9), barrier);
    }

    public static void loadMusic(SimpleContainer container, int page, int pageMusicCount) {
        if (pageMusicCount <= 0) return;
        for (int i = 0; i < pageMusicCount; i++) {
            int musicIndex = i + 28 * (page - 1); //page is validated by the pageMusicCount
            ItemStack menuItem = new ItemStack(getItem(musicIndex));
            String message = (currentTrack.equals(getSoundName(musicIndex))) ? "Selected" : "Left-click to select";
            int slotIndex = getSlotIndex(i);
            List<Component> loreLines = List.of(Component.literal(message).withStyle(ChatFormatting.GREEN).withStyle(style -> style.withItalic(false)));
            menuItem.set(DataComponents.LORE, new ItemLore(loreLines));
            menuItem.set(DataComponents.CUSTOM_NAME, Component.literal(getSoundName(musicIndex)).withStyle(ChatFormatting.BLUE).withStyle(style -> style.withItalic(false)));
            menuItem.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, currentTrack.equals(getSoundName(musicIndex)));
            container.setItem(slotIndex, menuItem);
        }
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
        setDefaultMusicMenu(container, defaultPage);
        setResetButton(container);
        loadMusic(container, defaultPage, defaultPageMusicCount);
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> new MusicMenu(containerId, playerInventory, container, defaultPage), Component.literal("Music Menu")));
    }

    public static void refreshMusicMenu(Player player, int page, Container container) {
        if (container instanceof SimpleContainer simple) {
            container.clearContent();
            setDefaultMusicMenu(simple, page);
            setResetButton(simple);
            loadMusic(simple, page, getPageMusicCount(page));
        }
        if (player.containerMenu instanceof MusicMenu musicMenu) {
            musicMenu.broadcastChanges();
        }
    }
}
