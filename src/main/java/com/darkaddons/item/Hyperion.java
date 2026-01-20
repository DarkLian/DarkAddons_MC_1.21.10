package com.darkaddons.item;

import com.darkaddons.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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

import static com.darkaddons.utils.utilities.getNearByEntities;
import static com.darkaddons.utils.utilities.getTeleportTarget;

public class Hyperion extends Item {

    public Hyperion(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        int durability = itemStack.getOrDefault(ModComponents.durability, 0);
        return durability > 0;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        int durability = itemStack.getOrDefault(ModComponents.durability, 0);
        BlockHitResult hitResult = getTeleportTarget(player, 10.0);

        if (level.isClientSide()) return InteractionResult.PASS;

        int cooldown = 3;
        if (durability <= 0) {
            player.displayClientMessage(Component.literal("Insufficient durability, please repair your weapon"), false);
            player.getCooldowns().addCooldown(itemStack, cooldown);
            return InteractionResult.PASS;
        }

        Vec3 rawHit = hitResult.getLocation();
        Vec3 finalPosition;
        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult.getDirection() == Direction.UP) {
            finalPosition = new Vec3(rawHit.x, rawHit.y, rawHit.z);
        } else {
            finalPosition = new Vec3(rawHit.x, rawHit.y - player.getEyeHeight(), rawHit.z);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Direction face = hitResult.getDirection();
            finalPosition = finalPosition.add(face.getStepX() * 0.3, face.getStepY() * 0.3, face.getStepZ() * 0.3);
            if (face == Direction.UP) {
                finalPosition = new Vec3(finalPosition.x, Math.floor(finalPosition.y) + 1.05, finalPosition.z);
            } else if (face == Direction.DOWN) {
                finalPosition = new Vec3(finalPosition.x, finalPosition.y - 0.05, finalPosition.z);
            }
        }
        BlockPos checkPos = BlockPos.containing(finalPosition.x, finalPosition.y, finalPosition.z);
        int attempts = 0;
        while (level.getBlockState(checkPos).blocksMotion() && attempts < 3) {
            finalPosition = finalPosition.add(0, 1.0, 0);
            checkPos = checkPos.above();
            attempts++;
        }

        player.teleportTo(finalPosition.x, finalPosition.y, finalPosition.z);

        List<Entity> nearByEntities = getNearByEntities(player, 10.0);
        DamageSource damageSource = level.damageSources().magic();
        for (Entity entity : nearByEntities) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightningBolt.setPos(entity.getX(), entity.getY(), entity.getZ());
            lightningBolt.setVisualOnly(true);
            level.addFreshEntity(lightningBolt);
            float damageAmount = 20.0f;
            if (entity instanceof LivingEntity livingEntity) livingEntity.hurt(damageSource, damageAmount);
        }
        itemStack.set(ModComponents.durability, durability - 1);
        if (!nearByEntities.isEmpty())
            player.displayClientMessage(Component.literal("Struck " + nearByEntities.size() + " entities"), false);
        player.getCooldowns().addCooldown(itemStack, cooldown);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        String durability = String.valueOf(itemStack.getOrDefault(ModComponents.durability, 0));
        consumer.accept(Component.literal("Durability: ").append(Component.literal(durability).withStyle(ChatFormatting.DARK_AQUA)));
    }


}
