package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface Leashable {
   String LEASH_TAG = "leash";
   double LEASH_TOO_FAR_DIST = 12.0;
   double LEASH_ELASTIC_DIST = 6.0;
   double MAXIMUM_ALLOWED_LEASHED_DIST = 16.0;
   Vec3 AXIS_SPECIFIC_ELASTICITY = new Vec3(0.8, 0.2, 0.8);
   float SPRING_DAMPENING = 0.7F;
   double TORSIONAL_ELASTICITY = 10.0;
   double STIFFNESS = 0.11;
   List<Vec3> ENTITY_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.5));
   List<Vec3> LEASHER_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.0));
   List<Vec3> SHARED_QUAD_ATTACHMENT_POINTS = ImmutableList.of(
      new Vec3(-0.5, 0.5, 0.5), new Vec3(-0.5, 0.5, -0.5), new Vec3(0.5, 0.5, -0.5), new Vec3(0.5, 0.5, 0.5)
   );

   @Nullable
   Leashable.LeashData getLeashData();

   void setLeashData(@Nullable Leashable.LeashData var1);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mayBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canHaveALeashAttachedTo(Entity $$0) {
      if (this == $$0) {
         return false;
      } else {
         return this.leashDistanceTo($$0) > this.leashSnapDistance() ? false : this.canBeLeashed();
      }
   }

   default double leashDistanceTo(Entity $$0) {
      return $$0.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setDelayedLeashHolderId(int $$0) {
      this.setLeashData(new Leashable.LeashData($$0));
      dropLeash((Entity)this, false, false);
   }

   default void readLeashData(ValueInput $$0) {
      Leashable.LeashData $$1 = (Leashable.LeashData)$$0.read("leash", Leashable.LeashData.CODEC).orElse(null);
      if (this.getLeashData() != null && $$1 == null) {
         this.removeLeash();
      }

      this.setLeashData($$1);
   }

   default void writeLeashData(ValueOutput $$0, @Nullable Leashable.LeashData $$1) {
      $$0.storeNullable("leash", Leashable.LeashData.CODEC, $$1);
   }

   private static <E extends Entity & Leashable> void restoreLeashFromSave(E $$0, Leashable.LeashData $$1) {
      if ($$1.delayedLeashInfo != null) {
         Level $$3 = $$0.level();
         if ($$3 instanceof ServerLevel $$2) {
            Optional<UUID> $$3x = $$1.delayedLeashInfo.left();
            Optional<BlockPos> $$4 = $$1.delayedLeashInfo.right();
            if ($$3x.isPresent()) {
               Entity $$5 = $$2.getEntity((UUID)$$3x.get());
               if ($$5 != null) {
                  setLeashedTo($$0, $$5, true);
                  return;
               }
            } else if ($$4.isPresent()) {
               setLeashedTo($$0, LeashFenceKnotEntity.getOrCreateKnot($$2, (BlockPos)$$4.get()), true);
               return;
            }

            if ($$0.tickCount > 100) {
               $$0.spawnAtLocation($$2, Items.LEAD);
               $$0.setLeashData(null);
            }
         }
      }
   }

   default void dropLeash() {
      dropLeash((Entity)this, true, true);
   }

   default void removeLeash() {
      dropLeash((Entity)this, true, false);
   }

   default void onLeashRemoved() {
   }

   private static <E extends Entity & Leashable> void dropLeash(E $$0, boolean $$1, boolean $$2) {
      Leashable.LeashData $$3 = $$0.getLeashData();
      if ($$3 != null && $$3.leashHolder != null) {
         $$0.setLeashData(null);
         $$0.onLeashRemoved();
         Level var5 = $$0.level();
         if (var5 instanceof ServerLevel $$4) {
            if ($$2) {
               $$0.spawnAtLocation($$4, Items.LEAD);
            }

            if ($$1) {
               $$4.getChunkSource().sendToTrackingPlayers($$0, new ClientboundSetEntityLinkPacket($$0, null));
            }

            $$3.leashHolder.notifyLeasheeRemoved($$0);
         }
      }
   }

   static <E extends Entity & Leashable> void tickLeash(ServerLevel $$0, E $$1) {
      Leashable.LeashData $$2 = $$1.getLeashData();
      if ($$2 != null && $$2.delayedLeashInfo != null) {
         restoreLeashFromSave($$1, $$2);
      }

      if ($$2 != null && $$2.leashHolder != null) {
         if (!$$1.canInteractWithLevel() || !$$2.leashHolder.canInteractWithLevel()) {
            if ($$0.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
               $$1.dropLeash();
            } else {
               $$1.removeLeash();
            }
         }

         Entity $$3 = $$1.getLeashHolder();
         if ($$3 != null && $$3.level() == $$1.level()) {
            double $$4 = $$1.leashDistanceTo($$3);
            $$1.whenLeashedTo($$3);
            if ($$4 > $$1.leashSnapDistance()) {
               $$0.playSound(null, $$3.getX(), $$3.getY(), $$3.getZ(), SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
               $$1.leashTooFarBehaviour();
            } else if ($$4 > $$1.leashElasticDistance() - (double)$$3.getBbWidth() - (double)$$1.getBbWidth() && $$1.checkElasticInteractions($$3, $$2)) {
               $$1.onElasticLeashPull();
            } else {
               $$1.closeRangeLeashBehaviour($$3);
            }

            $$1.setYRot((float)((double)$$1.getYRot() - $$2.angularMomentum));
            $$2.angularMomentum *= (double)angularFriction($$1);
         }
      }
   }

   default void onElasticLeashPull() {
      Entity $$0 = (Entity)this;
      $$0.checkFallDistanceAccumulation();
   }

   default double leashSnapDistance() {
      return 12.0;
   }

   default double leashElasticDistance() {
      return 6.0;
   }

   static <E extends Entity & Leashable> float angularFriction(E $$0) {
      if ($$0.onGround()) {
         return $$0.level().getBlockState($$0.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
      } else {
         return $$0.isInLiquid() ? 0.8F : 0.91F;
      }
   }

   default void whenLeashedTo(Entity $$0) {
      $$0.notifyLeashHolder(this);
   }

   default void leashTooFarBehaviour() {
      this.dropLeash();
   }

   default void closeRangeLeashBehaviour(Entity $$0) {
   }

   default boolean checkElasticInteractions(Entity $$0, Leashable.LeashData $$1) {
      boolean $$2 = $$0.supportQuadLeashAsHolder() && this.supportQuadLeash();
      List<Leashable.Wrench> $$3 = computeElasticInteraction(
         (Entity)this, $$0, $$2 ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT, $$2 ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT
      );
      if ($$3.isEmpty()) {
         return false;
      } else {
         Leashable.Wrench $$4 = Leashable.Wrench.accumulate($$3).scale($$2 ? 0.25 : 1.0);
         $$1.angularMomentum += 10.0 * $$4.torque();
         Vec3 $$5 = getHolderMovement($$0).subtract(((Entity)this).getKnownMovement());
         ((Entity)this).addDeltaMovement($$4.force().multiply(AXIS_SPECIFIC_ELASTICITY).add($$5.scale(0.11)));
         return true;
      }
   }

   private static Vec3 getHolderMovement(Entity $$0) {
      if ($$0 instanceof Mob $$1 && $$1.isNoAi()) {
         return Vec3.ZERO;
      }

      return $$0.getKnownMovement();
   }

   private static <E extends Entity & Leashable> List<Leashable.Wrench> computeElasticInteraction(E $$0, Entity $$1, List<Vec3> $$2, List<Vec3> $$3) {
      double $$4 = $$0.leashElasticDistance();
      Vec3 $$5 = getHolderMovement($$0);
      float $$6 = $$0.getYRot() * (float) (Math.PI / 180.0);
      Vec3 $$7 = new Vec3((double)$$0.getBbWidth(), (double)$$0.getBbHeight(), (double)$$0.getBbWidth());
      float $$8 = $$1.getYRot() * (float) (Math.PI / 180.0);
      Vec3 $$9 = new Vec3((double)$$1.getBbWidth(), (double)$$1.getBbHeight(), (double)$$1.getBbWidth());
      List<Leashable.Wrench> $$10 = new ArrayList();

      for(int $$11 = 0; $$11 < $$2.size(); ++$$11) {
         Vec3 $$12 = ((Vec3)$$2.get($$11)).multiply($$7).yRot(-$$6);
         Vec3 $$13 = $$0.position().add($$12);
         Vec3 $$14 = ((Vec3)$$3.get($$11)).multiply($$9).yRot(-$$8);
         Vec3 $$15 = $$1.position().add($$14);
         computeDampenedSpringInteraction($$15, $$13, $$4, $$5, $$12).ifPresent($$10::add);
      }

      return $$10;
   }

   private static Optional<Leashable.Wrench> computeDampenedSpringInteraction(Vec3 $$0, Vec3 $$1, double $$2, Vec3 $$3, Vec3 $$4) {
      double $$5 = $$1.distanceTo($$0);
      if ($$5 < $$2) {
         return Optional.empty();
      } else {
         Vec3 $$6 = $$0.subtract($$1).normalize().scale($$5 - $$2);
         double $$7 = Leashable.Wrench.torqueFromForce($$4, $$6);
         boolean $$8 = $$3.dot($$6) >= 0.0;
         if ($$8) {
            $$6 = $$6.scale(0.3F);
         }

         return Optional.of(new Leashable.Wrench($$6, $$7));
      }
   }

   default boolean supportQuadLeash() {
      return false;
   }

   default Vec3[] getQuadLeashOffsets() {
      return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
   }

   static Vec3[] createQuadLeashOffsets(Entity $$0, double $$1, double $$2, double $$3, double $$4) {
      float $$5 = $$0.getBbWidth();
      double $$6 = $$1 * (double)$$5;
      double $$7 = $$2 * (double)$$5;
      double $$8 = $$3 * (double)$$5;
      double $$9 = $$4 * (double)$$0.getBbHeight();
      return new Vec3[]{new Vec3(-$$8, $$9, $$7 + $$6), new Vec3(-$$8, $$9, -$$7 + $$6), new Vec3($$8, $$9, -$$7 + $$6), new Vec3($$8, $$9, $$7 + $$6)};
   }

   default Vec3 getLeashOffset(float $$0) {
      return this.getLeashOffset();
   }

   default Vec3 getLeashOffset() {
      Entity $$0 = (Entity)this;
      return new Vec3(0.0, (double)$$0.getEyeHeight(), (double)($$0.getBbWidth() * 0.4F));
   }

   default void setLeashedTo(Entity $$0, boolean $$1) {
      if (this != $$0) {
         setLeashedTo((Entity)this, $$0, $$1);
      }
   }

   private static <E extends Entity & Leashable> void setLeashedTo(E $$0, Entity $$1, boolean $$2) {
      Leashable.LeashData $$3 = $$0.getLeashData();
      if ($$3 == null) {
         $$3 = new Leashable.LeashData($$1);
         $$0.setLeashData($$3);
      } else {
         Entity $$4 = $$3.leashHolder;
         $$3.setLeashHolder($$1);
         if ($$4 != null && $$4 != $$1) {
            $$4.notifyLeasheeRemoved($$0);
         }
      }

      if ($$2) {
         Level var5 = $$0.level();
         if (var5 instanceof ServerLevel $$5) {
            $$5.getChunkSource().sendToTrackingPlayers($$0, new ClientboundSetEntityLinkPacket($$0, $$1));
         }
      }

      if ($$0.isPassenger()) {
         $$0.stopRiding();
      }
   }

   @Nullable
   default Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   @Nullable
   private static <E extends Entity & Leashable> Entity getLeashHolder(E $$0) {
      Leashable.LeashData $$1 = $$0.getLeashData();
      if ($$1 == null) {
         return null;
      } else {
         if ($$1.delayedLeashHolderId != 0 && $$0.level().isClientSide()) {
            Entity var3 = $$0.level().getEntity($$1.delayedLeashHolderId);
            if (var3 instanceof Entity) {
               $$1.setLeashHolder(var3);
            }
         }

         return $$1.leashHolder;
      }
   }

   static List<Leashable> leashableLeashedTo(Entity $$0) {
      return leashableInArea($$0, $$1 -> $$1.getLeashHolder() == $$0);
   }

   static List<Leashable> leashableInArea(Entity $$0, Predicate<Leashable> $$1) {
      return leashableInArea($$0.level(), $$0.getBoundingBox().getCenter(), $$1);
   }

   static List<Leashable> leashableInArea(Level $$0, Vec3 $$1, Predicate<Leashable> $$2) {
      double $$3 = 32.0;
      AABB $$4 = AABB.ofSize($$1, 32.0, 32.0, 32.0);
      return $$0.getEntitiesOfClass(Entity.class, $$4, $$1x -> {
         if ($$1x instanceof Leashable $$2xx && $$2.test($$2xx)) {
            return true;
         }

         return false;
      }).stream().map(Leashable.class::cast).toList();
   }

   public static final class LeashData {
      public static final Codec<Leashable.LeashData> CODEC = Codec.xor(UUIDUtil.CODEC.fieldOf("UUID").codec(), BlockPos.CODEC)
         .xmap(
            Leashable.LeashData::new,
            $$0 -> {
               Entity $$1 = $$0.leashHolder;
               if ($$1 instanceof LeashFenceKnotEntity $$2) {
                  return Either.right($$2.getPos());
               } else {
                  return $$0.leashHolder != null
                     ? Either.left($$0.leashHolder.getUUID())
                     : (Either)Objects.requireNonNull($$0.delayedLeashInfo, "Invalid LeashData had no attachment");
               }
            }
         );
      int delayedLeashHolderId;
      @Nullable
      public Entity leashHolder;
      @Nullable
      public Either<UUID, BlockPos> delayedLeashInfo;
      public double angularMomentum;

      private LeashData(Either<UUID, BlockPos> $$0) {
         this.delayedLeashInfo = $$0;
      }

      LeashData(Entity $$0) {
         this.leashHolder = $$0;
      }

      LeashData(int $$0) {
         this.delayedLeashHolderId = $$0;
      }

      public void setLeashHolder(Entity $$0) {
         this.leashHolder = $$0;
         this.delayedLeashInfo = null;
         this.delayedLeashHolderId = 0;
      }
   }

   public static record Wrench(Vec3 force, double torque) {
      static Leashable.Wrench ZERO = new Leashable.Wrench(Vec3.ZERO, 0.0);

      static double torqueFromForce(Vec3 $$0, Vec3 $$1) {
         return $$0.z * $$1.x - $$0.x * $$1.z;
      }

      static Leashable.Wrench accumulate(List<Leashable.Wrench> $$0) {
         if ($$0.isEmpty()) {
            return ZERO;
         } else {
            double $$1 = 0.0;
            double $$2 = 0.0;
            double $$3 = 0.0;
            double $$4 = 0.0;

            for(Leashable.Wrench $$5 : $$0) {
               Vec3 $$6 = $$5.force;
               $$1 += $$6.x;
               $$2 += $$6.y;
               $$3 += $$6.z;
               $$4 += $$5.torque;
            }

            return new Leashable.Wrench(new Vec3($$1, $$2, $$3), $$4);
         }
      }

      public Leashable.Wrench scale(double $$0) {
         return new Leashable.Wrench(this.force.scale($$0), this.torque * $$0);
      }
   }
}
