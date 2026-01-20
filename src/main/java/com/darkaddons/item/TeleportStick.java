package com.darkaddons.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.darkaddons.utils.utilities.distance;
import static com.darkaddons.utils.utilities.getNearByEntities;

public class TeleportStick extends Item {

    public TeleportStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        double nearestDistance;
        Entity nearEstEntity;
        ItemStack itemStack = player.getItemInHand(hand);
        List<Entity> nearByEntities = getNearByEntities(player, 20.0);
        if (nearByEntities.isEmpty()) {
            player.displayClientMessage(Component.literal("No target found"), false);
            player.getCooldowns().addCooldown(itemStack, 20);
            return InteractionResult.PASS;
        }
        nearEstEntity = nearByEntities.getFirst();
        nearestDistance = distance(nearEstEntity, player);
        for (Entity entity : nearByEntities) {
            double temp = distance(entity, player);
            if (temp < nearestDistance) {
                nearestDistance = temp;
                nearEstEntity = entity;
            }
        }
        player.teleportTo(nearEstEntity.getX(), nearEstEntity.getY(), nearEstEntity.getZ());
        player.displayClientMessage(Component.literal("Teleported successfully!"), false);
        player.getCooldowns().addCooldown(itemStack, 100);
        return InteractionResult.SUCCESS;
    }
}
