package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.RenderCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LayerCapuchinItem extends RenderLayer<EntityCapuchinMonkey, ModelCapuchinMonkey> {
   public static final ResourceLocation DART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/ancient_dart.png");
   public static final ModelAncientDart DART_MODEL = new ModelAncientDart();

   public LayerCapuchinItem(RenderCapuchinMonkey render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityCapuchinMonkey entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entitylivingbaseIn.hasDart()) {
         matrixStackIn.m_85836_();
         if (entitylivingbaseIn.m_6162_()) {
            matrixStackIn.m_85841_(0.35F, 0.35F, 0.35F);
            matrixStackIn.m_85837_(0.5, 2.6, 0.15);
            this.translateToHand(false, matrixStackIn);
            matrixStackIn.m_85837_(-0.65, -0.75, -0.1F);
            matrixStackIn.m_85841_(2.8F, 2.8F, 2.8F);
         } else {
            this.translateToHand(false, matrixStackIn);
         }

         float f = 0.0F;
         if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW) {
            if (entitylivingbaseIn.getAnimationTick() < 6) {
               f = Math.min(3.0F, entitylivingbaseIn.getAnimationTick() + partialTicks) * 60.0F;
            } else {
               f = (12.0F - (entitylivingbaseIn.getAnimationTick() + partialTicks)) * 30.0F;
            }
         }

         matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
         matrixStackIn.m_85841_(1.2F, 1.2F, 1.2F);
         matrixStackIn.m_85836_();
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(f));
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(DART_MODEL.m_103119_(DART_TEXTURE));
         DART_MODEL.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      } else if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW && entitylivingbaseIn.getAnimationTick() <= 5) {
         ItemStack itemstack = new ItemStack(Items.f_42594_);
         matrixStackIn.m_85836_();
         if (entitylivingbaseIn.m_6162_()) {
            matrixStackIn.m_85841_(0.35F, 0.35F, 0.35F);
            matrixStackIn.m_85837_(0.5, 2.6, 0.15);
            this.translateToHand(false, matrixStackIn);
            matrixStackIn.m_252880_(-0.4F, 0.75F, -0.0F);
            matrixStackIn.m_85841_(2.8F, 2.8F, 2.8F);
         } else {
            this.translateToHand(false, matrixStackIn);
            matrixStackIn.m_252880_(0.125F, 0.5F, 0.1F);
         }

         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-2.5F));
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
      }
   }

   protected void translateToHand(boolean left, PoseStack matrixStack) {
      ((ModelCapuchinMonkey)this.m_117386_()).root.translateAndRotate(matrixStack);
      ((ModelCapuchinMonkey)this.m_117386_()).body.translateAndRotate(matrixStack);
      ((ModelCapuchinMonkey)this.m_117386_()).arm_right.translateAndRotate(matrixStack);
   }
}
