package com.darkaddons.core;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModPackets {
    public record OpenChatPayload() implements CustomPacketPayload {
        public static final Type<OpenChatPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(DarkAddons.MOD_ID, "open_chat"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenChatPayload> CODEC = StreamCodec.unit(new OpenChatPayload());

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}
