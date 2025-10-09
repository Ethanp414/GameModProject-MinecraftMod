package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.model.layered.ModelSombrero;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class LayerCockroachMaracas extends RenderLayer<EntityCockroach, ModelCockroach> {
   private final ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.MARACA.get());
   private final ModelSombrero sombrero;
   private static final ResourceLocation SOMBRERO_TEX = new ResourceLocation("alexsmobs:textures/armor/sombrero.png");

   public LayerCockroachMaracas(RenderCockroach render, Context renderManagerIn) {
      super(render);
      this.sombrero = new ModelSombrero(renderManagerIn.m_174023_(AMModelLayers.SOMBRERO));
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityCockroach entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entitylivingbaseIn.hasMaracas()) {
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         matrixStackIn.m_85836_();
         if (entitylivingbaseIn.m_6162_()) {
            matrixStackIn.m_85841_(0.65F, 0.65F, 0.65F);
            matrixStackIn.m_85837_(0.0, 0.815, 0.125);
         }

         matrixStackIn.m_85836_();
         this.translateToHand(0, matrixStackIn);
         matrixStackIn.m_252880_(-0.25F, 0.0F, 0.0F);
         matrixStackIn.m_85841_(1.4F, 1.4F, 1.4F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(60.0F));
         renderer.m_269530_(entitylivingbaseIn, this.stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85836_();
         this.translateToHand(1, matrixStackIn);
         matrixStackIn.m_252880_(0.25F, 0.0F, 0.0F);
         matrixStackIn.m_85841_(1.4F, 1.4F, 1.4F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(-120.0F));
         renderer.m_269530_(entitylivingbaseIn, this.stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85836_();
         this.translateToHand(2, matrixStackIn);
         matrixStackIn.m_252880_(-0.35F, 0.0F, 0.0F);
         matrixStackIn.m_85841_(1.4F, 1.4F, 1.4F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(60.0F));
         renderer.m_269530_(entitylivingbaseIn, this.stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85836_();
         this.translateToHand(3, matrixStackIn);
         matrixStackIn.m_252880_(0.35F, 0.0F, 0.0F);
         matrixStackIn.m_85841_(1.4F, 1.4F, 1.4F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(-120.0F));
         renderer.m_269530_(entitylivingbaseIn, this.stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         if (!entitylivingbaseIn.isHeadless()) {
            matrixStackIn.m_85836_();
            this.translateToHand(4, matrixStackIn);
            matrixStackIn.m_252880_(0.0F, -0.4F, -0.01F);
            matrixStackIn.m_252880_(0.0F, entitylivingbaseIn.danceProgress * 0.045F, entitylivingbaseIn.danceProgress * -0.09F);
            matrixStackIn.m_85841_(0.8F, 0.8F, 0.8F);
            matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(60.0F * entitylivingbaseIn.danceProgress * 0.2F));
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110458_(SOMBRERO_TEX));
            this.sombrero
               .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.m_85849_();
         }

         matrixStackIn.m_85849_();
      }
   }

   protected void translateToHand(int hand, PoseStack matrixStack) {
      ((ModelCockroach)this.m_117386_()).root.translateAndRotate(matrixStack);
      ((ModelCockroach)this.m_117386_()).abdomen.translateAndRotate(matrixStack);
      if (hand == 0) {
         ((ModelCockroach)this.m_117386_()).right_leg_front.translateAndRotate(matrixStack);
      } else if (hand == 1) {
         ((ModelCockroach)this.m_117386_()).left_leg_front.translateAndRotate(matrixStack);
      } else if (hand == 2) {
         ((ModelCockroach)this.m_117386_()).right_leg_mid.translateAndRotate(matrixStack);
      } else if (hand == 3) {
         ((ModelCockroach)this.m_117386_()).left_leg_mid.translateAndRotate(matrixStack);
      } else {
         ((ModelCockroach)this.m_117386_()).neck.translateAndRotate(matrixStack);
         ((ModelCockroach)this.m_117386_()).head.translateAndRotate(matrixStack);
      }
   }
}
