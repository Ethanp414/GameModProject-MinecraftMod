package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateFlag;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateFlag;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class RenderEndPirateFlag<T extends TileEntityEndPirateFlag> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/flag.png");
   private static final ModelEndPirateFlag FLAG_MODEL = new ModelEndPirateFlag();

   public RenderEndPirateFlag(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.m_85836_();
      Direction dir = (Direction)tileEntityIn.m_58900_().m_61143_(BlockEndPirateFlag.FACING);
      switch (dir) {
         case NORTH:
            matrixStackIn.m_85837_(0.5, 1.5, 0.5);
            break;
         case EAST:
            matrixStackIn.m_252880_(0.5F, 1.5F, 0.5F);
            break;
         case SOUTH:
            matrixStackIn.m_85837_(0.5, 1.5, 0.5);
            break;
         case WEST:
            matrixStackIn.m_252880_(0.5F, 1.5F, 0.5F);
      }

      matrixStackIn.m_252781_(dir.m_122424_().m_253075_());
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F));
      matrixStackIn.m_252781_(Axis.f_252392_.m_252977_(dir.m_122434_() == net.minecraft.core.Direction.Axis.Y ? -90.0F : 90.0F));
      matrixStackIn.m_85836_();
      FLAG_MODEL.renderFlag(tileEntityIn, partialTicks);
      FLAG_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110458_(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }
}
