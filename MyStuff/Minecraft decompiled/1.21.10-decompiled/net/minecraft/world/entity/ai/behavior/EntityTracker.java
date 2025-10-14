package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3;

public class EntityTracker implements PositionTracker {
   private final Entity entity;
   private final boolean trackEyeHeight;
   private final boolean targetEyeHeight;

   public EntityTracker(Entity $$0, boolean $$1) {
      this($$0, $$1, false);
   }

   public EntityTracker(Entity $$0, boolean $$1, boolean $$2) {
      this.entity = $$0;
      this.trackEyeHeight = $$1;
      this.targetEyeHeight = $$2;
   }

   @Override
   public Vec3 currentPosition() {
      return this.trackEyeHeight ? this.entity.position().add(0.0, (double)this.entity.getEyeHeight(), 0.0) : this.entity.position();
   }

   @Override
   public BlockPos currentBlockPosition() {
      return this.targetEyeHeight ? BlockPos.containing(this.entity.getEyePosition()) : this.entity.blockPosition();
   }

   @Override
   public boolean isVisibleBy(LivingEntity $$0) {
      Entity $$3 = this.entity;
      if ($$3 instanceof LivingEntity $$1) {
         if (!$$1.isAlive()) {
            return false;
         } else {
            Optional<NearestVisibleLivingEntities> $$3x = $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
            return $$3x.isPresent() && ((NearestVisibleLivingEntities)$$3x.get()).contains($$1);
         }
      } else {
         return true;
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   public String toString() {
      return "EntityTracker for " + this.entity;
   }
}
