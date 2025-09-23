package Entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class GeckoModel 
{
// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "kaupengecko"), "main");
	private final ModelPart Body;
	private final ModelPart Head;
	private final ModelPart Tail;
	private final ModelPart FrontLegL;
	private final ModelPart FrontLegR;
	private final ModelPart BackLegL;
	private final ModelPart BackLegR;
	private final ModelPart bb_main;

	public kaupengecko(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Head = this.Body.getChild("Head");
		this.Tail = this.Body.getChild("Tail");
		this.FrontLegL = this.Body.getChild("FrontLegL");
		this.FrontLegR = this.Body.getChild("FrontLegR");
		this.BackLegL = this.Body.getChild("BackLegL");
		this.BackLegR = this.Body.getChild("BackLegR");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.25F, -2.25F, -3.0F, 2.5F, 2.25F, 4.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 1.0F));

		PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(8, 7).addBox(-1.0F, -1.0623F, -1.9587F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.75F, -0.3123F, -2.4087F, 1.5F, 1.25F, 0.45F, new CubeDeformation(0.0F))
		.texOffs(4, 19).addBox(-1.25F, -0.8123F, -1.7087F, 0.75F, 0.75F, 0.75F, new CubeDeformation(0.0F))
		.texOffs(4, 19).mirror().addBox(0.5F, -0.8123F, -1.7087F, 0.75F, 0.75F, 0.75F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -1.0F, -3.0F));

		PartDefinition Tail = Body.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.9F, 3.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 1.5F));

		PartDefinition FrontLegL = Body.addOrReplaceChild("FrontLegL", CubeListBuilder.create(), PartPose.offset(1.1986F, -0.4741F, -2.4807F));

		PartDefinition FLegL_r1 = FrontLegL.addOrReplaceChild("FLegL_r1", CubeListBuilder.create().texOffs(11, 12).addBox(-0.1428F, -0.4441F, -0.6528F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2612F, -0.0259F, 0.0752F, 0.0573F, 0.2106F, 0.2679F));

		PartDefinition FrontLegR = Body.addOrReplaceChild("FrontLegR", CubeListBuilder.create(), PartPose.offset(-1.275F, -0.4804F, -2.5515F));

		PartDefinition FLegR_r1 = FrontLegR.addOrReplaceChild("FLegR_r1", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, -0.25F, 0.0573F, -0.2106F, -0.2679F));

		PartDefinition BackLegL = Body.addOrReplaceChild("BackLegL", CubeListBuilder.create(), PartPose.offset(1.2612F, -0.5957F, 0.7739F));

		PartDefinition BLegL_r1 = BackLegL.addOrReplaceChild("BLegL_r1", CubeListBuilder.create().texOffs(11, 2).addBox(-0.832F, -0.4122F, -0.4744F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0612F, -0.0043F, -0.0239F, -0.0883F, -0.3958F, 0.2794F));

		PartDefinition BackLegR = Body.addOrReplaceChild("BackLegR", CubeListBuilder.create(), PartPose.offset(-1.2612F, -0.5957F, 0.7739F));

		PartDefinition BLegR_r1 = BackLegR.addOrReplaceChild("BLegR_r1", CubeListBuilder.create().texOffs(11, 0).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2612F, 0.5957F, 0.4761F, -0.0883F, 0.3958F, -0.2794F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition camera_r1 = bb_main.addOrReplaceChild("camera_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9795F, -0.6308F, 3.1416F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
