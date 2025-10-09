package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.SnowLeopardAIMelee;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySnowLeopard extends Animal implements IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_ATTACK_R = Animation.create(13);
   public static final Animation ANIMATION_ATTACK_L = Animation.create(13);
   private int animationTick;
   private Animation currentAnimation;
   public float prevSneakProgress;
   public float sneakProgress;
   public float prevTackleProgress;
   public float tackleProgress;
   public float prevSitProgress;
   public float sitProgress;
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.m_135353_(EntitySnowLeopard.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.m_135353_(EntitySnowLeopard.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntitySnowLeopard.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SL_SNEAKING = SynchedEntityData.m_135353_(EntitySnowLeopard.class, EntityDataSerializers.f_135035_);
   private boolean hasSlowedDown = false;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   public float prevSleepProgress;
   public float sleepProgress;

   protected EntitySnowLeopard(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.m_274367_(2.0F);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, false);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.snowLeopardSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static <T extends Mob> boolean canSnowLeopardSpawn(
      EntityType<EntitySnowLeopard> snowleperd, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      return worldIn.m_8055_(p_223317_3_.m_7495_()).m_204336_(AMTagRegistry.SNOW_LEOPARD_SPAWNS) && worldIn.m_45524_(p_223317_3_, 0) > 8;
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SNOW_LEOPARD_BREEDABLES);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new AnimalAIPanicBaby(this, 1.25));
      this.f_21345_.m_25352_(3, new SnowLeopardAIMelee(this));
      this.f_21345_.m_25352_(5, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(7, new RandomStrollGoal(this, 1.0, 70));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this));
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.SNOW_LEOPARD_TARGETS)
            )
         );
      this.f_21346_.m_25352_(3, new CreatureAITargetItems(this, false, 30));
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22281_, 6.0)
         .m_22268_(Attributes.f_22279_, 0.35F)
         .m_22268_(Attributes.f_22277_, 64.0);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SNOW_LEOPARD_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SNOW_LEOPARD_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SNOW_LEOPARD_HURT.get();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(SLEEPING, false);
      this.f_19804_.m_135372_(SL_SNEAKING, false);
      this.f_19804_.m_135372_(TACKLING, false);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void setSitting(boolean bar) {
      this.f_19804_.m_135381_(SITTING, bar);
   }

   public boolean isTackling() {
      return (Boolean)this.f_19804_.m_135370_(TACKLING);
   }

   public void setTackling(boolean bar) {
      this.f_19804_.m_135381_(TACKLING, bar);
   }

   public boolean isSLSneaking() {
      return (Boolean)this.f_19804_.m_135370_(SL_SNEAKING);
   }

   public void setSlSneaking(boolean bar) {
      this.f_19804_.m_135381_(SL_SNEAKING, bar);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return (AgeableMob)((EntityType)AMEntityRegistry.SNOW_LEOPARD.get()).m_20615_(serverWorld);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevSitProgress = this.sitProgress;
      this.prevSneakProgress = this.sneakProgress;
      this.prevTackleProgress = this.tackleProgress;
      this.prevSleepProgress = this.sleepProgress;
      boolean sitting = this.isSitting();
      boolean slSneaking = this.isSLSneaking();
      boolean tackling = this.isTackling();
      boolean sleeping = this.m_5803_();
      if (sitting) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress += 0.5F;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      if (slSneaking) {
         if (this.sneakProgress < 5.0F) {
            this.sneakProgress += 0.5F;
         }
      } else if (this.sneakProgress > 0.0F) {
         this.sneakProgress -= 0.5F;
      }

      if (tackling) {
         if (this.tackleProgress < 3.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      if (sleeping) {
         if (this.sleepProgress < 5.0F) {
            this.sleepProgress += 0.5F;
         }
      } else if (this.sleepProgress > 0.0F) {
         this.sleepProgress -= 0.5F;
      }

      if (slSneaking && !this.hasSlowedDown) {
         this.hasSlowedDown = true;
         this.m_21051_(Attributes.f_22279_).m_22100_(0.25);
      }

      if (!slSneaking && this.hasSlowedDown) {
         this.hasSlowedDown = false;
         this.m_21051_(Attributes.f_22279_).m_22100_(0.35F);
      }

      if (tackling) {
         this.f_20883_ = this.m_146908_();
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_5448_() != null && (this.isSitting() || this.m_5803_())) {
            this.setSitting(false);
            this.setSleeping(false);
         }

         if ((this.isSitting() || this.m_5803_()) && (++this.sittingTime > this.maxSitTime || this.m_5448_() != null || this.m_27593_() || this.m_20072_())) {
            this.setSitting(false);
            this.setSleeping(false);
            this.sittingTime = 0;
            this.maxSitTime = 100 + this.f_19796_.m_188503_(50);
         }

         if (this.m_5448_() == null
            && this.m_20184_().m_82556_() < 0.03
            && this.getAnimation() == NO_ANIMATION
            && !this.m_5803_()
            && !this.isSitting()
            && !this.m_20072_()
            && this.f_19796_.m_188503_(340) == 0) {
            this.sittingTime = 0;
            if (this.m_217043_().m_188503_(2) != 0) {
               this.maxSitTime = 200 + this.f_19796_.m_188503_(800);
               this.setSitting(true);
               this.setSleeping(false);
            } else {
               this.maxSitTime = 2000 + this.f_19796_.m_188503_(2600);
               this.setSitting(false);
               this.setSleeping(true);
            }
         }
      }

      LivingEntity attackTarget = this.m_5448_();
      if (attackTarget != null && this.m_20270_(attackTarget) < attackTarget.m_20205_() + this.m_20205_() + 0.6 && this.m_142582_(attackTarget)) {
         if (this.getAnimation() == ANIMATION_ATTACK_L && this.getAnimationTick() == 7) {
            this.m_7327_(attackTarget);
            float rot = this.m_146908_() + 90.0F;
            attackTarget.m_147240_(0.5, Mth.m_14031_(rot * (float) (Math.PI / 180.0)), -Mth.m_14089_(rot * (float) (Math.PI / 180.0)));
         }

         if (this.getAnimation() == ANIMATION_ATTACK_R && this.getAnimationTick() == 7) {
            this.m_7327_(attackTarget);
            float rot = this.m_146908_() - 90.0F;
            attackTarget.m_147240_(0.5, Mth.m_14031_(rot * (float) (Math.PI / 180.0)), -Mth.m_14089_(rot * (float) (Math.PI / 180.0)));
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         this.sittingTime = 0;
         this.setSleeping(false);
         this.setSitting(false);
      }

      return prev;
   }

   public void m_7023_(Vec3 vec3d) {
      if (this.isSitting() || this.m_5803_()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         vec3d = Vec3.f_82478_;
      }

      super.m_7023_(vec3d);
   }

   protected boolean m_6107_() {
      return super.m_6107_();
   }

   public boolean m_5803_() {
      return (Boolean)this.f_19804_.m_135370_(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.f_19804_.m_135381_(SLEEPING, sleeping);
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK_L, ANIMATION_ATTACK_R};
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_41720_().m_41472_() && stack.m_41720_().m_41473_() != null && stack.m_41720_().m_41473_().m_38746_();
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.m_5634_(5.0F);
   }
}
