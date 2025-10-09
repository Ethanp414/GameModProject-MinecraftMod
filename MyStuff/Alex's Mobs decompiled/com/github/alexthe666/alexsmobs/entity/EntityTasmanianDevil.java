package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityTasmanianDevil extends Animal implements IAnimatedEntity, ITargetsDroppedItems {
   private int animationTick;
   private Animation currentAnimation;
   public static final Animation ANIMATION_HOWL = Animation.create(40);
   public static final Animation ANIMATION_ATTACK = Animation.create(8);
   private static final EntityDataAccessor<Boolean> BASKING = SynchedEntityData.m_135353_(EntityTasmanianDevil.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityTasmanianDevil.class, EntityDataSerializers.f_135035_);
   public float prevBaskProgress;
   public float prevSitProgress;
   public float baskProgress;
   public float sitProgress;
   private int sittingTime;
   private int maxSitTime;
   private int scareMobsTime = 0;

   protected EntityTasmanianDevil(EntityType type, Level world) {
      super(type, world);
   }

   public boolean shouldMove() {
      return !this.isSitting() && !this.isBasking();
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.TASMANIAN_DEVIL_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.TASMANIAN_DEVIL_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.TASMANIAN_DEVIL_HURT.get();
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new MeleeAttackGoal(this, 1.5, true));
      this.f_21345_.m_25352_(2, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.TASMANIAN_DEVIL_HOWLING_FOODS), false) {
         public void m_8037_() {
            super.m_8037_();
            if (EntityTasmanianDevil.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               EntityTasmanianDevil.this.setBasking(false);
               EntityTasmanianDevil.this.setSitting(false);
            }
         }
      });
      this.f_21345_.m_25352_(3, new RandomStrollGoal(this, 1.0, 60));
      this.f_21345_.m_25352_(4, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[]{EntityTasmanianDevil.class}).m_26044_(new Class[0]));
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this, Animal.class, 120, false, false, p_213487_0_ -> p_213487_0_ instanceof Chicken || p_213487_0_ instanceof Rabbit
            )
         );
      this.f_21346_.m_25352_(3, new CreatureAITargetItems(this, false, 30));
   }

   public void killed(ServerLevel world, LivingEntity entity) {
      if (this.m_217043_().m_188499_() && (entity instanceof Animal || entity.m_6336_() == MobType.f_21641_)) {
         entity.m_19983_(new ItemStack(Items.f_42500_));
      }
   }

   public void m_7023_(Vec3 vec3d) {
      if (!this.shouldMove()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         vec3d = Vec3.f_82478_;
      }

      super.m_7023_(vec3d);
   }

   public void setSitting(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(BASKING, false);
      this.f_19804_.m_135372_(SITTING, false);
   }

   public boolean isBasking() {
      return (Boolean)this.f_19804_.m_135370_(BASKING);
   }

   public void setBasking(boolean basking) {
      this.f_19804_.m_135381_(BASKING, basking);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 14.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22279_, 0.3F)
         .m_22268_(Attributes.f_22281_, 2.0);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_41720_().m_41472_()
         && stack.m_41720_().m_41473_() != null
         && stack.m_41720_().m_41473_().m_38746_()
         && !stack.m_204117_(AMTagRegistry.TASMANIAN_DEVIL_HOWLING_FOODS);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevBaskProgress = this.baskProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isBasking()) {
         if (this.baskProgress < 5.0F) {
            this.baskProgress++;
         }
      } else if (this.baskProgress > 0.0F) {
         this.baskProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_5448_() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 5 && this.m_142582_(this.m_5448_())) {
            float f1 = this.m_146908_() * (float) (Math.PI / 180.0);
            this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f1) * 0.02F, 0.0, Mth.m_14089_(f1) * 0.02F));
            this.m_5448_().m_147240_(1.0, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
         }

         if ((this.isSitting() || this.isBasking()) && ++this.sittingTime > this.maxSitTime) {
            this.setSitting(false);
            this.setBasking(false);
            this.sittingTime = 0;
            this.maxSitTime = 75 + this.f_19796_.m_188503_(50);
         }

         if (this.m_20184_().m_82556_() < 0.03
            && this.getAnimation() == NO_ANIMATION
            && !this.isBasking()
            && !this.isSitting()
            && this.f_19796_.m_188503_(100) == 0) {
            this.sittingTime = 0;
            this.maxSitTime = 100 + this.f_19796_.m_188503_(550);
            if (this.m_217043_().m_188499_()) {
               this.setSitting(true);
               this.setBasking(false);
            } else {
               this.setSitting(false);
               this.setBasking(true);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() == 1) {
         this.m_146850_(GameEvent.f_223709_);
         this.m_5496_((SoundEvent)AMSoundRegistry.TASMANIAN_DEVIL_ROAR.get(), this.m_6121_() * 2.0F, this.m_6100_());
      }

      if (this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() > 3) {
         this.scareMobsTime = 40;
      }

      if (this.scareMobsTime > 0) {
         for (Monster e : this.m_9236_().m_45976_(Monster.class, this.m_20191_().m_82377_(16.0, 8.0, 16.0))) {
            e.m_6710_(null);
            e.m_6703_(null);
            if (this.scareMobsTime % 5 == 0) {
               Vec3 vec = LandRandomPos.m_148521_(e, 20, 7, this.m_20182_());
               if (vec != null) {
                  e.m_21573_().m_26519_(vec.f_82479_, vec.f_82480_, vec.f_82481_, 1.5);
               }
            }
         }

         this.scareMobsTime--;
      }

      if (this.m_5448_() != null && this.m_5448_().m_6084_() && (this.m_21188_() == null || !this.m_21188_().m_6084_())) {
         this.m_6703_(this.m_5448_());
      }

      if ((this.isSitting() || this.isBasking()) && (this.m_5448_() != null || this.m_27593_())) {
         this.setSitting(false);
         this.setBasking(false);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (itemstack.m_204117_(AMTagRegistry.TASMANIAN_DEVIL_HOWLING_FOODS) && this.getAnimation() != ANIMATION_HOWL) {
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11947_, this.m_6121_(), this.m_6100_());
         this.m_19983_(item.getCraftingRemainingItem(itemstack));
         if (!player.m_7500_()) {
            itemstack.m_41774_(1);
         }

         this.setAnimation(ANIMATION_HOWL);
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
      if (animation == ANIMATION_HOWL) {
         this.setSitting(true);
         this.setBasking(false);
         this.maxSitTime = Math.max(25, this.maxSitTime);
      }
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK, ANIMATION_HOWL};
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return (AgeableMob)((EntityType)AMEntityRegistry.TASMANIAN_DEVIL.get()).m_20615_(serverWorld);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_41720_().m_41472_() && stack.m_41720_().m_41473_() != null && stack.m_41720_().m_41473_().m_38746_() || stack.m_41720_() == Items.f_42500_;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.m_146850_(GameEvent.f_157806_);
      if (e.m_32055_().m_41720_() == Items.f_42500_) {
         this.dropBonemeal();
         this.m_5496_(SoundEvents.f_12383_, this.m_6121_(), this.m_6100_());
      } else {
         this.m_5496_(SoundEvents.f_11947_, this.m_6121_(), this.m_6100_());
         this.m_5634_(5.0F);
      }
   }

   public void dropBonemeal() {
      ItemStack stack = new ItemStack(Items.f_42499_);

      for (int i = 0; i < 3 + this.f_19796_.m_188503_(1); i++) {
         this.m_19983_(stack);
      }
   }
}
