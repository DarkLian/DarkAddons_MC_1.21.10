package com.darkaddons.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class InfoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("darkaddons").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.displayClientMessage(Component.literal("Commands for DarkAddons").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("\n"))
                    .append(line("/musicmenu", "open the music menu"))
                    .append(line("/items", "open a menu where you can obtain custom items made")), false);
            return 1;
        }));
    }

    private static MutableComponent line(String command, String description) {
        return Component.literal("\n" + command + ": ").withStyle(ChatFormatting.WHITE).append(Component.literal(description).withStyle(ChatFormatting.GREEN));
    }
}
