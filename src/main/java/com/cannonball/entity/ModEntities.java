package com.cannonball.entity;

import com.cannonball.CannonballMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<CannonEntity> CANNON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(CannonballMod.MOD_ID, "cannon"),
            FabricEntityTypeBuilder.<CannonEntity>create(MobCategory.MISC, CannonEntity::new)
                    .dimensions(EntityDimensions.fixed(1.6F, 1.1F))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(2)
                    .build()
    );

    public static final EntityType<CannonballEntity> CANNONBALL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(CannonballMod.MOD_ID, "cannonball"),
            FabricEntityTypeBuilder.<CannonballEntity>create(MobCategory.MISC, CannonballEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4F, 0.4F))
                    .trackRangeBlocks(128)
                    .trackedUpdateRate(1)
                    .build()
    );

    public static void initialize() {
        CannonballMod.LOGGER.info("Registering entities for " + CannonballMod.MOD_ID);
    }
}
