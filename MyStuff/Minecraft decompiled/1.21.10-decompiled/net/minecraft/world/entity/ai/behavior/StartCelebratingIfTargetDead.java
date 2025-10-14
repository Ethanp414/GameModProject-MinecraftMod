package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StartCelebratingIfTargetDead {
   public static BehaviorControl<LivingEntity> create(int $$0, BiPredicate<LivingEntity, LivingEntity> $$1) {
      return BehaviorBuilder.create(
         $$2 -> $$2.<MemoryAccessor, MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$2.present(MemoryModuleType.ATTACK_TARGET),
                  $$2.registered(MemoryModuleType.ANGRY_AT),
                  $$2.absent(MemoryModuleType.CELEBRATE_LOCATION),
                  $$2.registered(MemoryModuleType.DANCING)
               )
               .apply($$2, ($$3, $$4, $$5, $$6) -> ($$7, $$8, $$9) -> {
                     LivingEntity $$10 = $$2.get($$3);
                     if (!$$10.isDeadOrDying()) {
                        return false;
                     } else {
                        if ($$1.test($$8, $$10)) {
                           $$6.setWithExpiry(true, (long)$$0);
                        }
      
                        $$5.setWithExpiry($$10.blockPosition(), (long)$$0);
                        if ($$10.getType() != EntityType.PLAYER || $$7.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
                           $$3.erase();
                           $$4.erase();
                        }
      
                        return true;
                     }
                  })
      );
   }
}
