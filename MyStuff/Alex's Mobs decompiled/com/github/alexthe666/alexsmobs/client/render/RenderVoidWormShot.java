package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class RenderVoidWormShot extends EntityRenderer<EntityVoidWormShot> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_shot.png");
   private static final ModelVoidWormShot MODEL = new ModelVoidWormShot();

   public RenderVoidWormShot(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityVoidWormShot entity) {
      return TEXTURE;
   }

   public void render(EntityVoidWormShot entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      matrixStackIn.m_252781_(new Quaternionf().rotateX(Maths.rad(180.0)));
      matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_())));
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
      matrixStackIn.m_85836_();
      MODEL.animate(entityIn, entityIn.f_19797_ + partialTicks);
      float home = (entityIn.prevStopHomingProgress + (entityIn.getStopHomingProgress() - entityIn.prevStopHomingProgress) * partialTicks) / 40.0F;
      matrixStackIn.m_252880_(0.0F, -1.5F, 0.0F);
      VertexConsumer ivertexbuilder = bufferIn.m_6299_(AMRenderTypes.getFullBright(this.getTextureLocation(entityIn)));
      MODEL.m_7695_(matrixStackIn, ivertexbuilder, 210, OverlayTexture.f_118083_, Math.max(home, 0.2F), Math.max(home, 0.2F), 1.0F, 1.0F);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }
}
