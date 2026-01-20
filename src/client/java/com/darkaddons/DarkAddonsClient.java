package com.darkaddons;

import net.fabricmc.api.ClientModInitializer;

public class DarkAddonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DarkAddons.clientHelper = new ClientUtilities();
    }
}