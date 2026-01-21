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
    public static final SoundEvent ABSOLUTION = registerSound("absolution");
    public static final SoundEvent DREAMS = registerSound("dreams");
    public static final SoundEvent REGALITY = registerSound("regality");
    public static final SoundEvent VILLAGE_THEME = registerSound("village_theme");
    public static final SoundEvent RISING_SUN = registerSound("rising_sun");
    public static final SoundEvent FANTASY = registerSound("fantasy");
    public static final SoundEvent WORLD = registerSound("world");
    public static final SoundEvent ICE_CREAM_SANDWICH = registerSound("ice_cream_sandwich");
    public static final SoundEvent LEVITATE = registerSound("levitate");
    public static final SoundEvent LANTERNS = registerSound("lanterns");
    public static final SoundEvent ZAYTON = registerSound("zayton");
    public static final SoundEvent FRISBEE = registerSound("frisbee");
    public static final SoundEvent RADIANT = registerSound("radiant");
    public static final SoundEvent BEAUTIFUL_THINGS = registerSound("beautiful_things");
    public static final SoundEvent SPARKLE = registerSound("sparkle");
    public static final SoundEvent RELEASE = registerSound("release");
    public static final SoundEvent PROMISE = registerSound("promise");
    public static final SoundEvent FLYING = registerSound("flying");
    public static final SoundEvent GLISTEN = registerSound("glisten");
    public static final SoundEvent PARTICLES = registerSound("particles");
    public static final SoundEvent KAGETSU = registerSound("kagetsu");
    public static final SoundEvent REMEDY = registerSound("remedy");
    public static final SoundEvent CUTE_SWING = registerSound("cute_swing");
    public static final SoundEvent MOCHIE_FANTASY = registerSound("mochie_fantasy");
    public static final SoundEvent REMEMBER = registerSound("remember");
    public static final SoundEvent SKYDIVE = registerSound("skydive");
    public static final SoundEvent SKY_GARDEN = registerSound("sky_garden");
    public static final SoundEvent IGTL = registerSound("igtl");
    public static final SoundEvent DESIRE = registerSound("desire");
    public static final SoundEvent AGAIN = registerSound("again");
    public static final SoundEvent ADVENTURE = registerSound("adventure");
    public static final SoundEvent WANDERER = registerSound("wanderer");
    private static final TrackEntry[] musicEntries = {
            new TrackEntry(ModSounds.ABSOLUTION, "Makkon - Absolution", Items.END_CRYSTAL),
            new TrackEntry(ModSounds.DREAMS, "Dreams", Items.CLOCK),
            new TrackEntry(ModSounds.REGALITY, "Aurelleah - Regality", Items.NETHER_STAR),
            new TrackEntry(ModSounds.VILLAGE_THEME, "Banana - Village Theme", Items.VILLAGER_SPAWN_EGG),
            new TrackEntry(ModSounds.RISING_SUN, "Rising Sun", Items.GOLDEN_APPLE),
            new TrackEntry(ModSounds.FANTASY, "Sowyn - Fantasy", Items.TNT),
            new TrackEntry(ModSounds.WORLD, "PIKASONIC & Dankidz - World", Items.BEDROCK),
            new TrackEntry(ModSounds.ICE_CREAM_SANDWICH, "sacrofiz x Castlewater x Doffbeat - Ice Cream Sandwich", Items.SNOWBALL),
            new TrackEntry(ModSounds.LEVITATE, "Sowyn - Levitate", Items.WHITE_BANNER),
            new TrackEntry(ModSounds.LANTERNS, "Xomu - Lanterns", Items.LANTERN),
            new TrackEntry(ModSounds.ZAYTON, "木白 - Zayton (泉州)", Items.WATER_BUCKET),
            new TrackEntry(ModSounds.FRISBEE, "Ahxello-Frisbee", Items.SOUL_LANTERN),
            new TrackEntry(ModSounds.RADIANT, "Ampyx - Radiant", Items.ENCHANTING_TABLE),
            new TrackEntry(ModSounds.BEAUTIFUL_THINGS, "Ancare & iluniev - Beautiful Things", Items.DRAGON_EGG),
            new TrackEntry(ModSounds.SPARKLE, "Blend - 最後の花火 (Saigo no Hanabi)", Items.FIREWORK_ROCKET),
            new TrackEntry(ModSounds.RELEASE, "Cody-Sorenson-x-Kadenza-Starset-AirwaveMusic-Release", Items.LAVA_BUCKET),
            new TrackEntry(ModSounds.PROMISE, "Damon-Empero-I-Promise", Items.BLACK_BED),
            new TrackEntry(ModSounds.FLYING, "Hasenka - Flying", Items.DRAGON_HEAD),
            new TrackEntry(ModSounds.GLISTEN, "Hunter Milo - Glisten", Items.GLOWSTONE),
            new TrackEntry(ModSounds.PARTICLES, "JJD-Particles-_2014-Original-Mix_", Items.AMETHYST_SHARD),
            new TrackEntry(ModSounds.KAGETSU, "Kirara Magic - Kagetsu", Items.BELL),
            new TrackEntry(ModSounds.REMEDY, "JJD-Remedy", Items.POTION),
            new TrackEntry(ModSounds.CUTE_SWING, "Mihony-Cute-Swing-_Lc73_kiSe8I_", Items.BEE_SPAWN_EGG),
            new TrackEntry(ModSounds.MOCHIE_FANTASY, "Mochié-Fantasy-_Utm8m0T_nEU_", Items.EMERALD),
            new TrackEntry(ModSounds.REMEMBER, "PIKASONIC-Remember", Items.DIAMOND),
            new TrackEntry(ModSounds.SKYDIVE, "PIKASONIC-Skydive", Items.ELYTRA),
            new TrackEntry(ModSounds.SKY_GARDEN, "Rocket Start - Sky Garden", Items.GRASS_BLOCK),
            new TrackEntry(ModSounds.IGTL, "Salt. - I Got This Love", Items.HEAVY_CORE),
            new TrackEntry(ModSounds.DESIRE, "Sowyn - Desire", Items.GOLDEN_CARROT),
            new TrackEntry(ModSounds.AGAIN, "Sowyn-Again-_IjFPwIDsraI_", Items.BIRCH_SIGN),
            new TrackEntry(ModSounds.ADVENTURE, "Swempke - Adventure", Items.END_PORTAL_FRAME),
            new TrackEntry(ModSounds.WANDERER, "YUMMI - Wanderer", Items.GOLDEN_HELMET)
    };

    private static final int MUSIC_COUNT = musicEntries.length;

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
        for (int i = 0; i < MUSIC_COUNT; i++) {
            if (musicEntries[i].name().equals(currentTrack)) return i;
        }
        throw new IllegalArgumentException("No corresponding track found");
    }

    public static int getMusicCount() {
        return MUSIC_COUNT;
    }

    public record TrackEntry(SoundEvent sound, String name, Item icon) {
    }
}
