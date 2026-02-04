package com.darkaddons;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

import static com.darkaddons.ModMusicPlayer.PlayMode;
import static com.darkaddons.ModSounds.getSound;
import static com.darkaddons.ModSounds.getSoundDurationTick;
import static com.darkaddons.MusicMenuManager.getFilteredList;
import static com.darkaddons.MusicMenuManager.stopMusic;
import static com.darkaddons.item.MusicStick.*;

public class MusicLoopHandler {
    private static Long endTime = null;

    public static void startTracking(Player player, String soundName) {
        long soundDurationTick = Objects.requireNonNull(getSoundDurationTick(soundName));
        endTime = player.level().getGameTime() + soundDurationTick;
    }

    public static void stopTracking() {
        endTime = null;
    }

    public static void onTick(Player player) {
        if (endTime == null || getCurrentTrack() == null) return;
        if (player.level().getGameTime() >= endTime) {
            PlayMode currentPlayMode = getCurrentPlayMode();
            switch (currentPlayMode) {
                case DEFAULT -> reset();
                case LOOP -> {
                    String nextMusic = Objects.requireNonNull(getCurrentTrack());
                    playHelper(player, nextMusic);
                }
                case AUTOPLAY -> { // Autoplay depends on current filtered music list, empty -> stop; contains currentTrack -> play next (if last then jump back to first one); doesn't contain -> get first one
                    String currentTrack = getCurrentTrack();
                    List<ItemStack> filteredList = getFilteredList();
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

    private static void playHelper(Player player, String musicName) {
        setCurrentTrack(musicName);
        SoundEvent soundEvent = Objects.requireNonNull(getSound(musicName));
        stopMusic(player);
        startTracking(player, musicName);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.RECORDS, 1.0f, 1.0f);
    }

    private static void reset() {
        stopTracking();
        setCurrentTrack(null);
    }


}
