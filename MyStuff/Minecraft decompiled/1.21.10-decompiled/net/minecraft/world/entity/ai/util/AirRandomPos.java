package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class AirRandomPos {
   @Nullable
   public static Vec3 getPosTowards(PathfinderMob $$0, int $$1, int $$2, int $$3, Vec3 $$4, double $$5) {
      Vec3 $$6 = $$4.subtract($$0.getX(), $$0.getY(), $$0.getZ());
      boolean $$7 = GoalUtils.mobRestricted($$0, $$1);
      return RandomPos.generateRandomPos($$0, () -> {
         BlockPos $$7xx = AirAndWaterRandomPos.generateRandomPos($$0, $$1, $$2, $$3, $$6.x, $$6.z, $$5, $$7);
         return $$7xx != null && !GoalUtils.isWater($$0, $$7xx) ? $$7xx : null;
      });
   }
}
