package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {
   public static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
      true, false, Optional.empty(), BuiltInRegistries.BLOCK.get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
   );
   public static final double JUMP_SCALE = 0.25;

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> $$0, Level $$1) {
      super($$0, $$1);
      this.accelerationPower = 0.0;
   }

   public AbstractWindCharge(EntityType<? extends AbstractWindCharge> $$0, Level $$1, Entity $$2, double $$3, double $$4, double $$5) {
      super($$0, $$3, $$4, $$5, $$1);
      this.setOwner($$2);
      this.accelerationPower = 0.0;
   }

   AbstractWindCharge(EntityType<? extends AbstractWindCharge> $$0, double $$1, double $$2, double $$3, Vec3 $$4, Level $$5) {
      super($$0, $$1, $$2, $$3, $$4, $$5);
      this.accelerationPower = 0.0;
   }

   @Override
   protected AABB makeBoundingBox(Vec3 $$0) {
      float $$1 = this.getType().getDimensions().width() / 2.0F;
      float $$2 = this.getType().getDimensions().height();
      float $$3 = 0.15F;
      return new AABB($$0.x - (double)$$1, $$0.y - 0.15F, $$0.z - (double)$$1, $$0.x + (double)$$1, $$0.y - 0.15F + (double)$$2, $$0.z + (double)$$1);
   }

   @Override
   public boolean canCollideWith(Entity $$0) {
      return $$0 instanceof AbstractWindCharge ? false : super.canCollideWith($$0);
   }

   @Override
   protected boolean canHitEntity(Entity $$0) {
      if ($$0 instanceof AbstractWindCharge) {
         return false;
      } else {
         return $$0.getType() == EntityType.END_CRYSTAL ? false : super.canHitEntity($$0);
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      Level $$4 = this.level();
      if ($$4 instanceof ServerLevel $$1) {
         Entity $$6 = this.getOwner();
         LivingEntity $$4x = $$6 instanceof LivingEntity $$3 ? $$3 : null;
         Entity $$5 = $$0.getEntity();
         if ($$4x != null) {
            $$4x.setLastHurtMob($$5);
         }

         DamageSource $$6 = this.damageSources().windCharge(this, $$4x);
         if ($$5.hurtServer($$1, $$6, 1.0F) && $$5 instanceof LivingEntity $$7) {
            EnchantmentHelper.doPostAttackEffects($$1, $$7, $$6);
         }

         this.explode(this.position());
      }
   }

   @Override
   public void push(double $$0, double $$1, double $$2) {
   }

   protected abstract void explode(Vec3 var1);

   @Override
   protected void onHitBlock(BlockHitResult $$0) {
      super.onHitBlock($$0);
      if (!this.level().isClientSide()) {
         Vec3i $$1 = $$0.getDirection().getUnitVec3i();
         Vec3 $$2 = Vec3.atLowerCornerOf($$1).multiply(0.25, 0.25, 0.25);
         Vec3 $$3 = $$0.getLocation().add($$2);
         this.explode($$3);
         this.discard();
      }
   }

   @Override
   protected void onHit(HitResult $$0) {
      super.onHit($$0);
      if (!this.level().isClientSide()) {
         this.discard();
      }
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }

   @Override
   public ItemStack getItem() {
      return ItemStack.EMPTY;
   }

   @Override
   protected float getInertia() {
      return 1.0F;
   }

   @Override
   protected float getLiquidInertia() {
      return this.getInertia();
   }

   @Nullable
   @Override
   protected ParticleOptions getTrailParticle() {
      return null;
   }

   @Override
   public void tick() {
      if (!this.level().isClientSide() && this.getBlockY() > this.level().getMaxY() + 30) {
         this.explode(this.position());
         this.discard();
      } else {
         super.tick();
      }
   }
}
