package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GorillaAIChargeLooker extends Goal {
   private final EntityGorilla gorilla;
   private final double range = 20.0;
   private Player starer;
   private final double speed;
   private int runDelay = 0;

   public GorillaAIChargeLooker(EntityGorilla gorilla, double speed) {
      this.m_7021_(EnumSet.of(Flag.LOOK, Flag.MOVE));
      this.gorilla = gorilla;
      this.speed = speed;
   }

   public boolean m_8036_() {
      if (this.gorilla.isSilverback() && !this.gorilla.m_21824_() && this.runDelay-- == 0) {
         this.runDelay = 100 + this.gorilla.m_217043_().m_188503_(200);
         List<Player> playerList = this.gorilla.m_9236_().m_6443_(Player.class, this.gorilla.m_20191_().m_82377_(20.0, 20.0, 20.0), EntitySelector.f_20408_);
         Player closestPlayer = null;

         for (Player player : playerList) {
            if (this.isLookingAtMe(player) && (closestPlayer == null || player.m_20270_(this.gorilla) < closestPlayer.m_20270_(this.gorilla))) {
               closestPlayer = player;
            }
         }

         this.starer = closestPlayer;
         return this.starer != null;
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return this.starer != null && this.gorilla.m_6084_();
   }

   public void m_8041_() {
      this.starer = null;
      this.gorilla.m_6858_(false);
      this.runDelay = 300 + this.gorilla.m_217043_().m_188503_(200);
   }

   public void m_8037_() {
      this.gorilla.m_21839_(false);
      this.gorilla.poundChestCooldown = 50;
      if (this.gorilla.m_20270_(this.starer) > 1.0F + this.starer.m_20205_() + this.gorilla.m_20205_()) {
         this.gorilla.m_21573_().m_5624_(this.starer, this.speed);
         this.gorilla.m_6858_(!this.gorilla.isSitting() && !this.gorilla.isStanding());
      } else {
         this.gorilla.m_21573_().m_26573_();
         this.gorilla.m_21391_(this.starer, 180.0F, 30.0F);
         this.gorilla.m_6858_(false);
         if (this.gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.gorilla.setStanding(true);
            this.gorilla.maxStandTime = 45;
            this.gorilla.setAnimation(EntityGorilla.ANIMATION_POUNDCHEST);
         }

         if (this.gorilla.getAnimation() == EntityGorilla.ANIMATION_POUNDCHEST && this.gorilla.getAnimationTick() >= 10) {
            this.m_8041_();
         }
      }
   }

   private boolean isLookingAtMe(Player player) {
      Vec3 vec3 = player.m_20252_(1.0F).m_82541_();
      Vec3 vec31 = new Vec3(
         this.gorilla.m_20185_() - player.m_20185_(), this.gorilla.m_20188_() - player.m_20188_(), this.gorilla.m_20189_() - player.m_20189_()
      );
      double d0 = vec31.m_82553_();
      vec31 = vec31.m_82541_();
      double d1 = vec3.m_82526_(vec31);
      return d1 > 1.0 - 0.025 / d0 && player.m_142582_(this.gorilla);
   }
}
