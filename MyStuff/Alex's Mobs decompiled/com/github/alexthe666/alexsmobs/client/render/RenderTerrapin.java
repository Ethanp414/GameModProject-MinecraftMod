package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderTerrapin extends MobRenderer<EntityTerrapin, ModelTerrapin> {
   private static final ResourceLocation[] SHELL_TEXTURES = new ResourceLocation[]{
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_0.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_1.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_2.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_3.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_4.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_5.png")
   };
   private static final ResourceLocation[] SKIN_PATTERN_TEXTURES = new ResourceLocation[]{
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_0.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_1.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_2.png"),
      new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_3.png")
   };

   public RenderTerrapin(Context renderManagerIn) {
      super(renderManagerIn, new ModelTerrapin(), 0.3F);
      this.m_115326_(new RenderTerrapin.TurtleOverlayLayer(this, 0));
      this.m_115326_(new RenderTerrapin.TurtleOverlayLayer(this, 1));
      this.m_115326_(new RenderTerrapin.TurtleOverlayLayer(this, 2));
   }

   protected void scale(EntityTerrapin entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
   }

   public ResourceLocation getTextureLocation(EntityTerrapin entity) {
      return entity.isKoopa() ? TerrapinTypes.KOOPA.getTexture() : entity.getTurtleType().getTexture();
   }

   protected void setupRotations(EntityTerrapin entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
      if (this.m_5936_(entity)) {
         yawIn += (float)(Math.cos(entity.f_19797_ * 3.25) * Math.PI * 0.4F);
      }

      Pose pose = entity.m_20089_();
      if (pose != Pose.SLEEPING && !entity.isSpinning()) {
         stack.m_252781_(Axis.f_252436_.m_252977_(180.0F - yawIn));
      }

      if (entity.f_20919_ > 0) {
         float f = (entity.f_20919_ + partialTickTime - 1.0F) / 20.0F * 1.6F;
         f = Mth.m_14116_(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         stack.m_252781_(Axis.f_252403_.m_252977_(f * this.m_6441_(entity)));
      } else if (entity.m_21209_()) {
         stack.m_252781_(Axis.f_252529_.m_252977_(-90.0F - entity.m_146909_()));
         stack.m_252781_(Axis.f_252436_.m_252977_((entity.f_19797_ + partialTickTime) * -75.0F));
      } else if (pose != Pose.SLEEPING && m_194453_(entity)) {
         stack.m_85837_(0.0, entity.m_20206_() + 0.1F, 0.0);
         stack.m_252781_(Axis.f_252403_.m_252977_(180.0F));
      }
   }

   static class TurtleOverlayLayer extends RenderLayer<EntityTerrapin, ModelTerrapin> {
      private final int layer;

      public TurtleOverlayLayer(RenderTerrapin render, int layer) {
         super(render);
         this.layer = layer;
      }

      public void render(
         PoseStack matrixStackIn,
         MultiBufferSource buffer,
         int packedLightIn,
         EntityTerrapin turtle,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         if (turtle.getTurtleType() == TerrapinTypes.OVERLAY && !turtle.isKoopa()) {
            ResourceLocation tex = this.layer == 0
               ? this.m_117347_(turtle)
               : (
                  this.layer == 1
                     ? RenderTerrapin.SHELL_TEXTURES[turtle.getShellType() % RenderTerrapin.SHELL_TEXTURES.length]
                     : RenderTerrapin.SKIN_PATTERN_TEXTURES[turtle.getSkinType() % RenderTerrapin.SKIN_PATTERN_TEXTURES.length]
               );
            int color = this.layer == 0 ? turtle.getTurtleColor() : (this.layer == 1 ? turtle.getShellColor() : turtle.getSkinColor());
            float r = (color >> 16 & 0xFF) / 255.0F;
            float g = (color >> 8 & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            m_117376_(this.m_117386_(), tex, matrixStackIn, buffer, packedLightIn, turtle, r, g, b);
         }
      }
   }
}
