package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMurmurNeck;
import com.github.alexthe666.alexsmobs.client.model.ModelTendonClaw;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RenderTendonSegment extends EntityRenderer<EntityTendonSegment> {
   private static final ResourceLocation CLAW_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tendon_whip_claw.png");
   private static final ModelTendonClaw CLAW_MODEL = new ModelTendonClaw();

   public RenderTendonSegment(Context renderManagerIn) {
      super(renderManagerIn);
   }

   public boolean shouldRender(EntityTendonSegment entity, Frustum frustum, double x, double y, double z) {
      Entity next = entity.getFromEntity();
      return next != null && frustum.m_113029_(entity.m_20191_().m_82367_(next.m_20191_())) || super.m_5523_(entity, frustum, x, y, z);
   }

   public void render(EntityTendonSegment entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
      super.m_7392_(entity, yaw, partialTicks, poseStack, buffer, light);
      poseStack.m_85836_();
      Entity fromEntity = entity.getFromEntity();
      float x = (float)Mth.m_14139_(partialTicks, entity.f_19854_, entity.m_20185_());
      float y = (float)Mth.m_14139_(partialTicks, entity.f_19855_, entity.m_20186_());
      float z = (float)Mth.m_14139_(partialTicks, entity.f_19856_, entity.m_20189_());
      if (fromEntity != null) {
         float progress = (entity.prevProgress + (entity.getProgress() - entity.prevProgress) * partialTicks) / 3.0F;
         Vec3 distVec = this.getPositionOfPriorMob(entity, fromEntity, partialTicks).m_82492_(x, y, z);
         Vec3 to = distVec.m_82490_(1.0F - progress);
         Vec3 from = distVec;
         int segmentCount = 0;
         Vec3 currentNeckButt = distVec;
         VertexConsumer neckConsumer;
         if (entity.hasGlint()) {
            neckConsumer = AMRenderTypes.createMergedVertexConsumer(
               buffer.m_6299_(AMRenderTypes.m_110499_()), buffer.m_6299_(RenderType.m_110458_(RenderMurmurBody.TEXTURE))
            );
         } else {
            neckConsumer = buffer.m_6299_(RenderType.m_110458_(RenderMurmurBody.TEXTURE));
         }

         ModelMurmurNeck.THIN = true;

         for (double remainingDistance = to.m_82554_(distVec); segmentCount < 128 && remainingDistance > 0.0; segmentCount++) {
            remainingDistance = Math.min(from.m_82554_(to), 0.5);
            Vec3 linearVec = to.m_82546_(currentNeckButt);
            Vec3 powVec = new Vec3(this.modifyVecAngle(linearVec.f_82479_), this.modifyVecAngle(linearVec.f_82480_), this.modifyVecAngle(linearVec.f_82481_));
            Vec3 next = powVec.m_82541_().m_82490_(remainingDistance).m_82549_(currentNeckButt);
            int neckLight = this.getLightColor(entity, to.m_82549_(currentNeckButt).m_82520_(x, y, z));
            RenderMurmurHead.renderNeckCube(currentNeckButt, next, poseStack, neckConsumer, neckLight, OverlayTexture.f_118083_, 0.0F);
            currentNeckButt = next;
         }

         ModelMurmurNeck.THIN = false;
         VertexConsumer clawConsumer;
         if (entity.hasGlint()) {
            clawConsumer = AMRenderTypes.createMergedVertexConsumer(
               buffer.m_6299_(AMRenderTypes.m_110499_()), buffer.m_6299_(RenderType.m_110458_(CLAW_TEXTURE))
            );
         } else {
            clawConsumer = buffer.m_6299_(RenderType.m_110458_(CLAW_TEXTURE));
         }

         if (entity.hasClaw() || entity.isRetracting()) {
            poseStack.m_85836_();
            poseStack.m_85837_(to.f_82479_, to.f_82480_, to.f_82481_);
            float rotY = (float)(Mth.m_14136_(to.f_82479_, to.f_82481_) * 180.0F / (float)Math.PI);
            float rotX = (float)(-(Mth.m_14136_(to.f_82480_, to.m_165924_()) * 180.0F / (float)Math.PI));
            CLAW_MODEL.setAttributes(rotX, rotY, 1.0F - progress);
            CLAW_MODEL.m_7695_(poseStack, clawConsumer, this.getLightColor(entity, to.m_82520_(x, y, z)), OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.m_85849_();
         }
      }

      poseStack.m_85849_();
   }

   private Vec3 getPositionOfPriorMob(EntityTendonSegment segment, Entity mob, float partialTicks) {
      double d4 = Mth.m_14139_(partialTicks, mob.f_19854_, mob.m_20185_());
      double d5 = Mth.m_14139_(partialTicks, mob.f_19855_, mob.m_20186_());
      double d6 = Mth.m_14139_(partialTicks, mob.f_19856_, mob.m_20189_());
      float f3 = 0.0F;
      if (mob instanceof Player && segment.isCreator(mob)) {
         Player player = (Player)mob;
         float f = player.m_21324_(partialTicks);
         float f1 = Mth.m_14031_(Mth.m_14116_(f) * (float) Math.PI);
         float f2 = Mth.m_14179_(partialTicks, player.f_20884_, player.f_20883_) * (float) (Math.PI / 180.0);
         int i = player.m_5737_() == HumanoidArm.RIGHT ? 1 : -1;
         double d0 = Mth.m_14031_(f2);
         double d1 = Mth.m_14089_(f2);
         double d2 = i * 0.35;
         ItemStack itemstack = player.m_21205_();
         if (!itemstack.m_150930_((Item)AMItemRegistry.TENDON_WHIP.get())) {
            i = -i;
         }

         if ((this.f_114476_.f_114360_ == null || this.f_114476_.f_114360_.m_92176_().m_90612_()) && player == Minecraft.m_91087_().f_91074_) {
            double d7 = 960.0 / ((Integer)this.f_114476_.f_114360_.m_231837_().m_231551_()).intValue();
            Vec3 vec3 = this.f_114476_.f_114358_.m_167684_().m_167695_(i * 0.6F, -1.0F);
            vec3 = vec3.m_82490_(d7);
            vec3 = vec3.m_82524_(f1 * 0.25F);
            vec3 = vec3.m_82496_(-f1 * 0.35F);
            d4 = Mth.m_14139_(partialTicks, player.f_19854_, player.m_20185_()) + vec3.f_82479_;
            d5 = Mth.m_14139_(partialTicks, player.f_19855_, player.m_20186_()) + vec3.f_82480_;
            d6 = Mth.m_14139_(partialTicks, player.f_19856_, player.m_20189_()) + vec3.f_82481_;
            f3 = player.m_20192_() * 0.4F;
         } else {
            d4 = Mth.m_14139_(partialTicks, player.f_19854_, player.m_20185_()) - d1 * d2 - d0 * 0.2;
            d5 = player.f_19855_ + player.m_20192_() + (player.m_20186_() - player.f_19855_) * partialTicks - 1.0;
            d6 = Mth.m_14139_(partialTicks, player.f_19856_, player.m_20189_()) - d0 * d2 + d1 * 0.2;
            f3 = (player.m_6047_() ? -0.1875F : 0.0F) - player.m_20192_() * 0.3F;
         }
      }

      return new Vec3(d4, d5 + f3, d6);
   }

   private double modifyVecAngle(double dimension) {
      float abs = (float)Math.abs(dimension);
      return Math.signum(dimension) * Mth.m_14008_(Math.pow(abs, 0.1), 0.05 * abs, abs);
   }

   private int getLightColor(Entity head, Vec3 vec3) {
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

   public ResourceLocation getTextureLocation(EntityTendonSegment entity) {
      return null;
   }
}
