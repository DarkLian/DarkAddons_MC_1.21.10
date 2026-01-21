package com.darkaddons.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.darkaddons.utils.utilities.distance;
import static com.darkaddons.utils.utilities.getNearByLivingEntities;

public class TeleportStick extends Item {
    private static final double ABILITY_RADIUS = 20.0;
    private static final int SUCCESS_COOLDOWN_TICKS = 100;
    private static final int FAIL_COOLDOWN_TICKS = 20;

    public TeleportStick(Properties properties) {
        super(properties);
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
}
