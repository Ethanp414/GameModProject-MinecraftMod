package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class Mount {
   private static final int CLOSE_ENOUGH_TO_START_RIDING_DIST = 1;

   public static BehaviorControl<LivingEntity> create(float $$0) {
      return BehaviorBuilder.create(
         $$1 -> $$1.<MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$1.registered(MemoryModuleType.LOOK_TARGET), $$1.absent(MemoryModuleType.WALK_TARGET), $$1.present(MemoryModuleType.RIDE_TARGET)
               )
               .apply($$1, ($$2, $$3, $$4) -> ($$5, $$6, $$7) -> {
                     if ($$6.isPassenger()) {
                        return false;
                     } else {
                        Entity $$8 = $$1.get($$4);
                        if ($$8.closerThan($$6, 1.0)) {
                           $$6.startRiding($$8);
                        } else {
                           $$2.set(new EntityTracker($$8, true));
                           $$3.set(new WalkTarget(new EntityTracker($$8, false), $$0, 1));
                        }
      
                        return true;
                     }
                  })
      );
   }
}
