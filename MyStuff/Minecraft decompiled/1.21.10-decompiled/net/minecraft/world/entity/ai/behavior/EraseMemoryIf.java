package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class EraseMemoryIf {
   public static <E extends LivingEntity> BehaviorControl<E> create(Predicate<E> $$0, MemoryModuleType<?> $$1) {
      return BehaviorBuilder.create($$2 -> $$2.group($$2.present($$1)).apply($$2, $$1xx -> ($$2x, $$3, $$4) -> {
               if ($$0.test($$3)) {
                  $$1xx.erase();
                  return true;
               } else {
                  return false;
               }
            }));
   }
}
