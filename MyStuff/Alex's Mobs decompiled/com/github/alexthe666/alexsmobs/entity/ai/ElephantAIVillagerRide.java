package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.AbstractVillager;

public class ElephantAIVillagerRide extends Goal {
   private final EntityElephant elephant;
   private AbstractVillager villager;
   private final double speed;

   public ElephantAIVillagerRide(EntityElephant dragon, double speed) {
      this.elephant = dragon;
      this.speed = speed;
   }

   public boolean m_8036_() {
      if (this.elephant.getControllingVillager() != null) {
         this.villager = this.elephant.getControllingVillager();
         return true;
      } else {
         return false;
      }
   }

   public void m_8056_() {
   }

   public void m_8037_() {
      if (this.villager.m_21573_().m_26572_()) {
         this.elephant.m_21573_().m_26536_(this.villager.m_21573_().m_26570_(), 1.6);
      }
   }
}
