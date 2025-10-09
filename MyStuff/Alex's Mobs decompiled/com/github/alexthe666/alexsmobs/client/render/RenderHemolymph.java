package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityHemolymph;
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

public class RenderHemolymph extends EntityRenderer<EntityHemolymph> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/hemolymph.png");

   public RenderHemolymph(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EntityHemolymph p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
      p_225623_4_.m_85836_();
      p_225623_4_.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(p_225623_3_, p_225623_1_.f_19859_, p_225623_1_.m_146908_()) - 90.0F));
      p_225623_4_.m_252781_(Axis.f_252403_.m_252977_(Mth.m_14179_(p_225623_3_, p_225623_1_.f_19860_, p_225623_1_.m_146909_())));
      float lvt_17_1_ = 0.0F;
      if (lvt_17_1_ > 0.0F) {
         float lvt_18_1_ = -Mth.m_14031_(lvt_17_1_ * 3.0F) * lvt_17_1_;
         p_225623_4_.m_252781_(Axis.f_252403_.m_252977_(lvt_18_1_));
      }

      p_225623_4_.m_252781_(Axis.f_252529_.m_252977_(45.0F));
      p_225623_4_.m_85841_(0.05625F, 0.05625F, 0.05625F);
      p_225623_4_.m_85837_(-4.0, 0.0, 0.0);
      VertexConsumer lvt_18_2_ = p_225623_5_.m_6299_(RenderType.m_110452_(this.getTextureLocation(p_225623_1_)));
      Pose lvt_19_1_ = p_225623_4_.m_85850_();
      Matrix4f lvt_20_1_ = lvt_19_1_.m_252922_();
      Matrix3f lvt_21_1_ = lvt_19_1_.m_252943_();
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, 240);
      this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, 240);

      for (int lvt_22_1_ = 0; lvt_22_1_ < 4; lvt_22_1_++) {
         p_225623_4_.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 240);
         this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 240);
         this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 240);
         this.drawVertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 240);
      }

      p_225623_4_.m_85849_();
      super.m_7392_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
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

   public ResourceLocation getTextureLocation(EntityHemolymph entity) {
      return TEXTURE;
   }
}
