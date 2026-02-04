package com.darkaddons;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
    private long lastClickTime = 0;
    private int page;
    private int pageCount; // This is initialized during buildPage(), instead of when the instance of a musicMenu is created

    public MusicMenu(int id, Inventory inv, Container container, int page) {
        super(MenuType.GENERIC_9x6, id, inv, container, 6);
        this.page = page;
        this.musicContainer = container;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int x) {
        this.page = x;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int x) {
        this.pageCount = x;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (player.level().isClientSide()) return;
        if (clickType != ClickType.PICKUP) return;
        if (slotIndex < 0 || slotIndex >= 54) return;
        if (isMusic(slotIndex, this) || isFunctional(slotIndex, this)) {
            long now = player.level().getGameTime();
            if (now <= lastClickTime + 2) {
                player.displayClientMessage(Component.literal("The menu has been throttled, please slow down!").withStyle(ChatFormatting.RED), false);
                return;
            } else {
                lastClickTime = now;
            }
        } else return;

        if (isFunctional(slotIndex, this)) {
            switch (slotIndex) {
                case CLOSE_INDEX -> closeContainer(player);
                case RESET_INDEX -> handleResetClick(player, musicContainer, page, this);
                case LOOP_INDEX -> handlePlayModeClick(player, musicContainer, page, this, button);
                case SORT_INDEX -> handleSortClick(player, musicContainer, this, button);
                case FILTER_INDEX -> handleFilterClick(player, musicContainer, this, button);
                case PREVIOUS_PAGE_INDEX -> shiftPage(player, musicContainer, this, page, -1, button);
                case NEXT_PAGE_INDEX -> shiftPage(player, musicContainer, this, page, 1, button);
            }
        } else if (isMusic(slotIndex, this)) {
            handleMusicSwap(player, musicContainer, slotIndex, page, this);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return musicContainer.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}