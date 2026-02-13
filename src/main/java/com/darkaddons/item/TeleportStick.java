package com.darkaddons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.*;

public class TeleportStick extends Item {
    private static final double ABILITY_RADIUS = 40.0;
    private static final int SUCCESS_COOLDOWN_TICKS = 5;
    private static final int FAIL_COOLDOWN_TICKS = 5;

    public TeleportStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Aspect of the Warp").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack itemStack = player.getItemInHand(hand);
        List<LivingEntity> targets = getNearByLivingEntities(level, player, ABILITY_RADIUS, LivingEntity.class);

        if (targets.isEmpty()) {
            player.displayClientMessage(Component.literal("No target found"), false);
            player.getCooldowns().addCooldown(itemStack, FAIL_COOLDOWN_TICKS);
            return InteractionResult.PASS;
        }

        LivingEntity nearestTarget = targets.getFirst();
        double distance = distance(nearestTarget, player);
        for (LivingEntity target : targets) {
            double temp = distance(target, player);
            if (temp < distance) {
                distance = temp;
                nearestTarget = target;
            }
        }

        player.teleportTo(nearestTarget.getX(), nearestTarget.getY(), nearestTarget.getZ());
        player.displayClientMessage(Component.literal("Teleported successfully!"), false);
        player.getCooldowns().addCooldown(itemStack, SUCCESS_COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Ability: Instant Transmission").withStyle(ChatFormatting.GOLD));

        consumer.accept(Component.literal("Instantly teleport to the").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal("nearest living entity ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal("within ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("10 blocks").withStyle(ChatFormatting.GREEN)));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}
