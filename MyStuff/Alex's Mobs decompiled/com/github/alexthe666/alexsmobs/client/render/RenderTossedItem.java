package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class RenderTossedItem extends EntityRenderer<EntityTossedItem> {
   public static final ResourceLocation DART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/ancient_dart.png");
   public static final ModelAncientDart DART_MODEL = new ModelAncientDart();

   public RenderTossedItem(Context renderManager) {
      super(renderManager);
   }

   public ResourceLocation getTextureLocation(EntityTossedItem entity) {
      return TextureAtlas.f_118259_;
   }

   public void render(EntityTossedItem entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      matrixStackIn.m_85836_();
      if (entityIn.isDart()) {
         matrixStackIn.m_85837_(0.0, -0.15F, 0.0);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 180.0F));
         matrixStackIn.m_85836_();
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
         matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
         matrixStackIn.m_85841_(1.0F, 1.0F, 1.0F);
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(DART_MODEL.m_103119_(DART_TEXTURE));
         DART_MODEL.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.m_85849_();
      } else {
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19859_, entityIn.m_146908_()) - 90.0F));
         matrixStackIn.m_252781_(Axis.f_252403_.m_252977_(Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_())));
         matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
         matrixStackIn.m_85841_(1.0F, 1.0F, 1.0F);
         matrixStackIn.m_252781_(new Quaternionf().rotateZ(Maths.rad(-(entityIn.f_19797_ + partialTicks) * 30.0F)));
         matrixStackIn.m_252880_(0.0F, -0.15F, 0.0F);
         Minecraft.m_91087_()
            .m_91291_()
            .m_269128_(entityIn.m_7846_(), ItemDisplayContext.GROUND, packedLightIn, OverlayTexture.f_118083_, matrixStackIn, bufferIn, entityIn.m_9236_(), 0);
      }

      matrixStackIn.m_85849_();
   }
}
