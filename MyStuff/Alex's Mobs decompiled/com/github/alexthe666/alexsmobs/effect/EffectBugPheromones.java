package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectBugPheromones extends MobEffect {
   public EffectBugPheromones() {
      super(MobEffectCategory.BENEFICIAL, 7882315);
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
   }

   public boolean m_6584_(int duration, int amplifier) {
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.bug_pheromones";
   }
}
