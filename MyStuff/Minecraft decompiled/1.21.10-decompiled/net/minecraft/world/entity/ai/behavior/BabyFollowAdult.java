package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult {
   public static OneShot<LivingEntity> create(UniformInt $$0, float $$1) {
      return create($$0, $$1x -> $$1, MemoryModuleType.NEAREST_VISIBLE_ADULT, false);
   }

   public static OneShot<LivingEntity> create(UniformInt $$0, Function<LivingEntity, Float> $$1, MemoryModuleType<? extends LivingEntity> $$2, boolean $$3) {
      return BehaviorBuilder.create(
         $$4 -> $$4.<MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$4.present($$2), $$4.registered(MemoryModuleType.LOOK_TARGET), $$4.absent(MemoryModuleType.WALK_TARGET)
               )
               .apply($$4, ($$4x, $$5, $$6) -> ($$7, $$8, $$9) -> {
                     if (!$$8.isBaby()) {
                        return false;
                     } else {
                        LivingEntity $$10 = $$4.get($$4x);
                        if ($$8.closerThan($$10, (double)($$0.getMaxValue() + 1)) && !$$8.closerThan($$10, (double)$$0.getMinValue())) {
                           WalkTarget $$11 = new WalkTarget(new EntityTracker($$10, $$3, $$3), $$1.apply($$8), $$0.getMinValue() - 1);
                           $$5.set(new EntityTracker($$10, true, $$3));
                           $$6.set($$11);
                           return true;
                        } else {
                           return false;
                        }
                     }
                  })
      );
   }
}
