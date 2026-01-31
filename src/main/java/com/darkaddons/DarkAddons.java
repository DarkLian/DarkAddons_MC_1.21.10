package com.darkaddons;

import com.darkaddons.utils.ClientHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkAddons implements ModInitializer {
    public static final String MOD_ID = "darkaddons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ClientHelper clientHelper;

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
    }
}