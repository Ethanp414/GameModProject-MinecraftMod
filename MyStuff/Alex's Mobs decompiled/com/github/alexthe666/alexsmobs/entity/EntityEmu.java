package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class EntityEmu extends Animal implements IAnimatedEntity, IHerdPanic {
   public static final Animation ANIMATION_DODGE_LEFT = Animation.create(10);
   public static final Animation ANIMATION_DODGE_RIGHT = Animation.create(10);
   public static final Animation ANIMATION_PECK_GROUND = Animation.create(25);
   public static final Animation ANIMATION_SCRATCH = Animation.create(20);
   public static final Animation ANIMATION_PUZZLED = Animation.create(30);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityEmu.class, EntityDataSerializers.f_135028_);
   private int animationTick;
   private Animation currentAnimation;
   private int revengeCooldown = 0;
   private boolean emuAttackedDirectly = false;
   public int timeUntilNextEgg = this.f_19796_.m_188503_(6000) + 6000;

   protected EntityEmu(EntityType type, Level world) {
      super(type, world);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 20.0).m_22268_(Attributes.f_22279_, 0.35F).m_22268_(Attributes.f_22281_, 3.0);
   }

   public static <T extends Mob> boolean canEmuSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.EMU_SPAWNS);
      return spawnBlock && worldIn.m_45524_(pos, 0) > 8;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.emuSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.EMU_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.EMU_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.EMU_HURT.get();
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int variant) {
      this.f_19804_.m_135381_(VARIANT, variant);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(VARIANT, 0);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new MeleeAttackGoal(this, 1.3, true) {
         protected double m_6639_(LivingEntity attackTarget) {
            return super.m_6639_(attackTarget) + 2.5;
         }

         public boolean m_8036_() {
            return super.m_8036_() && EntityEmu.this.revengeCooldown <= 0;
         }

         public boolean m_8045_() {
            return super.m_8045_() && EntityEmu.this.revengeCooldown <= 0;
         }
      });
      this.f_21345_.m_25352_(2, new AnimalAIHerdPanic(this, 1.5));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.EMU_BREEDABLES), false));
      this.f_21345_.m_25352_(5, new AnimalAIWanderRanged(this, 110, 1.0, 10, 7));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(7, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new EntityEmu.HurtByTargetGoal());
      if (AMConfig.emuTargetSkeletons) {
         this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
         this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Pillager.class, false));
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.EMU_BREEDABLES);
   }

   public boolean m_6779_(LivingEntity target) {
      return !this.m_6162_() && super.m_6779_(target);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         double range = 15.0;
         int fleeTime = 100 + this.m_217043_().m_188503_(5);
         this.revengeCooldown = fleeTime;

         for (EntityEmu emu : this.m_9236_().m_45976_(this.getClass(), this.m_20191_().m_82377_(range, range / 2.0, range))) {
            emu.revengeCooldown = fleeTime;
            if (emu.m_6162_() && this.f_19796_.m_188503_(2) == 0) {
               emu.emuAttackedDirectly = this.m_21188_() != null;
               emu.revengeCooldown = emu.emuAttackedDirectly ? 10 + this.m_217043_().m_188503_(30) : fleeTime;
            }
         }

         this.emuAttackedDirectly = this.m_21188_() != null;
         this.revengeCooldown = this.emuAttackedDirectly ? 10 + this.m_217043_().m_188503_(30) : this.revengeCooldown;
      }

      return prev;
   }

   public void m_7023_(Vec3 travelVector) {
      this.m_7910_(
         (float)this.m_21133_(Attributes.f_22279_)
            * (this.getAnimation() != ANIMATION_PECK_GROUND && this.getAnimation() != ANIMATION_PUZZLED ? 1.0F : 0.15F)
            * (this.m_20077_() ? 0.2F : 1.0F)
      );
      super.m_7023_(travelVector);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.m_21188_() == null
            && this.m_5448_() == null
            && this.m_20184_().m_82556_() < 0.03
            && this.m_217043_().m_188503_(190) == 0
            && this.getAnimation() == NO_ANIMATION) {
            if (this.m_217043_().m_188503_(3) == 0) {
               this.setAnimation(ANIMATION_PUZZLED);
            } else if (this.m_20096_()) {
               this.setAnimation(ANIMATION_PECK_GROUND);
            }
         }

         if (this.revengeCooldown > 0) {
            this.revengeCooldown--;
         }

         if (this.revengeCooldown <= 0 && this.m_21188_() != null && !this.emuAttackedDirectly) {
            this.m_6703_(null);
            this.revengeCooldown = 0;
         }

         LivingEntity target = this.m_5448_();
         if (this.m_6084_()
            && target != null
            && this.getAnimation() == ANIMATION_SCRATCH
            && this.m_20270_(target) < 4.0F
            && (this.getAnimationTick() == 8 || this.getAnimationTick() == 15)) {
            float f1 = this.m_146908_() * (float) (Math.PI / 180.0);
            this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f1) * 0.02F, 0.0, Mth.m_14089_(f1) * 0.02F));
            target.m_147240_(0.4F, target.m_20185_() - this.m_20185_(), target.m_20189_() - this.m_20189_());
            target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
         }
      }

      if (!this.m_9236_().f_46443_ && this.m_6084_() && !this.m_6162_() && --this.timeUntilNextEgg <= 0) {
         this.m_5496_(SoundEvents.f_11752_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
         this.m_19998_((ItemLike)AMItemRegistry.EMU_EGG.get());
         this.timeUntilNextEgg = this.f_19796_.m_188503_(6000) + 6000;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_DODGE_LEFT, ANIMATION_DODGE_RIGHT, ANIMATION_PECK_GROUND, ANIMATION_SCRATCH, ANIMATION_PUZZLED};
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntityEmu emu = (EntityEmu)((EntityType)AMEntityRegistry.EMU.get()).m_20615_(serverWorld);
      emu.setVariant(this.getVariant());
      return emu;
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SCRATCH);
      }

      return true;
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setVariant(compound.m_128451_("Variant"));
      if (compound.m_128441_("EggLayTime")) {
         this.timeUntilNextEgg = compound.m_128451_("EggLayTime");
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128405_("Variant", this.getVariant());
      compound.m_128405_("EggLayTime", this.timeUntilNextEgg);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (this.f_19796_.m_188503_(200) == 0) {
         this.setVariant(2);
      } else if (this.f_19796_.m_188503_(3) == 0) {
         this.setVariant(1);
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return true;
   }

   class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(EntityEmu.this, new Class[0]);
      }

      public void m_8056_() {
         if (!EntityEmu.this.m_6162_() && EntityEmu.this.emuAttackedDirectly) {
            super.m_8056_();
         } else {
            this.m_26047_();
            this.m_8041_();
         }
      }

      protected void m_5766_(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityEmu && !mobIn.m_6162_() && !EntityEmu.this.emuAttackedDirectly && ((EntityEmu)mobIn).revengeCooldown <= 0) {
            super.m_5766_(mobIn, targetIn);
         }
      }
   }
}
