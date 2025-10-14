package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create($$0 -> $$0.group($$0.present(MemoryModuleType.HEARD_BELL_TIME)).apply($$0, $$0x -> ($$0xx, $$1, $$2) -> {
               Raid $$3 = $$0xx.getRaidAt($$1.blockPosition());
               if ($$3 == null) {
                  $$1.getBrain().setActiveActivityIfPossible(Activity.HIDE);
               }

               return true;
            }));
   }
}
