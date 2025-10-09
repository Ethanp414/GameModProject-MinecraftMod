package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreedGoal;

public class KomodoDragonAIBreed extends BreedGoal {
   boolean withPartner;
   private final EntityKomodoDragon komodo;
   int selfBreedTime = 0;

   public KomodoDragonAIBreed(EntityKomodoDragon entityKomodoDragon, double v) {
      super(entityKomodoDragon, v);
      this.komodo = entityKomodoDragon;
   }

   public boolean m_8036_() {
      boolean prev = super.m_8036_();
      this.withPartner = prev;
      return this.withPartner || this.f_25113_.m_27593_();
   }

   public boolean m_8045_() {
      return this.withPartner ? super.m_8045_() : this.selfBreedTime < 60;
   }

   public void m_8041_() {
      super.m_8041_();
      this.selfBreedTime = 0;
   }

   public void m_8037_() {
      if (this.withPartner) {
         super.m_8037_();
      } else {
         this.f_25113_.m_21573_().m_26573_();
         this.selfBreedTime++;
         if (this.selfBreedTime >= 60) {
            this.spawnParthogenicBaby();
         }
      }
   }

   protected void m_8026_() {
      for (int i = 0; i < 2 + this.f_25113_.m_217043_().m_188503_(2); i++) {
         this.f_25113_.m_27563_((ServerLevel)this.f_25114_, this.f_25115_);
      }

      this.komodo.slaughterCooldown = 200;
   }

   private void spawnParthogenicBaby() {
      for (int i = 0; i < 2 + this.f_25113_.m_217043_().m_188503_(2); i++) {
         this.f_25113_.m_27563_((ServerLevel)this.f_25114_, this.f_25113_);
      }

      this.komodo.slaughterCooldown = 200;
   }
}
