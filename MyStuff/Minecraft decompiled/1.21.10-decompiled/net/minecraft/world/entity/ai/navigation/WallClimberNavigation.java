package net.minecraft.world.entity.ai.navigation;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class WallClimberNavigation extends GroundPathNavigation {
   @Nullable
   private BlockPos pathToPosition;

   public WallClimberNavigation(Mob $$0, Level $$1) {
      super($$0, $$1);
   }

   @Override
   public Path createPath(BlockPos $$0, int $$1) {
      this.pathToPosition = $$0;
      return super.createPath($$0, $$1);
   }

   @Override
   public Path createPath(Entity $$0, int $$1) {
      this.pathToPosition = $$0.blockPosition();
      return super.createPath($$0, $$1);
   }

   @Override
   public boolean moveTo(Entity $$0, double $$1) {
      Path $$2 = this.createPath($$0, 0);
      if ($$2 != null) {
         return this.moveTo($$2, $$1);
      } else {
         this.pathToPosition = $$0.blockPosition();
         this.speedModifier = $$1;
         return true;
      }
   }

   @Override
   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else {
         if (this.pathToPosition != null) {
            if (!this.pathToPosition.closerToCenterThan(this.mob.position(), (double)this.mob.getBbWidth())
               && (
                  !(this.mob.getY() > (double)this.pathToPosition.getY())
                     || !BlockPos.containing((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ())
                        .closerToCenterThan(this.mob.position(), (double)this.mob.getBbWidth())
               )) {
               this.mob
                  .getMoveControl()
                  .setWantedPosition(
                     (double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier
                  );
            } else {
               this.pathToPosition = null;
            }
         }
      }
   }
}
