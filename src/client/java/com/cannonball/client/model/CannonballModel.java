package com.cannonball.client.model;

import com.cannonball.CannonballMod;
import com.cannonball.entity.CannonballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class CannonballModel extends EntityModel<CannonballEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(CannonballMod.MOD_ID, "cannonball"), "main");

    private final ModelPart sphere;

    public CannonballModel(ModelPart root) {
        this.sphere = root.getChild("sphere");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 3D Faceted Round Shot sphere
        partdefinition.addOrReplaceChild("sphere",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F) // Core
                        .texOffs(0, 10)
                        .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F) // Top/Bottom bulge
                        .texOffs(16, 0)
                        .addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F) // Left/Right bulge
                        .texOffs(16, 10)
                        .addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F), // Front/Back bulge
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(CannonballEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Continuous rolling / spin animation in flight
        this.sphere.xRot = (float) Math.toRadians(entity.spinAngle);
        this.sphere.yRot = (float) Math.toRadians(entity.spinAngle * 0.7F);
        this.sphere.zRot = (float) Math.toRadians(entity.spinAngle * 0.3F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.sphere.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
