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

public class ModelRockyChestplate extends HumanoidModel {
   private final ModelPart Body;
   private final ModelPart LeftArm;
   private final ModelPart RightArm;

   public ModelRockyChestplate(ModelPart root) {
      super(root);
      this.Body = root.m_171324_("body").m_171324_("BodyRocky");
      this.LeftArm = root.m_171324_("left_arm").m_171324_("LeftArmRocky");
      this.RightArm = root.m_171324_("right_arm").m_171324_("RightArmRocky");
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(new CubeDeformation(0.25F), 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition playerBody = partdefinition.m_171597_("body");
      PartDefinition playerLeftArm = partdefinition.m_171597_("left_arm");
      PartDefinition playerRightArm = partdefinition.m_171597_("right_arm");
      PartDefinition bodyRocky = playerBody.m_171599_(
         "BodyRocky",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 0)
            .m_171488_(-5.0F, -0.5F, -2.0F, 10.0F, 13.0F, 9.0F, deformation)
            .m_171514_(0, 23)
            .m_171488_(-4.0F, 0.5F, 6.0F, 8.0F, 11.0F, 4.0F, deformation)
            .m_171514_(25, 34)
            .m_171488_(-2.0F, -0.5F, 6.0F, 4.0F, 13.0F, 4.0F, deformation),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      PartDefinition leftArmRocky = playerLeftArm.m_171599_(
         "LeftArmRocky",
         CubeListBuilder.m_171558_()
            .m_171514_(25, 23)
            .m_171488_(-1.0F, -5.0F, -2.1F, 6.0F, 4.0F, 6.0F, deformation)
            .m_171514_(0, 39)
            .m_171488_(0.0F, -7.1F, -1.1F, 7.0F, 6.0F, 4.0F, deformation),
         PartPose.m_171419_(-1.0F, 2.0F, 0.0F)
      );
      PartDefinition rightArmRocky = playerRightArm.m_171599_(
         "RightArmRocky",
         CubeListBuilder.m_171558_()
            .m_171514_(25, 23)
            .m_171480_()
            .m_171488_(-5.0F, -5.0F, -2.1F, 6.0F, 4.0F, 6.0F, deformation)
            .m_171555_(false)
            .m_171514_(0, 39)
            .m_171480_()
            .m_171488_(-7.0F, -7.1F, -1.1F, 7.0F, 6.0F, 4.0F, deformation)
            .m_171555_(false),
         PartPose.m_171419_(1.0F, 2.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void m_6973_(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }
}
