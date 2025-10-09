package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class FroststalkerAIMelee extends Goal {
   private final EntityFroststalker froststalker;
   private boolean willJump = false;
   private boolean hasJumped = false;
   private boolean clockwise = false;
   private int pursuitTime = 0;
   private int maxPursuitTime = 0;
   private BlockPos pursuitPos = null;
   private int startingOrbit = 0;

   public FroststalkerAIMelee(EntityFroststalker froststalker) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.froststalker = froststalker;
   }

   public boolean m_8036_() {
      if (this.froststalker.m_5448_() != null && this.froststalker.m_5448_().m_6084_()) {
         return !this.froststalker.isValidLeader(this.froststalker.m_5448_())
            ? !this.froststalker.isFleeingFire()
            : this.froststalker.m_21188_() != null && this.froststalker.m_21188_().equals(this.froststalker.m_5448_());
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      LivingEntity target = this.froststalker.m_5448_();
      return target != null && !this.froststalker.isValidLeader(target);
   }

   public void m_8056_() {
      this.willJump = this.froststalker.m_217043_().m_188503_(2) == 0;
      this.hasJumped = false;
      this.clockwise = this.froststalker.m_217043_().m_188499_();
      this.pursuitPos = null;
      this.pursuitTime = 0;
      this.maxPursuitTime = 40 + this.froststalker.m_217043_().m_188503_(40);
      this.startingOrbit = this.froststalker.m_217043_().m_188503_(360);
      this.froststalker.frostJump();
   }

   public void m_8037_() {
      this.froststalker.setBipedal(true);
      this.froststalker.standFor(20);
      LivingEntity target = this.froststalker.m_5448_();
      boolean flag = false;
      if ((this.hasJumped || this.froststalker.isTackling()) && this.froststalker.m_20096_()) {
         this.hasJumped = false;
         this.willJump = false;
         this.froststalker.setTackling(false);
      }

      if (target != null && target.m_6084_()) {
         if (this.pursuitTime < this.maxPursuitTime) {
            this.pursuitTime++;
            this.pursuitPos = this.getBlockNearTarget(target);
            float extraSpeed = 0.2F * Math.max(5.0F - this.froststalker.m_20270_(target), 0.0F);
            if (this.pursuitPos != null) {
               this.froststalker.m_21573_().m_26519_(this.pursuitPos.m_123341_(), this.pursuitPos.m_123342_(), this.pursuitPos.m_123343_(), 1.0F + extraSpeed);
            } else {
               this.froststalker.m_21573_().m_5624_(target, 1.0);
            }
         } else if (this.willJump && this.pursuitTime == this.maxPursuitTime) {
            this.froststalker.m_21391_(target, 180.0F, 10.0F);
            if (this.froststalker.m_20270_(target) > 10.0F) {
               this.froststalker.m_21573_().m_5624_(target, 1.0);
            } else if (this.froststalker.m_20096_() && this.froststalker.m_142582_(target)) {
               this.froststalker.setTackling(true);
               this.hasJumped = true;
               Vec3 vector3d = this.froststalker.m_20184_();
               Vec3 vector3d1 = new Vec3(target.m_20185_() - this.froststalker.m_20185_(), 0.0, target.m_20189_() - this.froststalker.m_20189_());
               if (vector3d1.m_82556_() > 1.0E-7) {
                  vector3d1 = vector3d1.m_82541_().m_82490_(0.9).m_82549_(vector3d.m_82490_(0.8));
               }

               this.froststalker.m_20334_(vector3d1.f_82479_, 0.6F, vector3d1.f_82481_);
            } else {
               flag = true;
            }
         } else if (!this.froststalker.isTackling()) {
            this.froststalker.m_21573_().m_5624_(target, 1.0);
         }

         if (this.froststalker.isTackling()
            && this.froststalker.m_20270_(target) <= this.froststalker.m_20205_() + target.m_20205_() + 1.1F
            && this.froststalker.m_142582_(target)) {
            target.m_6469_(this.froststalker.m_269291_().m_269333_(this.froststalker), (float)this.froststalker.m_21133_(Attributes.f_22281_));
            this.m_8056_();
         }

         if (!flag
            && this.froststalker.m_20270_(target) <= this.froststalker.m_20205_() + target.m_20205_() + 1.1F
            && this.froststalker.m_142582_(target)
            && this.pursuitTime == this.maxPursuitTime) {
            if (!this.froststalker.isTackling()) {
               this.froststalker.m_7327_(target);
            }

            this.m_8056_();
         }
      }

      if (target != null && !this.froststalker.m_20096_()) {
         this.froststalker.m_21391_(target, 180.0F, 10.0F);
         this.froststalker.f_20883_ = this.froststalker.m_146908_();
      }
   }

   public BlockPos getBlockNearTarget(LivingEntity target) {
      float radius = this.froststalker.m_217043_().m_188503_(5) + 3 + target.m_20205_();
      int orbit = (int)(this.startingOrbit + (float)this.pursuitTime / this.maxPursuitTime * 360.0F);
      float angle = (float) (Math.PI / 180.0) * (this.clockwise ? -orbit : orbit);
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos circlePos = AMBlockPos.fromCoords(target.m_20185_() + extraX, target.m_20188_(), target.m_20189_() + extraZ);

      while (!this.froststalker.m_9236_().m_8055_(circlePos).m_60795_() && circlePos.m_123342_() < this.froststalker.m_9236_().m_151558_()) {
         circlePos = circlePos.m_7494_();
      }

      while (
         !this.froststalker.m_9236_().m_8055_(circlePos.m_7495_()).m_60634_(this.froststalker.m_9236_(), circlePos.m_7495_(), this.froststalker)
            && circlePos.m_123342_() > 1
      ) {
         circlePos = circlePos.m_7495_();
      }

      return this.froststalker.m_21692_(circlePos) > -1.0F ? circlePos : null;
   }

   public void m_8041_() {
      this.froststalker.setTackling(false);
   }
}
