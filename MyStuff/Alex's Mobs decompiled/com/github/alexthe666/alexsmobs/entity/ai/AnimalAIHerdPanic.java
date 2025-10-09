package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.IHerdPanic;
import com.google.common.base.Predicate;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AnimalAIHerdPanic extends Goal {
   protected final PathfinderMob creature;
   protected final double speed;
   protected final Predicate<? super PathfinderMob> targetEntitySelector;
   protected double randPosX;
   protected double randPosY;
   protected double randPosZ;
   protected boolean running;

   public AnimalAIHerdPanic(final PathfinderMob creature, double speedIn) {
      this.creature = creature;
      this.speed = speedIn;
      this.m_7021_(EnumSet.of(Flag.MOVE));
      this.targetEntitySelector = new Predicate<PathfinderMob>() {
         public boolean apply(@Nullable PathfinderMob animal) {
            return animal instanceof IHerdPanic && animal.m_6095_() == creature.m_6095_() ? ((IHerdPanic)animal).canPanic() : false;
         }
      };
   }

   public boolean m_8036_() {
      if (this.creature.m_21188_() != null && this.creature.m_21188_().m_6084_()) {
         if (this.creature.m_6060_() && !this.creature.m_5825_()) {
            BlockPos blockpos = this.getRandPos(this.creature.m_9236_(), this.creature, 5, 4);
            if (blockpos != null) {
               this.randPosX = blockpos.m_123341_();
               this.randPosY = blockpos.m_123342_();
               this.randPosZ = blockpos.m_123343_();
               return true;
            }
         }

         if (this.creature.m_21188_() != null && this.creature instanceof IHerdPanic && ((IHerdPanic)this.creature).canPanic()) {
            for (PathfinderMob creatureEntity : this.creature.m_9236_().m_6443_(this.creature.getClass(), this.getTargetableArea(), this.targetEntitySelector)) {
               creatureEntity.m_6703_(this.creature.m_21188_());
            }

            return this.findRandomPositionFrom(this.creature.m_21188_());
         } else {
            return this.findRandomPosition();
         }
      } else {
         return false;
      }
   }

   private boolean findRandomPositionFrom(LivingEntity revengeTarget) {
      Vec3 vector3d;
      if (this.creature instanceof EntityLaviathan) {
         vector3d = DefaultRandomPos.m_148407_(this.creature, 32, 16, revengeTarget.m_20182_());
      } else {
         vector3d = LandRandomPos.m_148521_(this.creature, 16, 7, revengeTarget.m_20182_());
      }

      if (vector3d == null) {
         return false;
      } else {
         this.randPosX = vector3d.f_82479_;
         this.randPosY = vector3d.f_82480_;
         this.randPosZ = vector3d.f_82481_;
         return true;
      }
   }

   protected AABB getTargetableArea() {
      Vec3 renderCenter = new Vec3(this.creature.m_20185_() + 0.5, this.creature.m_20186_() + 0.5, this.creature.m_20189_() + 0.5);
      double searchRadius = 15.0;
      AABB aabb = new AABB(-searchRadius, -searchRadius, -searchRadius, searchRadius, searchRadius, searchRadius);
      return aabb.m_82383_(renderCenter);
   }

   protected boolean findRandomPosition() {
      Vec3 vector3d = LandRandomPos.m_148488_(this.creature, 5, 4);
      if (vector3d == null) {
         return false;
      } else {
         this.randPosX = vector3d.f_82479_;
         this.randPosY = vector3d.f_82480_;
         this.randPosZ = vector3d.f_82481_;
         return true;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   public void m_8056_() {
      if (this.creature instanceof IHerdPanic) {
         ((IHerdPanic)this.creature).onPanic();
      }

      this.creature.m_21573_().m_26519_(this.randPosX, this.randPosY, this.randPosZ, this.speed);
      this.running = true;
   }

   public void m_8041_() {
      this.running = false;
   }

   public boolean m_8045_() {
      return !this.creature.m_21573_().m_26571_();
   }

   @Nullable
   protected BlockPos getRandPos(BlockGetter worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
      BlockPos blockpos = entityIn.m_20183_();
      int i = blockpos.m_123341_();
      int j = blockpos.m_123342_();
      int k = blockpos.m_123343_();
      float f = horizontalRange * horizontalRange * verticalRange * 2;
      BlockPos blockpos1 = null;
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int l = i - horizontalRange; l <= i + horizontalRange; l++) {
         for (int i1 = j - verticalRange; i1 <= j + verticalRange; i1++) {
            for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; j1++) {
               blockpos$mutable.m_122178_(l, i1, j1);
               if (worldIn.m_6425_(blockpos$mutable).m_205070_(FluidTags.f_13131_)) {
                  float f1 = (l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k);
                  if (f1 < f) {
                     f = f1;
                     blockpos1 = new BlockPos(blockpos$mutable);
                  }
               }
            }
         }
      }

      return blockpos1;
   }
}
