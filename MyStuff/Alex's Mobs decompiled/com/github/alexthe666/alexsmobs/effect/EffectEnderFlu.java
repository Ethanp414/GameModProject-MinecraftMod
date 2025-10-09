package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class EffectEnderFlu extends MobEffect {
   private int lastDuration = -1;

   public EffectEnderFlu() {
      super(MobEffectCategory.HARMFUL, 6829738);
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (this.lastDuration == 1) {
         int phages = amplifier + 1;
         entity.m_6469_(entity.m_269291_().m_269425_(), phages * 10);

         for (int i = 0; i < phages; i++) {
            EntityEnderiophage phage = (EntityEnderiophage)((EntityType)AMEntityRegistry.ENDERIOPHAGE.get()).m_20615_(entity.m_9236_());
            phage.m_20359_(entity);
            phage.onSpawnFromEffect();
            phage.setSkinForDimension();
            if (!entity.m_9236_().f_46443_) {
               phage.setStandardFleeTime();
               entity.m_9236_().m_7967_(phage);
            }
         }
      }
   }

   public boolean m_6584_(int duration, int amplifier) {
      this.lastDuration = duration;
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.ender_flu";
   }
}
