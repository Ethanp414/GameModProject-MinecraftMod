package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ButtonTransmute extends Button {
   private final Screen parent;

   public ButtonTransmute(Screen parent, int x, int y, OnPress onPress) {
      super(x, y, 117, 19, CommonComponents.f_237098_, onPress, f_252438_);
      this.parent = parent;
   }

   public void m_87963_(GuiGraphics guiGraphics, int x, int y, float partialTick) {
      int color = 8453920;
      int cost = AMConfig.transmutingExperienceCost;
      if (!this.canBeTransmuted(cost)) {
         color = 16736352;
      } else if (this.f_93623_ && this.f_93622_) {
         guiGraphics.m_280218_(GUITransmutationTable.TEXTURE, this.m_252754_(), this.m_252907_(), 0, 201, 117, 19);
         color = 13107152;
      }

      guiGraphics.m_280168_().m_85836_();
      guiGraphics.m_280614_(
         Minecraft.m_91087_().f_91062_,
         Component.m_237115_("alexsmobs.container.transmutation_table.cost").m_130946_(" " + cost),
         this.m_252754_() + 21,
         this.m_252907_() + (this.f_93619_ - 8) / 2,
         color,
         false
      );
      guiGraphics.m_280168_().m_85849_();
   }

   public boolean canBeTransmuted(int cost) {
      return Minecraft.m_91087_().f_91074_.f_36078_ >= cost || Minecraft.m_91087_().f_91074_.m_150110_().f_35937_;
   }

   public void m_7435_(SoundManager sounds) {
      if (this.canBeTransmuted(AMConfig.transmutingExperienceCost)) {
         super.m_7435_(sounds);
      }
   }

   public void m_5691_() {
      if (this.canBeTransmuted(AMConfig.transmutingExperienceCost)) {
         super.m_5691_();
      }

      this.f_93622_ = false;
      this.m_93692_(false);
   }
}
