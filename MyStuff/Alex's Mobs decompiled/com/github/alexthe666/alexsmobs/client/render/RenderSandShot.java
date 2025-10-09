package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class RenderSandShot extends EntityRenderer<EntitySandShot> {
   private static final ResourceLocation SAND_SHOT = new ResourceLocation("alexsmobs:textures/entity/sand_shot.png");
   private final LlamaSpitModel<LlamaSpit> model;

   public RenderSandShot(Context renderManagerIn) {
      super(renderManagerIn);
      this.model = new LlamaSpitModel(renderManagerIn.m_174023_(ModelLayers.f_171196_));
   }

   public void render(EntitySandShot entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_85837_(0.0, 0.15F, 0.0);
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 90.0F));
      matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
      matrixStackIn.m_85841_(1.2F, 1.2F, 1.2F);
      int i = EntityGuster.getColorForVariant(entityIn.getVariant());
      float r = (i >> 16 & 0xFF) / 255.0F;
      float g = (i >> 8 & 0xFF) / 255.0F;
      float b = (i & 0xFF) / 255.0F;
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(this.model.m_103119_(SAND_SHOT));
      this.model.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, r, g, b, 1.0F);
      matrixStackIn.m_85849_();
      super.m_7392_(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public ResourceLocation getTextureLocation(EntitySandShot entity) {
      return SAND_SHOT;
   }
}
