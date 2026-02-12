package com.darkaddons.core;

import com.darkaddons.init.MusicInit;
import com.darkaddons.utils.MusicLoopHandler;
import com.darkaddons.utils.MusicMenuManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkAddons implements ModInitializer {
    public static final String MOD_ID = "darkaddons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.initialize();
        ModSounds.initialize();
        ModItems.initialize();
        ModCommands.register();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                MusicLoopHandler.INSTANCE.onTick(player);
            }
        });
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, params) -> {
            if (MusicInit.INSTANCE.isSearching()) {
                String input = message.signedContent();
                MusicMenuManager.INSTANCE.setCurrentSearchQuery(input);
                MusicInit.INSTANCE.setSearching(false);
                player.level().getServer().execute(() -> MusicInit.INSTANCE.trigger(player.level(), player));
                return false;
            }
            return true;
        });
        PayloadTypeRegistry.playS2C().register(ModPackets.OpenChatPayload.ID, ModPackets.OpenChatPayload.CODEC);


    }
}