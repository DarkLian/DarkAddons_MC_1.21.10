package com.darkaddons;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.darkaddons.DarkAddons.MOD_ID;

public class ModSounds {
    // Add sounds here
    public static final SoundEvent absolution = registerSound("absolution");
    public static final SoundEvent dreams = registerSound("dreams");
    public static final SoundEvent regality = registerSound("regality");
    public static final SoundEvent village_theme = registerSound("village_theme");
    public static final SoundEvent rising_sun = registerSound("rising_sun");
    public static final SoundEvent fantasy = registerSound("fantasy");
    public static final SoundEvent world = registerSound("world");
    public static final SoundEvent ice_cream_sandwich = registerSound("ice_cream_sandwich");
    public static final SoundEvent levitate = registerSound("levitate");
    public static final SoundEvent lanterns = registerSound("lanterns");
    public static final SoundEvent zayton = registerSound("zayton");
    private static final TrackEntry[] musicEntries = {
            new TrackEntry(ModSounds.absolution, "Makkon - Absolution", Items.END_CRYSTAL),
            new TrackEntry(ModSounds.dreams, "Dreams", Items.CLOCK),
            new TrackEntry(ModSounds.regality, "Aurelleah - Regality", Items.NETHER_STAR),
            new TrackEntry(ModSounds.village_theme, "Banana - Village Theme", Items.VILLAGER_SPAWN_EGG),
            new TrackEntry(ModSounds.rising_sun, "Rising Sun", Items.ENCHANTED_GOLDEN_APPLE),
            new TrackEntry(ModSounds.fantasy, "Sowyn - Fantasy", Items.TNT),
            new TrackEntry(ModSounds.world, "PIKASONIC & Dankidz - World", Items.BEDROCK),
            new TrackEntry(ModSounds.ice_cream_sandwich, "sacrofiz x Castlewater x Doffbeat - Ice Cream Sandwich", Items.SNOWBALL),
            new TrackEntry(ModSounds.levitate, "Sowyn - Levitate", Items.WHITE_BANNER),
            new TrackEntry(ModSounds.lanterns, "Xomu - Lanterns", Items.LANTERN),
            new TrackEntry(ModSounds.zayton, "木白 - Zayton (泉州)", Items.WATER_BUCKET)
    };

    private static final int musicCount = musicEntries.length;

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Sounds for DarkAddons");
    }

    public static SoundEvent getSound(int index) {
        if (isInValidIndex(index)) throw new IllegalArgumentException("Index out of bound");
        TrackEntry entry = musicEntries[index];
        return entry.sound();
    }

    public static String getSoundName(int index) {
        if (isInValidIndex(index)) throw new IllegalArgumentException("Index out of bound");
        TrackEntry entry = musicEntries[index];
        return entry.name();
    }

    public static Item getItem(int index) {
        if (isInValidIndex(index)) throw new IllegalArgumentException("Index out of bound");
        TrackEntry entry = musicEntries[index];
        return entry.icon();
    }

    private static boolean isInValidIndex(int index) {
        return index < 0 || index >= getMusicCount();
    }

    public static int getMusicIndex(String currentTrack) {
        for (int i = 0; i < musicCount; i++) {
            if (musicEntries[i].name().equals(currentTrack)) return i;
        }
        throw new IllegalArgumentException("No corresponding track found");
    }

    public static int getMusicCount() {
        return musicCount;
    }

    public record TrackEntry(SoundEvent sound, String name, Item icon) {
    }
}
