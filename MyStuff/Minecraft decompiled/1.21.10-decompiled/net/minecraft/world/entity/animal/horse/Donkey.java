package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class Donkey extends AbstractChestedHorse {
   public Donkey(EntityType<? extends Donkey> $$0, Level $$1) {
      super($$0, $$1);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.DONKEY_AMBIENT;
   }

   @Override
   protected SoundEvent getAngrySound() {
      return SoundEvents.DONKEY_ANGRY;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.DONKEY_DEATH;
   }

   @Nullable
   @Override
   protected SoundEvent getEatingSound() {
      return SoundEvents.DONKEY_EAT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource $$0) {
      return SoundEvents.DONKEY_HURT;
   }

   @Override
   public boolean canMate(Animal $$0) {
      if ($$0 == this) {
         return false;
      } else if (!($$0 instanceof Donkey) && !($$0 instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)$$0).canParent();
      }
   }

   @Override
   protected void playJumpSound() {
      this.playSound(SoundEvents.DONKEY_JUMP, 0.4F, 1.0F);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
      EntityType<? extends AbstractHorse> $$2 = $$1 instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorse $$3 = $$2.create($$0, EntitySpawnReason.BREEDING);
      if ($$3 != null) {
         this.setOffspringAttributes($$1, $$3);
      }

      return $$3;
   }
}
