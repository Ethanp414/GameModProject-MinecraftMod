package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeapRandomly;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.BunfungusAIBeg;
import com.github.alexthe666.alexsmobs.entity.ai.BunfungusAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityBunfungus extends PathfinderMob implements IAnimatedEntity {
   public static final Animation ANIMATION_SLAM = Animation.create(20);
   public static final Animation ANIMATION_BELLY = Animation.create(10);
   public static final Animation ANIMATION_EAT = Animation.create(20);
   private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.m_135353_(EntityBunfungus.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.m_135353_(EntityBunfungus.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.m_135353_(EntityBunfungus.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> CARROTED = SynchedEntityData.m_135353_(EntityBunfungus.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> TRANSFORMS_IN = SynchedEntityData.m_135353_(EntityBunfungus.class, EntityDataSerializers.f_135028_);
   public float jumpProgress;
   public float prevJumpProgress;
   public float reboundProgress;
   public float prevReboundProgress;
   public float sleepProgress;
   public float prevSleepProgress;
   public float interestedProgress;
   public float prevInterestedProgress;
   private int animationTick;
   private Animation currentAnimation;
   public int prevTransformTime;
   public static final int MAX_TRANSFORM_TIME = 50;

   protected EntityBunfungus(EntityType t, Level lvl) {
      super(t, lvl);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 80.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22279_, 0.21F);
   }

   public void m_8032_() {
      if (!this.m_5803_()) {
         super.m_8032_();
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.BUNFUNGUS_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.BUNFUNGUS_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.BUNFUNGUS_HURT.get();
   }

   public boolean m_6785_(double p_27598_) {
      return false;
   }

   public static boolean canBunfungusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return worldIn.m_8055_(pos.m_7495_()).m_60815_();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mungusSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new GroundPathNavigatorWide(this, worldIn);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new BunfungusAIMelee(this));
      this.f_21345_.m_25352_(2, new BunfungusAIBeg(this, 1.0));
      this.f_21345_.m_25352_(3, new AnimalAIWanderRanged(this, 60, 1.0, 16, 7) {
         @Override
         public boolean m_8036_() {
            return super.m_8036_() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.f_21345_.m_25352_(4, new AnimalAILeapRandomly(this, 60, 7) {
         @Override
         public boolean m_8036_() {
            return super.m_8036_() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, Player.class, 10.0F) {
         public boolean m_8036_() {
            return super.m_8036_() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.f_21345_.m_25352_(10, new RandomLookAroundGoal(this) {
         public boolean m_8036_() {
            return super.m_8036_() && EntityBunfungus.this.canUseComplexAI();
         }
      });
      this.f_21346_.m_25352_(2, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_
         .m_25352_(
            3,
            new NearestAttackableTargetGoal(
               this,
               Mob.class,
               5,
               false,
               false,
               mob -> mob instanceof Enemy
                  && !(mob instanceof Creeper)
                  && (mob.m_6336_() != MobType.f_21644_ || !mob.m_20072_())
                  && !mob.m_6095_().m_204039_(AMTagRegistry.BUNFUNGUS_IGNORES)
            )
         );
   }

   private boolean canUseComplexAI() {
      return !this.isRabbitForm() && !this.m_5803_();
   }

   protected float m_6108_() {
      return 0.98F;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(JUMP_ACTIVE, false);
      this.f_19804_.m_135372_(SLEEPING, false);
      this.f_19804_.m_135372_(BEGGING, false);
      this.f_19804_.m_135372_(CARROTED, false);
      this.f_19804_.m_135372_(TRANSFORMS_IN, 0);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevJumpProgress = this.jumpProgress;
      this.prevReboundProgress = this.reboundProgress;
      this.prevSleepProgress = this.sleepProgress;
      this.prevInterestedProgress = this.interestedProgress;
      this.prevTransformTime = this.transformsIn();
      if (!this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(JUMP_ACTIVE, !this.m_20096_());
      }

      if ((Boolean)this.f_19804_.m_135370_(JUMP_ACTIVE) && !this.m_20072_()) {
         if (this.jumpProgress < 5.0F) {
            this.jumpProgress += 0.5F;
            if (this.reboundProgress > 0.0F) {
               this.reboundProgress--;
            }
         }

         if (this.jumpProgress >= 5.0F && this.reboundProgress < 5.0F) {
            this.reboundProgress += 0.5F;
         }
      } else {
         if (this.reboundProgress > 0.0F) {
            this.reboundProgress = Math.max(this.reboundProgress - 1.0F, 0.0F);
         }

         if (this.jumpProgress > 0.0F) {
            this.jumpProgress = Math.max(this.jumpProgress - 1.0F, 0.0F);
         }
      }

      if (this.isSleepingPose()) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress++;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress--;
      }

      if (this.isBegging()) {
         if (this.interestedProgress < 5.0F) {
            this.interestedProgress++;
         }
      } else if (this.interestedProgress > 0.0F) {
         this.interestedProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         LivingEntity target = this.m_5448_();
         if (target != null && target.m_6084_()) {
            if (this.m_5803_()) {
               this.setSleeping(false);
            }

            double dist = this.m_20270_(target);
            boolean flag = false;
            if (this.getAnimationTick() == 5) {
               if (dist < 3.5 && this.getAnimation() == ANIMATION_BELLY) {
                  for (LivingEntity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(2.0))) {
                     if ((entity == target || entity instanceof Monster) && !entity.m_6095_().m_204039_(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                        flag = true;
                        this.launch(entity);
                        entity.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
                     }
                  }
               } else if (dist < 2.5 && this.getAnimation() == ANIMATION_SLAM) {
                  for (LivingEntity entityx : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(2.0))) {
                     if ((entityx == target || entityx instanceof Monster) && !entityx.m_6095_().m_204039_(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                        flag = true;
                        entityx.m_147240_(0.2F, entityx.m_20185_() - this.m_20185_(), entityx.m_20189_() - this.m_20189_());
                        entityx.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
                     }
                  }
               }
            }

            if (flag) {
               this.m_5496_((SoundEvent)AMSoundRegistry.BUNFUNGUS_ATTACK.get(), this.m_6121_(), this.m_6100_());
            }
         }

         if (this.f_19797_ % 40 == 0) {
            this.m_5634_(1.0F);
         }
      }

      if (this.getAnimation() == NO_ANIMATION && this.isCarrot(this.m_21120_(InteractionHand.MAIN_HAND))) {
         this.setAnimation(ANIMATION_EAT);
      }

      if (this.getAnimation() == ANIMATION_EAT) {
         if (this.getAnimationTick() % 4 == 0) {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
         }

         if (this.getAnimationTick() >= 18) {
            ItemStack stack = this.m_21120_(InteractionHand.MAIN_HAND);
            if (!stack.m_41619_()) {
               stack.m_41774_(1);
               this.setCarroted(true);
               this.m_7292_(new MobEffectInstance(MobEffects.f_19600_, 1000));
               this.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 1000, 1));
               this.m_5634_(8.0F);
            }
         } else {
            for (int i = 0; i < 3; i++) {
               double d2 = this.f_19796_.m_188583_() * 0.02;
               double d0 = this.f_19796_.m_188583_() * 0.02;
               double d1 = this.f_19796_.m_188583_() * 0.02;
               this.m_9236_()
                  .m_7106_(
                     new ItemParticleOption(ParticleTypes.f_123752_, this.m_21120_(InteractionHand.MAIN_HAND)),
                     this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                     this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }
         }
      }

      if (!this.m_9236_().f_46443_ && this.transformsIn() > 0) {
         this.setTransformsIn(this.transformsIn() - 1);
      }

      if (this.m_9236_().f_46443_) {
         if (this.isRabbitForm()) {
            for (int i = 0; i < 3; i++) {
               double d2 = this.f_19796_.m_188583_() * 0.02;
               double d0 = this.f_19796_.m_188583_() * 0.02;
               double d1 = this.f_19796_.m_188583_() * 0.02;
               float f1 = (50 - this.transformsIn()) / 50.0F;
               float scale = f1 * 0.5F + 0.15F;
               this.m_9236_()
                  .m_7106_(
                     (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(),
                     this.m_20208_(scale),
                     this.m_20227_(this.f_19796_.m_188500_() * scale),
                     this.m_20262_(scale),
                     d0,
                     d1,
                     d2
                  );
            }
         }

         if (this.m_5803_() && this.f_19796_.m_188501_() < 0.3F) {
            double d0 = this.f_19796_.m_188583_() * 0.02;
            float radius = this.m_20205_() * (0.7F + this.f_19796_.m_188501_() * 0.1F);
            float angle = (float) (Math.PI / 180.0) * this.f_20883_;
            double extraX = radius * Mth.m_14031_((float) Math.PI + angle) + this.f_19796_.m_188501_() * 0.5F - 0.25F;
            double extraZ = radius * Mth.m_14089_(angle) + this.f_19796_.m_188501_() * 0.5F - 0.25F;
            ParticleOptions data = this.f_19796_.m_188501_() < 0.3F
               ? (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get()
               : (ParticleOptions)AMParticleRegistry.FUNGUS_BUBBLE.get();
            this.m_9236_().m_7106_(data, this.m_20185_() + extraX, this.m_20186_() + this.f_19796_.m_188501_() * 0.1F, this.m_20189_() + extraZ, 0.0, d0, 0.0);
         }
      } else if (this.m_9236_().m_46461_() && this.m_5448_() == null && !this.isBegging() && !this.m_20072_()) {
         if (this.f_19797_ % 10 == 0 && this.m_217043_().m_188503_(300) == 0) {
            this.setSleeping(true);
         }
      } else if (this.m_5803_()) {
         this.setSleeping(false);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void launch(LivingEntity target) {
      if (target.m_20096_()) {
         double d0 = target.m_20185_() - this.m_20185_();
         double d1 = target.m_20189_() - this.m_20189_();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = 6.0F + this.f_19796_.m_188501_() * 2.0F;
         target.m_5997_(d0 / d2 * f, 0.6F + this.f_19796_.m_188501_() * 0.7F, d1 / d2 * f);
      }
   }

   public boolean m_5803_() {
      return (Boolean)this.f_19804_.m_135370_(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.f_19804_.m_135381_(SLEEPING, sleeping);
   }

   public boolean isSleepingPose() {
      return this.m_5803_() || this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() < 10;
   }

   public boolean isCarroted() {
      return (Boolean)this.f_19804_.m_135370_(CARROTED);
   }

   public void setCarroted(boolean head) {
      this.f_19804_.m_135381_(CARROTED, head);
   }

   public boolean isBegging() {
      return (Boolean)this.f_19804_.m_135370_(BEGGING) && this.getAnimation() != ANIMATION_EAT;
   }

   public void setBegging(boolean begging) {
      this.f_19804_.m_135381_(BEGGING, begging);
   }

   public int transformsIn() {
      return Math.min((Integer)this.f_19804_.m_135370_(TRANSFORMS_IN), 50);
   }

   public boolean isRabbitForm() {
      return this.transformsIn() > 0;
   }

   public void setTransformsIn(int time) {
      this.f_19804_.m_135381_(TRANSFORMS_IN, time);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
      if (interactionresult != InteractionResult.SUCCESS
         && type != InteractionResult.SUCCESS
         && this.m_21120_(InteractionHand.MAIN_HAND).m_41619_()
         && this.isCarrot(itemstack)
         && this.m_21205_().m_41619_()) {
         ItemStack cop = itemstack.m_41777_();
         cop.m_41764_(1);
         this.m_21008_(InteractionHand.MAIN_HAND, cop);
         if (!player.m_7500_()) {
            itemstack.m_41774_(1);
         }
      }

      return type;
   }

   public void m_7023_(Vec3 travelVector) {
      if (!this.isRabbitForm() && !this.m_5803_()) {
         super.m_7023_(travelVector);
      } else {
         super.m_7023_(Vec3.f_82478_);
      }
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_EAT, ANIMATION_BELLY, ANIMATION_SLAM};
   }

   public boolean isCarrot(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.BUNFUNGUS_FOODSTUFFS);
   }

   public boolean defendsMungusAgainst(LivingEntity lastHurtByMob) {
      return !(lastHurtByMob instanceof Player) || this.isCarroted();
   }

   public void onJump() {
   }
}
