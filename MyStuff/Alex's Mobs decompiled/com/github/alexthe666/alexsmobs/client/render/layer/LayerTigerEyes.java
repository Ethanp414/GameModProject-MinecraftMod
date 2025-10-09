package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

public class LayerTigerEyes extends RenderLayer<EntityTiger, ModelTiger> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_eyes.png");
   private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white_eyes.png");
   private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_angry_eyes.png");

   public LayerTigerEyes(RenderTiger render) {
      super(render);
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityTiger tiger,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (!tiger.m_5803_()) {
         long roundedTime = tiger.m_9236_().m_46468_() % 24000L;
         boolean night = roundedTime >= 13000L && roundedTime <= 22000L;
         BlockPos ratPos = tiger.getLightPosition();
         int i = tiger.m_9236_().m_45517_(LightLayer.SKY, ratPos);
         int j = tiger.m_9236_().m_45517_(LightLayer.BLOCK, ratPos);
         int brightness;
         if (night) {
            brightness = j;
         } else {
            brightness = Math.max(i, j);
         }

         if (brightness < 7 || tiger.m_6784_() > 0) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(
               RenderType.m_110488_(tiger.m_6784_() > 0 ? TEXTURE_ANGRY : (tiger.isWhite() ? TEXTURE_WHITE : TEXTURE))
            );
            ((ModelTiger)this.m_117386_())
               .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(tiger, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
