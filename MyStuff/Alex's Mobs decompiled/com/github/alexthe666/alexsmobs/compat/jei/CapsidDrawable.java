package com.github.alexthe666.alexsmobs.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class CapsidDrawable implements IDrawable {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs", "textures/gui/capsid_jei_representation.png");

   public int getWidth() {
      return 125;
   }

   public int getHeight() {
      return 59;
   }

   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      guiGraphics.m_280163_(TEXTURE, xOffset, yOffset, 0.0F, 0.0F, 125, 59, 256, 256);
   }
}
