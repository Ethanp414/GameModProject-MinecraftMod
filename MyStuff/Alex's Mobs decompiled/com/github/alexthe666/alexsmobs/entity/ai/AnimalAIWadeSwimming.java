package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.EnumSet;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AnimalAIWadeSwimming extends Goal {
   private final Mob entity;

   public AnimalAIWadeSwimming(Mob entity) {
      this.entity = entity;
      this.m_7021_(EnumSet.of(Flag.JUMP));
      entity.m_21573_().m_7008_(true);
   }

   public boolean m_8036_() {
      return this.entity.m_20069_() && this.entity.m_204036_(FluidTags.f_13131_) > 1.0 || this.entity.m_20077_();
   }

   public void m_8037_() {
      if (this.entity.m_217043_().m_188501_() < 0.8F) {
         this.entity.m_21569_().m_24901_();
      }
   }
}
