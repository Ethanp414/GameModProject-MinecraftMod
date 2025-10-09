package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderBunfungus extends MobRenderer<EntityBunfungus, ModelBunfungus> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bunfungus.png");
   private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/bunfungus_sleeping.png");

   public RenderBunfungus(Context renderManagerIn) {
      super(renderManagerIn, new ModelBunfungus(), 0.6F);
      this.m_115326_(new RenderBunfungus.LayerHeldItem(this));
   }

   protected void scale(EntityBunfungus rabbit, PoseStack matrixStackIn, float partialTickTime) {
      float f = rabbit.prevTransformTime + (rabbit.transformsIn() - rabbit.prevTransformTime) * partialTickTime;
      float f1 = (50.0F - f) / 50.0F;
      float f2 = f1 * 0.7F + 0.3F;
      matrixStackIn.m_85841_(f2, f2, f2);
   }

   public ResourceLocation getTextureLocation(EntityBunfungus entity) {
      return entity.m_5803_() ? TEXTURE_SLEEPING : TEXTURE;
   }

   static class LayerHeldItem extends RenderLayer<EntityBunfungus, ModelBunfungus> {
      public LayerHeldItem(RenderBunfungus render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityBunfungus entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ItemStack itemstack = entitylivingbaseIn.m_6844_(EquipmentSlot.MAINHAND);
         matrixStackIn.m_85836_();
         if (entitylivingbaseIn.m_6162_()) {
            matrixStackIn.m_85841_(0.5F, 0.5F, 0.5F);
            matrixStackIn.m_85837_(0.0, 1.5, 0.0);
         }

         matrixStackIn.m_85836_();
         this.translateToHand(matrixStackIn);
         matrixStackIn.m_252880_(0.3F, 0.45F, -0.15F);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         matrixStackIn.m_85841_(1.15F, 1.15F, 1.15F);
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelBunfungus)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelBunfungus)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelBunfungus)this.m_117386_()).right_arm.translateAndRotate(matrixStack);
      }
   }
}
