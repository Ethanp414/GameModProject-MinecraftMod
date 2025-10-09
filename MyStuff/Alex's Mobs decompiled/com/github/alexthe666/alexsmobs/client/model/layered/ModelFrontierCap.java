package com.github.alexthe666.alexsmobs.client.model.layered;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelFrontierCap extends HumanoidModel {
   public ModelPart tail;
   public ModelPart hat;

   public ModelFrontierCap(ModelPart p_170677_) {
      super(p_170677_);
      this.hat = p_170677_.m_171324_("head").m_171324_("frontierhat");
      this.tail = this.hat.m_171324_("tail");
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      PartDefinition front = head.m_171599_(
         "frontierhat",
         CubeListBuilder.m_171558_().m_171514_(32, 32).m_171488_(-4.0F, -10.5F, -4.0F, 8.0F, 4.0F, 8.0F, deformation),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      front.m_171599_(
         "tail",
         CubeListBuilder.m_171558_().m_171514_(36, 46).m_171488_(-1.5F, -0.3F, -1.5F, 3.0F, 13.0F, 3.0F, deformation),
         PartPose.m_171423_(4.4F, -7.5F, 4.5F, 0.19565141F, -0.039095376F, -0.11728612F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public ModelFrontierCap withAnimations(LivingEntity entity) {
      if (entity != null) {
         float partialTick = Minecraft.m_91087_().m_91296_();
         float limbSwingAmount = entity.f_267362_.m_267711_(partialTick);
         float limbSwing = entity.f_267362_.m_267756_() + partialTick;
         this.tail.f_104203_ = 0.19565141F + limbSwingAmount * Maths.rad(80.0) + Mth.m_14089_(limbSwing * 0.3F) * 0.2F * limbSwingAmount;
         this.tail.f_104204_ = -0.039095376F + limbSwingAmount * Maths.rad(10.0) - Mth.m_14089_(limbSwing * 0.4F) * 0.3F * limbSwingAmount;
         this.tail.f_104205_ = -0.11728612F + limbSwingAmount * Maths.rad(10.0);
      }

      return this;
   }
}
