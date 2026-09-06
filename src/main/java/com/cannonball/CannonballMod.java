package com.cannonball;

import com.cannonball.entity.ModEntities;
import com.cannonball.item.ModItems;
import com.cannonball.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CannonballMod implements ModInitializer {
    public static final String MOD_ID = "cannonball";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Realistic Cannonball Mod!");
        ModSounds.initialize();
        ModItems.initialize();
        ModEntities.initialize();
    }
}
