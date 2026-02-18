package com.darkaddons.menu;

import com.darkaddons.api.BaseModMenu;
import com.darkaddons.api.BaseModMenuManager;
import com.darkaddons.api.Sortable;
import com.darkaddons.core.ModComponents;
import com.darkaddons.init.ItemInit;
import com.darkaddons.utils.ModUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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
import static com.darkaddons.utils.ModUtilities.getOptionLore;


public class ItemMenuManager extends BaseModMenuManager {
    public static final int RARITY_INDEX = 51;
    public static final int TYPE_INDEX = 52;
    public static final int RESET_INDEX = 53;
    public static final ItemMenuManager INSTANCE = new ItemMenuManager();
    private final ItemStack RARITY_EYE = createStaticItem(Items.ENDER_EYE, "Item Tier", ChatFormatting.GREEN);
    private final ItemStack TYPE_ANVIL = createStaticItem(Items.ANVIL, "Item Type", ChatFormatting.GREEN);
    private final ItemStack RESET_REDSTONE_BLOCK = createStaticItem(Items.REDSTONE_BLOCK, "Reset All", ChatFormatting.RED);
    private ModComponents.Rarity filterRarity = null;
    private ModComponents.ItemType filterType = null;


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
        Container container = itemMenu.getContainer();

        List<Component> tierLines = new ArrayList<>();
        tierLines.add(EMPTY_LINE);
        tierLines.add(getOptionLore("No Filter", getFilterRarity() == null));
        for (ModComponents.Rarity rarity : ModComponents.Rarity.values()) {
            boolean isSelected = (getFilterRarity() == rarity);
            tierLines.add(getOptionLore(rarity.getDisplayName(), isSelected));
        }
        tierLines.add(EMPTY_LINE);
        tierLines.add(ModUtilities.literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        tierLines.add(ModUtilities.literal("Click to switch filter!", ChatFormatting.YELLOW));

        List<Component> typeLines = new ArrayList<>();
        typeLines.add(EMPTY_LINE);
        typeLines.add(getOptionLore("All", getFilterType() == null));
        for (ModComponents.ItemType type : ModComponents.ItemType.values()) {
            boolean isSelected = (getFilterType() == type);
            typeLines.add(getOptionLore(type.getDisplayName(), isSelected));
        }
        typeLines.add(EMPTY_LINE);
        typeLines.add(ModUtilities.literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        typeLines.add(ModUtilities.literal("Click to switch filter!", ChatFormatting.YELLOW));

        RARITY_EYE.set(DataComponents.LORE, new ItemLore(tierLines));
        TYPE_ANVIL.set(DataComponents.LORE, new ItemLore(typeLines));
        container.setItem(RARITY_INDEX, RARITY_EYE);
        container.setItem(TYPE_INDEX, TYPE_ANVIL);
        if (isNotDefault()) container.setItem(RESET_INDEX, RESET_REDSTONE_BLOCK);
    }

    @Override
    public void handleInput(Player player, BaseModMenu itemMenu, ClickType clickType, int slotIndex, int button) {
        if (isFunctional(slotIndex, itemMenu)) {
            if (handleDefaultInput(player, itemMenu, slotIndex, button)) return;
            switch (slotIndex) {
                case RARITY_INDEX -> handleRarityClick(player, itemMenu, button);
                case TYPE_INDEX -> handleTypeClick(player, itemMenu, button);
                case RESET_INDEX -> handleResetClick(player, itemMenu);
            }
        } else {
            claimItem(player, slotIndex, itemMenu);
        }
    }

    private void handleRarityClick(Player player, BaseModMenu itemMenu, int button) {
        ModComponents.Rarity current = getFilterRarity();
        ModComponents.Rarity first = ModComponents.Rarity.values()[0];
        ModComponents.Rarity last = ModComponents.Rarity.values()[ModComponents.Rarity.values().length - 1];
        if (button == 0) {
            setFilterRarity(current == null ? first : (current == last ? null : current.next()));
        } else if (button == 1) {
            setFilterRarity(current == null ? last : (current == first ? null : current.prev()));
        }
        refreshMenu(player, itemMenu);
    }

    private void handleTypeClick(Player player, BaseModMenu itemMenu, int button) {
        ModComponents.ItemType current = getFilterType();
        ModComponents.ItemType first = ModComponents.ItemType.values()[0];
        ModComponents.ItemType last = ModComponents.ItemType.values()[ModComponents.ItemType.values().length - 1];
        if (button == 0) {
            setFilterType(current == null ? first : (current == last ? null : current.next()));
        } else if (button == 1) {
            setFilterType(current == null ? last : (current == first ? null : current.prev()));
        }
        refreshMenu(player, itemMenu);
    }

    private void handleResetClick(Player player, BaseModMenu itemMenu) {
        setFilterType(null);
        setFilterRarity(null);
        setCurrentSearchQuery("");
        setCurrentSortMode(ItemSortMode.DEFAULT);
        refreshMenu(player, itemMenu);
    }

    @Override
    protected boolean isFunctional(int slotIndex, BaseModMenu itemMenu) {
        return switch (slotIndex) {
            case CLOSE_INDEX, SORT_INDEX, FILTER_INDEX, RARITY_INDEX, TYPE_INDEX -> true;
            case PREVIOUS_PAGE_INDEX -> itemMenu.getPage() > 1;
            case NEXT_PAGE_INDEX -> itemMenu.getPage() < itemMenu.getPageCount();
            case RESET_INDEX -> isNotDefault();
            default -> false;
        };
    }

    private void claimItem(Player player, int slotIndex, BaseModMenu itemMenu) {
        ItemStack itemToGive = itemMenu.getContainer().getItem(slotIndex).copy();
        MutableComponent itemText = itemToGive.getHoverName().copy();

        itemText.withStyle(style -> style.withHoverEvent(new HoverEvent.ShowItem(itemToGive)));

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

    @Override
    public void applyFilterAndSort() {
        super.applyFilterAndSort();

        List<ItemStack> filteredList = getFilteredList();

        if (getFilterType() != null) {
            filteredList.removeIf(stack -> {
                ModComponents.ItemType type = stack.getOrDefault(ModComponents.ITEM_TYPE, null);
                return type != getFilterType();
            });
        }
        if (getFilterRarity() != null) {
            filteredList.removeIf(stack -> {
                ModComponents.Rarity rarity = stack.getOrDefault(ModComponents.RARITY, null);
                return rarity != getFilterRarity();
            });
        }
    }

    public ModComponents.ItemType getFilterType() {
        return filterType;
    }

    public void setFilterType(ModComponents.ItemType filterType) {
        this.filterType = filterType;
    }

    public ModComponents.Rarity getFilterRarity() {
        return filterRarity;
    }

    public void setFilterRarity(ModComponents.Rarity filterRarity) {
        this.filterRarity = filterRarity;
    }

    private boolean isNotDefault() {
        return getFilterRarity() != null || getFilterType() != null || !getCurrentSearchQuery().isEmpty() || getCurrentSortMode() != ItemSortMode.DEFAULT;
    }

    public enum ItemSortMode implements Sortable<ItemStack, ItemSortMode> {
        DEFAULT("Default", null),
        A_Z("A-Z", Comparator.comparing(s -> s.getHoverName().getString())),
        Z_A("Z-A", Comparator.comparing((ItemStack s) -> s.getHoverName().getString()).reversed()),
        RARITY("Rarity", Comparator.comparing(s -> s.getOrDefault(ModComponents.RARITY, ModComponents.Rarity.COMMON)));

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
