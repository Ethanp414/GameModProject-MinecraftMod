package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Function3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BecomePassiveIfMemoryPresent {
   public static BehaviorControl<LivingEntity> create(MemoryModuleType<?> $$0, int $$1) {
      return BehaviorBuilder.create(
         $$2 -> $$2.group($$2.registered(MemoryModuleType.ATTACK_TARGET), $$2.absent(MemoryModuleType.PACIFIED), $$2.present($$0))
               .apply(
                  $$2,
                  $$2.point(
                     () -> "[BecomePassive if " + $$0 + " present]",
                     (Function3<MemoryAccessor, MemoryAccessor, MemoryAccessor, Trigger<LivingEntity>>)($$1xx, $$2x, $$3) -> ($$3x, $$4, $$5) -> {
                           $$2x.setWithExpiry(true, (long)$$1);
                           $$1xx.erase();
                           return true;
                        }
                  )
               )
      );
   }
}
