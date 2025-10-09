package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelKomodoDragon;
import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderKomodoDragon extends MobRenderer<EntityKomodoDragon, ModelKomodoDragon> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/komodo_dragon.png");
   private static final ResourceLocation TEXTURE_SADDLE = new ResourceLocation("alexsmobs:textures/entity/komodo_dragon_saddle.png");
   private static final ResourceLocation TEXTURE_MAID = new ResourceLocation("alexsmobs:textures/entity/komodo_dragon_maid.png");

   public RenderKomodoDragon(Context renderManagerIn) {
      super(renderManagerIn, new ModelKomodoDragon(0.0F), 0.6F);
      this.m_115326_(new RenderKomodoDragon.LayerSaddle(this));
   }

   protected void scale(EntityKomodoDragon entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntityKomodoDragon entity) {
      return TEXTURE;
   }

   static class LayerSaddle extends RenderLayer<EntityKomodoDragon, ModelKomodoDragon> {
      private static final ModelKomodoDragon MAID_MODEL = new ModelKomodoDragon(0.3F);
      private static final ModelKomodoDragon SADDLE_MODEL = new ModelKomodoDragon(0.5F);

      public LayerSaddle(RenderKomodoDragon render) {
         super(render);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityKomodoDragon entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entitylivingbaseIn.isMaid()) {
            VertexConsumer maid = bufferIn.m_6299_(AMRenderTypes.m_110458_(RenderKomodoDragon.TEXTURE_MAID));
            ((ModelKomodoDragon)this.m_117386_()).m_102624_(MAID_MODEL);
            MAID_MODEL.m_6839_(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            MAID_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            MAID_MODEL.m_7695_(matrixStackIn, maid, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (entitylivingbaseIn.isSaddled()) {
            VertexConsumer saddle = bufferIn.m_6299_(AMRenderTypes.m_110458_(RenderKomodoDragon.TEXTURE_SADDLE));
            ((ModelKomodoDragon)this.m_117386_()).m_102624_(SADDLE_MODEL);
            SADDLE_MODEL.m_6839_(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            SADDLE_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            SADDLE_MODEL.m_7695_(matrixStackIn, saddle, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
