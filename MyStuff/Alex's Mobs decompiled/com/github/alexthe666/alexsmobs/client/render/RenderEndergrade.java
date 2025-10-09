package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerEndergradeSaddle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderEndergrade extends MobRenderer<EntityEndergrade, ModelEndergrade> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/endergrade.png");

   public RenderEndergrade(Context renderManagerIn) {
      super(renderManagerIn, new ModelEndergrade(), 0.6F);
      this.m_115326_(new LayerEndergradeSaddle(this));
   }

   @Nullable
   protected RenderType getRenderType(EntityEndergrade p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
      ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
      if (p_230496_3_) {
         return RenderType.m_110467_(resourcelocation);
      } else if (p_230496_2_) {
         return RenderType.m_110473_(resourcelocation);
      } else {
         return p_230496_4_ ? RenderType.m_110491_(resourcelocation) : null;
      }
   }

   protected void scale(EntityEndergrade entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(1.2F, 1.2F, 1.2F);
   }

   public ResourceLocation getTextureLocation(EntityEndergrade entity) {
      return TEXTURE;
   }
}
