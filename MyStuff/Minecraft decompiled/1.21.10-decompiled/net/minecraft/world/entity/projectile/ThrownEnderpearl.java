package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   private long ticketTimer = 0L;

   public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> $$0, Level $$1) {
      super($$0, $$1);
   }

   public ThrownEnderpearl(Level $$0, LivingEntity $$1, ItemStack $$2) {
      super(EntityType.ENDER_PEARL, $$1, $$0, $$2);
   }

   @Override
   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   @Override
   protected void setOwner(@Nullable EntityReference<Entity> $$0) {
      this.deregisterFromCurrentOwner();
      super.setOwner($$0);
      this.registerToCurrentOwner();
   }

   private void deregisterFromCurrentOwner() {
      Entity var2 = this.getOwner();
      if (var2 instanceof ServerPlayer $$0) {
         $$0.deregisterEnderPearl(this);
      }
   }

   private void registerToCurrentOwner() {
      Entity var2 = this.getOwner();
      if (var2 instanceof ServerPlayer $$0) {
         $$0.registerEnderPearl(this);
      }
   }

   @Nullable
   @Override
   public Entity getOwner() {
      if (this.owner != null) {
         Level var2 = this.level();
         if (var2 instanceof ServerLevel $$0) {
            return this.owner.getEntity($$0, Entity.class);
         }
      }

      return super.getOwner();
   }

   @Nullable
   private static Entity findOwnerIncludingDeadPlayer(ServerLevel $$0, UUID $$1) {
      Entity $$2 = $$0.getEntityInAnyDimension($$1);
      return (Entity)($$2 != null ? $$2 : $$0.getServer().getPlayerList().getPlayer($$1));
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      $$0.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
   }

   @Override
   protected void onHit(HitResult $$0) {
      super.onHit($$0);

      for(int $$1 = 0; $$1 < 32; ++$$1) {
         this.level()
            .addParticle(
               ParticleTypes.PORTAL,
               this.getX(),
               this.getY() + this.random.nextDouble() * 2.0,
               this.getZ(),
               this.random.nextGaussian(),
               0.0,
               this.random.nextGaussian()
            );
      }

      Level $$4 = this.level();
      if ($$4 instanceof ServerLevel $$2 && !this.isRemoved()) {
         Entity $$4x = this.getOwner();
         if ($$4x != null && isAllowedToTeleportOwner($$4x, $$2)) {
            Vec3 $$5 = this.oldPosition();
            if ($$4x instanceof ServerPlayer $$6) {
               if ($$6.connection.isAcceptingMessages()) {
                  if (this.random.nextFloat() < 0.05F && $$2.isSpawningMonsters()) {
                     Endermite $$7 = EntityType.ENDERMITE.create($$2, EntitySpawnReason.TRIGGERED);
                     if ($$7 != null) {
                        $$7.snapTo($$4x.getX(), $$4x.getY(), $$4x.getZ(), $$4x.getYRot(), $$4x.getXRot());
                        $$2.addFreshEntity($$7);
                     }
                  }

                  if (this.isOnPortalCooldown()) {
                     $$4x.setPortalCooldown();
                  }

                  ServerPlayer $$8 = $$6.teleport(
                     new TeleportTransition($$2, $$5, Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING)
                  );
                  if ($$8 != null) {
                     $$8.resetFallDistance();
                     $$8.resetCurrentImpulseContext();
                     $$8.hurtServer($$6.level(), this.damageSources().enderPearl(), 5.0F);
                  }

                  this.playSound($$2, $$5);
               }
            } else {
               Entity $$9 = $$4x.teleport(
                  new TeleportTransition($$2, $$5, $$4x.getDeltaMovement(), $$4x.getYRot(), $$4x.getXRot(), TeleportTransition.DO_NOTHING)
               );
               if ($$9 != null) {
                  $$9.resetFallDistance();
               }

               this.playSound($$2, $$5);
            }

            this.discard();
            return;
         }

         this.discard();
         return;
      }
   }

   private static boolean isAllowedToTeleportOwner(Entity $$0, Level $$1) {
      if ($$0.level().dimension() == $$1.dimension()) {
         if (!($$0 instanceof LivingEntity)) {
            return $$0.isAlive();
         } else {
            LivingEntity $$2 = (LivingEntity)$$0;
            return $$2.isAlive() && !$$2.isSleeping();
         }
      } else {
         return $$0.canUsePortal(true);
      }
   }

   @Override
   public void tick() {
      Level $$2 = this.level();
      if ($$2 instanceof ServerLevel $$0) {
         int $$3;
         Entity $$4;
         label39: {
            var7 = SectionPos.blockToSectionCoord(this.position().x());
            $$3 = SectionPos.blockToSectionCoord(this.position().z());
            $$4 = this.owner != null ? findOwnerIncludingDeadPlayer($$0, this.owner.getUUID()) : null;
            if ($$4 instanceof ServerPlayer $$5
               && !$$4.isAlive()
               && !$$5.wonGame
               && $$5.level().getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
               this.discard();
               break label39;
            }

            super.tick();
         }

         if (this.isAlive()) {
            BlockPos $$6 = BlockPos.containing(this.position());
            if ((--this.ticketTimer <= 0L || var7 != SectionPos.blockToSectionCoord($$6.getX()) || $$3 != SectionPos.blockToSectionCoord($$6.getZ()))
               && $$4 instanceof ServerPlayer $$7) {
               this.ticketTimer = $$7.registerAndUpdateEnderPearlTicket(this);
            }
         }
      } else {
         super.tick();
      }
   }

   private void playSound(Level $$0, Vec3 $$1) {
      $$0.playSound(null, $$1.x, $$1.y, $$1.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
   }

   @Nullable
   @Override
   public Entity teleport(TeleportTransition $$0) {
      Entity $$1 = super.teleport($$0);
      if ($$1 != null) {
         $$1.placePortalTicket(BlockPos.containing($$1.position()));
      }

      return $$1;
   }

   @Override
   public boolean canTeleport(Level $$0, Level $$1) {
      if ($$0.dimension() == Level.END && $$1.dimension() == Level.OVERWORLD) {
         Entity var4 = this.getOwner();
         if (var4 instanceof ServerPlayer $$2) {
            return super.canTeleport($$0, $$1) && $$2.seenCredits;
         }
      }

      return super.canTeleport($$0, $$1);
   }

   @Override
   protected void onInsideBlock(BlockState $$0) {
      super.onInsideBlock($$0);
      if ($$0.is(Blocks.END_GATEWAY)) {
         Entity var3 = this.getOwner();
         if (var3 instanceof ServerPlayer $$1) {
            $$1.onInsideBlock($$0);
         }
      }
   }

   @Override
   public void onRemoval(Entity.RemovalReason $$0) {
      if ($$0 != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
         this.deregisterFromCurrentOwner();
      }

      super.onRemoval($$0);
   }

   @Override
   public void onAboveBubbleColumn(boolean $$0, BlockPos $$1) {
      Entity.handleOnAboveBubbleColumn(this, $$0, $$1);
   }

   @Override
   public void onInsideBubbleColumn(boolean $$0) {
      Entity.handleOnInsideBubbleColumn(this, $$0);
   }
}
