package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class AnimalAIMeleeNearby extends Goal {
   private final Mob entity;
   private final int range;
   private final double speed;
   private BlockPos fightStartPos = null;

   public AnimalAIMeleeNearby(Mob entity, int range, double speed) {
      this.m_7021_(EnumSet.of(Flag.MOVE));
      this.entity = entity;
      this.range = range;
      this.speed = speed;
   }

   public boolean m_8036_() {
      return this.entity.m_5448_() != null && this.entity.m_5448_().m_6084_() && !this.entity.m_20160_();
   }

   public void m_8056_() {
      this.fightStartPos = this.entity.m_20097_();
   }

   public void m_8041_() {
      this.entity.m_21573_().m_26573_();
      this.fightStartPos = null;
   }

   public void m_8037_() {
      if (this.entity.m_20270_(this.entity.m_5448_()) < 3.0F + this.entity.m_20205_() + this.entity.m_5448_().m_20205_()) {
         this.entity.m_7327_(this.entity.m_5448_());
         this.entity.m_21391_(this.entity.m_5448_(), 180.0F, 180.0F);
      } else if (this.fightStartPos != null) {
         if (this.entity.m_20238_(Vec3.m_82512_(this.fightStartPos)) < this.range * this.range) {
            this.entity.m_21573_().m_5624_(this.entity.m_5448_(), this.speed);
         } else {
            this.entity
               .m_21573_()
               .m_26519_(this.fightStartPos.m_123341_() + 0.5F, this.fightStartPos.m_123342_() + 0.5F, this.fightStartPos.m_123343_() + 0.5F, 0.4F + this.speed);
         }
      }
   }
}
