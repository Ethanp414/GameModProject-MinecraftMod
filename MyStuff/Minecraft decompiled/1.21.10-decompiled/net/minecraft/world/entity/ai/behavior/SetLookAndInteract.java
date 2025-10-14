package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetLookAndInteract {
   public static BehaviorControl<LivingEntity> create(EntityType<?> $$0, int $$1) {
      int $$2 = $$1 * $$1;
      return BehaviorBuilder.create(
         $$2x -> $$2x.<MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$2x.registered(MemoryModuleType.LOOK_TARGET),
                  $$2x.absent(MemoryModuleType.INTERACTION_TARGET),
                  $$2x.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
               )
               .apply(
                  $$2x,
                  ($$3, $$4, $$5) -> ($$6, $$7, $$8) -> {
                        Optional<LivingEntity> $$9 = $$2x.<NearestVisibleLivingEntities>get($$5)
                           .findClosest($$3xx -> $$3xx.distanceToSqr($$7) <= (double)$$2 && $$0.equals($$3xx.getType()));
                        if ($$9.isEmpty()) {
                           return false;
                        } else {
                           LivingEntity $$10 = (LivingEntity)$$9.get();
                           $$4.set($$10);
                           $$3.set(new EntityTracker($$10, true));
                           return true;
                        }
                     }
               )
      );
   }
}
