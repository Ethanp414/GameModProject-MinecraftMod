package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class HummingbirdAIWander extends Goal {
   private final EntityHummingbird fly;
   private final int rangeXZ;
   private final int rangeY;
   private final int chance;
   private final float speed;
   private Vec3 moveToPoint = null;

   public HummingbirdAIWander(EntityHummingbird fly, int rangeXZ, int rangeY, int chance, float speed) {
      this.m_7021_(EnumSet.of(Flag.MOVE));
      this.fly = fly;
      this.rangeXZ = rangeXZ;
      this.rangeY = rangeY;
      this.chance = chance;
      this.speed = speed;
   }

   public boolean m_8036_() {
      return this.fly.hummingStill > 10 && this.fly.m_217043_().m_188503_(this.chance) == 0 && !this.fly.m_21566_().m_24995_();
   }

   public void m_8041_() {
      this.moveToPoint = null;
   }

   public boolean m_8045_() {
      return this.moveToPoint != null && this.fly.m_20238_(this.moveToPoint) > 0.85;
   }

   public void m_8056_() {
      this.moveToPoint = this.getRandomLocation();
      if (this.moveToPoint != null) {
         this.fly.m_21566_().m_6849_(this.moveToPoint.f_82479_, this.moveToPoint.f_82480_, this.moveToPoint.f_82481_, this.speed);
      }
   }

   public void m_8037_() {
      if (this.moveToPoint != null) {
         this.fly.m_21566_().m_6849_(this.moveToPoint.f_82479_, this.moveToPoint.f_82480_, this.moveToPoint.f_82481_, this.speed);
      }
   }

   @Nullable
   private Vec3 getRandomLocation() {
      RandomSource random = this.fly.m_217043_();
      BlockPos blockpos = null;
      BlockPos origin = this.fly.getFeederPos() == null ? this.fly.m_20183_() : this.fly.getFeederPos();

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = origin.m_7918_(random.m_188503_(this.rangeXZ) - this.rangeXZ / 2, 1, random.m_188503_(this.rangeXZ) - this.rangeXZ / 2);

         while (this.fly.m_9236_().m_46859_(blockpos1) && blockpos1.m_123342_() > 0) {
            blockpos1 = blockpos1.m_7495_();
         }

         blockpos1 = blockpos1.m_6630_(1 + random.m_188503_(3));
         if (this.fly.m_9236_().m_46859_(blockpos1.m_7495_())
            && this.fly.canBlockBeSeen(blockpos1)
            && this.fly.m_9236_().m_46859_(blockpos1)
            && !this.fly.m_9236_().m_46859_(blockpos1.m_6625_(2))) {
            blockpos = blockpos1;
         }
      }

      return blockpos == null ? null : new Vec3(blockpos.m_123341_() + 0.5, blockpos.m_123342_() + 0.5, blockpos.m_123343_() + 0.5);
   }

   public boolean canBlockPosBeSeen(BlockPos pos) {
      double x = pos.m_123341_() + 0.5F;
      double y = pos.m_123342_() + 0.5F;
      double z = pos.m_123343_() + 0.5F;
      HitResult result = this.fly
         .m_9236_()
         .m_45547_(
            new ClipContext(
               new Vec3(this.fly.m_20185_(), this.fly.m_20186_() + this.fly.m_20192_(), this.fly.m_20189_()),
               new Vec3(x, y, z),
               Block.COLLIDER,
               Fluid.NONE,
               this.fly
            )
         );
      double dist = result.m_82450_().m_82531_(x, y, z);
      return dist <= 1.0 || result.m_6662_() == Type.MISS;
   }
}
