package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurHead;
import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.entity.EntityMurmurHead;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderMurmurHead extends MobRenderer<EntityMurmurHead, ModelMurmurHead> {
   private static final ModelMurmurNeck NECK_MODEL = new ModelMurmurNeck();
   public static final int MAX_NECK_SEGMENTS = 128;

   public RenderMurmurHead(Context renderManagerIn) {
      super(renderManagerIn, new ModelMurmurHead(), 0.3F);
   }

   protected void scale(EntityMurmurHead entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.85F, 0.85F, 0.85F);
   }

   public boolean shouldRender(EntityMurmurHead livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
      if (super.m_5523_(livingEntityIn, camera, camX, camY, camZ)) {
         return true;
      } else if (livingEntityIn.hasNeckBottom()) {
         Vec3 vector3d = livingEntityIn.getNeckBottom(1.0F);
         Vec3 vector3d1 = livingEntityIn.getNeckTop(1.0F);
         return camera.m_113029_(new AABB(vector3d1.f_82479_, vector3d1.f_82480_, vector3d1.f_82481_, vector3d.f_82479_, vector3d.f_82480_, vector3d.f_82481_));
      } else {
         return false;
      }
   }

   protected float getFlipDegrees(EntityMurmurHead head) {
      return 0.0F;
   }

   public void render(EntityMurmurHead head, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      super.m_7392_(head, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.m_85836_();
      if (head.hasNeckBottom()) {
         float headYaw = Mth.m_14189_(partialTicks, head.f_20884_, head.f_20883_);
         Vec3 renderingAt = new Vec3(
            Mth.m_14139_(partialTicks, head.f_19854_, head.m_20185_()),
            Mth.m_14139_(partialTicks, head.f_19855_, head.m_20186_()),
            Mth.m_14139_(partialTicks, head.f_19856_, head.m_20189_())
         );
         Vec3 bottom = head.getNeckBottom(partialTicks).m_82546_(renderingAt);
         Vec3 top = head.getNeckTop(partialTicks).m_82546_(renderingAt);
         Vec3 moveDownFrom = bottom.m_82546_(top);
         Vec3 moveUpTowards = top.m_82546_(bottom);
         RenderType renderType = RenderType.m_110458_(this.getTextureLocation(head));
         int overlayCoords = m_115338_(head, this.m_6931_(head, partialTicks));
         matrixStackIn.m_85837_(moveDownFrom.f_82479_, moveDownFrom.f_82480_ - 0.5, moveDownFrom.f_82481_);
         Vec3 currentNeckButt = Vec3.f_82478_;

         for (int segmentCount = 0; segmentCount < 128 && currentNeckButt.m_82554_(moveUpTowards) > 0.2; segmentCount++) {
            double remainingDistance = Math.min(currentNeckButt.m_82554_(moveUpTowards), 1.0);
            Vec3 linearVec = moveUpTowards.m_82546_(currentNeckButt);
            Vec3 powVec = new Vec3(this.modifyVecAngle(linearVec.f_82479_), this.modifyVecAngle(linearVec.f_82480_), this.modifyVecAngle(linearVec.f_82481_));
            Vec3 smoothedVec = remainingDistance < 1.0 ? linearVec : powVec;
            Vec3 next = smoothedVec.m_82541_().m_82490_(remainingDistance).m_82549_(currentNeckButt);
            int neckLight = this.getLightColor(head, bottom.m_82549_(currentNeckButt).m_82549_(renderingAt));
            renderNeckCube(currentNeckButt, next, matrixStackIn, bufferIn.m_6299_(renderType), neckLight, overlayCoords, headYaw);
            currentNeckButt = next;
         }
      }

      matrixStackIn.m_85849_();
   }

   private double modifyVecAngle(double dimension) {
      float abs = (float)Math.abs(dimension);
      return Math.signum(dimension) * Mth.m_14008_(Math.pow(abs, 0.1), 0.01 * abs, abs);
   }

   public static void renderNeckCube(Vec3 from, Vec3 to, PoseStack poseStack, VertexConsumer buffer, int packedLightIn, int overlayCoords, float additionalYaw) {
      Vec3 sub = from.m_82546_(to);
      double d = sub.m_165924_();
      float rotY = (float)(Mth.m_14136_(sub.f_82479_, sub.f_82481_) * 180.0F / (float)Math.PI);
      float rotX = (float)(-(Mth.m_14136_(sub.f_82480_, d) * 180.0F / (float)Math.PI)) - 90.0F;
      poseStack.m_85836_();
      poseStack.m_85837_(from.f_82479_, from.f_82480_, from.f_82481_);
      NECK_MODEL.setAttributes((float)sub.m_82553_(), rotX, rotY, additionalYaw);
      NECK_MODEL.m_7695_(poseStack, buffer, packedLightIn, overlayCoords, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.m_85849_();
   }

   private int getLightColor(EntityMurmurHead head, Vec3 vec3) {
      BlockPos blockpos = AMBlockPos.fromVec3(vec3);
      if (head.m_9236_().m_46805_(blockpos)) {
         int i = LevelRenderer.m_109541_(head.m_9236_(), blockpos);
         int j = LevelRenderer.m_109541_(head.m_9236_(), blockpos.m_7494_());
         int k = i & 0xFF;
         int l = j & 0xFF;
         int i1 = i >> 16 & 0xFF;
         int j1 = j >> 16 & 0xFF;
         return Math.max(k, l) | Math.max(i1, j1) << 16;
      } else {
         return 0;
      }
   }

   public ResourceLocation getTextureLocation(EntityMurmurHead entity) {
      return entity.isAngry() ? RenderMurmurBody.TEXTURE_ANGRY : RenderMurmurBody.TEXTURE;
   }
}
