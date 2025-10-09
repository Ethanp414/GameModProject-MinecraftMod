package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGuster;
import com.github.alexthe666.alexsmobs.entity.EntityGust;
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

public class RenderGust extends EntityRenderer<EntityGust> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/guster.png");
   private final ModelGuster model = new ModelGuster();

   public RenderGust(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EntityGust entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, 0.5, 0.0);
      if (!entityIn.getVertical()) {
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(180.0F));
      } else {
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-180.0F));
      }

      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 90.0F));
      matrixStackIn.m_85841_(0.5F, 0.5F, 0.5F);
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110473_(TEXTURE));
      this.model.hideEyes();
      this.model.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      this.model.animateGust(entityIn, 0.0F, 0.0F, entityIn.f_19797_ + partialTicks);
      this.model.showEyes();
      matrixStackIn.m_85849_();
      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(EntityGust entity) {
      return TEXTURE;
   }
}
