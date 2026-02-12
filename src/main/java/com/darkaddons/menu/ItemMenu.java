package com.darkaddons.menu;

import com.darkaddons.api.BaseModMenu;
import com.darkaddons.utils.ItemMenuManager;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class ItemMenu extends BaseModMenu<ItemMenu, ItemMenuManager> {
    public ItemMenu(int id, Inventory inv, Container container, int page) {
        super(id, inv, container, page, ItemMenuManager.INSTANCE);
    }
}
