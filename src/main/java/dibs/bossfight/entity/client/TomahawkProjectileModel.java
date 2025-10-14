package dibs.bossfight.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dibs.bossfight.DePaulDibsBossFight;
import dibs.bossfight.entity.custom.TomahawkProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.entity.state.
// https://docs.neoforged.net/docs/entities/renderer/
// https://github.com/Ethanp414/GameModProject-MinecraftMod/blob/AIDevelopment/src/main/java/Entity/client/DibsModel.java
// https://github.com/Ethanp414/GameModProject-MinecraftMod/blob/AIDevelopment/src/main/java/Entity/client/DibsRenderState.java



public class TomahawkProjectileModel extends EntityModel<TomahawkProjectileEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "tomahawk"), "main");
    private final ModelPart tomahawk;

    public TomahawkProjectileModel(ModelPart root) {
        this.tomahawk = root.getChild("tomahawk");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition tomahawk = partdefinition.addOrReplaceChild("tomahawk", CubeListBuilder.create(), PartPose.offset(0.0F, 16.5F, 0.0F));

        PartDefinition cube_r1 = tomahawk.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 7).addBox(1.5F, 2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -4.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = tomahawk.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(7, 9).addBox(0.5F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -5.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r3 = tomahawk.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(3, 10).addBox(-2.5F, -1.5F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 5.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r4 = tomahawk.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(1, 4).addBox(-2.5F, -1.5F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r5 = tomahawk.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(18, 1).addBox(-0.5F, -9.0F, -0.5F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(TomahawkProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        tomahawk.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}