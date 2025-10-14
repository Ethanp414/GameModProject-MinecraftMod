package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStroll {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;
   private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

   public static OneShot<PathfinderMob> stroll(float $$0) {
      return stroll($$0, true);
   }

   public static OneShot<PathfinderMob> stroll(float $$0, boolean $$1) {
      return strollFlyOrSwim($$0, $$0x -> LandRandomPos.getPos($$0x, 10, 7), $$1 ? $$0x -> true : $$0x -> !$$0x.isInWater());
   }

   public static BehaviorControl<PathfinderMob> stroll(float $$0, int $$1, int $$2) {
      return strollFlyOrSwim($$0, $$2x -> LandRandomPos.getPos($$2x, $$1, $$2), $$0x -> true);
   }

   public static BehaviorControl<PathfinderMob> fly(float $$0) {
      return strollFlyOrSwim($$0, $$0x -> getTargetFlyPos($$0x, 10, 7), $$0x -> true);
   }

   public static BehaviorControl<PathfinderMob> swim(float $$0) {
      return strollFlyOrSwim($$0, RandomStroll::getTargetSwimPos, Entity::isInWater);
   }

   private static OneShot<PathfinderMob> strollFlyOrSwim(float $$0, Function<PathfinderMob, Vec3> $$1, Predicate<PathfinderMob> $$2) {
      return BehaviorBuilder.create($$3 -> $$3.group($$3.absent(MemoryModuleType.WALK_TARGET)).apply($$3, $$3x -> ($$4, $$5, $$6) -> {
               if (!$$2.test($$5)) {
                  return false;
               } else {
                  Optional<Vec3> $$7 = Optional.ofNullable((Vec3)$$1.apply($$5));
                  $$3x.setOrErase($$7.map($$1xxxx -> new WalkTarget($$1xxxx, $$0, 0)));
                  return true;
               }
            }));
   }

   @Nullable
   private static Vec3 getTargetSwimPos(PathfinderMob $$0) {
      Vec3 $$1 = null;
      Vec3 $$2 = null;

      for(int[] $$3 : SWIM_XY_DISTANCE_TIERS) {
         if ($$1 == null) {
            $$2 = BehaviorUtils.getRandomSwimmablePos($$0, $$3[0], $$3[1]);
         } else {
            $$2 = $$0.position().add($$0.position().vectorTo($$1).normalize().multiply((double)$$3[0], (double)$$3[1], (double)$$3[0]));
         }

         if ($$2 == null || $$0.level().getFluidState(BlockPos.containing($$2)).isEmpty()) {
            return $$1;
         }

         $$1 = $$2;
      }

      return $$2;
   }

   @Nullable
   private static Vec3 getTargetFlyPos(PathfinderMob $$0, int $$1, int $$2) {
      Vec3 $$3 = $$0.getViewVector(0.0F);
      return AirAndWaterRandomPos.getPos($$0, $$1, $$2, -2, $$3.x, $$3.z, (float) (Math.PI / 2));
   }
}
