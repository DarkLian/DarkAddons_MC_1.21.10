package com.darkaddons.core;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

import static com.darkaddons.core.DarkAddons.MOD_ID;

public class ModSounds {
    private static final List<TrackEntry> MUSIC_ENTRIES = new ArrayList<>();

    static {
        registerMusic("absolution", "Makkon - Absolution", Items.END_CRYSTAL, 180);
        registerMusic("dreams", "Dreams", Items.CLOCK, 250);
        registerMusic("regality", "Aurelleah - Regality", Items.NETHER_STAR, 228);
        registerMusic("village_theme", "Banana - Village Theme", Items.VILLAGER_SPAWN_EGG, 222);
        registerMusic("rising_sun", "Rising Sun", Items.GOLDEN_APPLE, 163);
        registerMusic("fantasy", "Sowyn - Fantasy", Items.GREEN_BANNER, 204);
        registerMusic("world", "PIKASONIC & Dankidz - World", Items.BEDROCK, 224);
        registerMusic("ice_cream_sandwich", "Sacrofiz x Castlewater x Doffbeat - Ice Cream Sandwich", Items.SNOWBALL, 200);
        registerMusic("levitate", "Sowyn - Levitate", Items.WHITE_BANNER, 178);
        registerMusic("lanterns", "Xomu - Lanterns", Items.LANTERN, 231);
        registerMusic("zayton", "木白 - Zayton (泉州)", Items.WATER_BUCKET, 257);
        registerMusic("frisbee", "Ahxello-Frisbee", Items.SOUL_LANTERN, 191);
        registerMusic("radiant", "Ampyx - Radiant", Items.ENCHANTING_TABLE, 226);
        registerMusic("beautiful_things", "Ancare & iluniev - Beautiful Things", Items.DRAGON_EGG, 188);
        registerMusic("sparkle", "Blend - 最後の花火 (Saigo no Hanabi)", Items.FIREWORK_ROCKET, 202);
        registerMusic("release", "Cody-Sorenson-x-Kadenza-Starset-AirwaveMusic-Release", Items.LAVA_BUCKET, 187);
        registerMusic("promise", "Damon-Empero-I-Promise", Items.BLACK_BED, 206);
        registerMusic("flying", "Hasenka - Flying", Items.DRAGON_HEAD, 222);
        registerMusic("glisten", "Hunter Milo - Glisten", Items.GLOWSTONE, 206);
        registerMusic("particles", "JJD-Particles-_2014-Original-Mix_", Items.AMETHYST_SHARD, 238);
        registerMusic("kagetsu", "Kirara Magic - Kagetsu", Items.BELL, 217);
        registerMusic("remedy", "JJD-Remedy", Items.POTION, 296);
        registerMusic("cute_swing", "Mihony-Cute-Swing-_Lc73_kiSe8I_", Items.BEE_SPAWN_EGG, 216);
        registerMusic("mochie_fantasy", "Mochié-Fantasy-_Utm8m0T_nEU_", Items.EMERALD, 191);
        registerMusic("remember", "PIKASONIC-Remember", Items.DIAMOND, 225);
        registerMusic("skydive", "PIKASONIC-Skydive", Items.ELYTRA, 192);
        registerMusic("sky_garden", "Rocket Start - Sky Garden", Items.GRASS_BLOCK, 203);
        registerMusic("igtl", "Salt. - I Got This Love", Items.HEAVY_CORE, 215);
        registerMusic("desire", "Sowyn - Desire", Items.GOLDEN_CARROT, 207);
        registerMusic("again", "Sowyn-Again-_IjFPwIDsraI_", Items.BIRCH_SIGN, 193);
        registerMusic("adventure", "Swempke - Adventure", Items.END_PORTAL_FRAME, 186);
        registerMusic("wanderer", "YUMMI - Wanderer", Items.GOLDEN_HELMET, 206);
    }

    private static void registerMusic(String name, String displayName, Item icon, int duration) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        SoundEvent sound = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        MUSIC_ENTRIES.add(new TrackEntry(sound, displayName, icon, duration));
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Sounds for DarkAddons");
    }

    public static SoundEvent getSound(String currentTrack) {
        return MUSIC_ENTRIES.stream().filter(entry -> entry.displayName().equals(currentTrack)).map(TrackEntry::sound).findFirst().orElse(null);
    }

    public static SoundEvent getSound(int index) {
        if (isInvalidIndex(index)) return null;
        return MUSIC_ENTRIES.get(index).sound();
    }

    public static String getSoundName(int index) {
        if (isInvalidIndex(index)) return null;
        return MUSIC_ENTRIES.get(index).displayName();
    }

    public static Item getIcon(int index) {
        if (isInvalidIndex(index)) return null;
        return MUSIC_ENTRIES.get(index).icon();
    }

    public static Integer getSoundDuration(int index) {
        if (isInvalidIndex(index)) return null;
        return MUSIC_ENTRIES.get(index).duration();
    }

    public static Integer getSoundDuration(String currentTrack) {
        return MUSIC_ENTRIES.stream().filter(entry -> entry.displayName().equals(currentTrack)).map(TrackEntry::duration).findFirst().orElse(null);
    }

    public static Integer getSoundDurationTick(String currentTrack) {
        Integer duration = getSoundDuration(currentTrack);
        return (duration == null) ? null : duration * 20;
    }

    private static boolean isInvalidIndex(int index) {
        return index < 0 || index >= MUSIC_ENTRIES.size();
    }

    public static int getTotalMusicCount() {
        return MUSIC_ENTRIES.size();
    }

    public record TrackEntry(SoundEvent sound, String displayName, Item icon, Integer duration) {
    }
}