package com.darkaddons.item;

import com.darkaddons.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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

import static com.darkaddons.utils.utilities.getNearByLivingEntities;
import static com.darkaddons.utils.utilities.getTeleportTarget;

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
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;

        ItemStack itemStack = player.getItemInHand(hand);
        int durability = itemStack.getOrDefault(ModComponents.DURABILITY, 0);

        if (durability <= 0) {
            player.displayClientMessage(Component.literal("Insufficient durability, please repair your weapon"), false);
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
            targetY += face.getStepY() * 0.3;
            targetZ += face.getStepZ() * 0.3;
        }
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(targetX, targetY, targetZ);
        for (int i = 0; i < 3 && level.getBlockState(checkPos).blocksMotion(); i++) {
            targetY += 1.0;
            checkPos.move(0, 1, 0);
        }

        player.teleportTo(targetX, targetY, targetZ);

        List<LivingEntity> targets = getNearByLivingEntities(level, player, ABILITY_RADIUS, LivingEntity.class);
        DamageSource damageSource = level.damageSources().magic();
        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                lightningBolt.setVisualOnly(true);
                lightningBolt.setPos(target.getX(), target.getY(), target.getZ());
                level.addFreshEntity(lightningBolt);
                target.hurt(damageSource, DAMAGE_AMOUNT);
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
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        String durability = String.valueOf(itemStack.getOrDefault(ModComponents.DURABILITY, 0));
        consumer.accept(Component.literal("Durability: ").append(Component.literal(durability).withStyle(ChatFormatting.DARK_AQUA)));
    }


}
