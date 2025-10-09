package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityVineLasso;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderVineLasso extends EntityRenderer<EntityVineLasso> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/vine_lasso.png");
   private static final float VINES_COLOR_R = 0.3764706F;
   private static final float VINES_COLOR_G = 0.56078434F;
   private static final float VINES_COLOR_B = 0.24313726F;
   private static final float VINES_COLOR_R2 = 0.6509804F;
   private static final float VINES_COLOR_G2 = 0.7490196F;
   private static final float VINES_COLOR_B2 = 0.38039216F;

   public RenderVineLasso(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(EntityVineLasso entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, 0.25, 0.0);
      matrixStackIn.m_252781_(Axis.f_252392_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 180.0F));
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
      matrixStackIn.m_85837_(0.0, -0.1F, 0.0);
      matrixStackIn.m_85836_();
      matrixStackIn.m_85841_(0.45F, 0.45F, 0.45F);
      this.renderCircle(matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      LivingEntity holder = Minecraft.m_91087_().f_91074_;
      if (holder != null) {
         double d0 = Mth.m_14139_(partialTicks, entityIn.f_19790_, entityIn.m_20185_());
         double d1 = Mth.m_14139_(partialTicks, entityIn.f_19791_, entityIn.m_20186_());
         double d2 = Mth.m_14139_(partialTicks, entityIn.f_19792_, entityIn.m_20189_());
         matrixStackIn.m_85836_();
         matrixStackIn.m_85837_(-d0, -d1, -d2);
         renderVine(entityIn, partialTicks, matrixStackIn, bufferIn, holder, holder.m_5737_() != HumanoidArm.LEFT, -0.4F);
         matrixStackIn.m_85849_();
      }
   }

   private void renderCircle(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110458_(TEXTURE));
      Pose lvt_19_1_ = matrixStackIn.m_85850_();
      Matrix4f lvt_20_1_ = lvt_19_1_.m_252922_();
      Matrix3f lvt_21_1_ = lvt_19_1_.m_252943_();
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0.0F, 0.0F, 1, 0, 1, packedLightIn);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0.0F, 1.0F, 1, 0, 1, packedLightIn);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1.0F, 1.0F, 1, 0, 1, packedLightIn);
      this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1.0F, 0.0F, 1, 0, 1, packedLightIn);
      matrixStackIn.m_85849_();
   }

   public static <E extends Entity> void renderVine(
      Entity mob, float partialTick, PoseStack p_115464_, MultiBufferSource p_115465_, LivingEntity player, boolean left, float zOffset
   ) {
      p_115464_.m_85836_();
      float bodyRot = mob instanceof LivingEntity ? ((LivingEntity)mob).f_20883_ : mob.m_146908_();
      float bodyRot0 = mob instanceof LivingEntity ? ((LivingEntity)mob).f_20884_ : mob.f_19859_;
      Vec3 vec3 = player.m_7398_(partialTick);
      double d0 = Mth.m_14179_(partialTick, bodyRot, bodyRot0) * (float) (Math.PI / 180.0) + (Math.PI / 2);
      Vec3 vec31 = new Vec3(left ? -0.05F : 0.05F, mob.m_20192_(), zOffset);
      double d1 = Math.cos(d0) * vec31.f_82481_ + Math.sin(d0) * vec31.f_82479_;
      double d2 = Math.sin(d0) * vec31.f_82481_ - Math.cos(d0) * vec31.f_82479_;
      double d3 = Mth.m_14139_(partialTick, mob.f_19854_, mob.m_20185_()) + d1;
      double d4 = Mth.m_14139_(partialTick, mob.f_19855_, mob.m_20186_()) + vec31.f_82480_;
      double d5 = Mth.m_14139_(partialTick, mob.f_19856_, mob.m_20189_()) + d2;
      p_115464_.m_85837_(d3, d4, d5);
      float f = (float)(vec3.f_82479_ - d3);
      float f1 = (float)(vec3.f_82480_ - d4);
      float f2 = (float)(vec3.f_82481_ - d5);
      VertexConsumer vertexconsumer = p_115465_.m_6299_(RenderType.m_110475_());
      Matrix4f matrix4f = p_115464_.m_85850_().m_252922_();
      float f4 = (float)(Mth.m_14193_(f * f + f2 * f2) * 0.025F / 2.0);
      float f5 = f2 * f4;
      float f6 = f * f4;
      BlockPos blockpos = AMBlockPos.fromVec3(mob.m_20299_(partialTick));
      BlockPos blockpos1 = AMBlockPos.fromVec3(player.m_20299_(partialTick));
      int i = getVineLightLevel(mob, blockpos);
      int j = mob.m_9236_().m_45517_(LightLayer.BLOCK, blockpos1);
      int k = mob.m_9236_().m_45517_(LightLayer.SKY, blockpos);
      int l = mob.m_9236_().m_45517_(LightLayer.SKY, blockpos1);
      float width = 0.1F;

      for (int i1 = 0; i1 <= 24; i1++) {
         addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
      }

      for (int j1 = 24; j1 >= 0; j1--) {
         addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
      }

      p_115464_.m_85849_();
   }

   protected static int getVineLightLevel(Entity p_114496_, BlockPos p_114497_) {
      return p_114496_.m_6060_() ? 15 : p_114496_.m_9236_().m_45517_(LightLayer.BLOCK, p_114497_);
   }

   private static Vec3 getVinePosition(LivingEntity entity, float p_36374_, boolean left, float shake) {
      double d0 = 0.4 * (left ? -1.0 : 1.0) - 0.0;
      float f = Mth.m_14179_(p_36374_ * 0.5F, entity.m_146909_(), entity.f_19860_) * (float) (Math.PI / 180.0);
      float f1 = Mth.m_14179_(p_36374_, entity.f_20884_, entity.f_20883_) * (float) (Math.PI / 180.0);
      if (entity.m_21255_() || entity.m_21209_()) {
         Vec3 vec3 = entity.m_20252_(p_36374_);
         Vec3 vec31 = entity.m_20184_();
         double d1 = vec31.m_165925_();
         double d2 = vec3.m_165925_();
         float f2;
         if (d1 > 0.0 && d2 > 0.0) {
            double d3 = (vec31.f_82479_ * vec3.f_82479_ + vec31.f_82481_ * vec3.f_82481_) / Math.sqrt(d1 * d2);
            double d4 = vec31.f_82479_ * vec3.f_82481_ - vec31.f_82481_ * vec3.f_82479_;
            f2 = (float)(Math.signum(d4) * Math.acos(d3));
         } else {
            f2 = 0.0F;
         }

         return entity.m_20318_(p_36374_).m_82549_(new Vec3(d0, -0.11, 0.85).m_82535_(-f2).m_82496_(-f).m_82524_(-f1));
      } else if (entity.m_6067_()) {
         return entity.m_20318_(p_36374_).m_82549_(new Vec3(d0, 0.3, -0.34).m_82496_(-f).m_82524_(-f1));
      } else {
         double d5 = entity.m_20191_().m_82376_() - 1.0;
         double d6 = entity.m_6047_() ? -0.2 : 0.07;
         return entity.m_20318_(p_36374_).m_82549_(new Vec3(d0, d5, d6).m_82524_(-f1));
      }
   }

   private static void addVertexPairAlex(
      VertexConsumer p_174308_,
      Matrix4f p_174309_,
      float p_174310_,
      float p_174311_,
      float p_174312_,
      int p_174313_,
      int p_174314_,
      int p_174315_,
      int p_174316_,
      float p_174317_,
      float p_174318_,
      float p_174319_,
      float p_174320_,
      int p_174321_,
      boolean p_174322_
   ) {
      float f = p_174321_ / 24.0F;
      int i = (int)Mth.m_14179_(f, p_174313_, p_174314_);
      int j = (int)Mth.m_14179_(f, p_174315_, p_174316_);
      int k = LightTexture.m_109885_(i, j);
      float f2 = 0.3764706F;
      float f3 = 0.56078434F;
      float f4 = 0.24313726F;
      if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
         f2 = 0.6509804F;
         f3 = 0.7490196F;
         f4 = 0.38039216F;
      }

      float f5 = p_174310_ * f;
      float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
      float f7 = p_174312_ * f;
      p_174308_.m_252986_(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).m_85950_(f2, f3, f4, 1.0F).m_85969_(k).m_5752_();
      p_174308_.m_252986_(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).m_85950_(f2, f3, f4, 1.0F).m_85969_(k).m_5752_();
   }

   public ResourceLocation getTextureLocation(EntityVineLasso entity) {
      return TEXTURE;
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
}
