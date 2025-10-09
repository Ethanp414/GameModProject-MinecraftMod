package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelToucan;
import com.github.alexthe666.alexsmobs.entity.EntityToucan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderToucan extends MobRenderer<EntityToucan, ModelToucan> {
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_1.png");
   private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_2.png");
   private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_3.png");
   private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_gold.png");
   private static final ResourceLocation TEXTURE_SAM = new ResourceLocation("alexsmobs:textures/entity/toucan/toucan_sam.png");

   public RenderToucan(Context renderManagerIn) {
      super(renderManagerIn, new ModelToucan(), 0.2F);
      this.m_115326_(new RenderToucan.LayerGlint(this));
      this.m_115326_(new RenderToucan.LayerHeldItem(this));
   }

   protected void scale(EntityToucan entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.9F, 0.9F, 0.9F);
   }

   public ResourceLocation getTextureLocation(EntityToucan entity) {
      if (entity.isSam()) {
         return TEXTURE_SAM;
      } else if (entity.isGolden()) {
         return TEXTURE_GOLDEN;
      } else {
         switch (entity.getVariant()) {
            case 1:
               return TEXTURE_1;
            case 2:
               return TEXTURE_2;
            case 3:
               return TEXTURE_3;
            default:
               return TEXTURE_0;
         }
      }
   }

   static class LayerGlint extends RenderLayer<EntityToucan, ModelToucan> {
      public LayerGlint(RenderToucan render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityToucan entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entitylivingbaseIn.isEnchanted()) {
            VertexConsumer vertexconsumer = ItemRenderer.m_115184_(bufferIn, RenderType.m_110431_(RenderToucan.TEXTURE_GOLDEN), false, true);
            ((ModelToucan)this.m_117386_())
               .m_7695_(matrixStackIn, vertexconsumer, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   static class LayerHeldItem extends RenderLayer<EntityToucan, ModelToucan> {
      public LayerHeldItem(RenderToucan render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityToucan entitylivingbaseIn,
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
         matrixStackIn.m_252880_(-0.07F, -0.1F, -0.25F);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-45.0F));
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      }

      protected void translateToHand(PoseStack matrixStack) {
         ((ModelToucan)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelToucan)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelToucan)this.m_117386_()).head.translateAndRotate(matrixStack);
      }
   }
}
