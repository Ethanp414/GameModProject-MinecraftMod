package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom {
   public static BehaviorControl<PathfinderMob> pos(MemoryModuleType<BlockPos> $$0, float $$1, int $$2, boolean $$3) {
      return create($$0, $$1, $$2, $$3, Vec3::atBottomCenterOf);
   }

   public static OneShot<PathfinderMob> entity(MemoryModuleType<? extends Entity> $$0, float $$1, int $$2, boolean $$3) {
      return create($$0, $$1, $$2, $$3, Entity::position);
   }

   private static <T> OneShot<PathfinderMob> create(MemoryModuleType<T> $$0, float $$1, int $$2, boolean $$3, Function<T, Vec3> $$4) {
      return BehaviorBuilder.create(
         $$5 -> $$5.group($$5.registered(MemoryModuleType.WALK_TARGET), $$5.present($$0)).apply($$5, ($$5x, $$6) -> ($$7, $$8, $$9) -> {
                  Optional<WalkTarget> $$10 = $$5.tryGet($$5x);
                  if ($$10.isPresent() && !$$3) {
                     return false;
                  } else {
                     Vec3 $$11 = $$8.position();
                     Vec3 $$12 = (Vec3)$$4.apply($$5.get($$6));
                     if (!$$11.closerThan($$12, (double)$$2)) {
                        return false;
                     } else {
                        if ($$10.isPresent() && ((WalkTarget)$$10.get()).getSpeedModifier() == $$1) {
                           Vec3 $$13 = ((WalkTarget)$$10.get()).getTarget().currentPosition().subtract($$11);
                           Vec3 $$14 = $$12.subtract($$11);
                           if ($$13.dot($$14) < 0.0) {
                              return false;
                           }
                        }
   
                        for(int $$15 = 0; $$15 < 10; ++$$15) {
                           Vec3 $$16 = LandRandomPos.getPosAway($$8, 16, 7, $$12);
                           if ($$16 != null) {
                              $$5x.set(new WalkTarget($$16, $$1, 0));
                              break;
                           }
                        }
   
                        return true;
                     }
                  }
               })
      );
   }
}
