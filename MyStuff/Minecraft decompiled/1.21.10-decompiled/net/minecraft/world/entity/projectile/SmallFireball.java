package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmallFireball extends Fireball {
   public SmallFireball(EntityType<? extends SmallFireball> $$0, Level $$1) {
      super($$0, $$1);
   }

   public SmallFireball(Level $$0, LivingEntity $$1, Vec3 $$2) {
      super(EntityType.SMALL_FIREBALL, $$1, $$2, $$0);
   }

   public SmallFireball(Level $$0, double $$1, double $$2, double $$3, Vec3 $$4) {
      super(EntityType.SMALL_FIREBALL, $$1, $$2, $$3, $$4, $$0);
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      Level $$3 = this.level();
      if ($$3 instanceof ServerLevel $$1) {
         Entity var7 = $$0.getEntity();
         Entity $$4 = this.getOwner();
         int $$5 = var7.getRemainingFireTicks();
         var7.igniteForSeconds(5.0F);
         DamageSource $$6 = this.damageSources().fireball(this, $$4);
         if (!var7.hurtServer($$1, $$6, 5.0F)) {
            var7.setRemainingFireTicks($$5);
         } else {
            EnchantmentHelper.doPostAttackEffects($$1, var7, $$6);
         }
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult $$0) {
      super.onHitBlock($$0);
      Level $$3 = this.level();
      if ($$3 instanceof ServerLevel) {
         ServerLevel $$1 = (ServerLevel)$$3;
         Entity $$3x = this.getOwner();
         if (!($$3x instanceof Mob) || $$1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos $$4 = $$0.getBlockPos().relative($$0.getDirection());
            if (this.level().isEmptyBlock($$4)) {
               this.level().setBlockAndUpdate($$4, BaseFireBlock.getState(this.level(), $$4));
            }
         }
      }
   }

   @Override
   protected void onHit(HitResult $$0) {
      super.onHit($$0);
      if (!this.level().isClientSide()) {
         this.discard();
      }
   }
}
