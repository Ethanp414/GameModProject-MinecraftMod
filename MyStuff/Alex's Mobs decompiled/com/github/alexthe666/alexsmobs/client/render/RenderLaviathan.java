package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathanPart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderLaviathan extends MobRenderer<EntityLaviathan, ModelLaviathan> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
   private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/laviathan_glow.png");
   private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");
   private static final ResourceLocation TEXTURE_GEAR = new ResourceLocation("alexsmobs:textures/entity/laviathan_gear.png");
   private static final ResourceLocation TEXTURE_HELMET = new ResourceLocation("alexsmobs:textures/entity/laviathan_helmet.png");
   private static final float REINS_COLOR_R = 0.38431373F;
   private static final float REINS_COLOR_G = 0.3019608F;
   private static final float REINS_COLOR_B = 0.20392157F;
   private static final float REINS_COLOR_R2 = 0.22745098F;
   private static final float REINS_COLOR_G2 = 0.15686275F;
   private static final float REINS_COLOR_B2 = 0.13333334F;
   public static boolean renderWithoutShaking = false;

   public RenderLaviathan(Context renderManagerIn) {
      super(renderManagerIn, new ModelLaviathan(), 4.0F);
      this.m_115326_(new RenderLaviathan.LayerOverlays(this));
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
      float f2 = 0.38431373F;
      float f3 = 0.3019608F;
      float f4 = 0.20392157F;
      if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
         f2 = 0.22745098F;
         f3 = 0.15686275F;
         f4 = 0.13333334F;
      }

      float f5 = p_174310_ * f;
      float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
      float f7 = p_174312_ * f;
      p_174308_.m_252986_(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).m_85950_(f2, f3, f4, 1.0F).m_85969_(k).m_5752_();
      p_174308_.m_252986_(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).m_85950_(f2, f3, f4, 1.0F).m_85969_(k).m_5752_();
   }

   public boolean shouldRender(EntityLaviathan livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
      if (super.m_5523_(livingEntityIn, camera, camX, camY, camZ)) {
         return true;
      } else {
         for (EntityLaviathanPart part : livingEntityIn.allParts) {
            if (camera.m_113029_(part.m_20191_())) {
               return true;
            }
         }

         return false;
      }
   }

   public void render(EntityLaviathan mob, float p_115456_, float partialTick, PoseStack ms, MultiBufferSource p_115459_, int p_115460_) {
      super.m_7392_(mob, p_115456_, partialTick, ms, p_115459_, p_115460_);
      Entity entity = mob.m_6688_();
      if (entity != null) {
         double d0 = Mth.m_14139_(partialTick, mob.f_19790_, mob.m_20185_());
         double d1 = Mth.m_14139_(partialTick, mob.f_19791_, mob.m_20186_());
         double d2 = Mth.m_14139_(partialTick, mob.f_19792_, mob.m_20189_());
         ms.m_85836_();
         ms.m_85837_(-d0, -d1, -d2);
         this.renderRein(mob, partialTick, ms, p_115459_, entity, true);
         this.renderRein(mob, partialTick, ms, p_115459_, entity, false);
         ms.m_85849_();
      }
   }

   protected void scale(EntityLaviathan entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   protected boolean isShaking(EntityLaviathan entity) {
      return entity.m_20071_() && !entity.isObsidian() && !renderWithoutShaking;
   }

   public ResourceLocation getTextureLocation(EntityLaviathan entity) {
      return entity.isObsidian() ? TEXTURE_OBSIDIAN : TEXTURE;
   }

   private float getHeadShakeForReins(EntityLaviathan mob, float partialTick) {
      float hh1 = mob.prevHeadHeight;
      float hh2 = mob.getHeadHeight();
      float rawHeadHeight = (hh1 + (hh2 - hh1) * partialTick) / 3.0F;
      float clampedNeckRot = Mth.m_14036_(-rawHeadHeight, -1.0F, 1.0F);
      float headStillProgress = 1.0F - Math.abs(clampedNeckRot);
      float swim = Mth.m_14179_(partialTick, mob.prevSwimProgress, mob.swimProgress);
      float limbSwingAmount = mob.f_267362_.m_267711_(partialTick);
      float swing = mob.f_267362_.m_267756_() + partialTick;
      float swingAmount = limbSwingAmount * swim * 0.2F * headStillProgress;
      float swimSpeed = mob.swimProgress >= 5.0F ? 0.3F : 0.9F;
      float swimDegree = 0.5F + swim * 0.05F;
      float boxOffset = (float) (-Math.PI * 7.0 / 2.0);
      float moveScale = 1.0F;
      return 1.3F * Mth.m_14089_(swing * swimSpeed * moveScale + boxOffset * 2.0F) * swingAmount * swimDegree * moveScale;
   }

   private float getHeadBobForReins(EntityLaviathan mob, float partialTick) {
      float swing = mob.f_19797_ + partialTick;
      float swingAmount = 1.0F;
      float idleSpeed = 0.04F;
      float idleDegree = 0.3F;
      float boxOffset = (float) (Math.PI * 3.0 / 2.0);
      float moveScale = 1.0F;
      return 0.8F * Mth.m_14089_(swing * idleSpeed * moveScale + boxOffset * 2.0F) * swingAmount * idleDegree * moveScale;
   }

   private <E extends Entity> void renderRein(EntityLaviathan mob, float partialTick, PoseStack p_115464_, MultiBufferSource p_115465_, E rider, boolean left) {
      p_115464_.m_85836_();
      Entity head = mob.headPart;
      if (head != null) {
         float limbSwingAmount = mob.f_267362_.m_267711_(partialTick);
         float shake = this.getHeadShakeForReins(mob, partialTick);
         float headYaw = Math.abs(mob.getHeadYaw(partialTick)) / 50.0F;
         float headPitch = 1.0F - Math.abs((mob.prevHeadHeight + (mob.getHeadHeight() - mob.prevHeadHeight) * partialTick) / 3.0F);
         float yawAdd = (1.0F - headYaw) * 0.4F * (1.0F - limbSwingAmount * 0.7F) - headPitch * 0.2F;
         Vec3 vec3 = rider instanceof LivingEntity ? this.getReinPosition((LivingEntity)rider, partialTick, left, shake) : rider.m_7398_(partialTick);
         double d0 = Mth.m_14179_(partialTick, mob.f_20883_, mob.f_20884_) * (float) (Math.PI / 180.0) + (Math.PI / 2);
         Vec3 vec31 = new Vec3((left ? -0.05F - yawAdd : 0.05F + yawAdd) + shake, 0.45F - headYaw * 0.2F + this.getHeadBobForReins(mob, partialTick), 0.1F);
         double d1 = Math.cos(d0) * vec31.f_82481_ + Math.sin(d0) * vec31.f_82479_;
         double d2 = Math.sin(d0) * vec31.f_82481_ - Math.cos(d0) * vec31.f_82479_;
         double d3 = Mth.m_14139_(partialTick, head.f_19854_, head.m_20185_()) + d1;
         double d4 = Mth.m_14139_(partialTick, head.f_19855_, head.m_20186_()) + vec31.f_82480_;
         double d5 = Mth.m_14139_(partialTick, head.f_19856_, head.m_20189_()) + d2;
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
         BlockPos blockpos1 = AMBlockPos.fromVec3(rider.m_20299_(partialTick));
         int i = this.m_6086_(mob, blockpos);
         int j = mob.m_9236_().m_45517_(LightLayer.BLOCK, blockpos1);
         int k = mob.m_9236_().m_45517_(LightLayer.SKY, blockpos);
         int l = mob.m_9236_().m_45517_(LightLayer.SKY, blockpos1);
         float width = 0.05F;

         for (int i1 = 0; i1 <= 24; i1++) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, i1, false);
         }

         for (int j1 = 24; j1 >= 0; j1--) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, width, width, f5, f6, j1, true);
         }

         p_115464_.m_85849_();
      }
   }

   private Vec3 getReinPosition(LivingEntity entity, float p_36374_, boolean left, float shake) {
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

   static class LayerOverlays extends RenderLayer<EntityLaviathan, ModelLaviathan> {
      public LayerOverlays(RenderLaviathan render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityLaviathan laviathan,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (!laviathan.isObsidian()) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110488_(RenderLaviathan.TEXTURE_GLOW));
            ((ModelLaviathan)this.m_117386_()).m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (laviathan.hasBodyGear()) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110458_(RenderLaviathan.TEXTURE_GEAR));
            ((ModelLaviathan)this.m_117386_()).m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (laviathan.hasHeadGear()) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110458_(RenderLaviathan.TEXTURE_HELMET));
            ((ModelLaviathan)this.m_117386_()).m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
