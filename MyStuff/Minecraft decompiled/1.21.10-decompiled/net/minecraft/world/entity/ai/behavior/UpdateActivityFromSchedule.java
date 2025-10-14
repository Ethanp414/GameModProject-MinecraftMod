package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create($$0 -> $$0.point((Trigger<LivingEntity>)($$0x, $$1, $$2) -> {
            $$1.getBrain().updateActivityFromSchedule($$0x.getDayTime(), $$0x.getGameTime());
            return true;
         }));
   }
}
