package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, ModelCrimsonMosquito> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito.png");
   private static final ResourceLocation TEXTURE_SICK = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_blue.png");
   private static final ResourceLocation TEXTURE_FLY = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_fly.png");
   private static final ResourceLocation TEXTURE_SICK_FLY = new ResourceLocation("alexsmobs:textures/entity/crimson_mosquito_fly_blue.png");

   public RenderCrimsonMosquito(Context renderManagerIn) {
      super(renderManagerIn, new ModelCrimsonMosquito(), 0.6F);
      this.m_115326_(new LayerCrimsonMosquitoBlood(this));
   }

   protected void scale(EntityCrimsonMosquito entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
      matrixStackIn.m_85841_(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
   }

   protected boolean isShaking(EntityCrimsonMosquito fly) {
      return fly.isSick() || fly.getFleeingEntityId() != -1;
   }

   protected void setupRotations(EntityCrimsonMosquito entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      if (this.isShaking(entityLiving)) {
         rotationYaw += (float)(Math.cos(entityLiving.f_19797_ * 7.0) * Math.PI * 0.9F);
         float vibrate = 0.05F * entityLiving.getMosquitoScale();
         matrixStackIn.m_252880_(
            (entityLiving.m_217043_().m_188501_() - 0.5F) * vibrate,
            (entityLiving.m_217043_().m_188501_() - 0.5F) * vibrate,
            (entityLiving.m_217043_().m_188501_() - 0.5F) * vibrate
         );
      }

      super.m_7523_(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
   }

   public ResourceLocation getTextureLocation(EntityCrimsonMosquito entity) {
      if (entity.isSick()) {
         return entity.isFromFly() ? TEXTURE_SICK_FLY : TEXTURE_SICK;
      } else {
         return entity.isFromFly() ? TEXTURE_FLY : TEXTURE;
      }
   }
}
