package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderMimicOctopus extends MobRenderer<EntityMimicOctopus, ModelMimicOctopus> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus.png");
   private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_overlay.png");
   private static final ResourceLocation TEXTURE_CREEPER = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_creeper.png");
   private static final ResourceLocation TEXTURE_GUARDIAN = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_guardian.png");
   private static final ResourceLocation TEXTURE_PUFFERFISH = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_pufferfish.png");
   private static final ResourceLocation TEXTURE_MIMICUBE = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_mimicube.png");
   private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/mimic_octopus_eyes.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
   private static final RenderType BEAM_RENDER_TYPE = RenderType.m_110458_(GUARDIAN_BEAM_TEXTURE);

   public RenderMimicOctopus(Context renderManagerIn) {
      super(renderManagerIn, new ModelMimicOctopus(), 0.4F);
      this.m_115326_(new RenderMimicOctopus.OverlayLayer(this));
   }

   private static void vertex(
      VertexConsumer p_229108_0_,
      Matrix4f p_229108_1_,
      Matrix3f p_229108_2_,
      float p_229108_3_,
      float p_229108_4_,
      float p_229108_5_,
      int p_229108_6_,
      int p_229108_7_,
      int p_229108_8_,
      float p_229108_9_,
      float p_229108_10_
   ) {
      p_229108_0_.m_252986_(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_)
         .m_6122_(p_229108_6_, p_229108_7_, p_229108_8_, 255)
         .m_7421_(p_229108_9_, p_229108_10_)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(p_229108_2_, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }

   public void render(EntityMimicOctopus entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      LivingEntity livingentity = entityIn.getGuardianLaser();
      if (livingentity != null) {
         float f = entityIn.getLaserAttackAnimationScale(partialTicks);
         float f1 = (float)entityIn.m_9236_().m_46467_() + partialTicks;
         float f2 = f1 * 0.5F % 1.0F;
         float f3 = entityIn.m_20192_();
         matrixStackIn.m_85836_();
         matrixStackIn.m_85837_(0.0, f3, 0.0);
         Vec3 vector3d = this.getPosition(livingentity, livingentity.m_20206_() * 0.5, partialTicks);
         Vec3 vector3d1 = this.getPosition(entityIn, f3, partialTicks);
         Vec3 vector3d2 = vector3d.m_82546_(vector3d1);
         float f4 = (float)(vector3d2.m_82553_() + 1.0);
         vector3d2 = vector3d2.m_82541_();
         float f5 = (float)Math.acos(vector3d2.f_82480_);
         float f6 = (float)Math.atan2(vector3d2.f_82481_, vector3d2.f_82479_);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(((float) (Math.PI / 2) - f6) * (180.0F / (float)Math.PI)));
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(f5 * (180.0F / (float)Math.PI)));
         int i = 1;
         float f7 = f1 * 0.05F * -1.5F;
         float f8 = f * f;
         int j = 64 + (int)(f8 * 191.0F);
         int k = 32 + (int)(f8 * 191.0F);
         int l = 128 - (int)(f8 * 64.0F);
         float f9 = 0.2F;
         float f10 = 0.282F;
         float f11 = Mth.m_14089_(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f12 = Mth.m_14031_(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f13 = Mth.m_14089_(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f14 = Mth.m_14031_(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f15 = Mth.m_14089_(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f16 = Mth.m_14031_(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f17 = Mth.m_14089_(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f18 = Mth.m_14031_(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f19 = Mth.m_14089_(f7 + (float) Math.PI) * 0.2F;
         float f20 = Mth.m_14031_(f7 + (float) Math.PI) * 0.2F;
         float f21 = Mth.m_14089_(f7 + 0.0F) * 0.2F;
         float f22 = Mth.m_14031_(f7 + 0.0F) * 0.2F;
         float f23 = Mth.m_14089_(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f24 = Mth.m_14031_(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f25 = Mth.m_14089_(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f26 = Mth.m_14031_(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f27 = 0.0F;
         float f28 = 0.4999F;
         float f29 = -1.0F + f2;
         float f30 = f4 * 2.5F + f29;
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(BEAM_RENDER_TYPE);
         Pose matrixstack$entry = matrixStackIn.m_85850_();
         Matrix4f matrix4f = matrixstack$entry.m_252922_();
         Matrix3f matrix3f = matrixstack$entry.m_252943_();
         vertex(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
         float f31 = 0.0F;
         if (entityIn.f_19797_ % 2 == 0) {
            f31 = 0.5F;
         }

         vertex(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
         vertex(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
         vertex(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
         vertex(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
         matrixStackIn.m_85849_();
      }

      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   protected void scale(EntityMimicOctopus octo, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_252880_(0.0F, -0.02F, 0.0F);
      matrixStackIn.m_85841_(0.9F * octo.m_6134_(), 0.9F * octo.m_6134_(), 0.9F * octo.m_6134_());
   }

   public boolean shouldRender(EntityMimicOctopus livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
      if (super.m_5523_(livingEntityIn, camera, camX, camY, camZ)) {
         return true;
      } else {
         if (livingEntityIn.hasGuardianLaser()) {
            LivingEntity livingentity = livingEntityIn.getGuardianLaser();
            if (livingentity != null) {
               Vec3 vector3d = this.getPosition(livingentity, livingentity.m_20206_() * 0.5, 1.0F);
               Vec3 vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.m_20192_(), 1.0F);
               return camera.m_113029_(
                  new AABB(vector3d1.f_82479_, vector3d1.f_82480_, vector3d1.f_82481_, vector3d.f_82479_, vector3d.f_82480_, vector3d.f_82481_)
               );
            }
         }

         return false;
      }
   }

   private Vec3 getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
      double d0 = Mth.m_14139_(p_177110_4_, entityLivingBaseIn.f_19790_, entityLivingBaseIn.m_20185_());
      double d1 = Mth.m_14139_(p_177110_4_, entityLivingBaseIn.f_19791_, entityLivingBaseIn.m_20186_()) + p_177110_2_;
      double d2 = Mth.m_14139_(p_177110_4_, entityLivingBaseIn.f_19792_, entityLivingBaseIn.m_20189_());
      return new Vec3(d0, d1, d2);
   }

   public ResourceLocation getTextureLocation(EntityMimicOctopus entity) {
      return TEXTURE;
   }

   static class OverlayLayer extends RenderLayer<EntityMimicOctopus, ModelMimicOctopus> {
      public OverlayLayer(RenderMimicOctopus render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource buffer,
         int packedLightIn,
         EntityMimicOctopus entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         float transProgress = entitylivingbaseIn.prevTransProgress + (entitylivingbaseIn.transProgress - entitylivingbaseIn.prevTransProgress) * partialTicks;
         float colorProgress = (
               entitylivingbaseIn.prevColorShiftProgress + (entitylivingbaseIn.colorShiftProgress - entitylivingbaseIn.prevColorShiftProgress) * partialTicks
            )
            * 0.2F;
         float r = 1.0F;
         float g = 1.0F;
         float b = 1.0F;
         float a = 1.0F;
         float startR = 1.0F;
         float startG = 1.0F;
         float startB = 1.0F;
         float startA = 1.0F;
         float finR = 1.0F;
         float finG = 1.0F;
         float finB = 1.0F;
         float finA = 1.0F;
         if (entitylivingbaseIn.getPrevMimicState() == EntityMimicOctopus.MimicState.OVERLAY) {
            if (entitylivingbaseIn.getPrevMimickedBlock() != null) {
               int j = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getPrevMimickedBlock());
               startR = (j >> 16 & 0xFF) / 255.0F;
               startG = (j >> 8 & 0xFF) / 255.0F;
               startB = (j & 0xFF) / 255.0F;
            } else {
               startA = 0.0F;
            }
         }

         if (entitylivingbaseIn.getMimicState() == EntityMimicOctopus.MimicState.OVERLAY) {
            if (entitylivingbaseIn.getMimickedBlock() != null) {
               int i = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getMimickedBlock());
               finR = (i >> 16 & 0xFF) / 255.0F;
               finG = (i >> 8 & 0xFF) / 255.0F;
               finB = (i & 0xFF) / 255.0F;
            } else {
               finA = 0.0F;
            }

            r = startR + (finR - startR) * colorProgress;
            g = startG + (finG - startG) * colorProgress;
            b = startB + (finB - startB) * colorProgress;
            a = startA + (finA - startA) * colorProgress;
         }

         if (a == 1.0F) {
            a *= 0.9F + 0.1F * (float)Math.sin(entitylivingbaseIn.f_19797_ * 0.1F);
         }

         if (entitylivingbaseIn.getPrevMimicState() != null) {
            float alphaPrev = 1.0F - transProgress * 0.2F;
            VertexConsumer prev = buffer.m_6299_(AMRenderTypes.m_110473_(this.getFor(entitylivingbaseIn.getPrevMimicState())));
            if (entitylivingbaseIn.getPrevMimicState() == entitylivingbaseIn.getMimicState()) {
               alphaPrev *= a;
            }

            ((ModelMimicOctopus)this.m_117386_())
               .m_7695_(matrixStackIn, prev, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), r, g, b, alphaPrev);
         }

         float alphaCurrent = transProgress * 0.2F;
         VertexConsumer current = buffer.m_6299_(AMRenderTypes.m_110473_(this.getFor(entitylivingbaseIn.getMimicState())));
         ((ModelMimicOctopus)this.m_117386_())
            .m_7695_(matrixStackIn, current, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), r, g, b, a * alphaCurrent);
         VertexConsumer eyes = buffer.m_6299_(AMRenderTypes.m_110473_(RenderMimicOctopus.TEXTURE_EYES));
         ((ModelMimicOctopus)this.m_117386_())
            .m_7695_(matrixStackIn, eyes, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      public ResourceLocation getFor(EntityMimicOctopus.MimicState state) {
         if (state == EntityMimicOctopus.MimicState.CREEPER) {
            return RenderMimicOctopus.TEXTURE_CREEPER;
         } else if (state == EntityMimicOctopus.MimicState.GUARDIAN) {
            return RenderMimicOctopus.TEXTURE_GUARDIAN;
         } else if (state == EntityMimicOctopus.MimicState.PUFFERFISH) {
            return RenderMimicOctopus.TEXTURE_PUFFERFISH;
         } else {
            return state == EntityMimicOctopus.MimicState.MIMICUBE ? RenderMimicOctopus.TEXTURE_MIMICUBE : RenderMimicOctopus.TEXTURE_OVERLAY;
         }
      }
   }
}
