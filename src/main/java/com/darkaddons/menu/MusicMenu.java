package com.darkaddons.menu;

import com.darkaddons.api.BaseModMenu;
import com.darkaddons.utils.MusicMenuManager;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

import static com.darkaddons.utils.MusicMenuManager.INSTANCE;

public class MusicMenu extends BaseModMenu<MusicMenu, MusicMenuManager> {
    public MusicMenu(int id, Inventory inv, Container container, int page) {
        super(id, inv, container, page, INSTANCE);
    }
}