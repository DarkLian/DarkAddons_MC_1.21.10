package com.darkaddons.item;

import com.darkaddons.core.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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

import static com.darkaddons.util.ModUtilities.getNearByLivingEntities;

public class LightningStick extends Item {
    private static final double ABILITY_RADIUS = 10.0;
    private static final int MAX_CHARGE = 20;
    private static final int COOLDOWN_TICKS = 20;
    private static final int RELOAD_COOLDOWN_TICKS = 100;

    public LightningStick(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack itemStack = player.getItemInHand(hand);

        if (player.isCrouching()) {
            player.displayClientMessage(Component.literal("Reloading charges"), false);
            player.getCooldowns().addCooldown(itemStack, RELOAD_COOLDOWN_TICKS);
            itemStack.set(ModComponents.CHARGE, MAX_CHARGE);
            return InteractionResult.SUCCESS;
        }

        int currentCharges = itemStack.getOrDefault(ModComponents.CHARGE, 0);
        if (currentCharges <= 0) {
            player.displayClientMessage(Component.literal("Not enough charges, please reload in a second."), false);
            player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
            return InteractionResult.PASS;
        }

        List<LivingEntity> targets = getNearByLivingEntities(level, player, ABILITY_RADIUS, LivingEntity.class);
        if (targets.isEmpty()) {
            player.displayClientMessage(Component.literal("No target found."), false);
            player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
            return InteractionResult.PASS;
        }

        for (LivingEntity target : targets) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightningBolt.setVisualOnly(true);
            lightningBolt.setPos(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(lightningBolt);
        }

        itemStack.set(ModComponents.CHARGE, currentCharges - 1);
        player.displayClientMessage(Component.literal("Struck " + targets.size() + " entities"), false);
        player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        String currentCharges = String.valueOf(itemStack.getOrDefault(ModComponents.CHARGE, 0));
        consumer.accept(Component.literal("Charge left: ").append(Component.literal(currentCharges).withStyle(ChatFormatting.DARK_AQUA)));
    }
}
