package com.darkaddons.item;

import com.darkaddons.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.darkaddons.utils.utilities.getNearByEntities;

public class LightningStick extends Item {
    public LightningStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        int count = 0;
        ItemStack itemStack = player.getItemInHand(hand);
        List<Entity> nearByEntities = getNearByEntities(player, 10.0);
        int currentCharges = itemStack.getOrDefault(ModComponents.charge, 0);

        final int coolDown = 20;
        final int reloadCoolDown = 100;

        if (player.isCrouching()) {
            player.displayClientMessage(Component.literal("Reloading charges"), false);
            player.getCooldowns().addCooldown(itemStack, reloadCoolDown);
            int MAX_CHARGE = 20;
            itemStack.set(ModComponents.charge, MAX_CHARGE);
            return InteractionResult.SUCCESS;
        }


        if (currentCharges <= 0) {
            player.displayClientMessage(Component.literal("Not enough charges, please reload in a second."), false);
            player.getCooldowns().addCooldown(itemStack, coolDown);
            return InteractionResult.PASS;
        }

        if (nearByEntities.isEmpty()) {
            player.displayClientMessage(Component.literal("No target found."), false);
            player.getCooldowns().addCooldown(itemStack, coolDown);
            return InteractionResult.PASS;
        }

        for (Entity entity : nearByEntities) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightningBolt.setPos(entity.getX(), entity.getY(), entity.getZ());
            level.addFreshEntity(lightningBolt);
            count++;
        }

        itemStack.set(ModComponents.charge, currentCharges - 1);
        player.displayClientMessage(Component.literal("Struck " + count + " entities"), false);
        player.getCooldowns().addCooldown(itemStack, coolDown);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        String currentCharges = String.valueOf(itemStack.getOrDefault(ModComponents.charge, 0));
        consumer.accept(Component.literal("Charge left: ").append(Component.literal(currentCharges).withStyle(ChatFormatting.DARK_AQUA)));
    }
}
