package com.darkaddons;

import com.darkaddons.item.MusicStick;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.darkaddons.item.MusicStick.setCurrentSearchQuery;
import static com.darkaddons.item.MusicStick.setSearching;

public class DarkAddons implements ModInitializer {
    public static final String MOD_ID = "darkaddons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.initialize();
        ModSounds.initialize();
        ModItems.initialize();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                MusicLoopHandler.onTick(player);
            }
        });
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, params) -> {
            if (MusicStick.isSearching()) {
                String input = message.signedContent();
                setCurrentSearchQuery(input);
                setSearching(false);
                player.level().getServer().execute(() -> MusicMenuManager.callMusicMenu(player));
                return false;
            }
            return true;
        });
        PayloadTypeRegistry.playS2C().register(ModPackets.OpenChatPayload.ID, ModPackets.OpenChatPayload.CODEC);
    }
}