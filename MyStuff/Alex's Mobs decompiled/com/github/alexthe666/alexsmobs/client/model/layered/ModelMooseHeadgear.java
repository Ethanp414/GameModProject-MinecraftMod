package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelMooseHeadgear extends HumanoidModel {
   public ModelMooseHeadgear(ModelPart p_170677_) {
      super(p_170677_);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      head.m_171599_(
         "hornL",
         CubeListBuilder.m_171558_().m_171514_(3, 17).m_171488_(0.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, deformation),
         PartPose.m_171419_(5.0F, -8.0F, 1.0F)
      );
      head.m_171599_(
         "hornR",
         CubeListBuilder.m_171558_().m_171514_(3, 17).m_171480_().m_171488_(-10.0F, -5.5F, -4.0F, 10.0F, 6.0F, 8.0F, deformation),
         PartPose.m_171419_(-5.0F, -8.0F, 1.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }
}
