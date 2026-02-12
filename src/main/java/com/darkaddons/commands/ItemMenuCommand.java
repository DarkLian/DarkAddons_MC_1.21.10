package com.darkaddons.commands;

import com.darkaddons.init.ItemInit;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ItemMenuCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("items")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    Level level = player.level();
                    ItemInit.INSTANCE.trigger(level, player);
                    return 1;
                })
        );
    }
}
