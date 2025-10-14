package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

public class FollowTemptation extends Behavior<PathfinderMob> {
   public static final int TEMPTATION_COOLDOWN = 100;
   public static final double DEFAULT_CLOSE_ENOUGH_DIST = 2.5;
   public static final double BACKED_UP_CLOSE_ENOUGH_DIST = 3.5;
   private final Function<LivingEntity, Float> speedModifier;
   private final Function<LivingEntity, Double> closeEnoughDistance;
   private final boolean lookInTheEyes;

   public FollowTemptation(Function<LivingEntity, Float> $$0) {
      this($$0, $$0x -> 2.5);
   }

   public FollowTemptation(Function<LivingEntity, Float> $$0, Function<LivingEntity, Double> $$1) {
      this($$0, $$1, false);
   }

   public FollowTemptation(Function<LivingEntity, Float> $$0, Function<LivingEntity, Double> $$1, boolean $$2) {
      super(Util.make(() -> {
         Builder<MemoryModuleType<?>, MemoryStatus> $$0xx = ImmutableMap.builder();
         $$0xx.put(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED);
         $$0xx.put(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED);
         $$0xx.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
         $$0xx.put(MemoryModuleType.IS_TEMPTED, MemoryStatus.VALUE_ABSENT);
         $$0xx.put(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_PRESENT);
         $$0xx.put(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT);
         $$0xx.put(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT);
         return $$0xx.build();
      }));
      this.speedModifier = $$0;
      this.closeEnoughDistance = $$1;
      this.lookInTheEyes = $$2;
   }

   protected float getSpeedModifier(PathfinderMob $$0) {
      return this.speedModifier.apply($$0);
   }

   private Optional<Player> getTemptingPlayer(PathfinderMob $$0) {
      return $$0.getBrain().getMemory(MemoryModuleType.TEMPTING_PLAYER);
   }

   @Override
   protected boolean timedOut(long $$0) {
      return false;
   }

   protected boolean canStillUse(ServerLevel $$0, PathfinderMob $$1, long $$2) {
      return this.getTemptingPlayer($$1).isPresent()
         && !$$1.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET)
         && !$$1.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
   }

   protected void start(ServerLevel $$0, PathfinderMob $$1, long $$2) {
      $$1.getBrain().setMemory(MemoryModuleType.IS_TEMPTED, true);
   }

   protected void stop(ServerLevel $$0, PathfinderMob $$1, long $$2) {
      Brain<?> $$3 = $$1.getBrain();
      $$3.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 100);
      $$3.eraseMemory(MemoryModuleType.IS_TEMPTED);
      $$3.eraseMemory(MemoryModuleType.WALK_TARGET);
      $$3.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel $$0, PathfinderMob $$1, long $$2) {
      Player $$3 = (Player)this.getTemptingPlayer($$1).get();
      Brain<?> $$4 = $$1.getBrain();
      $$4.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker($$3, true));
      double $$5 = this.closeEnoughDistance.apply($$1);
      if ($$1.distanceToSqr($$3) < Mth.square($$5)) {
         $$4.eraseMemory(MemoryModuleType.WALK_TARGET);
      } else {
         $$4.setMemory(
            MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker($$3, this.lookInTheEyes, this.lookInTheEyes), this.getSpeedModifier($$1), 2)
         );
      }
   }
}
