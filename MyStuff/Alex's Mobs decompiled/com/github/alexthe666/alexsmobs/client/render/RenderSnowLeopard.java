package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderSnowLeopard extends MobRenderer<EntitySnowLeopard, ModelSnowLeopard> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/snow_leopard.png");
   private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/snow_leopard_sleeping.png");

   public RenderSnowLeopard(Context renderManagerIn) {
      super(renderManagerIn, new ModelSnowLeopard(), 0.4F);
   }

   protected void scale(EntitySnowLeopard entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.9F, 0.9F, 0.9F);
   }

   public ResourceLocation getTextureLocation(EntitySnowLeopard entity) {
      return entity.m_5803_() ? TEXTURE_SLEEPING : TEXTURE;
   }
}
