package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCosmaw;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderCosmaw extends MobRenderer<EntityCosmaw, ModelCosmaw> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cosmaw.png");
   private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/cosmaw_glow.png");

   public RenderCosmaw(Context renderManagerIn) {
      super(renderManagerIn, new ModelCosmaw(), 0.9F);
      this.m_115326_(new RenderCosmaw.LayerHeldItem());
      this.m_115326_(new LayerBasicGlow(this, TEXTURE_GLOW));
   }

   protected void scale(EntityCosmaw entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_252880_(0.0F, -0.5F, 0.0F);
   }

   public ResourceLocation getTextureLocation(EntityCosmaw entity) {
      return TEXTURE;
   }

   class LayerHeldItem extends RenderLayer<EntityCosmaw, ModelCosmaw> {
      public LayerHeldItem() {
         super(RenderCosmaw.this);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityCosmaw entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ItemStack itemstack = entitylivingbaseIn.m_21205_();
         matrixStackIn.m_85836_();
         this.translateToHand(matrixStackIn);
         matrixStackIn.m_85837_(-0.0, 0.1F, -1.35F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-45.0F));
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-180.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(135.0F));
         matrixStackIn.m_85841_(2.0F, 2.0F, 2.0F);
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelCosmaw)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.m_117386_()).mouthArm1.translateAndRotate(matrixStack);
         ((ModelCosmaw)this.m_117386_()).mouthArm2.translateAndRotate(matrixStack);
      }
   }
}
