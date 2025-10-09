package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EffectPoisonResistance extends MobEffect {
   public EffectPoisonResistance() {
      super(MobEffectCategory.BENEFICIAL, 5373871);
   }

   public void m_6742_(LivingEntity LivingEntityIn, int amplifier) {
      if (LivingEntityIn.m_21023_(MobEffects.f_19614_)) {
         LivingEntityIn.m_21195_(MobEffects.f_19614_);
      }
   }

   public boolean m_6584_(int duration, int amplifier) {
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.poison_resistance";
   }
}
