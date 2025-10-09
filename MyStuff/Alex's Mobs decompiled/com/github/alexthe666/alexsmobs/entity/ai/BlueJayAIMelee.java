package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class BlueJayAIMelee extends Goal {
   private final EntityBlueJay blueJay;
   float circlingTime = 0.0F;
   float circleDistance = 1.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   private int maxCircleTime;

   public BlueJayAIMelee(EntityBlueJay blueJay) {
      this.blueJay = blueJay;
   }

   public boolean m_8036_() {
      Entity entity = this.blueJay.m_5448_();
      return entity != null && entity.m_6084_();
   }

   public void m_8056_() {
      this.clockwise = this.blueJay.m_217043_().m_188499_();
      this.yLevel = this.blueJay.m_217043_().m_188503_(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.blueJay.m_217043_().m_188503_(20);
      this.circleDistance = 0.5F + this.blueJay.m_217043_().m_188501_() * 2.0F;
   }

   public void m_8041_() {
      this.clockwise = this.blueJay.m_217043_().m_188499_();
      this.yLevel = this.blueJay.m_217043_().m_188503_(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.blueJay.m_217043_().m_188503_(20);
      this.circleDistance = 0.5F + this.blueJay.m_217043_().m_188501_() * 2.0F;
      if (this.blueJay.m_20096_()) {
         this.blueJay.setFlying(false);
      }
   }

   public void m_8037_() {
      if (this.blueJay.isFlying()) {
         this.circlingTime++;
      }

      LivingEntity target = this.blueJay.m_5448_();
      if (target != null) {
         if (this.blueJay.m_20270_(target) < 3.0F) {
            this.blueJay.peck();
            target.m_6469_(target.m_269291_().m_269264_(), 1.0F);
            this.m_8041_();
         }

         if (this.circlingTime > this.maxCircleTime) {
            this.blueJay.m_21566_().m_6849_(target.m_20185_(), target.m_20186_() + target.m_20192_() / 2.0F, target.m_20189_(), 1.6F);
         } else {
            Vec3 circlePos = this.getVultureCirclePos(target.m_20182_());
            if (circlePos == null) {
               circlePos = target.m_20182_();
            }

            this.blueJay.setFlying(true);
            this.blueJay.m_21566_().m_6849_(circlePos.m_7096_(), circlePos.m_7098_() + target.m_20192_() + 0.2F, circlePos.m_7094_(), 1.6F);
         }
      }
   }

   public Vec3 getVultureCirclePos(Vec3 target) {
      float angle = 0.2268928F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.m_14031_(angle);
      double extraZ = this.circleDistance * Mth.m_14089_(angle);
      Vec3 pos = new Vec3(target.m_7096_() + extraX, target.m_7098_() + this.yLevel, target.m_7094_() + extraZ);
      return this.blueJay.m_9236_().m_46859_(AMBlockPos.fromVec3(pos)) ? pos : null;
   }
}
