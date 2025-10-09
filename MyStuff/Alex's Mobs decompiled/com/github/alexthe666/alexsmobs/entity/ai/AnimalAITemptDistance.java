package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class AnimalAITemptDistance extends Goal {
   private final TargetingConditions targetingConditions;
   protected final PathfinderMob mob;
   private final double speedModifier;
   private double px;
   private double py;
   private double pz;
   private double pRotX;
   private double pRotY;
   protected Player player;
   private int calmDown;
   private boolean isRunning;
   private final Ingredient items;
   private final boolean canScare;

   public AnimalAITemptDistance(PathfinderMob p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_, double distance) {
      this.mob = p_25939_;
      this.speedModifier = p_25940_;
      this.items = p_25941_;
      this.canScare = p_25942_;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.targetingConditions = TargetingConditions.m_148353_().m_26883_(distance).m_148355_().m_148354_().m_26888_(this::shouldFollow);
   }

   public boolean m_8036_() {
      if (this.calmDown > 0) {
         this.calmDown--;
         return false;
      } else {
         this.player = this.mob.m_9236_().m_45946_(this.targetingConditions, this.mob);
         return this.player != null;
      }
   }

   private boolean shouldFollow(LivingEntity p_148139_) {
      return this.items.test(p_148139_.m_21205_()) || this.items.test(p_148139_.m_21206_());
   }

   public boolean m_8045_() {
      if (this.canScare()) {
         if (this.mob.m_20280_(this.player) < 36.0) {
            if (this.player.m_20275_(this.px, this.py, this.pz) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs(this.player.m_146909_() - this.pRotX) > 5.0 || Math.abs(this.player.m_146908_() - this.pRotY) > 5.0) {
               return false;
            }
         } else {
            this.px = this.player.m_20185_();
            this.py = this.player.m_20186_();
            this.pz = this.player.m_20189_();
         }

         this.pRotX = this.player.m_146909_();
         this.pRotY = this.player.m_146908_();
      }

      return this.m_8036_();
   }

   protected boolean canScare() {
      return this.canScare;
   }

   public void m_8056_() {
      this.px = this.player.m_20185_();
      this.py = this.player.m_20186_();
      this.pz = this.player.m_20189_();
      this.isRunning = true;
   }

   public void m_8041_() {
      this.player = null;
      this.mob.m_21573_().m_26573_();
      this.calmDown = 100;
      this.isRunning = false;
   }

   public void m_8037_() {
      this.mob.m_21563_().m_24960_(this.player, this.mob.m_8085_() + 20, this.mob.m_8132_());
      if (this.mob.m_20280_(this.player) < 6.25) {
         this.mob.m_21573_().m_26573_();
      } else {
         this.mob.m_21573_().m_5624_(this.player, this.speedModifier);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
