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
public class ModelRoadrunnerBoots extends HumanoidModel {
   public ModelRoadrunnerBoots(ModelPart p_170677_) {
      super(p_170677_);
   }

   public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
      MeshDefinition meshdefinition = HumanoidModel.m_170681_(deformation, 1.0F);
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition leftleg = partdefinition.m_171597_("left_leg");
      PartDefinition rightleg = partdefinition.m_171597_("right_leg");
      rightleg.m_171599_(
         "featherr",
         CubeListBuilder.m_171558_().m_171514_(20, 22).m_171488_(-3.0F, -7.5F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-1.5F, 9.5F, 0.4F, 0.0F, 0.9773844F, -0.312763F)
      );
      leftleg.m_171599_(
         "featherl",
         CubeListBuilder.m_171558_().m_171514_(20, 22).m_171480_().m_171488_(0.0F, -7.4F, 0.0F, 3.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(1.5F, 9.5F, -0.4F, 0.0F, -0.9773844F, 0.312763F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }
}
