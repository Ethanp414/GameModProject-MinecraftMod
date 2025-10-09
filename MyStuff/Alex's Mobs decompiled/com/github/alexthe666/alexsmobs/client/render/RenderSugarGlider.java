package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSugarGlider;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public class RenderSugarGlider extends MobRenderer<EntitySugarGlider, ModelSugarGlider> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/sugar_glider.png");

   public RenderSugarGlider(Context renderManagerIn) {
      super(renderManagerIn, new ModelSugarGlider(), 0.35F);
   }

   private Direction rotate(Direction attachmentFacing) {
      return attachmentFacing.m_122434_() == Axis.Y ? Direction.UP : attachmentFacing;
   }

   protected void setupRotations(EntitySugarGlider entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.m_20159_()) {
         super.m_7523_(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      } else {
         if (this.m_5936_(entityLiving)) {
            rotationYaw += (float)(Math.cos(entityLiving.f_19797_ * 3.25) * Math.PI * 0.4F);
         }

         float trans = entityLiving.m_6162_() ? 0.2F : 0.4F;
         Pose pose = entityLiving.m_20089_();
         if (pose != Pose.SLEEPING) {
            float prevProg = entityLiving.prevAttachChangeProgress + (entityLiving.attachChangeProgress - entityLiving.prevAttachChangeProgress) * partialTicks;
            float yawMul = 0.0F;
            if (entityLiving.prevAttachDir == entityLiving.getAttachmentFacing() && entityLiving.getAttachmentFacing().m_122434_() == Axis.Y) {
               yawMul = 1.0F;
            }

            matrixStackIn.m_252781_(com.mojang.math.Axis.f_252436_.m_252977_(180.0F - yawMul * rotationYaw));
            if (entityLiving.getAttachmentFacing() == Direction.DOWN) {
               matrixStackIn.m_85837_(0.0, trans, 0.0);
               if (entityLiving.f_19855_ <= entityLiving.m_20186_()) {
                  matrixStackIn.m_252781_(com.mojang.math.Axis.f_252529_.m_252977_(90.0F * prevProg));
               } else {
                  matrixStackIn.m_252781_(com.mojang.math.Axis.f_252529_.m_252977_(-90.0F * prevProg));
               }

               matrixStackIn.m_85837_(0.0, -trans, 0.0);
            }

            matrixStackIn.m_85837_(0.0, trans, 0.0);
            Quaternionf current = this.rotate(entityLiving.getAttachmentFacing()).m_253075_();
            current.mul(1.0F - prevProg);
            matrixStackIn.m_252781_(current);
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
         } else if (entityLiving.m_8077_()) {
            String s = ChatFormatting.m_126649_(entityLiving.m_7755_().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
               matrixStackIn.m_85837_(0.0, entityLiving.m_20206_() + 0.1F, 0.0);
               matrixStackIn.m_252781_(com.mojang.math.Axis.f_252403_.m_252977_(180.0F));
            }
         }
      }
   }

   protected void scale(EntitySugarGlider mob, PoseStack matrixStackIn, float partialTickTime) {
      if (mob.m_20159_() && mob.m_20202_() != null && mob.m_20202_() instanceof Player) {
         Player mount = (Player)mob.m_20202_();
         EntityRenderer playerRender = Minecraft.m_91087_().m_91290_().m_114382_(mount);
         if ((Minecraft.m_91087_().f_91074_ != mount || Minecraft.m_91087_().f_91066_.m_92176_() != CameraType.FIRST_PERSON)
            && playerRender instanceof LivingEntityRenderer
            && ((LivingEntityRenderer)playerRender).m_7200_() instanceof HumanoidModel) {
            matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
            ((HumanoidModel)((LivingEntityRenderer)playerRender).m_7200_()).f_102808_.m_104299_(matrixStackIn);
            matrixStackIn.m_252880_(0.0F, -0.5F, 0.0F);
         }
      }
   }

   public ResourceLocation getTextureLocation(EntitySugarGlider entity) {
      return TEXTURE;
   }
}
