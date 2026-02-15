package com.darkaddons.item;

import com.darkaddons.core.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.*;

public class Hyperion extends Item {
    private static final int COOLDOWN_TICKS = 3;
    private static final float DAMAGE_AMOUNT = 19.6f;
    private static final double ABILITY_RADIUS = 10.0;

    public Hyperion(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        int durability = itemStack.getOrDefault(ModComponents.DURABILITY, 0);
        return durability > 0;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Hyperion").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;

        ItemStack itemStack = player.getItemInHand(hand);
        int durability = itemStack.getOrDefault(ModComponents.DURABILITY, 0);

        if (durability <= 0) {
            player.displayClientMessage(Component.literal("Insufficient durability, get a new one!"), false);
            player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
            return InteractionResult.PASS;
        }

        BlockHitResult hitResult = getTeleportTarget(player, 10.0);
        Vec3 rawHit = hitResult.getLocation();
        double targetX = rawHit.x;
        double targetY = (hitResult.getType() == HitResult.Type.BLOCK && hitResult.getDirection() == Direction.UP) ? rawHit.y : rawHit.y - player.getEyeHeight();
        double targetZ = rawHit.z;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Direction face = hitResult.getDirection();
            targetX += face.getStepX() * 0.3;
            targetZ += face.getStepZ() * 0.3;
        }
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(targetX, targetY, targetZ);
        for (int i = 0; i < 3 && level.getBlockState(checkPos).blocksMotion(); i++) {
            targetY += 1.0;
            checkPos.move(0, 1, 0);
        }

        player.teleportTo(targetX, targetY, targetZ);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0);

            double radius = 4.0;
            int particleCount = 50;

            for (int i = 0; i < particleCount; i++) {
                double theta = level.random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(2 * level.random.nextDouble() - 1);

                double thickRadius = radius + (level.random.nextDouble() - 0.5);

                double x = thickRadius * Math.sin(phi) * Math.cos(theta);
                double z = thickRadius * Math.cos(phi);

                serverLevel.sendParticles(
                        ParticleTypes.EXPLOSION,
                        player.getX() + x,
                        player.getY(),
                        player.getZ() + z,
                        1, 0, 0, 0, 0
                );
            }

            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DRAGON_FIREBALL_EXPLODE,
                    SoundSource.PLAYERS, 1.0f, 2.0f);
        }

        List<LivingEntity> targets = getNearByLivingEntities(level, player, ABILITY_RADIUS, LivingEntity.class);
        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {
                float finalDamage = (target.getHealth() - DAMAGE_AMOUNT) <= 0 ? 0 : (target.getHealth() - DAMAGE_AMOUNT);
                target.setHealth(finalDamage);
            }
            String enemyNoun = (targets.size() == 1) ? " enemy" : " enemies";
            String damageText = String.format("%.1f", DAMAGE_AMOUNT * targets.size());
            Component message = Component.literal("Your Implosion hit ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(targets.size()))
                            .withStyle(ChatFormatting.RED))
                    .append(Component.literal(enemyNoun + " for ")
                            .withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(damageText)
                            .withStyle(ChatFormatting.RED))
                    .append(Component.literal(" damage.")
                            .withStyle(ChatFormatting.GRAY));
            player.displayClientMessage(message, false);
        }

        itemStack.set(ModComponents.DURABILITY, durability - 1);
        player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        int durability = itemStack.getOrDefault(ModComponents.DURABILITY, 0);
        consumer.accept(Component.literal("Ability: Wither Impact").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.literal("Teleports you ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("10 blocks").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" ahead and").withStyle(ChatFormatting.GRAY)));
        consumer.accept(Component.literal("implodes dealing ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("massive damage").withStyle(ChatFormatting.RED))
                .append(Component.literal(" to").withStyle(ChatFormatting.GRAY)));
        consumer.accept(Component.literal("nearby enemies.").withStyle(ChatFormatting.GRAY));

        consumer.accept(Component.empty());

        ChatFormatting durabilityColor = durability > 50 ? ChatFormatting.GREEN : (durability > 20 ? ChatFormatting.YELLOW : ChatFormatting.RED);
        consumer.accept(Component.literal("Durability: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(durability)).withStyle(durabilityColor)));

        consumer.accept(Component.empty());

        consumer.accept(getItemTypeLore(itemStack));
    }
}
