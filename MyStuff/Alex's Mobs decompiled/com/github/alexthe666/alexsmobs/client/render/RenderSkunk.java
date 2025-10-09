package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelSkunk;
import com.github.alexthe666.alexsmobs.entity.EntitySkunk;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class RenderSkunk extends MobRenderer<EntitySkunk, ModelSkunk> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/skunk.png");

   public RenderSkunk(Context renderManagerIn) {
      super(renderManagerIn, new ModelSkunk(), 0.45F);
   }

   public ResourceLocation getTextureLocation(EntitySkunk entity) {
      return TEXTURE;
   }
}
