package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovementControllerCustomCollisions extends MoveControl {
   public MovementControllerCustomCollisions(Mob mob) {
      super(mob);
   }

   public void m_8126_() {
      if (this.f_24981_ == Operation.STRAFE) {
         float f = (float)this.f_24974_.m_21133_(Attributes.f_22279_);
         float f1 = (float)this.f_24978_ * f;
         float f2 = this.f_24979_;
         float f3 = this.f_24980_;
         float f4 = Mth.m_14116_(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 *= f4;
         f3 *= f4;
         float f5 = Mth.m_14031_(this.f_24974_.m_146908_() * (float) (Math.PI / 180.0));
         float f6 = Mth.m_14089_(this.f_24974_.m_146908_() * (float) (Math.PI / 180.0));
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         if (!this.m_24996_(f7, f8)) {
            this.f_24979_ = 1.0F;
            this.f_24980_ = 0.0F;
         }

         this.f_24974_.m_7910_(f1);
         this.f_24974_.m_21564_(this.f_24979_);
         this.f_24974_.m_21570_(this.f_24980_);
         this.f_24981_ = Operation.WAIT;
      } else if (this.f_24981_ == Operation.MOVE_TO) {
         this.f_24981_ = Operation.WAIT;
         double d0 = this.f_24975_ - this.f_24974_.m_20185_();
         double d1 = this.f_24977_ - this.f_24974_.m_20189_();
         double d2 = this.f_24976_ - this.f_24974_.m_20186_();
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < 2.5000003E-7F) {
            this.f_24974_.m_21564_(0.0F);
            return;
         }

         float f9 = (float)(Mth.m_14136_(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
         this.f_24974_.m_146922_(this.m_24991_(this.f_24974_.m_146908_(), f9, 90.0F));
         this.f_24974_.m_7910_((float)(this.f_24978_ * this.f_24974_.m_21133_(Attributes.f_22279_)));
         BlockPos blockpos = this.f_24974_.m_20183_();
         BlockState blockstate = this.f_24974_.m_9236_().m_8055_(blockpos);
         VoxelShape voxelshape = blockstate.m_60816_(this.f_24974_.m_9236_(), blockpos);
         if ((!(this.f_24974_ instanceof ICustomCollisions) || !((ICustomCollisions)this.f_24974_).canPassThrough(blockpos, blockstate, voxelshape))
            && (
               d2 > this.f_24974_.getStepHeight() && d0 * d0 + d1 * d1 < Math.max(1.0F, this.f_24974_.m_20205_())
                  || !voxelshape.m_83281_()
                     && this.f_24974_.m_20186_() < voxelshape.m_83297_(Axis.Y) + blockpos.m_123342_()
                     && !blockstate.m_204336_(BlockTags.f_13103_)
                     && !blockstate.m_204336_(BlockTags.f_13039_)
            )) {
            this.f_24974_.m_21569_().m_24901_();
            this.f_24981_ = Operation.JUMPING;
         }
      } else if (this.f_24981_ == Operation.JUMPING) {
         this.f_24974_.m_7910_((float)(this.f_24978_ * this.f_24974_.m_21133_(Attributes.f_22279_)));
         if (this.f_24974_.m_20096_()) {
            this.f_24981_ = Operation.WAIT;
         }
      } else {
         this.f_24974_.m_21564_(0.0F);
      }
   }

   private boolean m_24996_(float p_234024_1_, float p_234024_2_) {
      PathNavigation pathnavigator = this.f_24974_.m_21573_();
      if (pathnavigator != null) {
         NodeEvaluator nodeprocessor = pathnavigator.m_26575_();
         if (nodeprocessor != null
            && nodeprocessor.m_8086_(
                  this.f_24974_.m_9236_(),
                  Mth.m_14107_(this.f_24974_.m_20185_() + p_234024_1_),
                  Mth.m_14107_(this.f_24974_.m_20186_()),
                  Mth.m_14107_(this.f_24974_.m_20189_() + p_234024_2_)
               )
               != BlockPathTypes.WALKABLE) {
            return false;
         }
      }

      return true;
   }
}
