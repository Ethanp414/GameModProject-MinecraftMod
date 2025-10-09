package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderEndPirateAnchor<T extends TileEntityEndPirateAnchor> implements BlockEntityRenderer<T> {
   protected static final ResourceLocation TEXTURE_ANCHOR = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor.png");
   protected static final ResourceLocation TEXTURE_ANCHOR_GLOW = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor_glow.png");
   protected static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();

   public RenderEndPirateAnchor(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.m_85836_();
      boolean east = (Boolean)tileEntityIn.m_58900_().m_61143_(BlockEndPirateAnchor.EASTORWEST);
      matrixStackIn.m_252880_(0.5F, 1.5F, 0.5F);
      matrixStackIn.m_85836_();
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(180.0F));
      if (east) {
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      }

      ANCHOR_MODEL.renderAnchor(tileEntityIn, partialTicks, east);
      ANCHOR_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110452_(TEXTURE_ANCHOR)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      ANCHOR_MODEL.m_7695_(
         matrixStackIn, bufferIn.m_6299_(RenderType.m_110488_(TEXTURE_ANCHOR_GLOW)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F
      );
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }

   public boolean shouldRenderOffScreen(T p_112306_) {
      return true;
   }

   public int m_142163_() {
      return 256;
   }
}
