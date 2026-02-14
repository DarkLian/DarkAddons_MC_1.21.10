package com.darkaddons.item;

import com.google.common.collect.LinkedHashMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.math.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static com.darkaddons.utils.ModUtilities.getItemTypeLore;
import static com.darkaddons.utils.ModUtilities.getRarityColor;

public class BonzoStaff extends Item {
    private static final String[] BALLOON_TEXTURE = {
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJkZDExZGEwNDI1MmY3NmI2OTM0YmMyNjYxMmY1NGYyNjRmMzBlZWQ3NGRmODk5NDEyMDllMTkxYmViYzBhMiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg2OGU2YTVjNGE0NDVkNjBhMzA1MGI1YmVjMWQzN2FmMWIyNTk0Mzc0NWQyZDQ3OTgwMGM4NDM2NDg4MDY1YSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI2ZWM3Y2QzYjZhZTI0OTk5NzEzN2MxYjk0ODY3YzY2ZTk3NDk5ZGEwNzFiZjUwYWRmZDM3MDM0MTMyZmEwMyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2IxYWU3YTQ3MTcyOTY1MWI1NjY3YjgxNjk0ZTQ5MjgwOGM1MDkwYzJiMTY4ZjBhOTE5MGZkMDAyZWU1MGEyNiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTdjOGQ0Yzc3ZTRkNjlkNzk0N2ZlMTRhOTMwZmJjN2Y2ZTAwYmMzNzYwOTc4MGQzZGZiZDk1ZTRhNGRmOTFiMiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA1MmJlMWMwNmE0YTMyNTEyOWQ2ZjQxYmI4NGYwZWExY2E2ZjlmNjllYmRmZmY0MzE2ZTc0MjQ1MWM3OWMyMSJ9fX0="
    };
    private static final List<BalloonProjectile> activeProjectiles = new ArrayList<>();

    public BonzoStaff(Properties properties) {
        super(properties);
    }

    public static void tick(ServerLevel level) {
        Iterator<BalloonProjectile> iterator = activeProjectiles.iterator();

        while (iterator.hasNext()) {
            BalloonProjectile proj = iterator.next();

            if (proj.entity.isRemoved() || !proj.entity.isAlive()) {
                iterator.remove();
                continue;
            }

            Vec3 currentPos = proj.entity.position();
            Vec3 nextPos = currentPos.add(proj.velocity);
            proj.entity.setPos(nextPos);

            proj.entity.setTransformation(new Transformation(
                    null,
                    null,
                    new Vector3f(1.5f, 1.5f, 1.5f),
                    null
            ));

            boolean hit = !level.getBlockState(new net.minecraft.core.BlockPos((int) nextPos.x, (int) nextPos.y, (int) nextPos.z)).isAir();

            AABB searchBox = proj.entity.getBoundingBox().inflate(0.5);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox);
            for (LivingEntity target : targets) {
                if (target != proj.owner) {
                    hit = true;
                    break;
                }
            }

            if (hit || proj.age++ > 100) {
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, nextPos.x, nextPos.y, nextPos.z, 1, 0, 0, 0, 0);
                level.playSound(null, nextPos.x, nextPos.y, nextPos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 1.0f);

                List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, proj.entity.getBoundingBox().inflate(3.0));
                for (LivingEntity victim : victims) {
                    if (victim != proj.owner) {
                        victim.hurt(level.damageSources().magic(), 15.0f);
                        Vec3 knockback = victim.position().subtract(nextPos).normalize().scale(1.5);
                        victim.setDeltaMovement(victim.getDeltaMovement().add(knockback));
                        victim.hurtMarked = true;
                    }
                }

                proj.entity.discard();
                iterator.remove();
            }
        }
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.literal("Bonzo's Staff").withStyle(getRarityColor(stack));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;


        Display.ItemDisplay balloon = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        balloon.setPos(player.getEyePosition().add(player.getLookAngle().scale(0.5)));

        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        String randomTexture = BALLOON_TEXTURE[ThreadLocalRandom.current().nextInt(BALLOON_TEXTURE.length)];

        LinkedHashMultimap<String, Property> tempMap = LinkedHashMultimap.create();
        tempMap.put("textures", new Property("textures", randomTexture));
        PropertyMap balloonProperties = new PropertyMap(tempMap);

        GameProfile profile = new GameProfile(
                UUID.randomUUID(),
                "BonzoBalloon",
                balloonProperties
        );

        head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile));
        balloon.setItemStack(head);

        balloon.setTransformation(new Transformation(
                null,
                null,
                new Vector3f(1.5f, 1.5f, 1.5f),
                null
        ));

        balloon.setBillboardConstraints(Display.BillboardConstraints.FIXED);
        level.addFreshEntity(balloon);

        Vec3 velocity = player.getLookAngle().normalize().scale(0.5);
        activeProjectiles.add(new BalloonProjectile(balloon, velocity, player));

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 1.0f, 2.0f);

        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        consumer.accept(Component.literal("Ability: Showtime").withStyle(ChatFormatting.GOLD));
        consumer.accept(Component.literal("Shoots a balloon that explodes").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal("on impact, dealing ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("massive damage").withStyle(ChatFormatting.RED))
                .append(Component.literal(".").withStyle(ChatFormatting.GRAY)));
        consumer.accept(Component.empty());
        consumer.accept(getItemTypeLore(stack));
    }

    private static class BalloonProjectile {
        Display.ItemDisplay entity;
        Vec3 velocity;
        Player owner;
        int age = 0;

        public BalloonProjectile(Display.ItemDisplay entity, Vec3 velocity, Player owner) {
            this.entity = entity;
            this.velocity = velocity;
            this.owner = owner;
        }
    }
}