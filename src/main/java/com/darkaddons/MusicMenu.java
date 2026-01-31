package com.darkaddons;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.darkaddons.MusicMenuManager.*;

public class MusicMenu extends ChestMenu {
    private final Container musicContainer;
    private int page;
    private Integer lastMusicIndex;

    public MusicMenu(int id, Inventory inv, Container container, int page) {
        super(MenuType.GENERIC_9x6, id, inv, container, 6);
        this.page = page;
        this.lastMusicIndex = getLastMusicSlotIndex(this.page);
        this.musicContainer = container;
    }

    public void setPage(int x) {
        this.page = x;
    }

    public void setLastMusicIndex(int page) {
        this.lastMusicIndex = getLastMusicSlotIndex(page);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (player.level().isClientSide()) return;
        if (slotIndex < 0 || slotIndex >= 54 || clickType == ClickType.PICKUP_ALL) return;
        switch (slotIndex) {
            case CLOSE_INDEX -> closeContainer(player);
            case RESET_INDEX -> handleResetClick(player, musicContainer, page);
            case LOOP_INDEX -> handleLoopClick(player, musicContainer, page);
            case FILTER_INDEX -> handleFilterClick(player, musicContainer, this);
            case PREVIOUS_PAGE_INDEX -> {
                if (page > 1) shiftPage(player, musicContainer, this, page, -1);
            }
            case NEXT_PAGE_INDEX -> {
                if (page < getPageCount()) shiftPage(player, musicContainer, this, page, 1);
            }
        }
        if (lastMusicIndex == null) return;
        if (slotIndex >= 10 && slotIndex <= lastMusicIndex) {
            if (slotIndex % 9 == 0 || slotIndex % 9 == 8) return;
            handleMusicSwap(player, musicContainer, slotIndex, page);
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