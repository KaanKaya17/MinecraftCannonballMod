package com.cannonball.client.renderer;

import com.cannonball.CannonballMod;
import com.cannonball.client.model.CannonballModel;
import com.cannonball.entity.CannonballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CannonballEntityRenderer extends EntityRenderer<CannonballEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CannonballMod.MOD_ID, "textures/entity/cannonball.png");
    private final CannonballModel model;

    public CannonballEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.25F;
        this.model = new CannonballModel(context.bakeLayer(CannonballModel.LAYER_LOCATION));
    }

    @Override
    public void render(CannonballEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        double horizontalDist = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        float yaw = (float) (Mth.atan2(motion.x, motion.z) * (180.0 / Math.PI));
        float pitch = (float) (Mth.atan2(motion.y, horizontalDist) * (180.0 / Math.PI));

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));

        // Spin animation
        float spin = Mth.lerp(partialTick, entity.prevSpinAngle, entity.spinAngle);
        poseStack.mulPose(Axis.XP.rotationDegrees(spin));

        poseStack.scale(-1.0F, -1.0F, 1.0F);

        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CannonballEntity entity) {
        return TEXTURE;
    }
}
