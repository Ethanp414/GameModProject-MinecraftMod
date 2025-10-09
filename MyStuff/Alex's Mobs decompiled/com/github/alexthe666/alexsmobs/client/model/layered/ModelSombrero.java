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
public class ModelSombrero extends HumanoidModel {
   public ModelPart sombrero;

   public ModelSombrero(ModelPart p_170677_) {
      super(p_170677_);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      head.m_171599_(
         "sombrero",
         CubeListBuilder.m_171558_().m_171514_(0, 64).m_171488_(-4.0F, -11.0F, -4.0F, 8.0F, 6.0F, 8.0F, deformation),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      head.m_171599_(
         "sombrero2",
         CubeListBuilder.m_171558_().m_171514_(22, 73).m_171488_(-11.0F, -8.0F, -11.0F, 22.0F, 3.0F, 22.0F, deformation),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 128, 128);
   }

   public static LayerDefinition createArmorLayerAprilFools(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      head.m_171599_(
         "sombrero",
         CubeListBuilder.m_171558_().m_171514_(0, 64).m_171488_(-4.0F, 7.0F, -4.0F, 8.0F, 6.0F, 8.0F, deformation),
         PartPose.m_171423_(0.0F, 0.0F, 0.0F, (float) Math.PI, 0.0F, (float) (Math.PI / 10))
      );
      head.m_171599_(
         "sombrero2",
         CubeListBuilder.m_171558_().m_171514_(22, 73).m_171488_(-11.0F, 10.0F, -11.0F, 22.0F, 3.0F, 22.0F, deformation),
         PartPose.m_171423_(0.0F, 0.0F, 0.0F, (float) Math.PI, 0.0F, (float) (Math.PI / 10))
      );
      return LayerDefinition.m_171565_(meshdefinition, 128, 128);
   }
}
