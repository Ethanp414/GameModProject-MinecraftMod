package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelEndergrade;
import com.github.alexthe666.alexsmobs.client.render.RenderEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LayerEndergradeSaddle extends RenderLayer<EntityEndergrade, ModelEndergrade> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/endergrade_saddle.png");

   public LayerEndergradeSaddle(RenderEndergrade renderGrizzlyBear) {
      super(renderGrizzlyBear);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityEndergrade entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (entitylivingbaseIn.isSaddled()) {
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(RenderType.m_110452_(TEXTURE));
         ((ModelEndergrade)this.m_117386_())
            .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
