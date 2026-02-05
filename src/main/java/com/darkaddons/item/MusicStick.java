package com.darkaddons.item;

import com.darkaddons.core.ModStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.darkaddons.core.ModSounds.getTotalMusicCount;
import static com.darkaddons.util.MusicMenuManager.handleMusicInput;

public class MusicStick extends Item {

    public MusicStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        handleMusicInput(level, player);
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        boolean isPlaying = ModStats.currentTrack != null;
        consumer.accept(Component.literal("Right-click to open music menu!").withStyle(ChatFormatting.GREEN));
        consumer.accept(Component.literal("Music count: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(getTotalMusicCount())).withStyle(ChatFormatting.BLUE)));
        consumer.accept(Component.literal("Currently Playing: ").withStyle(ChatFormatting.GRAY).append(Component.literal(isPlaying ? ModStats.currentTrack : "None").withStyle(isPlaying ? ChatFormatting.BLUE : ChatFormatting.RED)));
        consumer.accept(Component.literal("Mode: ").withStyle(ChatFormatting.GRAY).append(Component.literal(ModStats.currentPlayMode.getDisplayName()).withStyle(ChatFormatting.GREEN)));
    }
}
