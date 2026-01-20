package com.darkaddons;

import com.darkaddons.utils.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;


public class ClientUtilities implements ClientHelper {
    @Override
    public void stopMusic() {
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.RECORDS);
    }

    @Override
    public boolean isShiftPressed() {
        try {
            return Minecraft.getInstance().hasShiftDown();
        } catch (Exception e) {
            return false;
        }
    }
}