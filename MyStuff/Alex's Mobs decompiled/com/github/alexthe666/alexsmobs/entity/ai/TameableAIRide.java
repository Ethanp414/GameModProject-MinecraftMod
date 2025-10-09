package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TameableAIRide extends Goal {
   private final PathfinderMob tameableEntity;
   private LivingEntity player;
   private final double speed;
   private final boolean strafe;

   public TameableAIRide(PathfinderMob dragon, double speed) {
      this(dragon, speed, true);
   }

   public TameableAIRide(PathfinderMob dragon, double speed, boolean strafe) {
      this.tameableEntity = dragon;
      this.speed = speed;
      this.strafe = strafe;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (this.tameableEntity.m_6688_() instanceof Player && this.tameableEntity.m_20160_()) {
         this.player = (Player)this.tameableEntity.m_6688_();
         return true;
      } else {
         this.tameableEntity.m_6858_(false);
         return false;
      }
   }

   public void m_8056_() {
      this.tameableEntity.m_21573_().m_26573_();
   }

   public void m_8037_() {
      this.tameableEntity.m_274367_(1.0F);
      this.tameableEntity.m_21573_().m_26573_();
      this.tameableEntity.m_6710_(null);
      double x = this.tameableEntity.m_20185_();
      double y = this.tameableEntity.m_20186_();
      double z = this.tameableEntity.m_20189_();
      if (this.strafe) {
         this.tameableEntity.f_20900_ = this.player.f_20900_ * 0.15F;
      }

      if (this.shouldMoveForward() && this.tameableEntity.m_20160_()) {
         this.tameableEntity.m_6858_(true);
         Vec3 lookVec = this.player.m_20154_();
         if (this.shouldMoveBackwards()) {
            lookVec = lookVec.m_82524_((float) Math.PI);
         }

         x += lookVec.f_82479_ * 10.0;
         z += lookVec.f_82481_ * 10.0;
         y += this.modifyYPosition(lookVec.f_82480_);
         this.tameableEntity.m_21566_().m_6849_(x, y, z, this.speed);
      } else {
         this.tameableEntity.m_6858_(false);
      }
   }

   public double modifyYPosition(double lookVecY) {
      return this.tameableEntity instanceof FlyingAnimal ? lookVecY * 10.0 : 0.0;
   }

   public boolean shouldMoveForward() {
      return this.player.f_20902_ != 0.0F;
   }

   public boolean shouldMoveBackwards() {
      return this.player.f_20902_ < 0.0F;
   }
}
