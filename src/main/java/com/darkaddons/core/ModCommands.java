package com.darkaddons.core;

import com.darkaddons.commands.InfoCommand;
import com.darkaddons.commands.MusicMenuCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MusicMenuCommand.register(dispatcher);
            InfoCommand.register(dispatcher);
        });
    }
}
