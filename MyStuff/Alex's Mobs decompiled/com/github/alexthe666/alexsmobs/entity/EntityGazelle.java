package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class EntityGazelle extends Animal implements IAnimatedEntity, IHerdPanic {
   private int animationTick;
   private Animation currentAnimation;
   public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
   public static final Animation ANIMATION_FLICK_TAIL = Animation.create(14);
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
   private boolean hasSpedUp = false;
   private int revengeCooldown = 0;
   private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.m_135353_(EntityGazelle.class, EntityDataSerializers.f_135035_);

   protected EntityGazelle(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new AnimalAIHerdPanic(this, 1.1));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.GAZELLE_BREEDABLES), false));
      this.f_21345_.m_25352_(5, new AnimalAIWanderRanged(this, 100, 1.0, 25, 7));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(7, new RandomLookAroundGoal(this));
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.GAZELLE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.GAZELLE_HURT.get();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.gazelleSpawnRolls, this.m_217043_(), spawnReasonIn) && super.m_5545_(worldIn, spawnReasonIn);
   }

   public int m_5792_() {
      return 8;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         double range = 15.0;
         int fleeTime = 100 + this.m_217043_().m_188503_(150);
         this.revengeCooldown = fleeTime;

         for (EntityGazelle gaz : this.m_9236_().m_45976_(this.getClass(), this.m_20191_().m_82377_(range, range / 2.0, range))) {
            gaz.revengeCooldown = fleeTime;
         }
      }

      return prev;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(RUNNING, false);
   }

   public boolean isRunning() {
      return (Boolean)this.f_19804_.m_135370_(RUNNING);
   }

   public void setRunning(boolean running) {
      this.f_19804_.m_135381_(RUNNING, running);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.GAZELLE_BREEDABLES);
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.getAnimation() == NO_ANIMATION && this.m_217043_().m_188503_(70) == 0 && (this.m_21188_() == null || this.m_20270_(this.m_21188_()) > 30.0F)) {
            if (this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_) && this.m_217043_().m_188503_(3) == 0) {
               this.setAnimation(ANIMATION_EAT_GRASS);
            } else {
               this.setAnimation(this.m_217043_().m_188499_() ? ANIMATION_FLICK_EARS : ANIMATION_FLICK_TAIL);
            }
         }

         if (this.revengeCooldown >= 0) {
            this.revengeCooldown--;
         }

         if (this.revengeCooldown == 0 && this.m_21188_() != null) {
            this.m_6703_(null);
         }

         this.setRunning(this.revengeCooldown > 0);
         if (this.isRunning() && !this.hasSpedUp) {
            this.hasSpedUp = true;
            this.m_6858_(true);
            this.m_21051_(Attributes.f_22279_).m_22100_(0.475F);
         }

         if (!this.isRunning() && this.hasSpedUp) {
            this.hasSpedUp = false;
            this.m_6858_(false);
            this.m_21051_(Attributes.f_22279_).m_22100_(0.25);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("GazelleRunning", this.isRunning());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setRunning(compound.m_128471_("GazelleRunning"));
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_FLICK_TAIL, ANIMATION_EAT_GRASS};
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 8.0).m_22268_(Attributes.f_22281_, 2.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.GAZELLE.get()).m_20615_(p_241840_1_);
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return true;
   }
}
