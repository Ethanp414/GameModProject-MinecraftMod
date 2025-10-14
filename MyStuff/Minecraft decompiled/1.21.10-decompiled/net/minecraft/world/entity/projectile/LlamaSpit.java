package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit extends Projectile {
   public LlamaSpit(EntityType<? extends LlamaSpit> $$0, Level $$1) {
      super($$0, $$1);
   }

   public LlamaSpit(Level $$0, Llama $$1) {
      this(EntityType.LLAMA_SPIT, $$0);
      this.setOwner($$1);
      this.setPos(
         $$1.getX() - (double)($$1.getBbWidth() + 1.0F) * 0.5 * (double)Mth.sin($$1.yBodyRot * (float) (Math.PI / 180.0)),
         $$1.getEyeY() - 0.1F,
         $$1.getZ() + (double)($$1.getBbWidth() + 1.0F) * 0.5 * (double)Mth.cos($$1.yBodyRot * (float) (Math.PI / 180.0))
      );
   }

   @Override
   protected double getDefaultGravity() {
      return 0.06;
   }

   @Override
   public void tick() {
      super.tick();
      Vec3 $$0 = this.getDeltaMovement();
      HitResult $$1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      this.hitTargetOrDeflectSelf($$1);
      double $$2 = this.getX() + $$0.x;
      double $$3 = this.getY() + $$0.y;
      double $$4 = this.getZ() + $$0.z;
      this.updateRotation();
      float $$5 = 0.99F;
      if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
         this.discard();
      } else if (this.isInWater()) {
         this.discard();
      } else {
         this.setDeltaMovement($$0.scale(0.99F));
         this.applyGravity();
         this.setPos($$2, $$3, $$4);
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      Entity $$2 = this.getOwner();
      if ($$2 instanceof LivingEntity $$1) {
         $$2 = $$0.getEntity();
         DamageSource $$3 = this.damageSources().spit(this, $$1);
         Level var6 = this.level();
         if (var6 instanceof ServerLevel $$4 && $$2.hurtServer($$4, $$3, 1.0F)) {
            EnchantmentHelper.doPostAttackEffects($$4, $$2, $$3);
         }
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult $$0) {
      super.onHitBlock($$0);
      if (!this.level().isClientSide()) {
         this.discard();
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder $$0) {
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
      super.recreateFromPacket($$0);
      Vec3 $$1 = $$0.getMovement();

      for(int $$2 = 0; $$2 < 7; ++$$2) {
         double $$3 = 0.4 + 0.1 * (double)$$2;
         this.level().addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), $$1.x * $$3, $$1.y, $$1.z * $$3);
      }

      this.setDeltaMovement($$1);
   }
}
