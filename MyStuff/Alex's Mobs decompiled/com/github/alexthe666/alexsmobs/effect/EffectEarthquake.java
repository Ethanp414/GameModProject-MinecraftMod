package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectEarthquake extends MobEffect {
   public EffectEarthquake() {
      super(MobEffectCategory.HARMFUL, 15788513);
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
   }

   public boolean m_6584_(int duration, int amplifier) {
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.earthquake";
   }
}
