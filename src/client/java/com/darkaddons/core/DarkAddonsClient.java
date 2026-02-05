package com.darkaddons.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

@SuppressWarnings("unused")
public class DarkAddonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OpenChatPayload.ID, (payload, context) -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.execute(() -> minecraft.setScreen(new ChatScreen("", false)));
        });
    }
}