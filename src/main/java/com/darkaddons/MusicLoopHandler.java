package com.darkaddons;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

import static com.darkaddons.ModSounds.getSound;
import static com.darkaddons.ModSounds.getSoundDurationTick;
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
            if (isLooping()) {
                SoundEvent soundEvent = Objects.requireNonNull(getSound(getCurrentTrack()));
                DarkAddons.clientHelper.stopMusic();
                startTracking(player, getCurrentTrack());
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.RECORDS, 1.0f, 1.0f);
            } else {
                stopTracking();
                setCurrentTrack(null);
            }
        }
    }
}
