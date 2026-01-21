package com.darkaddons.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class utilities {
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

    public static String upperCaseFirst(String string) {
        if (string.isEmpty()) return string;
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
