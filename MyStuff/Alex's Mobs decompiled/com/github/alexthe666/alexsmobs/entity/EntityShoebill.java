package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWadeSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.ShoebillAIFish;
import com.github.alexthe666.alexsmobs.entity.ai.ShoebillAIFlightFlee;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityShoebill extends Animal implements IAnimatedEntity, ITargetsDroppedItems {
   public static final Animation ANIMATION_FISH = Animation.create(40);
   public static final Animation ANIMATION_BEAKSHAKE = Animation.create(20);
   public static final Animation ANIMATION_ATTACK = Animation.create(20);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.m_135353_(EntityShoebill.class, EntityDataSerializers.f_135035_);
   public float prevFlyProgress;
   public float flyProgress;
   public int revengeCooldown = 0;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isLandNavigator;
   public int fishingCooldown = 1200 + this.f_19796_.m_188503_(1200);
   public int lureLevel = 0;
   public int luckLevel = 0;
   public static final Predicate<LivingEntity> TARGET_BABY = animal -> animal.m_6162_();

   protected EntityShoebill(EntityType type, Level world) {
      super(type, world);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.shoebillSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22281_, 4.0).m_22268_(Attributes.f_22279_, 0.2F);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SHOEBILL_HURT.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SHOEBILL_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SHOEBILL_HURT.get();
   }

   public boolean m_6898_(ItemStack stack) {
      return false;
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev && source.m_7639_() != null && !(source.m_7639_() instanceof AbstractFish)) {
         double range = 15.0;
         int fleeTime = 100 + this.m_217043_().m_188503_(150);
         this.revengeCooldown = fleeTime;

         for (EntityShoebill gaz : this.m_9236_().m_45976_(this.getClass(), this.m_20191_().m_82377_(range, range / 2.0, range))) {
            gaz.revengeCooldown = fleeTime;
         }
      }

      return prev;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new FlightMoveController(this, 0.7F, false);
         this.f_21344_ = new DirectPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FLYING, false);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new AnimalAIWadeSwimming(this));
      this.f_21345_.m_25352_(1, new ShoebillAIFish(this));
      this.f_21345_.m_25352_(3, new MeleeAttackGoal(this, 1.2, true));
      this.f_21345_.m_25352_(4, new ShoebillAIFlightFlee(this));
      this.f_21345_.m_25352_(5, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.SHOEBILL_FOODSTUFFS), false));
      this.f_21345_.m_25352_(6, new RandomStrollGoal(this, 1.0, 1400));
      this.f_21345_.m_25352_(7, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new EntityAINearestTarget3D(this, AbstractFish.class, 30, false, true, null));
      this.f_21346_.m_25352_(2, new CreatureAITargetItems(this, false, 10));
      this.f_21346_.m_25352_(3, new HurtByTargetGoal(this, new Class[]{Player.class}).m_26044_(new Class[0]));
      this.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(this, EntityAlligatorSnappingTurtle.class, 40, false, false, TARGET_BABY));
      this.f_21346_.m_25352_(5, new NearestAttackableTargetGoal(this, Turtle.class, 40, false, false, TARGET_BABY));
      this.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(this, EntityCrocodile.class, 40, false, false, TARGET_BABY));
      this.f_21346_.m_25352_(7, new NearestAttackableTargetGoal(this, EntityCaiman.class, 40, false, false, TARGET_BABY));
      this.f_21346_.m_25352_(8, new EntityAINearestTarget3D(this, EntityTerrapin.class, 100, false, true, null));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_20069_()) {
         this.m_274367_(1.2F);
      } else {
         this.m_274367_(0.6F);
      }

      this.prevFlyProgress = this.flyProgress;
      boolean flying = this.isFlying();
      if (flying) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.revengeCooldown > 0) {
         this.revengeCooldown--;
      }

      if (this.revengeCooldown == 0 && this.m_21188_() != null) {
         this.m_6703_(null);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.fishingCooldown > 0) {
            this.fishingCooldown--;
         }

         if (this.getAnimation() == NO_ANIMATION && this.m_217043_().m_188503_(700) == 0) {
            this.setAnimation(ANIMATION_BEAKSHAKE);
         }

         if (flying) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.revengeCooldown > 0 && !this.isFlying() && (this.m_20096_() || this.m_20069_())) {
            this.setFlying(false);
         }

         if (this.isFlying()) {
            this.m_20242_(true);
         } else {
            this.m_20242_(false);
         }
      }

      if (!this.m_9236_().f_46443_
         && this.m_5448_() != null
         && this.getAnimation() == ANIMATION_ATTACK
         && this.getAnimationTick() == 9
         && this.m_142582_(this.m_5448_())) {
         this.m_5448_().m_147240_(0.3F, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
         this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Flying", this.isFlying());
      compound.m_128405_("FishingTimer", this.fishingCooldown);
      compound.m_128405_("FishingLuck", this.luckLevel);
      compound.m_128405_("FishingLure", this.lureLevel);
      compound.m_128405_("RevengeCooldownTimer", this.revengeCooldown);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setFlying(compound.m_128471_("Flying"));
      this.fishingCooldown = compound.m_128451_("FishingTimer");
      this.luckLevel = compound.m_128451_("FishingLuck");
      this.lureLevel = compound.m_128451_("FishingLure");
      this.revengeCooldown = compound.m_128451_("RevengeCooldownTimer");
   }

   protected float m_6108_() {
      return 0.98F;
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.f_19804_.m_135370_(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      this.f_19804_.m_135381_(FLYING, flying);
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
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_FISH, ANIMATION_BEAKSHAKE, ANIMATION_ATTACK};
   }

   public InteractionResult m_6071_(Player p_230254_1_, InteractionHand p_230254_2_) {
      ItemStack lvt_3_1_ = p_230254_1_.m_21120_(p_230254_2_);
      if (lvt_3_1_.m_204117_(AMTagRegistry.SHOEBILL_LUCK_FOODS) && this.m_6084_()) {
         if (this.luckLevel >= 10) {
            if (this.getAnimation() == NO_ANIMATION) {
               this.setAnimation(ANIMATION_BEAKSHAKE);
            }

            return InteractionResult.SUCCESS;
         } else {
            this.luckLevel = Mth.m_14045_(this.luckLevel + 1, 0, 10);

            for (int i = 0; i < 6 + this.f_19796_.m_188503_(3); i++) {
               double d2 = this.f_19796_.m_188583_() * 0.02;
               double d0 = this.f_19796_.m_188583_() * 0.02;
               double d1 = this.f_19796_.m_188583_() * 0.02;
               this.m_9236_()
                  .m_7106_(
                     new ItemParticleOption(ParticleTypes.f_123752_, lvt_3_1_),
                     this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                     this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11788_, this.m_6121_(), this.m_6100_());
            lvt_3_1_.m_41774_(1);
            return InteractionResult.m_19078_(this.m_9236_().f_46443_);
         }
      } else if (!lvt_3_1_.m_204117_(AMTagRegistry.SHOEBILL_LURE_FOODS) || !this.m_6084_()) {
         return super.m_6071_(p_230254_1_, p_230254_2_);
      } else if (this.lureLevel >= 10) {
         if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_BEAKSHAKE);
         }

         return InteractionResult.SUCCESS;
      } else {
         this.lureLevel = Mth.m_14045_(this.lureLevel + 1, 0, 10);
         this.fishingCooldown = Mth.m_14045_(this.fishingCooldown - 200, 200, 2400);

         for (int i = 0; i < 6 + this.f_19796_.m_188503_(3); i++) {
            double d2 = this.f_19796_.m_188583_() * 0.02;
            double d0 = this.f_19796_.m_188583_() * 0.02;
            double d1 = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, lvt_3_1_),
                  this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                  this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }

         lvt_3_1_.m_41774_(1);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11788_, this.m_6121_(), this.m_6100_());
         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      }
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return (AgeableMob)((EntityType)AMEntityRegistry.SHOEBILL.get()).m_20615_(serverWorld);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SHOEBILL_FOODSTUFFS)
         || stack.m_204117_(AMTagRegistry.SHOEBILL_LUCK_FOODS) && this.luckLevel < 10
         || stack.m_204117_(AMTagRegistry.SHOEBILL_LURE_FOODS) && this.lureLevel < 10;
   }

   public void resetFishingCooldown() {
      this.fishingCooldown = Math.max(1200 + this.f_19796_.m_188503_(1200) - this.lureLevel * 120, 200);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.m_146850_(GameEvent.f_157806_);
      this.m_5496_(SoundEvents.f_11788_, this.m_6121_(), this.m_6100_());
      if (e.m_32055_().m_204117_(AMTagRegistry.SHOEBILL_LUCK_FOODS)) {
         this.luckLevel = Mth.m_14045_(this.luckLevel + 1, 0, 10);
      } else if (e.m_32055_().m_204117_(AMTagRegistry.SHOEBILL_LURE_FOODS)) {
         this.lureLevel = Mth.m_14045_(this.lureLevel + 1, 0, 10);
      }

      this.m_5634_(5.0F);
   }
}
