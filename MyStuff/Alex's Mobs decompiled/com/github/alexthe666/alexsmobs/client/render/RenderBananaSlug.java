package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderBananaSlug extends MobRenderer<EntityBananaSlug, ModelBananaSlug> {
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_1.png");
   private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_2.png");
   private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_3.png");
   private static final ResourceLocation TEXTURE_SLIME = new ResourceLocation("alexsmobs:textures/entity/banana_slug/banana_slug_slime.png");

   public RenderBananaSlug(Context renderManagerIn) {
      super(renderManagerIn, new ModelBananaSlug(), 0.2F);
      this.m_115326_(new RenderBananaSlug.LayerSlime());
   }

   protected void scale(EntityBananaSlug entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.9F, 0.9F, 0.9F);
   }

   private Direction rotate(Direction attachmentFacing) {
      return attachmentFacing.m_122434_() == Axis.Y ? Direction.UP : attachmentFacing;
   }

   private void rotateForAngle(PoseStack matrixStackIn, Direction rotate, float f) {
      if (rotate.m_122434_() != Axis.Y) {
         matrixStackIn.m_252781_(com.mojang.math.Axis.f_252529_.m_252977_(90.0F * f));
      }

      switch (rotate) {
         case DOWN:
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(180.0F * f));
         case UP:
         case SOUTH:
         default:
            break;
         case NORTH:
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(180.0F * f));
            break;
         case WEST:
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(90.0F * f));
            break;
         case EAST:
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(-90.0F * f));
      }
   }

   protected void setupRotations(EntityBananaSlug entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.m_20159_()) {
         super.m_7523_(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      } else {
         if (this.m_5936_(entityLiving)) {
            rotationYaw += (float)(Math.cos(entityLiving.f_19797_ * 3.25) * Math.PI * 0.4F);
         }

         float trans = entityLiving.m_6162_() ? 0.2F : 0.4F;
         Pose pose = entityLiving.m_20089_();
         if (pose != Pose.SLEEPING) {
            float progress = (
                  entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks
               )
               * 0.2F;
            float yawMul = 0.0F;
            if (entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().m_122434_() == Axis.Y) {
               yawMul = 1.0F;
            }

            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252436_.m_252977_(180.0F - yawMul * rotationYaw));
            matrixStackIn.m_85837_(0.0, trans, 0.0);
            float prevProg = 1.0F - progress;
            this.rotateForAngle(matrixStackIn, this.rotate(entityLiving.prevAttachDir), prevProg);
            this.rotateForAngle(matrixStackIn, this.rotate(entityLiving.getAttachmentFacing()), progress);
            if (entityLiving.getAttachmentFacing() != Direction.DOWN) {
               matrixStackIn.m_85837_(0.0, trans, 0.0);
               if (entityLiving.m_20184_().f_82480_ <= -0.001F) {
                  matrixStackIn.m_252781_(com.mojang.math.Axis.f_252392_.m_252977_(180.0F * progress));
               }

               matrixStackIn.m_85837_(0.0, -trans, 0.0);
            }

            matrixStackIn.m_85837_(0.0, -trans, 0.0);
         }

         if (entityLiving.f_20919_ > 0) {
            float f = (entityLiving.f_20919_ + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.m_14116_(f);
            if (f > 1.0F) {
               f = 1.0F;
            }

            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(f * this.m_6441_(entityLiving)));
         } else if (entityLiving.m_21209_()) {
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252529_.m_252977_(-90.0F - entityLiving.m_146909_()));
            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252436_.m_252977_((entityLiving.f_19797_ + partialTicks) * -75.0F));
         } else if (pose != Pose.SLEEPING && entityLiving.m_8077_()) {
            String s = ChatFormatting.m_126649_(entityLiving.m_7755_().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
               matrixStackIn.m_85837_(0.0, entityLiving.m_20206_() + 0.1F, 0.0);
               matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(180.0F));
            }
         }
      }
   }

   public ResourceLocation getTextureLocation(EntityBananaSlug entity) {
      return switch (entity.getVariant()) {
         case 1 -> TEXTURE_1;
         case 2 -> TEXTURE_2;
         case 3 -> TEXTURE_3;
         default -> TEXTURE_0;
      };
   }

   class LayerSlime extends RenderLayer<EntityBananaSlug, ModelBananaSlug> {
      public LayerSlime() {
         super(RenderBananaSlug.this);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityBananaSlug entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         float alpha = entitylivingbaseIn.prevTrailVisability + (entitylivingbaseIn.trailVisability - entitylivingbaseIn.prevTrailVisability) * partialTicks;
         if (alpha > 0.0F) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110473_(RenderBananaSlug.TEXTURE_SLIME));
            ((ModelBananaSlug)this.m_117386_())
               .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
         }
      }
   }
}
