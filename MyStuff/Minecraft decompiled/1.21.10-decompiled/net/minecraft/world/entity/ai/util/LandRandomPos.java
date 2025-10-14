package net.minecraft.world.entity.ai.util;

import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class LandRandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2) {
      return getPos($$0, $$1, $$2, $$0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob $$0, int $$1, int $$2, ToDoubleFunction<BlockPos> $$3) {
      boolean $$4 = GoalUtils.mobRestricted($$0, $$1);
      return RandomPos.generateRandomPos(() -> {
         BlockPos $$4xx = RandomPos.generateRandomDirection($$0.getRandom(), $$1, $$2);
         BlockPos $$5 = generateRandomPosTowardDirection($$0, $$1, $$4, $$4xx);
         return $$5 == null ? null : movePosUpOutOfSolid($$0, $$5);
      }, $$3);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3) {
      Vec3 $$4 = $$3.subtract($$0.getX(), $$0.getY(), $$0.getZ());
      boolean $$5 = GoalUtils.mobRestricted($$0, $$1);
      return getPosInDirection($$0, $$1, $$2, $$4, $$5);
   }

   @Nullable
   public static Vec3 getPosAway(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3) {
      Vec3 $$4 = $$0.position().subtract($$3);
      boolean $$5 = GoalUtils.mobRestricted($$0, $$1);
      return getPosInDirection($$0, $$1, $$2, $$4, $$5);
   }

   @Nullable
   private static Vec3 getPosInDirection(PathfinderMob $$0, int $$1, int $$2, Vec3 $$3, boolean $$4) {
      return RandomPos.generateRandomPos($$0, () -> {
         BlockPos $$5 = RandomPos.generateRandomDirectionWithinRadians($$0.getRandom(), $$1, $$2, 0, $$3.x, $$3.z, (float) (Math.PI / 2));
         if ($$5 == null) {
            return null;
         } else {
            BlockPos $$6 = generateRandomPosTowardDirection($$0, $$1, $$4, $$5);
            return $$6 == null ? null : movePosUpOutOfSolid($$0, $$6);
         }
      });
   }

   @Nullable
   public static BlockPos movePosUpOutOfSolid(PathfinderMob $$0, BlockPos $$1) {
      $$1 = RandomPos.moveUpOutOfSolid($$1, $$0.level().getMaxY(), $$1x -> GoalUtils.isSolid($$0, $$1x));
      return !GoalUtils.isWater($$0, $$1) && !GoalUtils.hasMalus($$0, $$1) ? $$1 : null;
   }

   @Nullable
   public static BlockPos generateRandomPosTowardDirection(PathfinderMob $$0, int $$1, boolean $$2, BlockPos $$3) {
      BlockPos $$4 = RandomPos.generateRandomPosTowardDirection($$0, $$1, $$0.getRandom(), $$3);
      return !GoalUtils.isOutsideLimits($$4, $$0) && !GoalUtils.isRestricted($$2, $$0, $$4) && !GoalUtils.isNotStable($$0.getNavigation(), $$4) ? $$4 : null;
   }
}
