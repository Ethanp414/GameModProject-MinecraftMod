package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RenderCrocodile extends MobRenderer<EntityCrocodile, ModelCrocodile> {
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/crocodile_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/crocodile_1.png");
   private static final ResourceLocation TEXTURE_CROWN = new ResourceLocation("alexsmobs:textures/entity/crocodile_crown.png");

   public RenderCrocodile(Context renderManagerIn) {
      super(renderManagerIn, new ModelCrocodile(), 0.8F);
      this.m_115326_(new RenderCrocodile.CrownLayer(this));
   }

   protected void scale(EntityCrocodile entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.9F, 0.9F, 0.9F);
   }

   public ResourceLocation getTextureLocation(EntityCrocodile entity) {
      return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
   }

   static class CrownLayer extends RenderLayer<EntityCrocodile, ModelCrocodile> {
      public CrownLayer(RenderCrocodile p_i50928_1_) {
         super(p_i50928_1_);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityCrocodile entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entitylivingbaseIn.isCrowned()) {
            VertexConsumer shoeBuffer = bufferIn.m_6299_(AMRenderTypes.m_110458_(RenderCrocodile.TEXTURE_CROWN));
            matrixStackIn.m_85836_();
            ((ModelCrocodile)this.m_117386_()).m_7695_(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.m_85849_();
         }
      }
   }
}
