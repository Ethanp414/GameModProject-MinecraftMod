package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class TameableAIFollowOwner extends FollowOwnerGoal {
   private final IFollower follower;
   private final TamableAnimal tameable;

   public TameableAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
      super(tameable, speed, minDist, maxDist, teleportToLeaves);
      this.follower = (IFollower)tameable;
      this.tameable = tameable;
   }

   public boolean m_8036_() {
      return super.m_8036_() && this.follower.shouldFollow() && !this.isInCombat();
   }

   public boolean m_8045_() {
      return super.m_8045_() && this.follower.shouldFollow() && !this.isInCombat();
   }

   private boolean isInCombat() {
      Entity owner = this.tameable.m_269323_();
      return owner == null ? false : this.tameable.m_20270_(owner) < 30.0F && this.tameable.m_5448_() != null && this.tameable.m_5448_().m_6084_();
   }
}
