package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateDoor;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public class RenderEndPirateDoor<T extends TileEntityEndPirateDoor> implements BlockEntityRenderer<T> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/door.png");
   private static final ModelEndPirateDoor DOOR_MODEL = new ModelEndPirateDoor();

   public RenderEndPirateDoor(Context rendererDispatcherIn) {
   }

   public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      matrixStackIn.m_85836_();
      Direction dir = (Direction)tileEntityIn.m_58900_().m_61143_(BlockEndPirateDoor.HORIZONTAL_FACING);
      switch (dir) {
         case NORTH:
            matrixStackIn.m_85837_(0.5, 0.5, -0.5);
            break;
         case EAST:
            matrixStackIn.m_252880_(1.5F, 0.5F, 0.5F);
            break;
         case SOUTH:
            matrixStackIn.m_85837_(0.5, 0.5, 1.5);
            break;
         case WEST:
            matrixStackIn.m_252880_(-0.5F, 0.5F, 0.5F);
      }

      matrixStackIn.m_252781_(dir.m_122424_().m_253075_());
      matrixStackIn.m_85836_();
      matrixStackIn.m_252880_(0.0F, 1.0F, -1.0F);
      matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F));
      matrixStackIn.m_85841_(0.999F, 0.999F, 0.999F);
      DOOR_MODEL.renderDoor(tileEntityIn, partialTicks, tileEntityIn.m_58900_().m_61143_(BlockEndPirateDoor.HINGE) == DoorHingeSide.LEFT);
      DOOR_MODEL.m_7695_(matrixStackIn, bufferIn.m_6299_(RenderType.m_110473_(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.m_85849_();
      matrixStackIn.m_85849_();
   }

   public int m_142163_() {
      return 128;
   }
}
