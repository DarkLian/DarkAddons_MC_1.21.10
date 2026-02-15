package com.darkaddons.block;

import com.darkaddons.block.entity.ShowcaseBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShowcaseBlock extends BaseEntityBlock {
    public static final MapCodec<ShowcaseBlock> CODEC = simpleCodec(ShowcaseBlock::new);

    public ShowcaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ShowcaseBlockEntity showcase)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack displayed = showcase.getDisplayedStack();

        if (displayed.isEmpty() && !heldItem.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack copy = heldItem.copy();
                copy.setCount(1);
                showcase.setDisplayedStack(copy);
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (!displayed.isEmpty()) {
            if (player.getInventory().getFreeSlot() == -1) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.literal("Your inventory is full!").withStyle(ChatFormatting.RED), true);
                }
            } else if (!level.isClientSide()) {
                player.getInventory().placeItemBackInInventory(displayed);
                showcase.setDisplayedStack(ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShowcaseBlockEntity(pos, state);
    }
}