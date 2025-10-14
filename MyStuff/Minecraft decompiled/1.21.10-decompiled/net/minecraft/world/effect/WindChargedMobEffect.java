package net.minecraft.world.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;

class WindChargedMobEffect extends MobEffect {
   protected WindChargedMobEffect(MobEffectCategory $$0, int $$1) {
      super($$0, $$1, ParticleTypes.SMALL_GUST);
   }

   @Override
   public void onMobRemoved(ServerLevel $$0, LivingEntity $$1, int $$2, Entity.RemovalReason $$3) {
      if ($$3 == Entity.RemovalReason.KILLED) {
         double $$4 = $$1.getX();
         double $$5 = $$1.getY() + (double)($$1.getBbHeight() / 2.0F);
         double $$6 = $$1.getZ();
         float $$7 = 3.0F + $$1.getRandom().nextFloat() * 2.0F;
         $$0.explode(
            $$1,
            null,
            AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
            $$4,
            $$5,
            $$6,
            $$7,
            false,
            Level.ExplosionInteraction.TRIGGER,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            WeightedList.of(),
            SoundEvents.BREEZE_WIND_CHARGE_BURST
         );
      }
   }
}
