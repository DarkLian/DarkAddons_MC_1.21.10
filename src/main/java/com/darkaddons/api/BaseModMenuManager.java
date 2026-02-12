package com.darkaddons.api;

import com.darkaddons.core.ModPackets;
import com.darkaddons.utils.ModUtilities;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.darkaddons.utils.ModUtilities.createStaticItem;

public abstract class BaseModMenuManager<T extends BaseModMenu<T, ?>, S extends Sortable<ItemStack, S>> {
    public static final int EMPTY_INDEX = 10;
    public static final int SORT_INDEX = 46;
    public static final int FILTER_INDEX = 47;
    public static final int PREVIOUS_PAGE_INDEX = 48;
    public static final int CLOSE_INDEX = 49;
    public static final int NEXT_PAGE_INDEX = 50;
    public static final int DEFAULT_PAGE = 1;
    protected final ItemStack GLASS_PANE = createStaticItem(Items.GRAY_STAINED_GLASS_PANE, "", ChatFormatting.WHITE);
    protected final ItemStack EMPTY_FEATHER = createStaticItem(Items.FEATHER, "Nothing found :(", ChatFormatting.RED);
    protected final ItemStack SORT_HOPPER = createStaticItem(Items.HOPPER, "Sort", ChatFormatting.GREEN);
    protected final ItemStack FILTER_SIGN = createStaticItem(Items.OAK_SIGN, "Search", ChatFormatting.GREEN);
    protected final ItemStack NEXT_ARROW = createStaticItem(Items.ARROW, "Next Page", ChatFormatting.GREEN);
    protected final ItemStack PREV_ARROW = createStaticItem(Items.ARROW, "Previous Page", ChatFormatting.GREEN);
    protected final ItemStack CLOSE_BARRIER = createStaticItem(Items.BARRIER, "Close", ChatFormatting.RED);
    protected final MutableComponent EMPTY_LINE = ModUtilities.literal("", ChatFormatting.WHITE);
    private final List<ItemStack> FILTERED_LIST = new ArrayList<>();
    private final ModInit dataProvider;
    private String currentSearchQuery = "";
    private S currentSortMode;

    public BaseModMenuManager(S sortMode, ModInit dataProvider) {
        this.currentSortMode = sortMode;
        this.dataProvider = dataProvider;
    }

    protected abstract String getSearchName(ItemStack stack);

    protected abstract void decorateContent(ItemStack itemStack);

    protected abstract List<S> getSortValues();

    protected abstract void handleInput(Player player, T menu, ClickType clickType, int slotIndex, int button);

    protected abstract T createMenuInstance(int containerId, Inventory playerInventory);

    protected abstract String MenuName();

    protected abstract boolean isFunctional(int slotIndex, T menu);

    protected int getPageContentCount(int page) {
        // 0 ~ 28
        // if 0 it indicates that there is no content in the page
        int remaining = FILTERED_LIST.size() - 28 * (page - 1);
        return Math.max(0, Math.min(remaining, 28));
    }

    protected Integer getLastContentSlotIndex(int page) {
        int a = getPageContentCount(page);
        if (a == 0) return null;
        return (a - 1) + 10 + 2 * ((a - 1) / 7);
    }

    private void createMenu(T menu) {
        drawBorders(menu);
        loadGUI(menu);
        loadContent(menu);
    }

    private void drawBorders(T menu) {
        Container container = menu.getContainer();
        for (int i = 0; i < 9; i++) {
            container.setItem(i, GLASS_PANE);
            container.setItem(45 + i, GLASS_PANE);
        }
        for (int i = 1; i < 5; i++) {
            container.setItem(i * 9, GLASS_PANE);
            container.setItem(i * 9 + 8, GLASS_PANE);
        }
    }

    protected void loadGUI(T menu) {
        Container container = menu.getContainer();
        int page = menu.getPage();
        int totalPages = menu.getPageCount();

        MutableComponent pageInfo = ModUtilities.literal("(" + page + "/" + totalPages + ")", ChatFormatting.GRAY);
        ItemLore navigationLore = new ItemLore(List.of(
                pageInfo,
                EMPTY_LINE,
                ModUtilities.literal("Right-Click to skip!", ChatFormatting.WHITE),
                ModUtilities.literal("Click to turn page!", ChatFormatting.YELLOW)
        ));

        List<Component> sortLore = new ArrayList<>();
        sortLore.add(EMPTY_LINE);
        for (S mode : getSortValues()) {
            boolean selected = (mode == getCurrentSortMode());
            String prefix = selected ? "▶ " : "";
            ChatFormatting color = selected ? ChatFormatting.WHITE : ChatFormatting.GRAY;
            sortLore.add(ModUtilities.literal(prefix + mode.getDisplayName(), color));
        }
        sortLore.add(EMPTY_LINE);
        sortLore.add(ModUtilities.literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        sortLore.add(ModUtilities.literal("Click to switch filter!", ChatFormatting.GREEN));

        List<Component> searchLore = new ArrayList<>();
        searchLore.add(EMPTY_LINE);
        if (!getCurrentSearchQuery().isEmpty()) {
            searchLore.add(ModUtilities.literal("Filtered: ", ChatFormatting.GRAY).append(ModUtilities.literal(getCurrentSearchQuery(), ChatFormatting.GREEN)));
            searchLore.add(EMPTY_LINE);
        }
        searchLore.add(ModUtilities.literal("Right-Click to clear!", ChatFormatting.WHITE));
        searchLore.add(ModUtilities.literal("Click to edit filter!", ChatFormatting.YELLOW));

        SORT_HOPPER.set(DataComponents.LORE, new ItemLore(sortLore));
        FILTER_SIGN.set(DataComponents.LORE, new ItemLore(searchLore));
        NEXT_ARROW.set(DataComponents.LORE, navigationLore);
        PREV_ARROW.set(DataComponents.LORE, navigationLore);
        container.setItem(SORT_INDEX, SORT_HOPPER);
        container.setItem(FILTER_INDEX, FILTER_SIGN);
        container.setItem(CLOSE_INDEX, CLOSE_BARRIER);
        container.setItem(PREVIOUS_PAGE_INDEX, (page > 1) ? PREV_ARROW : GLASS_PANE);
        container.setItem(NEXT_PAGE_INDEX, (page < totalPages) ? NEXT_ARROW : GLASS_PANE);
    }

    private void loadContent(T menu) {
        Container container = menu.getContainer();
        int page = menu.getPage();
        int pageContentCount = getPageContentCount(page);
        if (pageContentCount == 0) {
            container.setItem(EMPTY_INDEX, EMPTY_FEATHER);
        } else {
            for (int i = 0; i < pageContentCount; i++) {
                int contentIndex = i + 28 * (page - 1);
                int slotIndex = 10 + i + 2 * (i / 7);
                ItemStack menuItem = getFilteredList().get(contentIndex).copy();
                decorateContent(menuItem);
                container.setItem(slotIndex, menuItem);
            }
        }
    }

    private void buildMenu(T menu) {
        Container container = menu.getContainer();
        container.clearContent();
        applyFilterAndSort();
        menu.setPageCount(Math.max((getFilteredList().size() + 27) / 28, 1));
        createMenu(menu);
    }

    public void closeContainer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }

    protected void shiftPage(Player player, T menu, int delta, int button) {
        int page = menu.getPage();
        if (button == 0) {
            menu.setPage(page + delta);
        } else if (button == 1) {
            menu.setPage((delta == 1) ? menu.getPageCount() : DEFAULT_PAGE);
        } else return;
        refreshMenu(player, menu);
    }

    protected void refreshMenu(Player player, T menu) {
        if (player.containerMenu instanceof BaseModMenu<?, ?> openMenu) {
            if (openMenu.getManager() == this) {
                buildMenu(menu);
                menu.broadcastChanges();
            }
        }
    }

    public void callMenu(Player player) {
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> {
            T menu = createMenuInstance(containerId, playerInventory);
            buildMenu(menu);
            return menu;
        }, Component.literal(MenuName())));
    }

    public void handleSortClick(Player player, T menu, int button) {
        if (button == 0) setCurrentSortMode(getCurrentSortMode().next());
        else if (button == 1) setCurrentSortMode(getCurrentSortMode().prev());
        menu.setPage(DEFAULT_PAGE);
        refreshMenu(player, menu);
    }

    public void handleFilterClick(Player player, T menu, int button) {
        if (button == 0) {
            getDataProvider().setSearching(true);
            closeContainer(player);
            player.displayClientMessage(Component.literal("Type in chat and press Enter!").withStyle(ChatFormatting.YELLOW), false);
            if (player instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new ModPackets.OpenChatPayload());
            }
        } else if (button == 1) {
            setCurrentSearchQuery("");
            menu.setPage(DEFAULT_PAGE);
            refreshMenu(player, menu);
        }
    }

    public void applyFilterAndSort() {
        String searchQuery = getCurrentSearchQuery();
        S mode = getCurrentSortMode();
        Comparator<ItemStack> rule = mode.getSortRule();

        List<ItemStack> items = new ArrayList<>(getDataProvider().getCache());

        if (!searchQuery.isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase();
            items.removeIf(stack -> {
                String displayName = getSearchName(stack).toLowerCase();
                return !displayName.contains(lowerQuery);
            });
        }

        if (rule != null) {
            items.sort(rule);
        }

        List<ItemStack> filteredList = getFilteredList();
        filteredList.clear();
        filteredList.addAll(items);
    }

    protected boolean handleDefaultInput(Player player, T menu, int slotIndex, int button) {
        switch (slotIndex) {
            case CLOSE_INDEX -> {
                closeContainer(player);
                return true;
            }
            case SORT_INDEX -> {
                handleSortClick(player, menu, button);
                return true;
            }
            case FILTER_INDEX -> {
                handleFilterClick(player, menu, button);
                return true;
            }
            case PREVIOUS_PAGE_INDEX -> {
                shiftPage(player, menu, -1, button);
                return true;
            }
            case NEXT_PAGE_INDEX -> {
                shiftPage(player, menu, 1, button);
                return true;
            }
        }
        return false;
    }

    protected boolean isContent(int slotIndex, T menu) {
        Integer lastMusicIndex = getLastContentSlotIndex(menu.getPage());
        if (lastMusicIndex != null && slotIndex >= 10 && slotIndex <= lastMusicIndex) {
            return slotIndex % 9 != 0 && slotIndex % 9 != 8;
        }
        return false;
    }


    public String getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    public void setCurrentSearchQuery(String currentSearchQuery) {
        this.currentSearchQuery = currentSearchQuery;
    }

    public S getCurrentSortMode() {
        return this.currentSortMode;
    }

    public void setCurrentSortMode(S sortMode) {
        this.currentSortMode = sortMode;
    }

    protected ModInit getDataProvider() {
        return dataProvider;
    }

    public List<ItemStack> getFilteredList() {
        return this.FILTERED_LIST;
    }
}