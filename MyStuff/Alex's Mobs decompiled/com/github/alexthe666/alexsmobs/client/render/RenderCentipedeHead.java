package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCaveCentipede;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCentipedeHeadEyes;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderCentipedeHead extends MobRenderer<EntityCentipedeHead, AdvancedEntityModel<EntityCentipedeHead>> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede.png");

   public RenderCentipedeHead(Context renderManagerIn) {
      super(renderManagerIn, new ModelCaveCentipede(0), 0.5F);
      this.m_115326_(new LayerCentipedeHeadEyes(this));
   }

   protected void setupRotations(EntityCentipedeHead entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
      if (this.m_5936_(entity)) {
         yawIn += (float)(Math.cos(entity.f_19797_ * 3.25) * Math.PI * 0.4F);
      }

      Pose pose = entity.m_20089_();
      if (pose != Pose.SLEEPING) {
         stack.m_252781_(Axis.f_252436_.m_252977_(180.0F - yawIn));
      }

      if (entity.f_20919_ > 0) {
         float f = (entity.f_20919_ + partialTickTime - 1.0F) / 20.0F * 1.6F;
         f = Mth.m_14116_(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         stack.m_252880_(0.0F, f * 1.0F, 0.0F);
         stack.m_252781_(Axis.f_252403_.m_252977_(f * this.getFlipDegrees(entity)));
      } else if (entity.m_8077_()) {
         String s = ChatFormatting.m_126649_(entity.m_7755_().getString());
         if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
            stack.m_85837_(0.0, entity.m_20206_() + 0.1F, 0.0);
            stack.m_252781_(Axis.f_252403_.m_252977_(180.0F));
         }
      }
   }

   protected float getFlipDegrees(EntityCentipedeHead centipede) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(EntityCentipedeHead entity) {
      return TEXTURE;
   }
}
