package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPollenBall;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderPollenBall extends EntityRenderer<EntityPollenBall> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/pollen_ball.png");
   private static final ModelPollenBall MODEL_POLLEN_BALL = new ModelPollenBall();

   public RenderPollenBall(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityPollenBall entity) {
      return TEXTURE;
   }

   public void render(EntityPollenBall entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, -0.25, 0.0);
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 180.0F));
      matrixStackIn.m_85836_();
      matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
      matrixStackIn.m_85841_(1.0F, 1.0F, 1.0F);
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(AMRenderTypes.getFullBright(this.getTextureLocation(entityIn)));
      MODEL_POLLEN_BALL.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }
}
