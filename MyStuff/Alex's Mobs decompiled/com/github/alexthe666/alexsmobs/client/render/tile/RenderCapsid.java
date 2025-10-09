package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class RenderCapsid<T extends TileEntityCapsid> implements BlockEntityRenderer<T> {
   private final Random random = new Random();

   public RenderCapsid(Context rendererDispatcherIn) {
   }

   protected int getModelCount(ItemStack stack) {
      int i = 1;
      if (stack.m_41613_() > 48) {
         i = 5;
      } else if (stack.m_41613_() > 32) {
         i = 4;
      } else if (stack.m_41613_() > 16) {
         i = 3;
      } else if (stack.m_41613_() > 1) {
         i = 2;
      }

      return i;
   }

   public void render(T entity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      ItemStack stack = entity.m_8020_(0);
      if (!stack.m_41619_()) {
         int i = Item.m_41393_(stack.m_41720_()) + stack.m_41773_();
         this.random.setSeed(i);
         float floatProgress = entity.prevFloatUpProgress + (entity.floatUpProgress - entity.prevFloatUpProgress) * partialTicks;
         float yaw = entity.prevYawSwitchProgress + (entity.yawSwitchProgress - entity.prevYawSwitchProgress) * partialTicks;
         int j = this.getModelCount(stack);
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.5F, 0.5F + floatProgress, 0.5F);
         matrixStackIn.m_252781_(new Quaternionf().rotateY(Maths.rad(entity.getBlockAngle() + yaw)));
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.0F, -0.1F, 0.0F);
         if (entity.vibratingThisTick && entity.m_58904_() != null) {
            float vibrate = 0.05F;
            matrixStackIn.m_252880_(
               (entity.m_58904_().f_46441_.m_188501_() - 0.5F) * vibrate,
               (entity.m_58904_().f_46441_.m_188501_() - 0.5F) * vibrate,
               (entity.m_58904_().f_46441_.m_188501_() - 0.5F) * vibrate
            );
         }

         matrixStackIn.m_85841_(1.3F, 1.3F, 1.3F);
         BakedModel ibakedmodel = Minecraft.m_91087_().m_91291_().m_174264_(stack, entity.m_58904_(), (LivingEntity)null, 0);
         boolean flag = ibakedmodel.m_7539_();
         if (!flag) {
            float f7 = -0.0F * (j - 1) * 0.5F;
            float f8 = -0.0F * (j - 1) * 0.5F;
            float f9 = -0.09375F * (j - 1) * 0.5F;
            matrixStackIn.m_85837_(f7, f8, f9);
         }

         for (int k = 0; k < j; k++) {
            matrixStackIn.m_85836_();
            if (k > 0) {
               if (flag) {
                  float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  matrixStackIn.m_252880_(f11, f13, f10);
               } else {
                  float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                  float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                  matrixStackIn.m_85837_(f12, f14, 0.0);
               }
            }

            Minecraft.m_91087_()
               .m_91291_()
               .m_115143_(stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.f_118083_, ibakedmodel);
            matrixStackIn.m_85849_();
            if (!flag) {
               matrixStackIn.m_85837_(0.0, 0.0, 0.09375);
            }
         }

         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      }
   }
}
