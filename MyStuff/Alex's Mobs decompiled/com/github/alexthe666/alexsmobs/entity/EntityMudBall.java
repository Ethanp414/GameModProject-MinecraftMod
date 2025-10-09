package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityMudBall extends EntityMobProjectile {
   public EntityMudBall(EntityType type, Level level) {
      super(type, level);
   }

   public EntityMudBall(Level worldIn, EntityMudskipper mudskipper) {
      super((EntityType)AMEntityRegistry.MUD_BALL.get(), worldIn, mudskipper);
      Vec3 vec3 = mudskipper.m_20182_().m_82549_(this.calcOffsetVec(new Vec3(0.0, 0.0, 0.2F * mudskipper.m_6134_()), 0.0F, mudskipper.m_146908_()));
      this.m_6034_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
   }

   public EntityMudBall(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.MUD_BALL.get(), world);
   }

   @Override
   public void doBehavior() {
      this.m_20256_(this.m_20184_().m_82490_(0.9F));
      if (!this.m_20068_()) {
         this.m_20256_(this.m_20184_().m_82520_(0.0, -0.06F, 0.0));
      }
   }

   @Override
   protected boolean removeInWater() {
      return false;
   }

   @Override
   protected float getDamage() {
      return 1 + this.f_19796_.m_188503_(3);
   }

   @Override
   protected void onEntityHit(EntityHitResult result) {
      super.onEntityHit(result);
      if (result.m_82443_() instanceof LivingEntity hurt) {
         hurt.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60));
      }
   }

   public void m_7822_(byte event) {
      if (event == 3) {
         ParticleOptions particle = new BlockParticleOption(ParticleTypes.f_123794_, Blocks.f_220864_.m_49966_());

         for (int i = 0; i < 8; i++) {
            this.m_9236_().m_7106_(particle, this.m_20185_(), this.m_20186_(), this.m_20189_(), 0.0, 0.0, 0.0);
         }
      } else {
         super.m_7822_(event);
      }
   }

   @Override
   protected void onImpact(HitResult result) {
      if (!this.m_9236_().f_46443_) {
         this.m_9236_().m_7605_(this, (byte)3);
      }

      super.onImpact(result);
   }
}
