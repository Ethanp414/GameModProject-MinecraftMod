package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
   public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
   public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = $$0 -> $$0.isAlive() && $$0 instanceof LivingEntity;
   public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = $$0 -> $$0.isAlive() && !$$0.isVehicle() && !$$0.isPassenger();
   public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = $$0 -> $$0 instanceof Container && $$0.isAlive();
   public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = $$0 -> {
      if ($$0 instanceof Player $$1 && ($$0.isSpectator() || $$1.isCreative())) {
         return false;
      }

      return true;
   };
   public static final Predicate<Entity> NO_SPECTATORS = $$0 -> !$$0.isSpectator();
   public static final Predicate<Entity> CAN_BE_COLLIDED_WITH = NO_SPECTATORS.and($$0 -> $$0.canBeCollidedWith(null));
   public static final Predicate<Entity> CAN_BE_PICKED = NO_SPECTATORS.and(Entity::isPickable);

   private EntitySelector() {
   }

   public static Predicate<Entity> withinDistance(double $$0, double $$1, double $$2, double $$3) {
      double $$4 = $$3 * $$3;
      return $$4x -> $$4x != null && $$4x.distanceToSqr($$0, $$1, $$2) <= $$4;
   }

   public static Predicate<Entity> pushableBy(Entity $$0) {
      Team $$1 = $$0.getTeam();
      Team.CollisionRule $$2 = $$1 == null ? Team.CollisionRule.ALWAYS : $$1.getCollisionRule();
      return (Predicate<Entity>)($$2 == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NO_SPECTATORS.and($$3 -> {
         if (!$$3.isPushable()) {
            return false;
         } else {
            if ($$0.level().isClientSide()) {
               if (!($$3 instanceof Player)) {
                  return false;
               }

               Player $$4 = (Player)$$3;
               if (!$$4.isLocalPlayer()) {
                  return false;
               }
            }

            Team $$5 = $$3.getTeam();
            Team.CollisionRule $$6 = $$5 == null ? Team.CollisionRule.ALWAYS : $$5.getCollisionRule();
            if ($$6 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean $$7 = $$1 != null && $$1.isAlliedTo($$5);
               if (($$2 == Team.CollisionRule.PUSH_OWN_TEAM || $$6 == Team.CollisionRule.PUSH_OWN_TEAM) && $$7) {
                  return false;
               } else {
                  return $$2 != Team.CollisionRule.PUSH_OTHER_TEAMS && $$6 != Team.CollisionRule.PUSH_OTHER_TEAMS || $$7;
               }
            }
         }
      }));
   }

   public static Predicate<Entity> notRiding(Entity $$0) {
      return $$1 -> {
         while($$1.isPassenger()) {
            $$1 = $$1.getVehicle();
            if ($$1 == $$0) {
               return false;
            }
         }

         return true;
      };
   }
}
