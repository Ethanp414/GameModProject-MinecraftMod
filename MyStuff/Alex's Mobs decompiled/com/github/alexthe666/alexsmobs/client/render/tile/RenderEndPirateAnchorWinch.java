package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorChain;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderEndPirateAnchorWinch<T extends TileEntityEndPirateAnchorWinch> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
   private static final ResourceLocation TEXTURE_CHAIN = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor_chain.png");
   private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
   private static final ModelEndPirateAnchorChain CHAIN_MODEL = new ModelEndPirateAnchorChain();

   public RenderEndPirateAnchorWinch(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.m_85836_();
      boolean east = (Boolean)tileEntityIn.m_58900_().m_61143_(BlockEndPirateAnchorWinch.EASTORWEST);
      matrixStackIn.m_252880_(0.5F, 1.5F, 0.5F);
      matrixStackIn.m_85836_();
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(180.0F));
      if (east) {
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      }

      boolean flag = false;
      matrixStackIn.m_85836_();
      if (!tileEntityIn.isAnchorEW()) {
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
      }

      float bottomOfChain = tileEntityIn.getChainLength(partialTicks);

      for (float i = 0.0F; i < tileEntityIn.getChainLengthForRender(); i += 0.5F) {
         matrixStackIn.m_85836_();
         float moveDown = Math.max(bottomOfChain - i, 0.0F);
         matrixStackIn.m_252880_(0.0F, 0.1F + moveDown, 0.0F);
         if (flag) {
            matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
         }

         if (moveDown <= 1.0F) {
            float modulatedScale = 0.5F + moveDown * 0.5F;
            matrixStackIn.m_252880_(0.0F, (1.0F - moveDown) * 0.5F, 0.0F);
            matrixStackIn.m_85841_(modulatedScale, modulatedScale, modulatedScale);
         }

         CHAIN_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110452_(TEXTURE_CHAIN)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         CHAIN_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110488_(TEXTURE_CHAIN)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.m_85849_();
         flag = !flag;
      }

      matrixStackIn.m_85849_();
      WINCH_MODEL.renderAnchor(tileEntityIn, partialTicks, east);
      WINCH_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110452_(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      WINCH_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110488_(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
      if (tileEntityIn.hasAnchor()) {
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.5F, -1.5F - bottomOfChain, 0.5F);
         matrixStackIn.m_85836_();
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(180.0F));
         if (tileEntityIn.isAnchorEW()) {
            matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
         }

         RenderEndPirateAnchor.ANCHOR_MODEL.resetToDefaultPose();
         RenderEndPirateAnchor.ANCHOR_MODEL
            .m_7695_(
               matrixStackIn,
               bufferIn.m_6299_(RenderType.m_110452_(RenderEndPirateAnchor.TEXTURE_ANCHOR)),
               combinedLightIn,
               combinedOverlayIn,
               1.0F,
               1.0F,
               1.0F,
               1.0F
            );
         RenderEndPirateAnchor.ANCHOR_MODEL
            .m_7695_(
               matrixStackIn,
               bufferIn.m_6299_(RenderType.m_110488_(RenderEndPirateAnchor.TEXTURE_ANCHOR_GLOW)),
               combinedLightIn,
               combinedOverlayIn,
               1.0F,
               1.0F,
               1.0F,
               1.0F
            );
         matrixStackIn.m_85849_();
         matrixStackIn.m_85849_();
      }
   }

   public boolean shouldRenderOffScreen(T entity) {
      return true;
   }

   public int m_142163_() {
      return 256;
   }
}
