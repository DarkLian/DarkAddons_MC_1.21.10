package com.darkaddons.block.entity;

import com.darkaddons.core.ModBlockEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisplayBaseEntity extends BlockEntity {

    private ItemStack displayedStack = ItemStack.EMPTY;
    private float rotation = 0.0f;

    public DisplayBaseEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPLAY_BASE_ENTITY, pos, state);
    }

    public ItemStack getDisplayedStack() {
        return this.displayedStack;
    }

    public void setDisplayedStack(ItemStack stack) {
        this.displayedStack = stack;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        if (!displayedStack.isEmpty()) {
            out.store("DisplayedItem", ItemStack.CODEC, displayedStack);
            out.store("Rotation", Codec.FLOAT, this.rotation);
        }
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        this.displayedStack = in.read("DisplayedItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.rotation = in.read("Rotation", Codec.FLOAT).orElse(0.0f);
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