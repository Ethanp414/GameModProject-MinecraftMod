package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class SetHiddenState {
   private static final int HIDE_TIMEOUT = 300;

   public static BehaviorControl<LivingEntity> create(int $$0, int $$1) {
      int $$2 = $$0 * 20;
      MutableInt $$3 = new MutableInt(0);
      return BehaviorBuilder.create(
         $$3x -> $$3x.group($$3x.present(MemoryModuleType.HIDING_PLACE), $$3x.present(MemoryModuleType.HEARD_BELL_TIME))
               .apply($$3x, ($$4, $$5) -> ($$6, $$7, $$8) -> {
                     long $$9 = $$3x.get($$5);
                     boolean $$10 = $$9 + 300L <= $$8;
                     if ($$3.getValue() <= $$2 && !$$10) {
                        BlockPos $$11 = ((GlobalPos)$$3x.get($$4)).pos();
                        if ($$11.closerThan($$7.blockPosition(), (double)$$1)) {
                           $$3.increment();
                        }
      
                        return true;
                     } else {
                        $$5.erase();
                        $$4.erase();
                        $$7.getBrain().updateActivityFromSchedule($$6.getDayTime(), $$6.getGameTime());
                        $$3.setValue(0);
                        return true;
                     }
                  })
      );
   }
}
