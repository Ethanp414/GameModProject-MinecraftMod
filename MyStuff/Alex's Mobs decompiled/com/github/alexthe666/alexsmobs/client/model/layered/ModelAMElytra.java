package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ModelAMElytra extends HumanoidModel {
   private final ModelPart rightWing;
   private final ModelPart leftWing;

   public ModelAMElytra(ModelPart part) {
      super(part);
      this.leftWing = part.m_171324_("body").m_171324_("left_wing");
      this.rightWing = part.m_171324_("body").m_171324_("right_wing");
   }

   public static LayerDefinition createLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_().m_171597_("body");
      CubeDeformation cubedeformation = new CubeDeformation(1.0F);
      partdefinition.m_171599_(
         "left_wing",
         CubeListBuilder.m_171558_().m_171514_(32, 32).m_171488_(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation),
         PartPose.m_171423_(5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
      );
      partdefinition.m_171599_(
         "right_wing",
         CubeListBuilder.m_171558_().m_171514_(32, 32).m_171480_().m_171488_(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation),
         PartPose.m_171423_(-5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public ModelAMElytra withAnimations(LivingEntity entity) {
      if (entity != null) {
         float partialTick = Minecraft.m_91087_().m_91296_();
         float limbSwingAmount = entity.f_267362_.m_267711_(partialTick);
         float limbSwing = entity.f_267362_.m_267756_() + partialTick;
         this.m_6973_(entity, limbSwing, limbSwingAmount, entity.f_19797_ + partialTick, 0.0F, 0.0F);
      }

      return this;
   }

   public void m_6973_(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = (float) (Math.PI / 12);
      float f1 = (float) (-Math.PI / 12);
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (entityIn.m_21255_()) {
         float f4 = 1.0F;
         Vec3 vector3d = entityIn.m_20184_();
         if (vector3d.f_82480_ < 0.0) {
            Vec3 vector3d1 = vector3d.m_82541_();
            f4 = 1.0F - (float)Math.pow(-vector3d1.f_82480_, 1.5);
         }

         f = f4 * (float) (Math.PI / 9) + (1.0F - f4) * f;
         f1 = f4 * (float) (-Math.PI / 2) + (1.0F - f4) * f1;
      } else if (entityIn.m_6047_()) {
         f = (float) (Math.PI * 2.0 / 9.0);
         f1 = (float) (-Math.PI / 4);
         f2 = -1.0F;
         f3 = 0.08726646F;
      }

      this.leftWing.f_104200_ = 5.0F;
      this.leftWing.f_104201_ = f2;
      if (entityIn instanceof AbstractClientPlayer abstractclientplayerentity) {
         abstractclientplayerentity.f_108542_ = (float)(abstractclientplayerentity.f_108542_ + (f - abstractclientplayerentity.f_108542_) * 0.1);
         abstractclientplayerentity.f_108543_ = (float)(abstractclientplayerentity.f_108543_ + (f3 - abstractclientplayerentity.f_108543_) * 0.1);
         abstractclientplayerentity.f_108544_ = (float)(abstractclientplayerentity.f_108544_ + (f1 - abstractclientplayerentity.f_108544_) * 0.1);
         this.leftWing.f_104203_ = abstractclientplayerentity.f_108542_;
         this.leftWing.f_104204_ = abstractclientplayerentity.f_108543_;
         this.leftWing.f_104205_ = abstractclientplayerentity.f_108544_;
      } else {
         this.leftWing.f_104203_ = f;
         this.leftWing.f_104205_ = f1;
         this.leftWing.f_104204_ = f3;
      }

      this.rightWing.f_104200_ = -this.leftWing.f_104200_;
      this.rightWing.f_104204_ = -this.leftWing.f_104204_;
      this.rightWing.f_104201_ = this.leftWing.f_104201_;
      this.rightWing.f_104203_ = this.leftWing.f_104203_;
      this.rightWing.f_104205_ = -this.leftWing.f_104205_;
   }
}
