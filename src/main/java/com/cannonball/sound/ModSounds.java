package com.cannonball.sound;

import com.cannonball.CannonballMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent CANNON_FIRE = register("cannon_fire");
    public static final SoundEvent CANNON_LOAD = register("cannon_load");
    public static final SoundEvent CANNON_RAM = register("cannon_ram");
    public static final SoundEvent CANNON_AIM = register("cannon_aim");
    public static final SoundEvent CANNONBALL_WHISTLE = register("cannonball_whistle");
    public static final SoundEvent CANNONBALL_RICOCHET = register("cannonball_ricochet");
    public static final SoundEvent CANNONBALL_IMPACT = register("cannonball_impact");

    private static SoundEvent register(String name) {
        ResourceLocation id = new ResourceLocation(CannonballMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        CannonballMod.LOGGER.info("Registering sounds for " + CannonballMod.MOD_ID);
    }
}
