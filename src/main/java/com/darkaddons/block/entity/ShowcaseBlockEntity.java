package com.darkaddons.block.entity;

import com.darkaddons.core.ModBlockEntities;
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

public class ShowcaseBlockEntity extends BlockEntity {
    private ItemStack displayedStack = ItemStack.EMPTY;

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
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        if (!displayedStack.isEmpty()) {
            valueOutput.store("DisplayedItem", ItemStack.CODEC, displayedStack);
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        this.displayedStack = valueInput.read("DisplayedItem", ItemStack.CODEC)
                .orElse(ItemStack.EMPTY);
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