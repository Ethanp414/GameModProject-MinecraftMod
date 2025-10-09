package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AnimalAIFindWaterLava extends Goal {
   private final PathfinderMob creature;
   private BlockPos targetPos;
   private final int executionChance = 30;
   private final double speed;

   public AnimalAIFindWaterLava(PathfinderMob creature) {
      this(creature, 1.0);
   }

   public AnimalAIFindWaterLava(PathfinderMob creature, double speed) {
      this.creature = creature;
      this.speed = speed;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (this.creature.m_20096_()
         && !this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13131_)
         && !this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13132_)
         && this.creature instanceof ISemiAquatic
         && ((ISemiAquatic)this.creature).shouldEnterWater()
         && (this.creature.m_5448_() != null || this.creature.m_217043_().m_188503_(30) == 0)) {
         this.targetPos = this.generateTarget();
         return this.targetPos != null;
      } else {
         return false;
      }
   }

   public void m_8056_() {
      if (this.targetPos != null) {
         this.creature.m_21573_().m_26519_(this.targetPos.m_123341_(), this.targetPos.m_123342_(), this.targetPos.m_123343_(), this.speed);
      }
   }

   public void m_8037_() {
      if (this.targetPos != null) {
         this.creature.m_21573_().m_26519_(this.targetPos.m_123341_(), this.targetPos.m_123342_(), this.targetPos.m_123343_(), this.speed);
      }
   }

   public boolean m_8045_() {
      if (this.creature instanceof ISemiAquatic && !((ISemiAquatic)this.creature).shouldEnterWater()) {
         this.creature.m_21573_().m_26573_();
         return false;
      } else {
         return !this.creature.m_21573_().m_26571_()
            && this.targetPos != null
            && !this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13132_)
            && !this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13131_);
      }
   }

   public BlockPos generateTarget() {
      BlockPos blockpos = null;
      RandomSource random = this.creature.m_217043_();
      int range = this.creature instanceof ISemiAquatic ? ((ISemiAquatic)this.creature).getWaterSearchRange() : 14;

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = this.creature.m_20183_().m_7918_(random.m_188503_(range) - range / 2, 3, random.m_188503_(range) - range / 2);

         while (this.creature.m_9236_().m_46859_(blockpos1) && blockpos1.m_123342_() > 1) {
            blockpos1 = blockpos1.m_7495_();
         }

         if (this.creature.m_9236_().m_6425_(blockpos1).m_205070_(FluidTags.f_13131_)
            || this.creature.m_9236_().m_6425_(blockpos1).m_205070_(FluidTags.f_13132_)) {
            blockpos = blockpos1;
         }
      }

      return blockpos;
   }
}
