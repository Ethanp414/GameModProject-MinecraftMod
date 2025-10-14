package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
   private final ShoulderRidingEntity entity;
   private boolean isSittingOnShoulder;

   public LandOnOwnersShoulderGoal(ShoulderRidingEntity $$0) {
      this.entity = $$0;
   }

   @Override
   public boolean canUse() {
      LivingEntity $$1 = this.entity.getOwner();
      if (!($$1 instanceof ServerPlayer)) {
         return false;
      } else {
         ServerPlayer $$0 = (ServerPlayer)$$1;
         boolean $$1 = !$$0.isSpectator() && !$$0.getAbilities().flying && !$$0.isInWater() && !$$0.isInPowderSnow;
         return !this.entity.isOrderedToSit() && $$1 && this.entity.canSitOnShoulder();
      }
   }

   @Override
   public boolean isInterruptable() {
      return !this.isSittingOnShoulder;
   }

   @Override
   public void start() {
      this.isSittingOnShoulder = false;
   }

   @Override
   public void tick() {
      if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
         LivingEntity var2 = this.entity.getOwner();
         if (var2 instanceof ServerPlayer $$0 && this.entity.getBoundingBox().intersects($$0.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.setEntityOnShoulder($$0);
         }
      }
   }
}
