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
            new TrackEntry(ModSounds.ABSOLUTION, "Makkon - Absolution", Items.END_CRYSTAL,180),
            new TrackEntry(ModSounds.DREAMS, "Dreams", Items.CLOCK, 250),
            new TrackEntry(ModSounds.REGALITY, "Aurelleah - Regality", Items.NETHER_STAR, 228),
            new TrackEntry(ModSounds.VILLAGE_THEME, "Banana - Village Theme", Items.VILLAGER_SPAWN_EGG, 222),
            new TrackEntry(ModSounds.RISING_SUN, "Rising Sun", Items.GOLDEN_APPLE, 163),
            new TrackEntry(ModSounds.FANTASY, "Sowyn - Fantasy", Items.GREEN_BANNER, 204),
            new TrackEntry(ModSounds.WORLD, "PIKASONIC & Dankidz - World", Items.BEDROCK, 224),
            new TrackEntry(ModSounds.ICE_CREAM_SANDWICH, "Sacrofiz x Castlewater x Doffbeat - Ice Cream Sandwich", Items.SNOWBALL, 200),
            new TrackEntry(ModSounds.LEVITATE, "Sowyn - Levitate", Items.WHITE_BANNER, 178),
            new TrackEntry(ModSounds.LANTERNS, "Xomu - Lanterns", Items.LANTERN, 231),
            new TrackEntry(ModSounds.ZAYTON, "木白 - Zayton (泉州)", Items.WATER_BUCKET, 257),
            new TrackEntry(ModSounds.FRISBEE, "Ahxello-Frisbee", Items.SOUL_LANTERN, 191),
            new TrackEntry(ModSounds.RADIANT, "Ampyx - Radiant", Items.ENCHANTING_TABLE, 226),
            new TrackEntry(ModSounds.BEAUTIFUL_THINGS, "Ancare & iluniev - Beautiful Things", Items.DRAGON_EGG, 188),
            new TrackEntry(ModSounds.SPARKLE, "Blend - 最後の花火 (Saigo no Hanabi)", Items.FIREWORK_ROCKET, 202),
            new TrackEntry(ModSounds.RELEASE, "Cody-Sorenson-x-Kadenza-Starset-AirwaveMusic-Release", Items.LAVA_BUCKET, 187),
            new TrackEntry(ModSounds.PROMISE, "Damon-Empero-I-Promise", Items.BLACK_BED, 206),
            new TrackEntry(ModSounds.FLYING, "Hasenka - Flying", Items.DRAGON_HEAD, 222),
            new TrackEntry(ModSounds.GLISTEN, "Hunter Milo - Glisten", Items.GLOWSTONE, 206),
            new TrackEntry(ModSounds.PARTICLES, "JJD-Particles-_2014-Original-Mix_", Items.AMETHYST_SHARD, 238),
            new TrackEntry(ModSounds.KAGETSU, "Kirara Magic - Kagetsu", Items.BELL, 217),
            new TrackEntry(ModSounds.REMEDY, "JJD-Remedy", Items.POTION, 296),
            new TrackEntry(ModSounds.CUTE_SWING, "Mihony-Cute-Swing-_Lc73_kiSe8I_", Items.BEE_SPAWN_EGG, 216),
            new TrackEntry(ModSounds.MOCHIE_FANTASY, "Mochié-Fantasy-_Utm8m0T_nEU_", Items.EMERALD, 191),
            new TrackEntry(ModSounds.REMEMBER, "PIKASONIC-Remember", Items.DIAMOND, 225),
            new TrackEntry(ModSounds.SKYDIVE, "PIKASONIC-Skydive", Items.ELYTRA, 192),
            new TrackEntry(ModSounds.SKY_GARDEN, "Rocket Start - Sky Garden", Items.GRASS_BLOCK, 203),
            new TrackEntry(ModSounds.IGTL, "Salt. - I Got This Love", Items.HEAVY_CORE, 215),
            new TrackEntry(ModSounds.DESIRE, "Sowyn - Desire", Items.GOLDEN_CARROT, 207),
            new TrackEntry(ModSounds.AGAIN, "Sowyn-Again-_IjFPwIDsraI_", Items.BIRCH_SIGN, 193),
            new TrackEntry(ModSounds.ADVENTURE, "Swempke - Adventure", Items.END_PORTAL_FRAME, 186),
            new TrackEntry(ModSounds.WANDERER, "YUMMI - Wanderer" , Items.GOLDEN_HELMET, 206)
    };

    private static final int TOTAL_MUSIC_COUNT = musicEntries.length;

    private static SoundEvent registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Sounds for DarkAddons");
    }

    public static SoundEvent getSound(String currentTrack) {
        for (int i = 0; i < TOTAL_MUSIC_COUNT; i++) {
            if (musicEntries[i].name().equals(currentTrack)) return musicEntries[i].sound();
        }
        return null;
    }

    public static SoundEvent getSound(int index) {
        TrackEntry entry = musicEntries[index];
        return isInValidIndex(index) ? null : entry.sound();
    }

    //for init
    public static String getSoundName(int index) {
        TrackEntry entry = musicEntries[index];
        return isInValidIndex(index) ? null : entry.name();
    }

    // for init
    public static Item getItem(int index) {
        TrackEntry entry = musicEntries[index];
        return isInValidIndex(index) ? null : entry.icon();
    }

    //for init
    public static Integer getSoundDuration(int index) {
        TrackEntry entry = musicEntries[index];
        return isInValidIndex(index) ? null : entry.duration();
    }

    public static Integer getSoundDuration(String currentTrack) {
        for (int i = 0; i < TOTAL_MUSIC_COUNT; i++) {
            if (musicEntries[i].name().equals(currentTrack)) return musicEntries[i].duration();
        }
        return null;
    }

    public static Integer getSoundDurationTick(String currentTrack) {
        Integer duration = getSoundDuration(currentTrack);
        return (duration == null) ? null : duration * 20;
    }

    private static boolean isInValidIndex(int index) {
        return index < 0 || index >= getTotalMusicCount();
    }

    public static int getTotalMusicCount() {
        return TOTAL_MUSIC_COUNT;
    }

    public record TrackEntry(SoundEvent sound, String name, Item icon, Integer duration) {
    }
}
