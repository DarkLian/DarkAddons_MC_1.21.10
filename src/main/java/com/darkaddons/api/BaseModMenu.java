package com.darkaddons.api;

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

public abstract class BaseModMenu<T extends BaseModMenu<T, M>, M extends BaseModMenuManager<T, ?>> extends ChestMenu {
    private final M manager;
    private long lastClickTime = 0;
    private int page;
    private int pageCount; // This is initialized during buildMenu(), instead of when the instance of a menu is created

    public BaseModMenu(int id, Inventory inv, Container container, int page, M manager) {
        super(MenuType.GENERIC_9x6, id, inv, container, 6);
        this.page = page;
        this.manager = manager;
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

    public M getManager() {
        return this.manager;
    }

    public long getLastClickTime() {
        return lastClickTime;
    }

    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (player.level().isClientSide()) return;
        if (clickType != ClickType.PICKUP) return;
        if (slotIndex < 0 || slotIndex >= 54) return;
        M manager = this.getManager();

        @SuppressWarnings("unchecked")
        T menu = (T) this;

        if (manager.isContent(slotIndex, menu) || manager.isFunctional(slotIndex, menu)) {
            long now = player.level().getGameTime();
            if (now <= getLastClickTime() + 2) {
                player.displayClientMessage(Component.literal("The menu has been throttled, please slow down!").withStyle(ChatFormatting.RED), false);
                return;
            } else {
                setLastClickTime(now);
            }
        } else return;
        manager.handleInput(player, menu, clickType, slotIndex, button);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.getContainer().stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}