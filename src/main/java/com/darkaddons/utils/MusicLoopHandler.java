package com.darkaddons.utils;

import com.darkaddons.core.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

import static com.darkaddons.core.ModSounds.getSound;
import static com.darkaddons.core.ModSounds.getSoundDurationTick;


public class MusicLoopHandler {
    public static final MusicLoopHandler INSTANCE = new MusicLoopHandler();
    private Long endTime = null;

    public void startTracking(Player player, String soundName) {
        long soundDurationTick = Objects.requireNonNull(getSoundDurationTick(soundName));
        endTime = player.level().getGameTime() + soundDurationTick;
    }

    public void stopTracking() {
        endTime = null;
    }

    public void onTick(Player player) {
        if (endTime == null || MusicMenuManager.INSTANCE.getCurrentTrack() == null) return;
        if (player.level().getGameTime() >= endTime) {
            MusicMenuManager.MusicPlayMode currentPlayMode = MusicMenuManager.INSTANCE.getCurrentPlayMode();
            switch (currentPlayMode) {
                case DEFAULT -> reset();
                case LOOP -> {
                    String nextMusic = Objects.requireNonNull(MusicMenuManager.INSTANCE.getCurrentTrack());
                    playHelper(player, nextMusic);
                }
                case AUTOPLAY -> { // Autoplay depends on current filtered music list, empty -> stop; contains currentTrack -> play next (if last then jump back to first one); doesn't contain -> get first one
                    String currentTrack = MusicMenuManager.INSTANCE.getCurrentTrack();
                    List<ItemStack> filteredList = MusicMenuManager.INSTANCE.getFilteredList();
                    boolean inList = false;
                    int index = 0;
                    for (ItemStack s : filteredList) {
                        if (s.getOrDefault(ModComponents.SOUND_NAME, "").equals(currentTrack)) {
                            inList = true;
                            index = filteredList.indexOf(s);
                            break;
                        }
                    }
                    if (inList) {
                        boolean isLast = filteredList.getLast().getOrDefault(ModComponents.SOUND_NAME, "").equals(currentTrack);
                        String nextMusic = (isLast) ? filteredList.getFirst().getOrDefault(ModComponents.SOUND_NAME, "") : filteredList.get(index + 1).getOrDefault(ModComponents.SOUND_NAME, "");
                        playHelper(player, nextMusic);
                    } else {
                        if (filteredList.isEmpty()) reset();
                        else {
                            String nextMusic = filteredList.getFirst().getOrDefault(ModComponents.SOUND_NAME, "");
                            playHelper(player, nextMusic);
                        }
                    }
                }
            }
        }
    }

    private void playHelper(Player player, String musicName) {
        MusicMenuManager.INSTANCE.setCurrentTrack(musicName);
        SoundEvent soundEvent = Objects.requireNonNull(getSound(musicName));
        MusicMenuManager.INSTANCE.stopMusic(player);
        startTracking(player, musicName);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.RECORDS, 1.0f, 1.0f);
        player.displayClientMessage(Component.literal("Playing: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(musicName).withStyle(ChatFormatting.BLUE)), false);
    }

    private void reset() {
        stopTracking();
        MusicMenuManager.INSTANCE.setCurrentTrack(null);
    }


}
