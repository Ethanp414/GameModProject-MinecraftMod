package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelRaccoon;
import com.github.alexthe666.alexsmobs.client.render.RenderRaccoon;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerRaccoonItem extends RenderLayer<EntityRaccoon, ModelRaccoon> {
   public LayerRaccoonItem(RenderRaccoon render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityRaccoon entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemstack = entitylivingbaseIn.m_6844_(EquipmentSlot.MAINHAND);
      matrixStackIn.m_85836_();
      boolean inHand = entitylivingbaseIn.begProgress > 0.0F || entitylivingbaseIn.standProgress > 0.0F || entitylivingbaseIn.washProgress > 0.0F;
      if (entitylivingbaseIn.m_6162_()) {
         matrixStackIn.m_85841_(0.5F, 0.5F, 0.5F);
         matrixStackIn.m_85837_(0.0, 1.5, 0.0);
      }

      matrixStackIn.m_85836_();
      this.translateToHand(inHand, matrixStackIn);
      if (inHand) {
         matrixStackIn.m_252880_(0.2F, 0.4F, 0.0F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F * entitylivingbaseIn.washProgress * 0.2F));
      } else {
         matrixStackIn.m_252880_(0.0F, 0.1F, -0.35F);
      }

      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-2.5F));
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-90.0F));
      ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
      renderer.m_269530_(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }

   protected void translateToHand(boolean inHand, PoseStack matrixStack) {
      if (inHand) {
         ((ModelRaccoon)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.m_117386_()).arm_right.translateAndRotate(matrixStack);
      } else {
         ((ModelRaccoon)this.m_117386_()).root.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.m_117386_()).body.translateAndRotate(matrixStack);
         ((ModelRaccoon)this.m_117386_()).head.translateAndRotate(matrixStack);
      }
   }
}
