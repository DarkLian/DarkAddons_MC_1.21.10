package com.darkaddons.init;

import com.darkaddons.api.BaseModInit;
import com.darkaddons.api.ModInit;
import com.darkaddons.utils.ItemMenuManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.darkaddons.core.ModItems.getItem;
import static com.darkaddons.core.ModItems.getTotalItemCount;

public class ItemInit extends BaseModInit {
    public static final ItemInit INSTANCE = new ItemInit();

    static {
        ModInit.register(INSTANCE);
    }

    @Override
    protected ItemMenuManager getManager() {
        return ItemMenuManager.INSTANCE;
    }

    @Override
    protected String getDataName() {
        return "Items";
    }

    @Override
    public List<Integer> initializeCache() {
        List<Integer> failedIndices = new ArrayList<>();
        cache.clear();
        for (int i = 0; i < getTotalItemCount(); i++) {
            Item item = getItem(i);
            if (item == null) {
                failedIndices.add(i);
                continue;
            }
            ItemStack menuItem = new ItemStack(item);
            cache.add(menuItem);
        }
        return failedIndices;
    }
}