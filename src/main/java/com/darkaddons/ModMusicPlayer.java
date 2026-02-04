package com.darkaddons;

public class ModMusicPlayer {
    public enum PlayMode {
        DEFAULT("Default"),
        LOOP("Loop"),
        AUTOPLAY("Autoplay");

        private static final ModMusicPlayer.PlayMode[] MODES = values();
        private final String displayName;

        PlayMode(String displayName) {
            this.displayName = displayName;
        }

        public ModMusicPlayer.PlayMode next() { return MODES[(ordinal() + 1) % MODES.length]; }
        public ModMusicPlayer.PlayMode prev() { return (ordinal() == 0) ? MODES[MODES.length - 1] : MODES[ordinal() - 1]; }
        public String getDisplayName() { return this.displayName; }
    }
}
