package com.cannonball.client;

import com.cannonball.client.model.CannonModel;
import com.cannonball.client.model.CannonballModel;
import com.cannonball.client.renderer.CannonEntityRenderer;
import com.cannonball.client.renderer.CannonballEntityRenderer;
import com.cannonball.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CannonballModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register 3D Model Layers
        EntityModelLayerRegistry.registerModelLayer(CannonModel.LAYER_LOCATION, CannonModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CannonballModel.LAYER_LOCATION, CannonballModel::createBodyLayer);

        // Register Entity Renderers
        EntityRendererRegistry.register(ModEntities.CANNON, CannonEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANNONBALL, CannonballEntityRenderer::new);
    }
}
