package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelNoveltyHat extends HumanoidModel {
   public ModelNoveltyHat(ModelPart part) {
      super(part);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition head = partdefinition.m_171597_("head");
      PartDefinition hat = head.m_171599_(
         "hat",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 30)
            .m_171488_(-4.5F, -5.0F, -4.5F, 9.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171514_(31, 55)
            .m_171488_(4.5F, -2.0F, -2.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .m_171514_(31, 55)
            .m_171480_()
            .m_171488_(-8.5F, -2.0F, -2.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .m_171555_(false)
            .m_171514_(0, 46)
            .m_171488_(-5.5F, 1.0F, -9.5F, 11.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.0F, -6.0F, 0.0F)
      );
      PartDefinition pipes = hat.m_171599_(
         "pipes",
         CubeListBuilder.m_171558_().m_171514_(0, 55).m_171488_(-7.5F, 0.0F, 0.0F, 15.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 2.0F, -0.5F, -0.6545F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }
}
