package com.darkaddons.block.entity;

import com.darkaddons.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShowcaseBlockEntity extends BlockEntity {

    private ItemStack displayedStack = ItemStack.EMPTY;
    private UUID displayEntityUUID = null;

    public ShowcaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOWCASE_BLOCK_ENTITY, pos, state);
    }

    public ItemStack getDisplayedStack() {
        return this.displayedStack;
    }

    public void setDisplayedStack(ItemStack stack) {
        this.displayedStack = stack;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            updateDisplayEntity();
        }
    }

    public void spawnDisplayEntity() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, serverLevel);
        display.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5);
        display.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        display.setViewRange(4.0f);
        display.setBackgroundColor(0x40000000);
        display.setText(displayedStack.isEmpty() ? Component.empty() : displayedStack.getHoverName());

        serverLevel.addFreshEntity(display);
        displayEntityUUID = display.getUUID();
        setChanged();
    }

    public void updateDisplayEntity() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Display.TextDisplay display = findDisplayEntity(serverLevel);
        if (display == null) {
            spawnDisplayEntity();
            return;
        }
        display.setText(displayedStack.isEmpty() ? Component.empty() : displayedStack.getHoverName());
    }

    public void killDisplayEntity() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        Display.TextDisplay display = findDisplayEntity(serverLevel);
        if (display != null) display.discard();
        displayEntityUUID = null;
    }

    @Nullable
    private Display.TextDisplay findDisplayEntity(ServerLevel serverLevel) {
        if (displayEntityUUID == null) return null;
        Entity entity = serverLevel.getEntity(displayEntityUUID);
        return entity instanceof Display.TextDisplay d ? d : null;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        if (!displayedStack.isEmpty()) {
            out.store("DisplayedItem", ItemStack.CODEC, displayedStack);
        }
        if (displayEntityUUID != null) {
            out.store("DisplayEntityUUID", com.mojang.serialization.Codec.STRING, displayEntityUUID.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        this.displayedStack = in.read("DisplayedItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.displayEntityUUID = in.read("DisplayEntityUUID", com.mojang.serialization.Codec.STRING)
                .map(UUID::fromString)
                .orElse(null);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}