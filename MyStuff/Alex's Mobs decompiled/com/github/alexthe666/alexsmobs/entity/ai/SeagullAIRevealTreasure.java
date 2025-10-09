package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class SeagullAIRevealTreasure extends Goal {
   private final EntitySeagull seagull;
   private BlockPos sitPos;

   public SeagullAIRevealTreasure(EntitySeagull entitySeagull) {
      this.seagull = entitySeagull;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.TARGET));
   }

   public boolean m_8036_() {
      return this.seagull.getTreasurePos() != null && this.seagull.treasureSitTime > 0;
   }

   public void m_8056_() {
      this.seagull.aiItemFlag = true;
      this.sitPos = this.seagull.getSeagullGround(this.seagull.getTreasurePos());
   }

   public void m_8041_() {
      this.sitPos = null;
      this.seagull.setSitting(false);
      this.seagull.aiItemFlag = false;
   }

   public void m_8037_() {
      if (this.sitPos != null) {
         if (this.seagull.m_20238_(new Vec3(this.sitPos.m_123341_() + 0.5F, this.seagull.m_20186_(), this.sitPos.m_123343_() + 0.5F)) > 2.5) {
            this.seagull.m_21566_().m_6849_(this.sitPos.m_123341_() + 0.5F, this.sitPos.m_123342_() + 2, this.sitPos.m_123343_() + 0.5F, 1.0);
            if (!this.seagull.m_20096_()) {
               this.seagull.setFlying(true);
            }
         } else {
            Vec3 vec = Vec3.m_82514_(this.sitPos, 1.0);
            if (vec.m_82546_(this.seagull.m_20182_()).m_82553_() > 0.04F) {
               this.seagull.m_20256_(vec.m_82546_(this.seagull.m_20182_()).m_82490_(0.2F));
            }

            this.seagull.eatItem();
            this.seagull.treasureSitTime = Math.min(this.seagull.treasureSitTime, 100);
            this.seagull.setFlying(false);
            this.seagull.setSitting(true);
         }
      }
   }
}
