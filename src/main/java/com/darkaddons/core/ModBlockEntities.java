package com.darkaddons.core;

import com.darkaddons.block.entity.ShowcaseBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DarkAddons.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {
        DarkAddons.LOGGER.info("Registering Block Entities for DarkAddons");
    }

    public static final BlockEntityType<ShowcaseBlockEntity> SHOWCASE_BLOCK_ENTITY = register("showcase_block_entity", ShowcaseBlockEntity::new, ModBlocks.SHOWCASE_BLOCK);


}