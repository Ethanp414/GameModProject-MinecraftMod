package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderCachalotEcho extends EntityRenderer<EntityCachalotEcho> {
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_1.png");
   private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_2.png");
   private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_3.png");
   private static final ResourceLocation GREEN_TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_0_green.png");
   private static final ResourceLocation GREEN_TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_1_green.png");
   private static final ResourceLocation GREEN_TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_2_green.png");
   private static final ResourceLocation GREEN_TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/cachalot/whale_echo_3_green.png");

   public RenderCachalotEcho(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EntityCachalotEcho entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, 0.25, 0.0);
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 90.0F));
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
      int arcs = Mth.m_14045_(Mth.m_14143_(entityIn.f_19797_ / 5.0F), 1, 4);
      matrixStackIn.m_85837_(0.0, 0.0, 0.4);

      for (int i = 0; i < arcs; i++) {
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.0F, 0.0F, -0.5F * i);
         this.renderArc(matrixStackIn, bufferIn, (i + 1) * 5, entityIn.isFasterAnimation(), entityIn.isGreen());
         matrixStackIn.m_85849_();
      }

      matrixStackIn.m_85849_();
      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private void renderArc(PoseStack matrixStackIn, MultiBufferSource bufferIn, int age, boolean fast, boolean green) {
      matrixStackIn.m_85836_();
      ResourceLocation res;
      if (fast) {
         res = this.getEntityTextureFaster(age, green);
      } else {
         res = this.getEntityTexture(age);
      }

      VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110458_(res));
      Pose lvt_19_1_ = matrixStackIn.m_85850_();
      Matrix4f lvt_20_1_ = lvt_19_1_.m_252922_();
      Matrix3f lvt_21_1_ = lvt_19_1_.m_252943_();
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0.0F, 0.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0.0F, 1.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1.0F, 1.0F, 1, 0, 1, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1.0F, 0.0F, 1, 0, 1, 240);
      matrixStackIn.m_85849_();
   }

   public ResourceLocation getTextureLocation(EntityCachalotEcho entity) {
      return TEXTURE_0;
   }

   public void drawVertex(
      Matrix4f p_229039_1_,
      Matrix3f p_229039_2_,
      VertexConsumer p_229039_3_,
      int p_229039_4_,
      int p_229039_5_,
      int p_229039_6_,
      float p_229039_7_,
      float p_229039_8_,
      int p_229039_9_,
      int p_229039_10_,
      int p_229039_11_,
      int p_229039_12_
   ) {
      p_229039_3_.m_252986_(p_229039_1_, p_229039_4_, p_229039_5_, p_229039_6_)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(p_229039_7_, p_229039_8_)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(p_229039_12_)
         .m_252939_(p_229039_2_, p_229039_9_, p_229039_11_, p_229039_10_)
         .m_5752_();
   }

   public ResourceLocation getEntityTexture(int age) {
      if (age < 5) {
         return TEXTURE_0;
      } else if (age < 10) {
         return TEXTURE_1;
      } else {
         return age < 15 ? TEXTURE_2 : TEXTURE_3;
      }
   }

   public ResourceLocation getEntityTextureFaster(int age, boolean green) {
      if (age < 3) {
         return green ? GREEN_TEXTURE_0 : TEXTURE_0;
      } else if (age < 6) {
         return green ? GREEN_TEXTURE_1 : TEXTURE_1;
      } else if (age < 9) {
         return green ? GREEN_TEXTURE_2 : TEXTURE_2;
      } else {
         return green ? GREEN_TEXTURE_3 : TEXTURE_3;
      }
   }
}
