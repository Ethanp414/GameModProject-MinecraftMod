package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PathfindingContext {
   private final CollisionGetter level;
   @Nullable
   private final PathTypeCache cache;
   private final BlockPos mobPosition;
   private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

   public PathfindingContext(CollisionGetter $$0, Mob $$1) {
      this.level = $$0;
      Level var4 = $$1.level();
      if (var4 instanceof ServerLevel $$2) {
         this.cache = $$2.getPathTypeCache();
      } else {
         this.cache = null;
      }

      this.mobPosition = $$1.blockPosition();
   }

   public PathType getPathTypeFromState(int $$0, int $$1, int $$2) {
      BlockPos $$3 = this.mutablePos.set($$0, $$1, $$2);
      return this.cache == null ? WalkNodeEvaluator.getPathTypeFromState(this.level, $$3) : this.cache.getOrCompute(this.level, $$3);
   }

   public BlockState getBlockState(BlockPos $$0) {
      return this.level.getBlockState($$0);
   }

   public CollisionGetter level() {
      return this.level;
   }

   public BlockPos mobPosition() {
      return this.mobPosition;
   }
}
