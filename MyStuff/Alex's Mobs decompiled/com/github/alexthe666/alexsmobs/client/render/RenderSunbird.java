package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSunbird;
import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderSunbird extends MobRenderer<EntitySunbird, ModelSunbird> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sunbird.png");
   private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/sunbird_glow.png");

   public RenderSunbird(Context renderManagerIn) {
      super(renderManagerIn, new ModelSunbird(), 0.5F);
      this.m_115326_(new RenderSunbird.LayerScorch(this));
   }

   private static void vertex(
      VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, float p_114095_, int p_114096_, int p_114097_
   ) {
      p_114090_.m_252986_(p_114091_, p_114094_, p_114095_, 0.0F)
         .m_6122_(255, 255, 255, 100)
         .m_7421_(p_114096_, p_114097_)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(p_114093_)
         .m_252939_(p_114092_, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }

   public void render(EntitySunbird entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
      super.m_7392_(entity, yaw, partialTicks, poseStack, buffer, light);
      float ageInTicks = entity.f_19797_ + partialTicks;
      float scale = (12.0F + (float)Math.sin(ageInTicks * 0.3F)) * entity.getScorchProgress(partialTicks);
      if (scale > 0.0F) {
         poseStack.m_85836_();
         poseStack.m_252880_(0.0F, entity.m_20206_() * 0.5F, 0.0F);
         poseStack.m_252781_(this.f_114476_.m_253208_());
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252403_.m_252977_(ageInTicks * 8.0F));
         poseStack.m_252880_(-scale * 0.5F, -scale * 0.5F, 0.0F);
         Pose posestack$pose = poseStack.m_85850_();
         Matrix4f matrix4f = posestack$pose.m_252922_();
         Matrix3f matrix3f = posestack$pose.m_252943_();
         VertexConsumer vertexconsumer = buffer.m_6299_(AMRenderTypes.getSunbirdShine());
         vertex(vertexconsumer, matrix4f, matrix3f, light, 0.0F, 0.0F, 0, 1);
         vertex(vertexconsumer, matrix4f, matrix3f, light, scale, 0.0F, 1, 1);
         vertex(vertexconsumer, matrix4f, matrix3f, light, scale, scale, 1, 0);
         vertex(vertexconsumer, matrix4f, matrix3f, light, 0.0F, scale, 0, 0);
         poseStack.m_85849_();
         poseStack.m_85849_();
      }
   }

   protected void scale(EntitySunbird entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   protected int getBlockLightLevel(EntitySunbird entityIn, BlockPos partialTicks) {
      return 15;
   }

   public ResourceLocation getTextureLocation(EntitySunbird entity) {
      return TEXTURE;
   }

   static class LayerScorch extends RenderLayer<EntitySunbird, ModelSunbird> {
      public LayerScorch(RenderSunbird p_i50928_1_) {
         super(p_i50928_1_);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntitySunbird entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         VertexConsumer scorch = bufferIn.m_6299_(AMRenderTypes.getEyesAlphaEnabled(RenderSunbird.TEXTURE_GLOW));
         float alpha = entitylivingbaseIn.getScorchProgress(partialTicks);
         ((ModelSunbird)this.m_117386_())
            .m_7695_(matrixStackIn, scorch, 240, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
      }
   }
}
