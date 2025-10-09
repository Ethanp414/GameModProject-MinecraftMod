package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelMoose;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class RenderMoose extends MobRenderer<EntityMoose, ModelMoose> {
   private static final ResourceLocation TEXTURE_ANTLERED = new ResourceLocation("alexsmobs:textures/entity/moose_antlered.png");
   private static final ResourceLocation TEXTURE_SNOWY_ANTLERED = new ResourceLocation("alexsmobs:textures/entity/moose_snowy_antlered.png");
   private static final ResourceLocation TEXTURE_SNOWY = new ResourceLocation("alexsmobs:textures/entity/moose_snowy.png");
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/moose.png");

   public RenderMoose(Context renderManagerIn) {
      super(renderManagerIn, new ModelMoose(), 0.8F);
      this.m_115326_(new RenderMoose.LayerSnow());
   }

   protected void scale(EntityMoose entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntityMoose entity) {
      return entity.isAntlered() && !entity.m_6162_() ? TEXTURE_ANTLERED : TEXTURE;
   }

   class LayerSnow extends RenderLayer<EntityMoose, ModelMoose> {
      public LayerSnow() {
         super(RenderMoose.this);
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource bufferIn,
         int packedLightIn,
         EntityMoose entitylivingbaseIn,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (entitylivingbaseIn.isSnowy()) {
            VertexConsumer ivertexbuilder = bufferIn.m_6299_(
               RenderType.m_110458_(
                  entitylivingbaseIn.isAntlered() && !entitylivingbaseIn.m_6162_() ? RenderMoose.TEXTURE_SNOWY_ANTLERED : RenderMoose.TEXTURE_SNOWY
               )
            );
            ((ModelMoose)this.m_117386_())
               .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
