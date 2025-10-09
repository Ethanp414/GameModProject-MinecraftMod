package com.github.alexthe666.alexsmobs.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class LaviathanAIRandomSwimming extends LavaAndWaterAIRandomSwimming {
   public LaviathanAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
      super(creature, speed, chance);
   }

   @Nullable
   @Override
   protected Vec3 m_7037_() {
      BlockPos pos = this.f_25725_.m_20183_().m_121955_(RandomPos.m_217851_(this.f_25725_.m_217043_(), 16, 5));
      int i = 0;

      while (pos != null && this.f_25725_.m_9236_().m_8055_(new BlockPos(pos)).m_60819_().m_76178_() && i++ < 10) {
         pos = this.f_25725_.m_20183_().m_121955_(RandomPos.m_217851_(this.f_25725_.m_217043_(), 16, 5));
      }

      if (this.f_25725_.m_9236_().m_8055_(new BlockPos(pos)).m_60819_().m_76178_()) {
         return null;
      } else {
         if (this.f_25725_.m_217043_().m_188503_(3) == 0) {
            while (!this.f_25725_.m_9236_().m_8055_(pos).m_60819_().m_76178_() && pos.m_123342_() < this.f_25725_.m_9236_().m_151558_()) {
               pos = pos.m_7494_();
            }

            pos = pos.m_7495_();
         }

         return new Vec3(pos.m_123341_() + 0.5F, pos.m_123342_() + 0.5F, pos.m_123343_() + 0.5F);
      }
   }
}
