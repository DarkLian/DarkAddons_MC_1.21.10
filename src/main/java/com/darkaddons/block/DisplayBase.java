package com.darkaddons.block;

import com.darkaddons.block.entity.DisplayBaseEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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

import java.util.function.BiConsumer;

public class DisplayBase extends BaseEntityBlock {

    public static final MapCodec<DisplayBase> CODEC = simpleCodec(DisplayBase::new);

    private static final VoxelShape MODEL = Shapes.box(0, 0, 0, 1, 0.03125, 1);

    public DisplayBase(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MODEL;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MODEL;
    }

    @Override
    protected void onExplosionHit(
            BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof DisplayBaseEntity base)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack displayed = base.getDisplayedStack();

        if (displayed.isEmpty() && !heldItem.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack copy = heldItem.copy();
                copy.setCount(1);
                float rotation = -player.getYRot();
                base.setRotation(rotation);
                base.setDisplayedStack(copy);
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (!displayed.isEmpty()) {
            if (!level.isClientSide()) {
                if (player.getInventory().getFreeSlot() == -1) {
                    player.displayClientMessage(
                            Component.literal("Your inventory is full!").withStyle(ChatFormatting.RED), true);
                } else {
                    player.getInventory().placeItemBackInInventory(displayed);
                    base.setDisplayedStack(ItemStack.EMPTY);
                    base.setRotation(0.0f);
                }
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
        return new DisplayBaseEntity(pos, state);
    }
}