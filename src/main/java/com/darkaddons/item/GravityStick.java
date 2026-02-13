package com.darkaddons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.*;

public class GravityStick extends Item {
    private static final double PULL_RADIUS = 15.0;
    private static final double PULL_STRENGTH = 1.0;

    public GravityStick(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Gyrokinetic Wand").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        BlockHitResult hitResult = getTeleportTarget(player, 20.0);
        Vec3 center = hitResult.getLocation();

        level.playSound(null, center.x, center.y, center.z, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 2.0f, 0.5f);

        List<LivingEntity> victims = getNearByLivingEntities(level, player, PULL_RADIUS, LivingEntity.class);

        for (LivingEntity victim : victims) {
            if (victim == player) continue;
            Vec3 direction = center.subtract(victim.position());
            Vec3 pullForce = direction.normalize().scale(PULL_STRENGTH).add(0, 0.3, 0);
            victim.setDeltaMovement(pullForce);
            victim.hurtMarked = true;
        }
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y + 1, center.z, 50, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y + 1, center.z, 1, 0, 0, 0, 0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal("Ability: Gravity Storm").withStyle(ChatFormatting.GOLD));

        consumer.accept(Component.literal("Create a ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("Singularity").withStyle(ChatFormatting.DARK_PURPLE))
                .append(Component.literal(" that pulls").withStyle(ChatFormatting.GRAY)));
        consumer.accept(Component.literal("all nearby enemies into a").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal("single spot.").withStyle(ChatFormatting.GRAY));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}