package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class SetRaidStatus {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create($$0 -> $$0.point((Trigger<LivingEntity>)($$0x, $$1, $$2) -> {
            if ($$0x.random.nextInt(20) != 0) {
               return false;
            } else {
               Brain<?> $$3 = $$1.getBrain();
               Raid $$4 = $$0x.getRaidAt($$1.blockPosition());
               if ($$4 != null) {
                  if ($$4.hasFirstWaveSpawned() && !$$4.isBetweenWaves()) {
                     $$3.setDefaultActivity(Activity.RAID);
                     $$3.setActiveActivityIfPossible(Activity.RAID);
                  } else {
                     $$3.setDefaultActivity(Activity.PRE_RAID);
                     $$3.setActiveActivityIfPossible(Activity.PRE_RAID);
                  }
               }

               return true;
            }
         }));
   }
}
