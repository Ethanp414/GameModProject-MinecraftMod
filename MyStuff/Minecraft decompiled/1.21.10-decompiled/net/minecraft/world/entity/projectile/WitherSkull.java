package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WitherSkull extends AbstractHurtingProjectile {
   private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);
   private static final boolean DEFAULT_DANGEROUS = false;

   public WitherSkull(EntityType<? extends WitherSkull> $$0, Level $$1) {
      super($$0, $$1);
   }

   public WitherSkull(Level $$0, LivingEntity $$1, Vec3 $$2) {
      super(EntityType.WITHER_SKULL, $$1, $$2, $$0);
   }

   @Override
   protected float getInertia() {
      return this.isDangerous() ? 0.73F : super.getInertia();
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   public float getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4, float $$5) {
      return this.isDangerous() && WitherBoss.canDestroy($$3) ? Math.min(0.8F, $$5) : $$5;
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      Level $$3 = this.level();
      if ($$3 instanceof ServerLevel $$1) {
         Entity var8 = $$0.getEntity();
         Entity $$4 = this.getOwner();
         boolean $$7;
         if ($$4 instanceof LivingEntity $$5) {
            DamageSource $$6 = this.damageSources().witherSkull(this, $$5);
            $$7 = var8.hurtServer($$1, $$6, 8.0F);
            if ($$7) {
               if (var8.isAlive()) {
                  EnchantmentHelper.doPostAttackEffects($$1, var8, $$6);
               } else {
                  $$5.heal(5.0F);
               }
            }
         } else {
            $$7 = var8.hurtServer($$1, this.damageSources().magic(), 5.0F);
         }

         if ($$7 && var8 instanceof LivingEntity $$9) {
            int $$10 = 0;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               $$10 = 10;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               $$10 = 40;
            }

            if ($$10 > 0) {
               $$9.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * $$10, 1), this.getEffectSource());
            }
         }
      }
   }

   @Override
   protected void onHit(HitResult $$0) {
      super.onHit($$0);
      if (!this.level().isClientSide()) {
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, Level.ExplosionInteraction.MOB);
         this.discard();
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder $$0) {
      $$0.define(DATA_DANGEROUS, false);
   }

   public boolean isDangerous() {
      return this.entityData.get(DATA_DANGEROUS);
   }

   public void setDangerous(boolean $$0) {
      this.entityData.set(DATA_DANGEROUS, $$0);
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput $$0) {
      super.addAdditionalSaveData($$0);
      $$0.putBoolean("dangerous", this.isDangerous());
   }

   @Override
   protected void readAdditionalSaveData(ValueInput $$0) {
      super.readAdditionalSaveData($$0);
      this.setDangerous($$0.getBooleanOr("dangerous", false));
   }
}
