package com.darkaddons.menu;

import com.darkaddons.api.BaseModMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class MusicMenu extends BaseModMenu {
    public MusicMenu(int id, Inventory inv, Container container, int page) {
        super(id, inv, container, page, MusicMenuManager.INSTANCE);
    }
}