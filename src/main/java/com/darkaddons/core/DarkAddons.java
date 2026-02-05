package com.darkaddons.core;

import com.darkaddons.util.MusicLoopHandler;
import com.darkaddons.util.MusicMenuManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.darkaddons.util.MusicMenuManager.handleMusicInput;

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
            if (ModStats.isSearching()) {
                String input = message.signedContent();
                ModStats.setCurrentSearchQuery(input);
                ModStats.setSearching(false);
                player.level().getServer().execute(() -> MusicMenuManager.callMusicMenu(player));
                return false;
            }
            return true;
        });
        PayloadTypeRegistry.playS2C().register(ModPackets.OpenChatPayload.ID, ModPackets.OpenChatPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("musicmenu").requires(source -> source.hasPermission(2)).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            Level level = player.level();
            handleMusicInput(level, player);
            return 1;
        })));
    }
}