package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EffectSunbird extends MobEffect {
   public final boolean curse;

   public EffectSunbird(boolean curse) {
      super(curse ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, 16771769);
      this.curse = curse;
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (this.curse) {
         if (entity.m_21255_() && entity instanceof Player) {
            ((Player)entity).m_36321_();
         }

         boolean forceFall = false;
         if (entity instanceof Player player && (!player.m_7500_() || !player.m_150110_().f_35935_)) {
            forceFall = true;
         }

         if ((forceFall || !(entity instanceof Player)) && !entity.m_20096_()) {
            entity.m_20256_(entity.m_20184_().m_82520_(0.0, -0.2F, 0.0));
         }
      } else {
         entity.f_19789_ = 0.0F;
         if (entity.m_21255_()) {
            if (entity.m_146909_() < -10.0F) {
               float pitchMulti = Math.abs(entity.m_146909_()) / 90.0F;
               entity.m_20256_(entity.m_20184_().m_82520_(0.0, 0.02 + pitchMulti * 0.02, 0.0));
            }
         } else if (!entity.m_20096_() && !entity.m_6047_()) {
            Vec3 vector3d = entity.m_20184_();
            if (vector3d.f_82480_ < 0.0) {
               entity.m_20256_(vector3d.m_82542_(1.0, 0.6, 1.0));
            }
         }
      }
   }

   public boolean m_6584_(int duration, int amplifier) {
      return duration > 0;
   }

   public String m_19481_() {
      return this.curse ? "alexsmobs.potion.sunbird_curse" : "alexsmobs.potion.sunbird_blessing";
   }
}
