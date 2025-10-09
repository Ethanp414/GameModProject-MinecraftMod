package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelSpikedTurtleShell extends HumanoidModel {
   public ModelSpikedTurtleShell(ModelPart p_170677_) {
      super(p_170677_);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      head.m_171599_(
         "spikes1",
         CubeListBuilder.m_171558_().m_171514_(34, 15).m_171488_(0.0F, -33.0F, -4.5F, 4.0F, 1.0F, 9.0F, deformation),
         PartPose.m_171419_(0.0F, 24.0F, 0.0F)
      );
      head.m_171599_(
         "spikes2",
         CubeListBuilder.m_171558_().m_171514_(34, 15).m_171488_(-4.0F, -33.0F, -4.5F, 4.0F, 1.0F, 9.0F, deformation),
         PartPose.m_171419_(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }
}
