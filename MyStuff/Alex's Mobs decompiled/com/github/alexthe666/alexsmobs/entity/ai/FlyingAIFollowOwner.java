package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class FlyingAIFollowOwner extends Goal {
   private final TamableAnimal tameable;
   private LivingEntity owner;
   private final LevelReader world;
   private final double followSpeed;
   private final PathNavigation navigator;
   private int timeToRecalcPath;
   private final float maxDist;
   private final float minDist;
   private float oldWaterCost;
   private final boolean teleportToLeaves;
   private final IFollower follower;

   public FlyingAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
      this.tameable = tameable;
      this.world = tameable.m_9236_();
      this.followSpeed = speed;
      this.navigator = tameable.m_21573_();
      this.minDist = minDist;
      this.maxDist = maxDist;
      this.teleportToLeaves = teleportToLeaves;
      this.follower = (IFollower)tameable;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      LivingEntity livingentity = this.tameable.m_269323_();
      if (livingentity == null) {
         return false;
      } else if (livingentity.m_5833_()) {
         return false;
      } else if (this.tameable.m_21827_()) {
         return false;
      } else if (!(this.tameable.m_20280_(livingentity) < this.minDist * this.minDist) && !this.isInCombat()) {
         this.owner = livingentity;
         return this.follower.shouldFollow();
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return !this.tameable.m_21827_() && !this.isInCombat() ? this.tameable.m_20280_(this.owner) > this.maxDist * this.maxDist : false;
   }

   private boolean isInCombat() {
      Entity owner = this.tameable.m_269323_();
      return owner == null ? false : this.tameable.m_20270_(owner) < 30.0F && this.tameable.m_5448_() != null && this.tameable.m_5448_().m_6084_();
   }

   public void m_8056_() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tameable.m_21439_(BlockPathTypes.WATER);
      this.tameable.m_21441_(BlockPathTypes.WATER, 0.0F);
   }

   public void m_8041_() {
      this.owner = null;
      this.navigator.m_26573_();
      this.tameable.m_21441_(BlockPathTypes.WATER, this.oldWaterCost);
   }

   public void m_8037_() {
      this.tameable.m_21563_().m_24960_(this.owner, 10.0F, this.tameable.m_8132_());
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if (!this.tameable.m_21523_() && !this.tameable.m_20159_()) {
            if (this.tameable.m_20280_(this.owner) >= 144.0) {
               this.tryToTeleportNearEntity();
            }

            this.follower.followEntity(this.tameable, this.owner, this.followSpeed);
         }
      }
   }

   private void tryToTeleportNearEntity() {
      BlockPos blockpos = this.owner.m_20183_();

      for (int i = 0; i < 10; i++) {
         int j = this.getRandomNumber(-3, 3);
         int k = this.getRandomNumber(-1, 1);
         int l = this.getRandomNumber(-3, 3);
         boolean flag = this.tryToTeleportToLocation(blockpos.m_123341_() + j, blockpos.m_123342_() + k, blockpos.m_123343_() + l);
         if (flag) {
            return;
         }
      }
   }

   private boolean tryToTeleportToLocation(int x, int y, int z) {
      if (Math.abs(x - this.owner.m_20185_()) < 2.0 && Math.abs(z - this.owner.m_20189_()) < 2.0) {
         return false;
      } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.tameable.m_7678_(x + 0.5, y, z + 0.5, this.tameable.m_146908_(), this.tameable.m_146909_());
         this.navigator.m_26573_();
         return true;
      }
   }

   private boolean isTeleportFriendlyBlock(BlockPos pos) {
      if (this.world.m_8055_(pos).m_60795_()) {
         BlockPos blockpos = pos.m_121996_(this.tameable.m_20183_());
         return this.world.m_45756_(this.tameable, this.tameable.m_20191_().m_82338_(blockpos));
      } else {
         BlockPathTypes pathnodetype = WalkNodeEvaluator.m_77604_(this.world, pos.m_122032_());
         if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
         } else {
            BlockState blockstate = this.world.m_8055_(pos.m_7495_());
            if (!this.teleportToLeaves && blockstate.m_60734_() instanceof LeavesBlock) {
               return false;
            } else {
               BlockPos blockpos = pos.m_121996_(this.tameable.m_20183_());
               return this.world.m_45756_(this.tameable, this.tameable.m_20191_().m_82338_(blockpos));
            }
         }
      }
   }

   private int getRandomNumber(int min, int max) {
      return this.tameable.m_217043_().m_188503_(max - min + 1) + min;
   }
}
