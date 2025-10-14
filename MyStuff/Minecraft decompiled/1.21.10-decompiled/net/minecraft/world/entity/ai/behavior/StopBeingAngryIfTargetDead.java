package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create(
         $$0 -> $$0.group($$0.present(MemoryModuleType.ANGRY_AT))
               .apply(
                  $$0,
                  $$1 -> ($$2, $$3, $$4) -> {
                        Optional.ofNullable($$2.getEntity($$0.get($$1)))
                           .map($$0xxx -> $$0xxx instanceof LivingEntity $$1xxx ? $$1xxx : null)
                           .filter(LivingEntity::isDeadOrDying)
                           .filter($$1xx -> $$1xx.getType() != EntityType.PLAYER || $$2.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))
                           .ifPresent($$1xx -> $$1.erase());
                        return true;
                     }
               )
      );
   }
}
