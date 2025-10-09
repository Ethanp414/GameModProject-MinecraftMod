package com.github.alexthe666.alexsmobs.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class SmartClimbPathNavigator extends GroundPathNavigation {
   @Nullable
   private BlockPos pathToPosition;

   public SmartClimbPathNavigator(Mob entitylivingIn, Level worldIn) {
      super(entitylivingIn, worldIn);
   }

   public Path m_7864_(BlockPos p_26589_, int p_26590_) {
      this.pathToPosition = p_26589_;
      return super.m_7864_(p_26589_, p_26590_);
   }

   public Path m_6570_(Entity p_26586_, int p_26587_) {
      this.pathToPosition = p_26586_.m_20183_();
      return super.m_6570_(p_26586_, p_26587_);
   }

   public boolean m_5624_(Entity p_26583_, double p_26584_) {
      Path path = this.m_6570_(p_26583_, 0);
      if (path != null) {
         return this.m_26536_(path, p_26584_);
      } else {
         this.pathToPosition = p_26583_.m_20183_();
         this.f_26497_ = p_26584_;
         return true;
      }
   }

   public void m_7638_() {
      super.m_7638_();
      if (!this.m_26571_()) {
         super.m_7638_();
      } else if (this.pathToPosition != null) {
         Vec3 xzOff = new Vec3(
            this.pathToPosition.m_123341_() + 0.5F - this.f_26494_.m_20185_(), 0.0, this.pathToPosition.m_123343_() + 0.5F - this.f_26494_.m_20189_()
         );
         double dist = xzOff.m_82553_();
         if (!(dist < this.f_26494_.m_20205_()) && !(this.f_26494_.m_20186_() > this.pathToPosition.m_123342_())) {
            this.f_26494_.m_21566_().m_6849_(this.pathToPosition.m_123341_(), this.f_26494_.m_20186_(), this.pathToPosition.m_123343_(), this.f_26497_);
         } else {
            this.pathToPosition = null;
         }
      }
   }

   protected void m_6481_(Vec3 vec) {
      if (this.f_26498_ - this.f_26499_ > 40) {
         if (vec.m_82557_(new Vec3(this.f_26500_.f_82479_, vec.f_82480_, this.f_26500_.f_82481_)) < 2.25) {
            this.m_26573_();
         }

         this.f_26499_ = this.f_26498_;
         this.f_26500_ = vec;
      }
   }
}
