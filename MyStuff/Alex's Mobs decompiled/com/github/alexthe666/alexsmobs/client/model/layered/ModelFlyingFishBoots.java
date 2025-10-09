package com.github.alexthe666.alexsmobs.client.model.layered;

import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class ModelFlyingFishBoots extends HumanoidModel {
   private final ModelPart rightFish;
   private final ModelPart leftFish;
   private final ModelPart rightWingOuter;
   private final ModelPart leftWingOuter;
   private final ModelPart rightWingInner;
   private final ModelPart leftWingInner;

   public ModelFlyingFishBoots(ModelPart root) {
      super(root);
      this.rightFish = root.m_171324_("right_leg").m_171324_("RBoot");
      this.leftFish = root.m_171324_("left_leg").m_171324_("LBoot");
      this.rightWingOuter = this.rightFish.m_171324_("RwingR");
      this.leftWingOuter = this.leftFish.m_171324_("LwingL");
      this.rightWingInner = this.rightFish.m_171324_("RwingL");
      this.leftWingInner = this.leftFish.m_171324_("LwingR");
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 1.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition leftleg = partdefinition.m_171597_("left_leg");
      PartDefinition rightleg = partdefinition.m_171597_("right_leg");
      PartDefinition RBoot = rightleg.m_171599_(
         "RBoot",
         CubeListBuilder.m_171558_()
            .m_171514_(18, 12)
            .m_171480_()
            .m_171488_(-1.9F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
            .m_171555_(false)
            .m_171514_(0, 25)
            .m_171480_()
            .m_171488_(0.0F, -2.0F, 2.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
            .m_171555_(false)
            .m_171514_(0, 0)
            .m_171480_()
            .m_171488_(-2.5F, 0.0F, -5.0F, 5.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171555_(false),
         PartPose.m_171419_(-0.1F, 10.0F, 0.0F)
      );
      RBoot.m_171599_(
         "RwingR",
         CubeListBuilder.m_171558_().m_171514_(9, 47).m_171480_().m_171488_(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).m_171555_(false),
         PartPose.m_171423_(-2.5F, 1.0F, -3.0F, 0.0F, -0.5672F, 0.0F)
      );
      RBoot.m_171599_(
         "RwingL",
         CubeListBuilder.m_171558_().m_171514_(0, 42).m_171480_().m_171488_(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).m_171555_(false),
         PartPose.m_171423_(2.5F, 1.0F, -3.0F, 0.0F, 0.5672F, 0.0F)
      );
      PartDefinition LBoot = leftleg.m_171599_(
         "LBoot",
         CubeListBuilder.m_171558_()
            .m_171514_(18, 12)
            .m_171488_(-2.1F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
            .m_171514_(0, 25)
            .m_171488_(0.0F, -2.0F, 2.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 0)
            .m_171488_(-2.5F, 0.0F, -5.0F, 5.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.1F, 10.0F, 0.0F)
      );
      LBoot.m_171599_(
         "LwingL",
         CubeListBuilder.m_171558_().m_171514_(9, 47).m_171488_(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(2.5F, 1.0F, -3.0F, 0.0F, 0.5672F, 0.0F)
      );
      LBoot.m_171599_(
         "LwingR",
         CubeListBuilder.m_171558_().m_171514_(0, 42).m_171488_(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-2.5F, 1.0F, -3.0F, 0.0F, -0.5672F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public ModelFlyingFishBoots withAnimations(LivingEntity entity) {
      if (entity != null) {
         float partialTick = Minecraft.m_91087_().m_91296_();
         float ageInTicks = entity.f_19797_ + partialTick;
         float fly = Mth.m_14089_(ageInTicks * 0.2F) * 0.1F;
         float fly2 = fly * 0.35F;
         boolean flying = FlyingFishBootsUtil.getBoostTicks(entity) > 0;
         if (flying) {
            fly = (1.0F + Mth.m_14031_(ageInTicks * 1.2F)) * 0.8F;
            fly2 = fly;
         }

         this.rightWingOuter.f_104204_ = -0.5672F - fly;
         this.leftWingOuter.f_104204_ = 0.5672F + fly;
         this.rightWingInner.f_104204_ = 0.5672F + fly2;
         this.leftWingInner.f_104204_ = -0.5672F - fly2;
         if (flying || entity.m_20089_() == Pose.SWIMMING) {
            this.leftFish.f_104203_ = Maths.rad(-45.0);
            this.rightFish.f_104203_ = Maths.rad(-45.0);
            this.rightFish.f_104201_ = 11.0F;
            this.leftFish.f_104201_ = 11.0F;
            this.rightFish.f_104202_ = -1.5F;
            this.leftFish.f_104202_ = -1.5F;
         } else if (entity.m_20089_() == Pose.CROUCHING) {
            this.leftFish.f_104203_ = 0.0F;
            this.rightFish.f_104203_ = 0.0F;
            this.rightFish.f_104201_ = 8.0F;
            this.leftFish.f_104201_ = 8.0F;
            this.rightFish.f_104202_ = 0.0F;
            this.leftFish.f_104202_ = 0.0F;
         } else {
            this.leftFish.f_104203_ = 0.0F;
            this.rightFish.f_104203_ = 0.0F;
            this.rightFish.f_104201_ = 10.0F;
            this.leftFish.f_104201_ = 10.0F;
            this.rightFish.f_104202_ = 0.0F;
            this.leftFish.f_104202_ = 0.0F;
         }
      }

      return this;
   }
}
