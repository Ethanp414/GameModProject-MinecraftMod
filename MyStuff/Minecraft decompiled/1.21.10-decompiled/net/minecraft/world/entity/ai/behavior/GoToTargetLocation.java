package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class GoToTargetLocation {
   private static BlockPos getNearbyPos(Mob $$0, BlockPos $$1) {
      RandomSource $$2 = $$0.level().random;
      return $$1.offset(getRandomOffset($$2), 0, getRandomOffset($$2));
   }

   private static int getRandomOffset(RandomSource $$0) {
      return $$0.nextInt(3) - 1;
   }

   public static <E extends Mob> OneShot<E> create(MemoryModuleType<BlockPos> $$0, int $$1, float $$2) {
      return BehaviorBuilder.create(
         $$3 -> $$3.<MemoryAccessor, MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$3.present($$0),
                  $$3.absent(MemoryModuleType.ATTACK_TARGET),
                  $$3.absent(MemoryModuleType.WALK_TARGET),
                  $$3.registered(MemoryModuleType.LOOK_TARGET)
               )
               .apply($$3, ($$3x, $$4, $$5, $$6) -> ($$4x, $$5x, $$6x) -> {
                     BlockPos $$7 = $$3.get($$3x);
                     boolean $$8 = $$7.closerThan($$5x.blockPosition(), (double)$$1);
                     if (!$$8) {
                        BehaviorUtils.setWalkAndLookTargetMemories($$5x, getNearbyPos($$5x, $$7), $$2, $$1);
                     }
      
                     return true;
                  })
      );
   }
}
