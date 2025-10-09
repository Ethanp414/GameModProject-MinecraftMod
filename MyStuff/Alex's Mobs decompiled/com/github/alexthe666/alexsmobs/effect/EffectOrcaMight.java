package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectOrcaMight extends MobEffect {
   public EffectOrcaMight() {
      super(MobEffectCategory.BENEFICIAL, 4868690);
      this.m_19472_(Attributes.f_22283_, "03C3C89D-7037-4B42-869F-B146BCB64D3A", 3.0, Operation.ADDITION);
   }

   public boolean m_6584_(int duration, int amplifier) {
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.orcas_might";
   }
}
