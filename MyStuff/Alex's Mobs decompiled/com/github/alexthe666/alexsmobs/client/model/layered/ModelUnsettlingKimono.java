package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class ModelUnsettlingKimono extends HumanoidModel {
   private final ModelPart body;
   private final ModelPart left_arm;
   private final ModelPart right_arm;

   public ModelUnsettlingKimono(ModelPart root) {
      super(root);
      this.body = root.m_171324_("body");
      this.left_arm = root.m_171324_("left_arm");
      this.right_arm = root.m_171324_("right_arm");
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(new CubeDeformation(0.25F), 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition playerBody = partdefinition.m_171597_("body");
      PartDefinition playerLeftArm = partdefinition.m_171597_("left_arm");
      PartDefinition playerRightArm = partdefinition.m_171597_("right_arm");
      PartDefinition body = playerBody.m_171599_(
         "body",
         CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-4.0F, 0.0F, -2.0F, 8.0F, 17.0F, 4.0F, new CubeDeformation(0.75F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = playerLeftArm.m_171599_(
         "left_arm",
         CubeListBuilder.m_171558_().m_171514_(21, 18).m_171488_(-1.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.6F)),
         PartPose.m_171419_(-0.5F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = playerRightArm.m_171599_(
         "right_arm",
         CubeListBuilder.m_171558_()
            .m_171514_(21, 18)
            .m_171480_()
            .m_171488_(-3.0F, -2.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.6F))
            .m_171555_(false),
         PartPose.m_171419_(0.5F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void m_6973_(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
