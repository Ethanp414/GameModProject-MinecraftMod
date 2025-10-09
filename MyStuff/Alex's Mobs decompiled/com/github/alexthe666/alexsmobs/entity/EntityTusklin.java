package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIMeleeNearby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIRide;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityTusklin extends Animal implements IAnimatedEntity {
   public static final Animation ANIMATION_RUT = Animation.create(26);
   public static final Animation ANIMATION_GORE_L = Animation.create(25);
   public static final Animation ANIMATION_GORE_R = Animation.create(25);
   public static final Animation ANIMATION_FLING = Animation.create(15);
   public static final Animation ANIMATION_BUCK = Animation.create(15);
   private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.m_135353_(EntityTusklin.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> PASSIVETICKS = SynchedEntityData.m_135353_(EntityTusklin.class, EntityDataSerializers.f_135028_);
   private int animationTick;
   private Animation currentAnimation;
   private int ridingTime = 0;
   private int entityToLaunchId = -1;
   private int conversionTime = 0;

   protected EntityTusklin(EntityType<? extends Animal> type, Level level) {
      super(type, level);
      this.m_274367_(1.1F);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.tusklinSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static boolean canTusklinSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return worldIn.m_45524_(pos, 0) > 8
         && (worldIn.m_8055_(pos.m_7495_()).m_280296_() || worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.TUSKLIN_SPAWNS));
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 40.0)
         .m_22268_(Attributes.f_22281_, 9.0)
         .m_22268_(Attributes.f_22279_, 0.3F)
         .m_22268_(Attributes.f_22278_, 0.9F);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.TUSKLIN_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.TUSKLIN_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.TUSKLIN_HURT.get();
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public boolean isInNether() {
      return this.m_9236_().m_46472_() == Level.f_46429_ && !this.m_21525_();
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new AnimalAIMeleeNearby(this, 15, 1.25));
      this.f_21345_.m_25352_(3, new TameableAIRide(this, 2.0, false) {
         @Override
         public boolean shouldMoveForward() {
            return true;
         }

         @Override
         public boolean shouldMoveBackwards() {
            return false;
         }
      });
      this.f_21345_.m_25352_(4, new AnimalAIPanicBaby(this, 1.25));
      this.f_21345_.m_25352_(5, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(7, new AnimalAIWanderRanged(this, 120, 0.6F, 14, 7));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this).m_26044_(new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, 100, true, false, this::isAngryAt));
   }

   public boolean isAngryAt(LivingEntity p_21675_) {
      return this.m_6779_(p_21675_);
   }

   protected Vec3 m_274312_(Player player, Vec3 deltaIn) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   protected void m_274498_(Player player, Vec3 vec3) {
      super.m_274498_(player, vec3);
      this.m_19915_(player.m_146908_(), player.m_146909_() * 0.25F);
      this.f_19859_ = this.f_20883_ = this.f_20885_ = this.m_146908_();
      this.m_274367_(1.0F);
      this.m_21573_().m_26573_();
      this.m_6710_(null);
      this.m_6858_(true);
   }

   protected float m_245547_(Player rider) {
      return (float)this.m_21133_(Attributes.f_22279_);
   }

   @Nullable
   public LivingEntity m_6688_() {
      if (this.isSaddled()) {
         for (Entity passenger : this.m_20197_()) {
            if (passenger instanceof Player player) {
               return player;
            }
         }
      }

      return null;
   }

   public boolean m_6779_(LivingEntity entity) {
      boolean prev = super.m_6779_(entity);
      return !(entity instanceof Player)
            || this.m_21188_() != null && this.m_21188_().equals(entity)
            || this.getPassiveTicks() <= 0
               && !this.isMushroom(entity.m_21120_(InteractionHand.MAIN_HAND))
               && !this.isMushroom(entity.m_21120_(InteractionHand.OFF_HAND))
         ? prev
         : false;
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         int anim = this.f_19796_.m_188503_(3);
         switch (anim) {
            case 0:
               this.setAnimation(ANIMATION_FLING);
               break;
            case 1:
               this.setAnimation(ANIMATION_GORE_L);
               break;
            case 2:
               this.setAnimation(ANIMATION_GORE_R);
         }
      }

      return true;
   }

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (this.m_20363_(passenger)) {
         float radius = 0.4F;
         if (this.getAnimation() == ANIMATION_GORE_L || this.getAnimation() == ANIMATION_GORE_R) {
            if (this.getAnimationTick() <= 4) {
               radius -= this.getAnimationTick() * 0.1F;
            } else {
               radius -= -0.4F + Math.min(this.getAnimationTick() - 4, 4) * 0.1F;
            }
         }

         if (this.getAnimation() == ANIMATION_BUCK) {
            if (this.getAnimationTick() < 5) {
               radius -= this.getAnimationTick() * 0.1F;
            } else if (this.getAnimationTick() < 10) {
               radius -= 0.4F - (this.getAnimationTick() - 5) * 0.1F;
            }
         }

         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + this.m_6048_() + passenger.m_6049_(), this.m_20189_() + extraZ);
      }
   }

   public double m_6048_() {
      float f = this.f_267362_.m_267756_();
      float f1 = this.f_267362_.m_267731_();
      float f2 = 0.0F;
      if (this.getAnimation() == ANIMATION_FLING) {
         if (this.getAnimationTick() <= 3.0F) {
            f2 = this.getAnimationTick() * -0.1F;
         } else {
            f2 = -0.3F + Mth.m_14045_(this.getAnimationTick() - 3, 0, 3) * 0.1F;
         }
      }

      if (this.getAnimation() == ANIMATION_BUCK) {
         if (this.getAnimationTick() < 5) {
            f2 = this.getAnimationTick() * 0.2F * 0.8F;
         } else if (this.getAnimationTick() < 10) {
            f2 = (0.8F - (this.getAnimationTick() - 5) * 0.2F) * 0.8F;
         }
      }

      return this.m_20206_() - 0.3 + (float)Math.abs(Math.sin(f * 0.7F) * f1 * 0.0625 * 1.6F) + f2;
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      if (item == Items.f_42450_ && !this.isSaddled() && !this.m_6162_()) {
         if (!player.m_7500_()) {
            itemstack.m_41774_(1);
         }

         this.setSaddled(true);
         return InteractionResult.SUCCESS;
      } else if (item == AMItemRegistry.PIGSHOES.get() && this.getShoeStack().m_41619_() && !this.m_6162_()) {
         this.setShoeStack(itemstack.m_41777_());
         if (!player.m_7500_()) {
            itemstack.m_41774_(1);
         }

         return InteractionResult.SUCCESS;
      } else if (!this.isMushroom(itemstack) || this.getPassiveTicks() > 0 && !(this.m_21223_() < this.m_21233_())) {
         InteractionResult type = super.m_6071_(player, hand);
         if (type != InteractionResult.SUCCESS
            && !this.m_6898_(itemstack)
            && !player.m_6144_()
            && !this.m_6162_()
            && this.isSaddled()
            && this.getAnimation() != ANIMATION_BUCK) {
            player.m_20329_(this);
            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      } else {
         if (!player.m_7500_()) {
            itemstack.m_41774_(1);
         }

         this.m_5634_(6.0F);
         this.setPassiveTicks(this.getPassiveTicks() + 1200);
         return InteractionResult.SUCCESS;
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.TUSKLIN_BREEDABLES);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.m_20088_().m_135372_(SADDLED, false);
      this.m_20088_().m_135372_(PASSIVETICKS, 0);
   }

   public void m_7380_(CompoundTag p_31808_) {
      super.m_7380_(p_31808_);
      if (!this.getShoeStack().m_41619_()) {
         p_31808_.m_128365_("ShoeItem", this.getShoeStack().m_41739_(new CompoundTag()));
      }

      p_31808_.m_128405_("PassiveTicks", this.getPassiveTicks());
      p_31808_.m_128379_("Saddle", this.isSaddled());
   }

   public void m_7378_(CompoundTag p_31795_) {
      super.m_7378_(p_31795_);
      this.setSaddled(p_31795_.m_128471_("Saddle"));
      this.setPassiveTicks(p_31795_.m_128451_("PassiveTicks"));
      CompoundTag compoundtag = p_31795_.m_128469_("ShoeItem");
      if (compoundtag != null && !compoundtag.m_128456_()) {
         ItemStack itemstack = ItemStack.m_41712_(compoundtag);
         if (itemstack.m_41619_()) {
            AlexsMobs.LOGGER.warn("Unable to load item from: {}", compoundtag);
         }

         this.setShoeStack(itemstack);
      }
   }

   public boolean isMushroom(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.TUSKLIN_FOODSTUFFS);
   }

   public int getPassiveTicks() {
      return (Integer)this.f_19804_.m_135370_(PASSIVETICKS);
   }

   private void setPassiveTicks(int passiveTicks) {
      this.f_19804_.m_135381_(PASSIVETICKS, passiveTicks);
   }

   public boolean isSaddled() {
      return (Boolean)this.f_19804_.m_135370_(SADDLED);
   }

   public void setSaddled(boolean saddled) {
      this.f_19804_.m_135381_(SADDLED, saddled);
   }

   protected void m_5907_() {
      super.m_5907_();
      if (this.isSaddled() && !this.m_9236_().f_46443_) {
         this.m_19998_(Items.f_42450_);
      }

      if (!this.getShoeStack().m_41619_() && !this.m_9236_().f_46443_) {
         this.m_19983_(this.getShoeStack().m_41777_());
      }

      this.setSaddled(false);
      this.setShoeStack(ItemStack.f_41583_);
   }

   public ItemStack getShoeStack() {
      return this.m_6844_(EquipmentSlot.FEET);
   }

   public void setShoeStack(ItemStack shoe) {
      this.m_8061_(EquipmentSlot.FEET, shoe);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.isInNether()) {
         this.conversionTime++;
         if (this.conversionTime > 300 && !this.m_9236_().f_46443_) {
            Hoglin hoglin = (Hoglin)this.m_21406_(EntityType.f_20456_, false);
            if (hoglin != null) {
               hoglin.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 200, 0));
               this.m_5907_();
               this.m_9236_().m_7967_(hoglin);
               this.m_142687_(RemovalReason.DISCARDED);
            }
         }
      }

      if (this.entityToLaunchId != -1 && this.m_6084_()) {
         Entity launch = this.m_9236_().m_6815_(this.entityToLaunchId);
         this.m_20153_();
         this.entityToLaunchId = -1;
         if (launch != null && !launch.m_20159_() && launch instanceof LivingEntity) {
            launch.m_146884_(this.m_146892_().m_82520_(0.0, 1.0, 0.0));
            float rot = 180.0F + this.m_146908_();
            float strength = (float)(this.getLaunchStrength() * (1.0 - ((LivingEntity)launch).m_21133_(Attributes.f_22278_)));
            float x = Mth.m_14031_(rot * (float) (Math.PI / 180.0));
            float z = -Mth.m_14089_(rot * (float) (Math.PI / 180.0));
            if (!(strength <= 0.0)) {
               launch.f_19812_ = true;
               Vec3 vec3 = this.m_20184_();
               Vec3 vec31 = vec3.m_82549_(new Vec3(x, 0.0, z).m_82541_().m_82490_(strength));
               launch.m_20334_(vec31.f_82479_, strength, vec31.f_82481_);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_BUCK && this.getAnimationTick() >= 5) {
         Entity passenger = this.m_6688_();
         if (passenger instanceof LivingEntity) {
            this.entityToLaunchId = passenger.m_19879_();
         }
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_20160_()) {
            this.ridingTime++;
            if (this.ridingTime >= this.getMaxRidingTime() && this.getAnimation() != ANIMATION_BUCK) {
               this.setAnimation(ANIMATION_BUCK);
            }
         } else {
            this.ridingTime = 0;
         }

         if (this.m_6084_() && this.ridingTime > 0 && this.m_20184_().m_165925_() > 0.1) {
            for (Entity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(1.0))) {
               if (!(entity instanceof EntityTusklin) && !entity.m_20365_(this)) {
                  entity.m_6469_(this.m_269291_().m_269333_(this), 4.0F + this.f_19796_.m_188501_() * 3.0F);
                  if (entity.m_20096_()) {
                     double d0 = entity.m_20185_() - this.m_20185_();
                     double d1 = entity.m_20189_() - this.m_20189_();
                     double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                     float f = 0.5F;
                     entity.m_5997_(d0 / d2 * f, f, d1 / d2 * f);
                  }
               }
            }

            this.m_274367_(2.0F);
         } else {
            this.m_274367_(1.1F);
         }

         if (this.m_5448_() != null && this.m_142582_(this.m_5448_()) && this.m_20270_(this.m_5448_()) < this.m_5448_().m_20205_() + this.m_20205_() + 1.8F) {
            if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 6) {
               this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
               this.knockbackTarget(this.m_5448_(), 0.9F, 0.0F);
            }

            if (this.getAnimation() == ANIMATION_GORE_L && this.getAnimationTick() == 6) {
               this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
               this.knockbackTarget(this.m_5448_(), 0.5F, -90.0F);
            }

            if (this.getAnimation() == ANIMATION_GORE_R && this.getAnimationTick() == 6) {
               this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
               this.knockbackTarget(this.m_5448_(), 0.5F, 90.0F);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_RUT
         && this.getAnimationTick() == 23
         && this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_)
         && this.m_217043_().m_188503_(3) == 0) {
         if (this.m_6162_() && this.m_9236_().m_8055_(this.m_20183_()).m_247087_() && this.f_19796_.m_188503_(3) == 0) {
            this.m_9236_().m_46597_(this.m_20183_(), Blocks.f_50072_.m_49966_());
            this.m_146850_(GameEvent.f_157794_);
            this.m_5496_(SoundEvents.f_11839_, this.m_6121_(), this.m_6100_());
         }

         this.m_9236_().m_46796_(2001, this.m_20183_().m_7495_(), Block.m_49956_(Blocks.f_50440_.m_49966_()));
         this.m_9236_().m_7731_(this.m_20183_().m_7495_(), Blocks.f_50493_.m_49966_(), 2);
         this.m_5634_(5.0F);
      }

      if (!this.m_9236_().f_46443_
         && this.getAnimation() == NO_ANIMATION
         && this.m_217043_().m_188503_(this.m_6162_() ? 140 : 70) == 0
         && (this.m_21188_() == null || this.m_20270_(this.m_21188_()) > 30.0F)
         && this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_)
         && this.m_217043_().m_188503_(3) == 0) {
         this.setAnimation(ANIMATION_RUT);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private float getLaunchStrength() {
      return this.getShoeStack().m_150930_((Item)AMItemRegistry.PIGSHOES.get()) ? 0.4F : 0.9F;
   }

   private int getMaxRidingTime() {
      return this.getShoeStack().m_150930_((Item)AMItemRegistry.PIGSHOES.get()) ? 160 : 60;
   }

   private void knockbackTarget(LivingEntity entity, float strength, float angle) {
      float rot = this.m_146908_() + angle;
      if (entity != null) {
         entity.m_147240_(strength, Mth.m_14031_(rot * (float) (Math.PI / 180.0)), -Mth.m_14089_(rot * (float) (Math.PI / 180.0)));
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

   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(0.34F);
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_RUT, ANIMATION_GORE_L, ANIMATION_GORE_R, ANIMATION_FLING, ANIMATION_BUCK};
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel level, AgeableMob mob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.TUSKLIN.get()).m_20615_(this.m_9236_());
   }
}
