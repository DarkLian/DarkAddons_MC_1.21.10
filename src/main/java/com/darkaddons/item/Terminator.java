package com.darkaddons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.getItemTypeLore;
import static com.darkaddons.utils.ModUtilities.getRarityColor;

public class Terminator extends BowItem {
    private static final int COOL_DOWN = 4;

    public Terminator(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Terminator").withStyle(getRarityColor(stack));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.FAIL;
        ItemStack itemStack = player.getItemInHand(hand);

        boolean isCreative = player.getAbilities().instabuild;
        ItemStack ammoStack = player.getProjectile(itemStack);
        boolean hasAmmo = !ammoStack.isEmpty() || isCreative;

        if (!hasAmmo) {
            player.displayClientMessage(Component.literal("You ran out of arrows!").withStyle(ChatFormatting.RED), false);
            return InteractionResult.FAIL;
        }

        float[] angles = {0.0f, -3.0f, 3.0f};
        for (float angle : angles) {
            Arrow arrowEntity = new Arrow(level, player, ammoStack.copyWithCount(1), itemStack) {
                @Override
                protected void onHitBlock(BlockHitResult result) {
                    super.onHitBlock(result);
                    this.discard();
                }

                @Override
                protected void onHitEntity(EntityHitResult result) {
                    super.onHitEntity(result);
                    this.discard();
                }
            };
            arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot() + angle, 0.0F, 3.0F, 1.0F);
            arrowEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
            level.addFreshEntity(arrowEntity);

            if (!isCreative) {
                ammoStack.shrink(1);
            }

        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        player.getCooldowns().addCooldown(player.getItemInHand(hand), COOL_DOWN);
        return InteractionResult.PASS;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 0;
    }


    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        consumer.accept(Component.literal("Shortbow: Instantly shoots!").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.empty());

        consumer.accept(Component.literal("Ability: Annihilation").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.literal("Shoots ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("3 arrows").withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" at once while").withStyle(ChatFormatting.GRAY)));
        consumer.accept(Component.literal("consuming only ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("1 arrow").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("!").withStyle(ChatFormatting.GRAY)));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}