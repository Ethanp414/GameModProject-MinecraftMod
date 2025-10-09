package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFart;
import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderFart extends EntityRenderer<EntityFart> {
   private static final ResourceLocation FART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/fart.png");
   private static final ModelFart MODEL = new ModelFart();

   public RenderFart(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EntityFart entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      float f = Math.min(entityIn.f_19797_ + partialTicks, 30.0F) / 30.0F;
      float alpha = 1.0F - f;
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, 0.15F, 0.0);
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 180.0F));
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110473_(FART_TEXTURE));
      MODEL.setupAnim(entityIn, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
      MODEL.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, alpha);
      matrixStackIn.m_85849_();
      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(EntityFart entity) {
      return FART_TEXTURE;
   }
}
