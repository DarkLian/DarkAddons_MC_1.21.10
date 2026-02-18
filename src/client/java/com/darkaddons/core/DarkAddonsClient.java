package com.darkaddons.core;

import com.darkaddons.client.renderer.DisplayBaseEntityRenderer;
import com.darkaddons.client.renderer.ShowcaseBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@SuppressWarnings("unused")
public class DarkAddonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OpenChatPayload.ID, (payload, context) -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.execute(() -> minecraft.setScreen(new ChatScreen("", false)));
        });

        BlockEntityRenderers.register(ModBlockEntities.SHOWCASE_BLOCK_ENTITY, ShowcaseBlockEntityRenderer::new);

        BlockRenderLayerMap.putBlock(ModBlocks.SHOWCASE_BLOCK, ChunkSectionLayer.CUTOUT);

        BlockEntityRenderers.register(ModBlockEntities.DISPLAY_BASE_ENTITY, DisplayBaseEntityRenderer::new);

        BlockRenderLayerMap.putBlock(ModBlocks.DISPLAY_BASE, ChunkSectionLayer.CUTOUT);
    }
}