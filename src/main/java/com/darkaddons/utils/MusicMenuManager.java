package com.darkaddons.utils;

import com.darkaddons.api.BaseModMenuManager;
import com.darkaddons.api.MenuOption;
import com.darkaddons.api.Sortable;
import com.darkaddons.core.ModComponents;
import com.darkaddons.init.MusicInit;
import com.darkaddons.menu.MusicMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import java.util.Objects;

import static com.darkaddons.core.ModSounds.getSound;
import static com.darkaddons.core.ModSounds.getSoundDuration;
import static com.darkaddons.utils.ModUtilities.createStaticItem;

public class MusicMenuManager extends BaseModMenuManager<MusicMenu, MusicMenuManager.MusicSortMode, MusicInit> {
    public static final int STATUS_INDEX = 4;
    public static final int MODE_INDEX = 45;
    public static final int RESET_INDEX = 53;
    public static final MusicMenuManager INSTANCE = new MusicMenuManager();
    private static final MutableComponent SELECTED_LORE = ModUtilities.literal("Selected!", ChatFormatting.RED);
    private static final MutableComponent UNSELECTED_LORE = ModUtilities.literal("Click to select!", ChatFormatting.GREEN);
    private final ItemStack STATUS_DISPLAY = createStaticItem(Items.ENCHANTED_BOOK, "", ChatFormatting.WHITE);
    private final ItemStack MODE_BUTTON = createStaticItem(Items.GOLD_INGOT, "Play Mode", ChatFormatting.GREEN);
    private final ItemStack RESET_REDSTONE_BLOCK = createStaticItem(Items.REDSTONE_BLOCK, "Reset Music", ChatFormatting.RED);
    private String currentTrack = null;
    private MusicPlayMode currentPlayMode = MusicPlayMode.DEFAULT;

    public MusicMenuManager() {
        super(MusicSortMode.DEFAULT, MusicInit.INSTANCE);
    }

    public String getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public MusicPlayMode getCurrentPlayMode() {
        return currentPlayMode;
    }

    public void setCurrentPlayMode(MusicPlayMode currentPlayMode) {
        this.currentPlayMode = currentPlayMode;
    }

    @Override
    protected String getSearchName(ItemStack stack) {
        return stack.getOrDefault(ModComponents.SOUND_NAME, "");
    }

    @Override
    protected void decorateContent(ItemStack s) {
        boolean isSelected = s.getOrDefault(ModComponents.SOUND_NAME, "").equals(getCurrentTrack());
        ItemLore itemLore = s.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(isSelected ? SELECTED_LORE : UNSELECTED_LORE);
        s.set(DataComponents.LORE, itemLore);
        s.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, isSelected);
    }

    @Override
    protected List<MusicSortMode> getSortValues() {
        return List.of(MusicSortMode.values());
    }

    @Override
    protected MusicMenu createMenuInstance(int containerId, Inventory playerInventory) {
        return new MusicMenu(containerId, playerInventory, new SimpleContainer(54), DEFAULT_PAGE);
    }

    @Override
    protected String MenuName() {
        return "Music Menu";
    }

    @Override
    protected void loadGUI(MusicMenu musicMenu) {
        super.loadGUI(musicMenu);

        Container container = musicMenu.getContainer();
        boolean isPlaying = getCurrentTrack() != null;

        MutableComponent status = ModUtilities.literal("Currently Playing: ", ChatFormatting.GREEN)
                .append(ModUtilities.literal(isPlaying ? getCurrentTrack() : "None", ChatFormatting.BLUE, ChatFormatting.RED, isPlaying));

        List<Component> pLines = new ArrayList<>();
        pLines.add(EMPTY_LINE);
        for (MusicPlayMode mode : MusicPlayMode.values()) {
            boolean selected = (mode == getCurrentPlayMode());
            String prefix = selected ? "▶ " : "";
            ChatFormatting color = selected ? ChatFormatting.WHITE : ChatFormatting.GRAY;
            pLines.add(ModUtilities.literal(prefix + mode.getDisplayName(), color));
        }
        pLines.add(EMPTY_LINE);
        pLines.add(ModUtilities.literal("Right-Click to go backwards!", ChatFormatting.WHITE));
        pLines.add(ModUtilities.literal("Click to switch mode!", ChatFormatting.GREEN));

        MODE_BUTTON.set(DataComponents.LORE, new ItemLore(pLines));
        STATUS_DISPLAY.set(DataComponents.CUSTOM_NAME, status);
        container.setItem(STATUS_INDEX, STATUS_DISPLAY);
        container.setItem(MODE_INDEX, MODE_BUTTON);
        container.setItem(RESET_INDEX, RESET_REDSTONE_BLOCK);
    }

    @Override
    public void handleInput(Player player, MusicMenu musicMenu, ClickType clickType, int slotIndex, int button) {
        if (isFunctional(slotIndex, musicMenu)) {
            if (handleDefaultInput(player, musicMenu, slotIndex, button)) return;
            switch (slotIndex) {
                case RESET_INDEX -> handleResetClick(player, musicMenu);
                case MODE_INDEX -> handlePlayModeClick(player, musicMenu, button);
            }
        } else {
            handleMusicSwap(player, slotIndex, musicMenu);
        }
    }

    private void handleResetClick(Player player, MusicMenu musicMenu) {
        MusicLoopHandler.INSTANCE.stopTracking();
        stopMusic(player);
        player.displayClientMessage(Component.literal("Reset Music!").withStyle(ChatFormatting.RED), false);
        setCurrentTrack(null);
        refreshMenu(player, musicMenu);
    }

    private void handlePlayModeClick(Player player, MusicMenu musicMenu, int button) {
        if (button == 0) setCurrentPlayMode(getCurrentPlayMode().next());
        else if (button == 1) setCurrentPlayMode(getCurrentPlayMode().prev());
        refreshMenu(player, musicMenu);
    }

    private void swapMusic(String soundName, Player player) {
        stopMusic(player);
        MusicLoopHandler.INSTANCE.startTracking(player, soundName);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), getSound(soundName), SoundSource.RECORDS, 1.0f, 1.0f);
    }

    private void handleMusicSwap(Player player, int slotIndex, MusicMenu musicMenu) {
        Container container = musicMenu.getContainer();
        String newSoundName = container.getItem(slotIndex).getOrDefault(ModComponents.SOUND_NAME, "");
        boolean isSelected = newSoundName.equals(getCurrentTrack());
        if (isSelected) {
            player.displayClientMessage(Component.literal("Music is already selected!").withStyle(ChatFormatting.RED), false);
        } else {
            setCurrentTrack(newSoundName);
            swapMusic(newSoundName, player);
            player.displayClientMessage(Component.literal("Playing: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(newSoundName).withStyle(ChatFormatting.BLUE)), false);
        }
        refreshMenu(player, musicMenu);
    }

    @Override
    protected boolean isFunctional(int slotIndex, MusicMenu musicMenu) {
        return switch (slotIndex) {
            case CLOSE_INDEX, RESET_INDEX, MODE_INDEX, SORT_INDEX, FILTER_INDEX -> true;
            case PREVIOUS_PAGE_INDEX -> musicMenu.getPage() > 1;
            case NEXT_PAGE_INDEX -> musicMenu.getPage() < musicMenu.getPageCount();
            default -> false;
        };
    }

    public void stopMusic(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundStopSoundPacket(null, SoundSource.RECORDS));
        }
    }

    public enum MusicSortMode implements Sortable<ItemStack, MusicSortMode> {
        DEFAULT("Default", null),
        A_Z("A-Z", Comparator.comparing(s -> s.getOrDefault(ModComponents.SOUND_NAME, ""))),
        Z_A("Z-A", Comparator.comparing((ItemStack s) -> s.getOrDefault(ModComponents.SOUND_NAME, "")).reversed()),
        DURATION("Shortest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, ""))))),
        DURATION_INVERSE("Longest First", Comparator.comparingInt((ItemStack s) -> Objects.requireNonNull(getSoundDuration(s.getOrDefault(ModComponents.SOUND_NAME, "")))).reversed());

        private static final MusicSortMode[] MODES = values();
        private final String displayName;
        private final Comparator<ItemStack> sortRule;

        MusicSortMode(String displayName, Comparator<ItemStack> sortRule) {
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
        public MusicSortMode next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        @Override
        public MusicSortMode prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }
    }

    public enum MusicPlayMode implements MenuOption {
        DEFAULT("Default"),
        LOOP("Loop"),
        AUTOPLAY("Autoplay");

        private static final MusicPlayMode[] MODES = values();
        private final String displayName;

        MusicPlayMode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return this.displayName;
        }

        @Override
        public MusicPlayMode next() {
            return MODES[(ordinal() + 1) % MODES.length];
        }

        @Override
        public MusicPlayMode prev() {
            return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1];
        }
    }
}
