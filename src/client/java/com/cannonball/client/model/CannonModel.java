package com.cannonball.client.model;

import com.cannonball.CannonballMod;
import com.cannonball.entity.CannonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class CannonModel extends EntityModel<CannonEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(CannonballMod.MOD_ID, "cannon"), "main");

    private final ModelPart root;
    private final ModelPart carriage;
    private final ModelPart barrelMount;
    private final ModelPart leftWheel;
    private final ModelPart rightWheel;

    public CannonModel(ModelPart root) {
        this.root = root;
        this.carriage = root.getChild("carriage");
        this.barrelMount = this.carriage.getChild("barrel_mount");
        this.leftWheel = this.carriage.getChild("left_wheel");
        this.rightWheel = this.carriage.getChild("right_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Carriage root (located at axle center level: Y = 18 in Minecraft model space)
        PartDefinition carriage = partdefinition.addOrReplaceChild("carriage",
                CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

        // Axle: heavy cast iron bar connecting wheels
        carriage.addOrReplaceChild("axle",
                CubeListBuilder.create().texOffs(0, 28)
                        .addBox(-11.0F, -2.0F, -2.0F, 22.0F, 4.0F, 4.0F),
                PartPose.ZERO);

        // Wooden Trail extending back to the ground with slight upward slope
        carriage.addOrReplaceChild("trail",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, -1.0F, -2.0F, 6.0F, 4.0F, 24.0F)
                        .texOffs(36, 0)
                        .addBox(-4.0F, 1.0F, 20.0F, 8.0F, 4.0F, 4.0F), // Trail spade / ground bumper
                PartPose.rotation(0.12F, 0.0F, 0.0F));

        // Left and Right wooden side cheeks
        carriage.addOrReplaceChild("left_cheek",
                CubeListBuilder.create().texOffs(52, 28)
                        .addBox(3.0F, -9.0F, -5.0F, 3.0F, 8.0F, 12.0F),
                PartPose.ZERO);

        carriage.addOrReplaceChild("right_cheek",
                CubeListBuilder.create().texOffs(52, 28)
                        .addBox(-6.0F, -9.0F, -5.0F, 3.0F, 8.0F, 12.0F),
                PartPose.ZERO);

        // Wheels: Spoked carriage wheels with wooden rims and iron bands
        // Left wheel (X = 11)
        PartDefinition leftWheel = carriage.addOrReplaceChild("left_wheel",
                CubeListBuilder.create().texOffs(0, 36)
                        .addBox(0.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F) // Hub
                        .texOffs(16, 36)
                        .addBox(0.5F, -9.0F, -8.0F, 1.0F, 2.0F, 16.0F) // Top rim
                        .addBox(0.5F, 7.0F, -8.0F, 1.0F, 2.0F, 16.0F) // Bottom rim
                        .addBox(0.5F, -7.0F, 7.0F, 1.0F, 14.0F, 2.0F) // Front rim
                        .addBox(0.5F, -7.0F, -9.0F, 1.0F, 14.0F, 2.0F) // Back rim
                        .texOffs(0, 48)
                        .addBox(0.5F, -7.0F, -1.0F, 1.0F, 14.0F, 2.0F) // Vertical spoke
                        .addBox(0.5F, -1.0F, -7.0F, 1.0F, 2.0F, 14.0F), // Horizontal spoke
                PartPose.offset(11.0F, 0.0F, 0.0F));

        // Right wheel (X = -13)
        PartDefinition rightWheel = carriage.addOrReplaceChild("right_wheel",
                CubeListBuilder.create().texOffs(0, 36)
                        .addBox(-2.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F) // Hub
                        .texOffs(16, 36)
                        .addBox(-1.5F, -9.0F, -8.0F, 1.0F, 2.0F, 16.0F)
                        .addBox(-1.5F, 7.0F, -8.0F, 1.0F, 2.0F, 16.0F)
                        .addBox(-1.5F, -7.0F, 7.0F, 1.0F, 14.0F, 2.0F)
                        .addBox(-1.5F, -7.0F, -9.0F, 1.0F, 14.0F, 2.0F)
                        .texOffs(0, 48)
                        .addBox(-1.5F, -7.0F, -1.0F, 1.0F, 14.0F, 2.0F)
                        .addBox(-1.5F, -1.0F, -7.0F, 1.0F, 2.0F, 14.0F),
                PartPose.offset(-11.0F, 0.0F, 0.0F));

        // Barrel Mount (Pivots for vertical aiming / pitch at trunnion level: Y = -7)
        PartDefinition barrelMount = carriage.addOrReplaceChild("barrel_mount",
                CubeListBuilder.create()
                        .texOffs(0, 52)
                        .addBox(-6.0F, -2.0F, -2.0F, 12.0F, 4.0F, 4.0F) // Trunnions
                        .texOffs(32, 52)
                        .addBox(-3.5F, -3.5F, 1.0F, 7.0F, 7.0F, 8.0F) // Breech chamber (back towards carriage trail)
                        .texOffs(62, 52)
                        .addBox(-1.5F, -1.5F, 9.0F, 3.0F, 3.0F, 3.0F) // Cascabel button (rear)
                        .texOffs(0, 64)
                        .addBox(-3.0F, -3.0F, -13.0F, 6.0F, 6.0F, 14.0F) // Main chase tube (extending forward)
                        .texOffs(40, 64)
                        .addBox(-2.5F, -2.5F, -24.0F, 5.0F, 5.0F, 11.0F) // Barrel forward taper
                        .texOffs(0, 84)
                        .addBox(-3.0F, -3.0F, -27.0F, 6.0F, 6.0F, 3.0F) // Muzzle ring flare (front tip)
                        .texOffs(18, 84)
                        .addBox(-1.5F, -1.5F, -27.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F)), // Bore hole
                PartPose.offset(0.0F, -7.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(CannonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Vertical aiming (Pitch: in Minecraft entity coordinates, negative is looking UP)
        float pitchAngle = (float) Math.toRadians(entity.getCannonPitch());
        this.barrelMount.xRot = pitchAngle;

        // Visual Recoil Kickback along the barrel's longitudinal axis (pushes backward to +Z)
        int recoil = entity.getRecoilTicks();
        if (recoil > 0) {
            float recoilFactor = (float) Math.sin((recoil / 10.0F) * Math.PI);
            float recoilDist = recoilFactor * 2.5F;
            this.barrelMount.z = (float) (recoilDist * Math.cos(pitchAngle));
            this.barrelMount.y = -7.0F + (float) (recoilDist * Math.sin(pitchAngle));
        } else {
            this.barrelMount.z = 0.0F;
            this.barrelMount.y = -7.0F;
        }

        // Wheel rolling animation when the cannon moves
        this.leftWheel.xRot = limbSwing * 0.7F;
        this.rightWheel.xRot = limbSwing * 0.7F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
