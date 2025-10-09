package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelFedora extends HumanoidModel {
   public ModelFedora(ModelPart part) {
      super(part);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      head.m_171599_(
         "fedora",
         CubeListBuilder.m_171558_().m_171514_(0, 44).m_171488_(-3.0F, -3.55F, -3.0F, 6.0F, 4.0F, 6.0F, deformation),
         PartPose.m_171419_(0.0F, -8.0F, 0.0F)
      );
      head.m_171599_(
         "fedora_shade",
         CubeListBuilder.m_171558_().m_171514_(0, 32).m_171488_(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 10.0F, deformation),
         PartPose.m_171419_(0.0F, -8.05F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }
}
