package com.darkaddons.menu;

import com.darkaddons.api.BaseModMenu;
import com.darkaddons.api.BaseModMenuManager;
import com.darkaddons.api.Sortable;
import com.darkaddons.init.ItemInit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;


public class ItemMenuManager extends BaseModMenuManager {
    public static final ItemMenuManager INSTANCE = new ItemMenuManager();

    public ItemMenuManager() {
        super(ItemSortMode.DEFAULT, ItemInit.INSTANCE);
    }

    @Override
    protected String getSearchName(ItemStack stack) {
        return stack.getHoverName().getString();
    }

    @Override
    protected void decorateContent(ItemStack s) {
    }

    @Override
    protected List<Sortable<ItemStack, ?>> getSortValues() {
        return List.of(ItemSortMode.values());
    }

    @Override
    protected ItemMenu createMenuInstance(int containerId, Inventory playerInventory) {
        return new ItemMenu(containerId, playerInventory, new SimpleContainer(54), DEFAULT_PAGE);
    }

    @Override
    protected String MenuName() {
        return "Item Menu";
    }

    @Override
    protected void loadGUI(BaseModMenu itemMenu) {
        super.loadGUI(itemMenu);
    }

    @Override
    public void handleInput(Player player, BaseModMenu itemMenu, ClickType clickType, int slotIndex, int button) {
        if (isFunctional(slotIndex, itemMenu)) {
            handleDefaultInput(player, itemMenu, slotIndex, button);
        } else {
            claimItem(player, slotIndex, itemMenu);
        }
    }

    @Override
    protected boolean isFunctional(int slotIndex, BaseModMenu itemMenu) {
        return switch (slotIndex) {
            case CLOSE_INDEX, SORT_INDEX, FILTER_INDEX -> true;
            case PREVIOUS_PAGE_INDEX -> itemMenu.getPage() > 1;
            case NEXT_PAGE_INDEX -> itemMenu.getPage() < itemMenu.getPageCount();
            default -> false;
        };
    }

    private void claimItem(Player player, int slotIndex, BaseModMenu itemMenu) {
        ItemStack itemToGive = itemMenu.getContainer().getItem(slotIndex).copy();
        MutableComponent itemText = itemToGive.getHoverName().copy();

        itemText.withStyle(style -> style
                .withColor(itemToGive.getRarity().color())
                .withHoverEvent(new HoverEvent.ShowItem(itemToGive))
        );

        if (itemToGive.getMaxStackSize() == 1 && player.getInventory().getFreeSlot() == -1) {
            player.displayClientMessage(Component.literal("Your inventory is full!").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (player.getInventory().add(itemToGive)) {
            player.displayClientMessage(Component.literal("You claimed: [").append(itemText).append(Component.literal("]")), false);
            player.inventoryMenu.broadcastChanges();
        } else {
            player.displayClientMessage(Component.literal("Your inventory is full!").withStyle(ChatFormatting.RED), false);
        }
    }

    public enum ItemSortMode implements Sortable<ItemStack, ItemSortMode> {
        DEFAULT("Default", null),
        A_Z("A-Z", Comparator.comparing(s -> s.getHoverName().getString())),
        Z_A("Z-A", Comparator.comparing((ItemStack s) -> s.getHoverName().getString()).reversed());

        private static final ItemSortMode[] MODES = values();
        private final String displayName;
        private final Comparator<ItemStack> sortRule;

        ItemSortMode(String displayName, Comparator<ItemStack> sortRule) {
            this.displayName = displayName;
            this.sortRule = sortRule;
        }

        @Override
        public String getDisplayName() {
            return this.displayName;
        }

        @Override
        public Comparator<ItemStack> getSortRule() {
            return this.sortRule;
        }

        @Override
        public ItemSortMode next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        @Override
        public ItemSortMode prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }
    }
}
