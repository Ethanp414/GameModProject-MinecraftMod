package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolem golem;
   @Nullable
   private LivingEntity potentialTarget;
   private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);

   public DefendVillageTargetGoal(IronGolem $$0) {
      super($$0, false, true);
      this.golem = $$0;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   @Override
   public boolean canUse() {
      AABB $$0 = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
      ServerLevel $$1 = getServerLevel(this.golem);
      List<? extends LivingEntity> $$2 = $$1.getNearbyEntities(Villager.class, this.attackTargeting, this.golem, $$0);
      List<Player> $$3 = $$1.getNearbyPlayers(this.attackTargeting, this.golem, $$0);

      for(LivingEntity $$4 : $$2) {
         Villager $$5 = (Villager)$$4;

         for(Player $$6 : $$3) {
            int $$7 = $$5.getPlayerReputation($$6);
            if ($$7 <= -100) {
               this.potentialTarget = $$6;
            }
         }
      }

      if (this.potentialTarget == null) {
         return false;
      } else {
         LivingEntity var12 = this.potentialTarget;
         if (var12 instanceof Player $$8 && ($$8.isSpectator() || $$8.isCreative())) {
            return false;
         }

         return true;
      }
   }

   @Override
   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
