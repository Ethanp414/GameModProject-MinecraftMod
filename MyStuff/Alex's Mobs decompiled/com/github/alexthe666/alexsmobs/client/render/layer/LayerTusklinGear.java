package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelTusklin;
import com.github.alexthe666.alexsmobs.client.render.RenderTusklin;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LayerTusklinGear extends RenderLayer<EntityTusklin, ModelTusklin> {
   private static final ResourceLocation TEXTURE_SADDLE = new ResourceLocation("alexsmobs:textures/entity/tusklin_saddle.png");
   private static final ResourceLocation TEXTURE_SHOES = new ResourceLocation("alexsmobs:textures/entity/tusklin_hooves.png");

   public LayerTusklinGear(RenderTusklin render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityTusklin entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entitylivingbaseIn.isSaddled()) {
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110452_(TEXTURE_SADDLE));
         ((ModelTusklin)this.m_117386_())
            .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (!entitylivingbaseIn.getShoeStack().m_41619_()) {
         VertexConsumer ivertexbuilder = ItemRenderer.m_115184_(
            bufferIn, RenderType.m_110431_(TEXTURE_SHOES), false, entitylivingbaseIn.getShoeStack().m_41790_()
         );
         ((ModelTusklin)this.m_117386_())
            .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
