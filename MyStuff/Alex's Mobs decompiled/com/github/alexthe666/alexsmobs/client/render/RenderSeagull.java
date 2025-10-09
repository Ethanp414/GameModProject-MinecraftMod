package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSeagull;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
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

public class RenderSeagull extends MobRenderer<EntitySeagull, ModelSeagull> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/seagull.png");
   private static final ResourceLocation TEXTURE_WINGULL = new ResourceLocation("alexsmobs:textures/entity/seagull_wingull.png");

   public RenderSeagull(Context renderManagerIn) {
      super(renderManagerIn, new ModelSeagull(), 0.2F);
      this.m_115326_(new RenderSeagull.LayerHeldItem(this));
   }

   protected void scale(EntitySeagull entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntitySeagull entity) {
      return entity.isWingull() ? TEXTURE_WINGULL : TEXTURE;
   }

   static class LayerHeldItem extends RenderLayer<EntitySeagull, ModelSeagull> {
      public LayerHeldItem(RenderSeagull render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntitySeagull entitylivingbaseIn,
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
         matrixStackIn.m_252880_(0.0F, -0.24F, -0.25F);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-2.5F));
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelSeagull)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelSeagull)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelSeagull)this.m_117386_()).head.translateAndRotate(matrixStack);
      }
   }
}
