package com.darkaddons.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ModUtilities {
    public static <T extends Entity> List<T> getNearByLivingEntities(Level level, Entity center, double radius, Class<T> entityClass) {
        AABB box = center.getBoundingBox().inflate(radius);
        return level.getEntitiesOfClass(entityClass, box, entity -> entity != center);
    }

    public static double distance(Entity entity, Player player) {
        double dx = entity.getX() - player.getX();
        double dy = entity.getY() - player.getY();
        double dz = entity.getZ() - player.getZ();
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
    }

    public static BlockHitResult getTeleportTarget(Player player, double z) {
        Level level = player.level();
        Vec3 startPos = player.getEyePosition();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 endPos = startPos.add(lookAngle.x * z, lookAngle.y * z, lookAngle.z * z);
        return level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }


    // Return non italic colored component literal
    public static MutableComponent literal(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(s -> s.withColor(color).withItalic(false));
    }

    public static MutableComponent literal(String text, ChatFormatting color1, ChatFormatting color2, boolean isFirst) {
        return Component.literal(text).withStyle(s -> s.withColor(isFirst ? color1 : color2).withItalic(false));
    }

    public static ItemStack createStaticItem(Item item, String name, ChatFormatting color) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.CUSTOM_NAME, ModUtilities.literal(name, color));
        itemStack.set(DataComponents.LORE, ItemLore.EMPTY);
        return itemStack;
    }
}
