package com.darkaddons.init;

import com.darkaddons.api.BaseModInit;
import com.darkaddons.api.ModInit;
import com.darkaddons.core.ModComponents;
import com.darkaddons.menu.MusicMenuManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

import static com.darkaddons.core.ModSounds.*;
import static com.darkaddons.utils.ModUtilities.literal;

public class MusicInit extends BaseModInit {

    public static final MusicInit INSTANCE = new MusicInit();

    static {
        ModInit.register(INSTANCE);
    }

    @Override
    protected MusicMenuManager getManager() {
        return MusicMenuManager.INSTANCE;
    }

    @Override
    protected String getDataName() {
        return "Music";
    }

    @Override
    public List<Integer> initializeCache() {
        List<Integer> failedIndices = new ArrayList<>();
        cache.clear();

        for (int i = 0; i < getTotalMusicCount(); i++) {
            Item icon = getIcon(i);
            Integer duration = getSoundDuration(i);
            String soundName = getSoundName(i);
            SoundEvent soundEvent = getSound(i);

            if (icon == null || duration == null || soundName == null || soundEvent == null) {
                failedIndices.add(i);
                continue;
            }

            ItemStack item = new ItemStack(icon);
            String length = String.format("%02dm %02ds", duration / 60, duration % 60);
            ItemLore itemLore = new ItemLore(List.of(literal("Duration: ", ChatFormatting.GRAY).append(literal(length, ChatFormatting.GREEN))));
            item.set(DataComponents.CUSTOM_NAME, literal(soundName, ChatFormatting.BLUE));
            item.set(DataComponents.LORE, itemLore);
            item.set(ModComponents.SOUND_NAME, soundName);

            cache.add(item);
        }
        return failedIndices;
    }
}