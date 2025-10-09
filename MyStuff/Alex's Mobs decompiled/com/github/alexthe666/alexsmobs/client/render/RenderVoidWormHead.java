package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class RenderVoidWormHead extends MobRenderer<EntityVoidWorm, ModelVoidWorm> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_head.png");
   private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_head_glow.png");

   public RenderVoidWormHead(Context renderManagerIn) {
      super(renderManagerIn, new ModelVoidWorm(0.0F), 1.0F);
      this.m_115326_(new LayerVoidWormGlow(this, renderManagerIn.m_174026_(), new ModelVoidWorm(1.001F)) {
         @Override
         public ResourceLocation getGlowTexture(LivingEntity worm) {
            return RenderVoidWormHead.TEXTURE_GLOW;
         }

         @Override
         public boolean isGlowing(LivingEntity worm) {
            return true;
         }

         @Override
         public float getAlpha(LivingEntity livingEntity) {
            return 1.0F;
         }
      });
   }

   @Nullable
   protected RenderType getRenderType(EntityVoidWorm jelly, boolean normal, boolean invis, boolean outline) {
      ResourceLocation resourcelocation = this.getTextureLocation(jelly);
      if (invis) {
         return RenderType.m_110467_(resourcelocation);
      } else if (normal) {
         return RenderType.m_110473_(resourcelocation);
      } else {
         return outline ? RenderType.m_110491_(resourcelocation) : null;
      }
   }

   public boolean shouldRender(EntityVoidWorm worm, Frustum camera, double camX, double camY, double camZ) {
      return worm.getPortalTicks() <= 0 && super.m_5523_(worm, camera, camX, camY, camZ);
   }

   public ResourceLocation getTextureLocation(EntityVoidWorm entity) {
      return TEXTURE;
   }
}
