package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CapuchinAIMelee extends MeleeAttackGoal {
   private final EntityCapuchinMonkey monkey;

   public CapuchinAIMelee(EntityCapuchinMonkey monkey, double speedIn, boolean useLongMemory) {
      super(monkey, speedIn, useLongMemory);
      this.monkey = monkey;
   }

   public boolean m_8036_() {
      return super.m_8036_() && !this.monkey.attackDecision;
   }

   public boolean m_8045_() {
      return super.m_8045_() && !this.monkey.attackDecision;
   }

   protected void m_6739_(LivingEntity enemy, double distToEnemySqr) {
      double d0 = this.m_6639_(enemy);
      if (distToEnemySqr <= d0) {
         this.m_25563_();
         this.f_25540_.m_6674_(InteractionHand.MAIN_HAND);
         this.f_25540_.m_7327_(enemy);
      }
   }
}
