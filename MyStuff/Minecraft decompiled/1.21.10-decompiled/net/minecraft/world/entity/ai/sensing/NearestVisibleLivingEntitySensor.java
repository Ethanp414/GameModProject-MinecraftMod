package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
   protected abstract boolean isMatchingEntity(ServerLevel var1, LivingEntity var2, LivingEntity var3);

   protected abstract MemoryModuleType<LivingEntity> getMemory();

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(this.getMemory());
   }

   @Override
   protected void doTick(ServerLevel $$0, LivingEntity $$1) {
      $$1.getBrain().setMemory(this.getMemory(), this.getNearestEntity($$0, $$1));
   }

   private Optional<LivingEntity> getNearestEntity(ServerLevel $$0, LivingEntity $$1) {
      return this.getVisibleEntities($$1).flatMap($$2 -> $$2.findClosest($$2x -> this.isMatchingEntity($$0, $$1, $$2x)));
   }

   protected Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity $$0) {
      return $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}
