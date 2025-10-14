package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface NeutralMob {
   String TAG_ANGER_TIME = "AngerTime";
   String TAG_ANGRY_AT = "AngryAt";

   int getRemainingPersistentAngerTime();

   void setRemainingPersistentAngerTime(int var1);

   @Nullable
   UUID getPersistentAngerTarget();

   void setPersistentAngerTarget(@Nullable UUID var1);

   void startPersistentAngerTimer();

   default void addPersistentAngerSaveData(ValueOutput $$0) {
      $$0.putInt("AngerTime", this.getRemainingPersistentAngerTime());
      $$0.storeNullable("AngryAt", UUIDUtil.CODEC, this.getPersistentAngerTarget());
   }

   default void readPersistentAngerSaveData(Level $$0, ValueInput $$1) {
      this.setRemainingPersistentAngerTime($$1.getIntOr("AngerTime", 0));
      if ($$0 instanceof ServerLevel $$2) {
         UUID $$4 = (UUID)$$1.read("AngryAt", UUIDUtil.CODEC).orElse(null);
         this.setPersistentAngerTarget($$4);
         Entity $$5 = $$4 != null ? $$2.getEntity($$4) : null;
         if ($$5 instanceof LivingEntity $$6) {
            this.setTarget($$6);
         }
      }
   }

   default void updatePersistentAnger(ServerLevel $$0, boolean $$1) {
      LivingEntity $$2 = this.getTarget();
      UUID $$3 = this.getPersistentAngerTarget();
      if (($$2 == null || $$2.isDeadOrDying()) && $$3 != null && $$0.getEntity($$3) instanceof Mob) {
         this.stopBeingAngry();
      } else {
         if ($$2 != null && !Objects.equals($$3, $$2.getUUID())) {
            this.setPersistentAngerTarget($$2.getUUID());
            this.startPersistentAngerTimer();
         }

         if (this.getRemainingPersistentAngerTime() > 0 && ($$2 == null || $$2.getType() != EntityType.PLAYER || !$$1)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
               this.stopBeingAngry();
            }
         }
      }
   }

   default boolean isAngryAt(LivingEntity $$0, ServerLevel $$1) {
      if (!this.canAttack($$0)) {
         return false;
      } else {
         return $$0.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers($$1) ? true : $$0.getUUID().equals(this.getPersistentAngerTarget());
      }
   }

   default boolean isAngryAtAllPlayers(ServerLevel $$0) {
      return $$0.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
   }

   default boolean isAngry() {
      return this.getRemainingPersistentAngerTime() > 0;
   }

   default void playerDied(ServerLevel $$0, Player $$1) {
      if ($$0.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         if ($$1.getUUID().equals(this.getPersistentAngerTarget())) {
            this.stopBeingAngry();
         }
      }
   }

   default void forgetCurrentTargetAndRefreshUniversalAnger() {
      this.stopBeingAngry();
      this.startPersistentAngerTimer();
   }

   default void stopBeingAngry() {
      this.setLastHurtByMob(null);
      this.setPersistentAngerTarget(null);
      this.setTarget(null);
      this.setRemainingPersistentAngerTime(0);
   }

   @Nullable
   LivingEntity getLastHurtByMob();

   void setLastHurtByMob(@Nullable LivingEntity var1);

   void setTarget(@Nullable LivingEntity var1);

   boolean canAttack(LivingEntity var1);

   @Nullable
   LivingEntity getTarget();
}
