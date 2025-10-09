package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

public class AnimalAIRideParent extends Goal {
   private final Animal childAnimal;
   private Animal parentAnimal;
   private final double moveSpeed;
   private int delayCounter;

   public AnimalAIRideParent(Animal animal, double speed) {
      this.childAnimal = animal;
      this.moveSpeed = speed;
   }

   public boolean m_8036_() {
      if (this.childAnimal.m_146764_() < 0 && !this.childAnimal.m_20159_()) {
         List<? extends Animal> list = this.childAnimal.m_9236_().m_45976_(this.childAnimal.getClass(), this.childAnimal.m_20191_().m_82377_(8.0, 4.0, 8.0));
         Animal animalentity = null;
         double d0 = Double.MAX_VALUE;

         for (Animal animalentity1 : list) {
            if (animalentity1.m_146764_() >= 0 && animalentity1.m_20197_().isEmpty()) {
               double d1 = this.childAnimal.m_20280_(animalentity1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  animalentity = animalentity1;
               }
            }
         }

         if (animalentity == null) {
            return false;
         } else if (d0 < 2.0) {
            return false;
         } else {
            this.parentAnimal = animalentity;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      if (this.childAnimal.m_146764_() >= 0) {
         return false;
      } else if (this.parentAnimal != null && this.parentAnimal.m_6084_() && this.parentAnimal.m_20197_().isEmpty()) {
         double d0 = this.childAnimal.m_20280_(this.parentAnimal);
         return !(d0 < 2.0) && !(d0 > 256.0) && !this.childAnimal.m_20365_(this.parentAnimal);
      } else {
         return false;
      }
   }

   public void m_8056_() {
      this.delayCounter = 0;
   }

   public void m_8041_() {
      this.parentAnimal = null;
   }

   public void m_8037_() {
      if (--this.delayCounter <= 0) {
         this.delayCounter = 10;
         this.childAnimal.m_21573_().m_5624_(this.parentAnimal, this.moveSpeed);
      }

      if (this.childAnimal.m_20270_(this.parentAnimal) < 2.0) {
         this.childAnimal.m_7998_(this.parentAnimal, false);
         this.m_8041_();
      }
   }
}
