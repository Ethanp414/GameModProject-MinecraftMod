package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerKangarooItem extends RenderLayer<EntityKangaroo, ModelKangaroo> {
   public LayerKangarooItem(RenderKangaroo render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityKangaroo entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack itemstack = entitylivingbaseIn.m_6844_(EquipmentSlot.MAINHAND);
      matrixStackIn.m_85836_();
      boolean left = entitylivingbaseIn.m_21526_();
      if (entitylivingbaseIn.m_6162_()) {
         matrixStackIn.m_85841_(0.5F, 0.5F, 0.5F);
         matrixStackIn.m_85837_(0.0, 1.5, 0.0);
      }

      matrixStackIn.m_85836_();
      this.translateToHand(matrixStackIn, left);
      matrixStackIn.m_252880_(0.0F, 0.75F, -0.125F);
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-110.0F));
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(180.0F));
      matrixStackIn.m_85841_(0.8F, 0.8F, 0.8F);
      ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
      renderer.m_269530_(
         entitylivingbaseIn,
         itemstack,
         left ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
         false,
         matrixStackIn,
         bufferIn,
         packedLightIn
      );
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }

   protected void translateToHand(PoseStack matrixStack, boolean left) {
      ((ModelKangaroo)this.m_117386_()).root.translateAndRotate(matrixStack);
      ((ModelKangaroo)this.m_117386_()).body.translateAndRotate(matrixStack);
      ((ModelKangaroo)this.m_117386_()).chest.translateAndRotate(matrixStack);
      if (left) {
         ((ModelKangaroo)this.m_117386_()).arm_left.translateAndRotate(matrixStack);
      } else {
         ((ModelKangaroo)this.m_117386_()).arm_right.translateAndRotate(matrixStack);
      }
   }
}
