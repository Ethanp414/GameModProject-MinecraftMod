package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.item.ItemRainbowJelly;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class LayerRainbow extends RenderLayer {
   private final RenderLayerParent parent;

   public LayerRainbow(RenderLayerParent parent) {
      super(parent);
      this.parent = parent;
   }

   public void m_6494_(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      Entity entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      int i = RainbowUtil.getRainbowType((LivingEntity)entity);
      if (entity instanceof LivingEntity && i > 0) {
         ItemRainbowJelly.RainbowType rainbowType = ItemRainbowJelly.RainbowType.values()[Mth.m_14045_(
            i - 1, 0, ItemRainbowJelly.RainbowType.values().length - 1
         )];
         VertexConsumer ivertexbuilder = bufferIn.m_6299_(this.getRenderType(rainbowType));
         float alpha = 0.5F;
         matrixStackIn.m_85836_();
         this.m_117386_()
            .m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.m_115338_((LivingEntity)entity, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
         matrixStackIn.m_85849_();
      }
   }

   private RenderType getRenderType(ItemRainbowJelly.RainbowType rainbowType) {
      return switch (rainbowType) {
         case TRANS -> AMRenderTypes.TRANS_GLINT;
         case NONBI -> AMRenderTypes.NONBI_GLINT;
         case BI -> AMRenderTypes.BI_GLINT;
         case ACE -> AMRenderTypes.ACE_GLINT;
         case WEEZER -> AMRenderTypes.WEEZER_GLINT;
         case BRAZIL -> AMRenderTypes.BRAZIL_GLINT;
         default -> AMRenderTypes.RAINBOW_GLINT;
      };
   }
}
