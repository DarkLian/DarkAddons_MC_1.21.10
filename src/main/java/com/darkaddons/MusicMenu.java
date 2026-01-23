package com.darkaddons;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.darkaddons.MusicMenuManager.*;

public class MusicMenu extends ChestMenu {
    private final Container musicContainer;
    private int page;
    private int lastMusicIndex;

    public MusicMenu(int id, Inventory inv, Container container, int page) {
        super(MenuType.GENERIC_9x6, id, inv, container, 6);
        this.page = page;
        this.lastMusicIndex = getLastMusicSlotIndex(this.page);
        this.musicContainer = container;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int x) {
        this.page = x;
    }

    public void setLastMusicIndex(int page) {
        this.lastMusicIndex = getLastMusicSlotIndex(page);
    }

    @Override
    public void clicked(int slotIndex, int button, net.minecraft.world.inventory.ClickType clickType, Player player) {
        if (slotIndex < 0 || slotIndex >= 54) return;
        if ((slotIndex >= 10 && slotIndex <= lastMusicIndex)) {
            if (slotIndex % 9 == 0 || slotIndex % 9 == 8) return;
            int musicIndex = getMusicIndex(slotIndex, page);
            handleMusicSwap(player, musicContainer, musicIndex, page);
            return;
        }
        switch (slotIndex) {
            case CLOSE_INDEX -> closeContainer(player);
            case RESET_INDEX -> handleResetClick(player, musicContainer, page);
            case PREVIOUS_PAGE_INDEX -> {
                if (page > 1) shiftPage(player, musicContainer, this, -1);
            }
            case NEXT_PAGE_INDEX -> {
                if (page < pageCount) shiftPage(player, musicContainer, this, 1);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}