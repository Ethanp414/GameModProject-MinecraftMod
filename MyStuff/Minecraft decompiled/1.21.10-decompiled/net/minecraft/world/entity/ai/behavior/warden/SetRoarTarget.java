package net.minecraft.world.entity.ai.behavior.warden;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetRoarTarget {
   public static <E extends Warden> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> $$0) {
      return BehaviorBuilder.create(
         $$1 -> $$1.<MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$1.absent(MemoryModuleType.ROAR_TARGET),
                  $$1.absent(MemoryModuleType.ATTACK_TARGET),
                  $$1.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
               )
               .apply($$1, ($$1x, $$2, $$3) -> ($$3x, $$4, $$5) -> {
                     Optional<? extends LivingEntity> $$6 = (Optional)$$0.apply($$4);
                     if ($$6.filter($$4::canTargetEntity).isEmpty()) {
                        return false;
                     } else {
                        $$1x.set((LivingEntity)$$6.get());
                        $$3.erase();
                        return true;
                     }
                  })
      );
   }
}
