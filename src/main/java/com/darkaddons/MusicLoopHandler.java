package com.darkaddons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import static com.darkaddons.ModSounds.*;
import static com.darkaddons.item.MusicStick.*;

public class MusicLoopHandler {
    private static Long endTime = null;

    public static void startTracking(Player player, int musicIndex) {
        endTime = player.level().getGameTime() + getSoundDurationTick(musicIndex);
    }

    public static void stopTracking() {
        endTime = null;
    }

    public static void onTick(Player player) {
        if (endTime == null) return;
        if (player.level().getGameTime() >= endTime) {
            if (isLooping()) {
                DarkAddons.clientHelper.stopMusic();
                int index = getMusicIndex(getCurrentTrack());
                startTracking(player, index);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), getSound(index), SoundSource.RECORDS, 1.0f, 1.0f);
            } else {
                stopTracking();
                setCurrentTrack("None");
            }
        }
    }
}
