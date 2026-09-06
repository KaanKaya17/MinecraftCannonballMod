package com.cannonball.item;

import com.cannonball.CannonballMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item CANNON = register("cannon", new CannonItem(new Item.Properties().stacksTo(1)));
    public static final Item CANNONBALL = register("cannonball", new CannonballItem(new Item.Properties().stacksTo(16)));
    public static final Item RAMROD = register("ramrod", new RamrodItem(new Item.Properties()));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(CannonballMod.MOD_ID, name), item);
    }

    public static void initialize() {
        CannonballMod.LOGGER.info("Registering items for " + CannonballMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(content -> {
            content.accept(CANNON);
            content.accept(CANNONBALL);
            content.accept(RAMROD);
        });

        net.minecraft.world.level.block.DispenserBlock.registerBehavior(CANNONBALL,
                new net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior() {
                    @Override
                    protected net.minecraft.world.entity.projectile.Projectile getProjectile(
                            net.minecraft.world.level.Level level,
                            net.minecraft.core.Position position,
                            net.minecraft.world.item.ItemStack stack) {
                        return new com.cannonball.entity.CannonballEntity(level, position.x(), position.y(), position.z(), 2);
                    }
                });
    }
}
