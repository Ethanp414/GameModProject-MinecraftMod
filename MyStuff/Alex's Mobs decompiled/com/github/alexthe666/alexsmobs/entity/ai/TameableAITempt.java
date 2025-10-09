package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.crafting.Ingredient;

public class TameableAITempt extends TemptGoal {
   private static final TargetingConditions DEF = TargetingConditions.m_148353_().m_26883_(10.0).m_148355_();
   private final Animal tameable;
   private int calmDown;
   private final TargetingConditions targetingConditions;
   private final Ingredient items;

   public TameableAITempt(Animal tameable, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
      super(tameable, speedIn, temptItemsIn, scaredByPlayerMovementIn);
      this.tameable = tameable;
      this.items = temptItemsIn;
      this.targetingConditions = DEF.m_148354_().m_26888_(this::shouldFollowAM);
   }

   public boolean shouldFollowAM(LivingEntity p_148139_) {
      return this.items.test(p_148139_.m_21205_()) || this.items.test(p_148139_.m_21206_());
   }

   public boolean m_8036_() {
      if (this.calmDown > 0) {
         this.calmDown--;
         return false;
      } else {
         this.f_25925_ = this.f_25924_.m_9236_().m_45946_(this.targetingConditions, this.f_25924_);
         return (!(this.tameable instanceof TamableAnimal) || !((TamableAnimal)this.tameable).m_21824_()) && this.f_25925_ != null;
      }
   }

   public void m_8041_() {
      super.m_8041_();
      this.calmDown = m_186073_(100);
   }
}
