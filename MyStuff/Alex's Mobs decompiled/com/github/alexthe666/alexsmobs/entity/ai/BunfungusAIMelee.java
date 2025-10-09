package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class BunfungusAIMelee extends Goal {
   private final EntityBunfungus chungus;
   private LivingEntity target;
   private boolean hasJumped = false;
   private int jumpCooldown = 0;

   public BunfungusAIMelee(EntityBunfungus chungus) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.chungus = chungus;
   }

   public boolean m_8036_() {
      if (this.chungus.m_5448_() != null && this.chungus.m_5448_().m_6084_()) {
         this.hasJumped = false;
         return true;
      } else {
         return false;
      }
   }

   public void m_8037_() {
      if (this.jumpCooldown > 0) {
         this.jumpCooldown--;
      }

      double dist = this.chungus.m_20270_(this.chungus.m_5448_()) - this.chungus.m_5448_().m_20205_();
      if (dist < 2.0) {
         if (this.hasJumped) {
            if (!this.chungus.m_20096_()) {
               this.chungus.m_5448_().m_6469_(this.chungus.m_269291_().m_269333_(this.chungus), 10.0F);
            }

            this.hasJumped = false;
         } else if (this.chungus.m_217043_().m_188499_()) {
            this.chungus.setAnimation(EntityBunfungus.ANIMATION_SLAM);
         } else {
            this.chungus.setAnimation(EntityBunfungus.ANIMATION_BELLY);
         }
      } else if (!(dist < 5.0) && this.chungus.m_142582_(this.chungus.m_5448_()) && this.jumpCooldown <= 0 && !this.chungus.m_20072_()) {
         this.chungus.m_21573_().m_26573_();
         if (this.chungus.m_20096_()) {
            Vec3 vector3d = this.chungus.m_20184_();
            Vec3 vector3d1 = new Vec3(
               this.chungus.m_5448_().m_20185_() - this.chungus.m_20185_(), 0.0, this.chungus.m_5448_().m_20189_() - this.chungus.m_20189_()
            );
            if (vector3d1.m_82556_() > 1.0E-7) {
               vector3d1 = vector3d1.m_82541_().m_82490_(0.9).m_82549_(vector3d.m_82490_(0.8));
            }

            this.chungus.onJump();
            this.chungus.m_20334_(vector3d1.f_82479_, 0.6F, vector3d1.f_82481_);
            this.chungus.m_146922_(-((float)Mth.m_14136_(vector3d1.f_82479_, vector3d1.f_82481_)) * (180.0F / (float)Math.PI));
            this.chungus.f_20883_ = this.chungus.m_146908_();
            this.hasJumped = true;
            this.jumpCooldown = 10;
         }
      } else {
         this.chungus.m_21573_().m_5624_(this.chungus.m_5448_(), 1.0);
      }
   }
}
