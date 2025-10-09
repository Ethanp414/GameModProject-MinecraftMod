package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CrocodileAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.CrocodileAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityCrocodile extends TamableAnimal implements IAnimatedEntity, ISemiAquatic {
   public static final Animation ANIMATION_LUNGE = Animation.create(23);
   public static final Animation ANIMATION_DEATHROLL = Animation.create(40);
   public static final Predicate<Entity> NOT_CREEPER = entity -> entity.m_6084_() && !(entity instanceof Creeper);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DESERT = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> IS_DIGGING = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.m_135353_(EntityCrocodile.class, EntityDataSerializers.f_135028_);
   public float groundProgress = 0.0F;
   public float prevGroundProgress = 0.0F;
   public float swimProgress = 0.0F;
   public float prevSwimProgress = 0.0F;
   public float baskingProgress = 0.0F;
   public float prevBaskingProgress = 0.0F;
   public float grabProgress = 0.0F;
   public float prevGrabProgress = 0.0F;
   public int baskingType = 0;
   public boolean forcedSit = false;
   private int baskingTimer = 0;
   private int swimTimer = -1000;
   private int ticksSinceInWater = 0;
   private int passengerTimer = 0;
   private boolean isLandNavigator;
   private boolean hasSpedUp = false;
   private int animationTick;
   private Animation currentAnimation;

   protected EntityCrocodile(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
      this.baskingType = this.f_19796_.m_188503_(1);
   }

   public static boolean canCrocodileSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.CROCODILE_SPAWNS);
      return spawnBlock && pos.m_123342_() < worldIn.m_5736_() + 4;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 15.0)
         .m_22268_(Attributes.f_22284_, 8.0)
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22278_, 0.4F)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.crocSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public int m_5792_() {
      return 2;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   protected void m_30232_() {
      super.m_30232_();
      if (!this.m_6162_() && this.m_9236_().m_46469_().m_46207_(GameRules.f_46135_)) {
         this.m_5552_(new ItemStack((ItemLike)AMItemRegistry.CROCODILE_SCUTE.get(), this.f_19796_.m_188503_(1) + 1), 1.0F);
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setDesert(this.isBiomeDesert(worldIn, this.m_20183_()));
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   private boolean isBiomeDesert(LevelAccessor worldIn, BlockPos position) {
      return worldIn.m_204166_(position).m_203656_(AMTagRegistry.SPAWNS_DESERT_CROCODILES);
   }

   protected SoundEvent m_7515_() {
      return this.m_6162_() ? (SoundEvent)AMSoundRegistry.CROCODILE_BABY.get() : (SoundEvent)AMSoundRegistry.CROCODILE_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.CROCODILE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.CROCODILE_HURT.get();
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("CrocodileSitting", this.isSitting());
      compound.m_128379_("Desert", this.isDesert());
      compound.m_128379_("ForcedToSit", this.forcedSit);
      compound.m_128405_("BaskingStyle", this.baskingType);
      compound.m_128405_("BaskingTimer", this.baskingTimer);
      compound.m_128405_("SwimTimer", this.swimTimer);
      compound.m_128405_("StunTimer", this.getStunTicks());
      compound.m_128379_("HasEgg", this.hasEgg());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_21839_(compound.m_128471_("CrocodileSitting"));
      this.setDesert(compound.m_128471_("Desert"));
      this.forcedSit = compound.m_128471_("ForcedToSit");
      this.baskingType = compound.m_128451_("BaskingStyle");
      this.baskingTimer = compound.m_128451_("BaskingTimer");
      this.swimTimer = compound.m_128451_("SwimTimer");
      this.setHasEgg(compound.m_128471_("HasEgg"));
      this.setStunTicks(compound.m_128451_("StunTimer"));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         PathNavigation prevNav = this.f_21344_;
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new AquaticMoveController(this, 1.0F);
         PathNavigation prevNav = this.f_21344_;
         this.f_21344_ = new SemiAquaticPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(DESERT, false);
      this.f_19804_.m_135372_(HAS_EGG, false);
      this.f_19804_.m_135372_(IS_DIGGING, false);
      this.f_19804_.m_135372_(CLIMBING, (byte)0);
      this.f_19804_.m_135372_(STUN_TICKS, 0);
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.f_19804_.m_135370_(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = (Byte)this.f_19804_.m_135370_(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.f_19804_.m_135381_(CLIMBING, b0);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevGroundProgress = this.groundProgress;
      this.prevSwimProgress = this.swimProgress;
      this.prevBaskingProgress = this.baskingProgress;
      this.prevGrabProgress = this.grabProgress;
      boolean ground = !this.m_20069_();
      boolean groundAnimate = !this.m_20069_();
      boolean basking = groundAnimate && this.isSitting();
      boolean grabbing = !this.m_20197_().isEmpty();
      if (!ground && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (ground && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (groundAnimate) {
         if (this.groundProgress < 10.0F) {
            this.groundProgress++;
         }

         if (this.swimProgress > 0.0F) {
            this.swimProgress--;
         }
      } else {
         if (this.groundProgress > 0.0F) {
            this.groundProgress--;
         }

         if (this.swimProgress < 10.0F) {
            this.swimProgress++;
         }
      }

      if (basking) {
         if (this.baskingProgress < 10.0F) {
            this.baskingProgress++;
         }
      } else if (this.baskingProgress > 0.0F) {
         this.baskingProgress--;
      }

      if (grabbing) {
         if (this.grabProgress < 10.0F) {
            this.grabProgress++;
         }
      } else if (this.grabProgress > 0.0F) {
         this.grabProgress--;
      }

      if (this.m_5448_() == null) {
         if (this.hasSpedUp) {
            this.hasSpedUp = false;
            this.m_21051_(Attributes.f_22279_).m_22100_(0.25);
         }
      } else if (!this.hasSpedUp) {
         this.hasSpedUp = true;
         this.m_21051_(Attributes.f_22279_).m_22100_(0.28F);
      }

      if (!this.m_9236_().f_46443_) {
         this.setBesideClimbableBlock(this.f_19862_);
      }

      if (this.baskingTimer < 0) {
         this.baskingTimer++;
      }

      if (this.passengerTimer > 0 && this.m_20197_().isEmpty()) {
         this.passengerTimer = 0;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_20069_()) {
            this.swimTimer++;
            this.ticksSinceInWater = 0;
         } else {
            this.ticksSinceInWater++;
            this.swimTimer--;
         }

         if (!this.m_20069_() && this.m_20096_() && !this.m_21824_()) {
            if (!this.isSitting() && this.baskingTimer == 0 && this.m_5448_() == null && this.m_21573_().m_26571_()) {
               this.m_21839_(true);
               this.baskingTimer = 1000 + this.f_19796_.m_188503_(750);
            }

            if (this.isSitting() && (this.baskingTimer <= 0 || this.m_5448_() != null || this.swimTimer < -1000)) {
               this.m_21839_(false);
               this.baskingTimer = -2000 - this.f_19796_.m_188503_(750);
            }

            if (this.isSitting() && this.baskingTimer > 0) {
               this.baskingTimer--;
            }
         }

         if (this.getStunTicks() == 0
            && this.m_6084_()
            && this.m_5448_() != null
            && this.getAnimation() == ANIMATION_LUNGE
            && (this.m_9236_().m_46791_() != Difficulty.PEACEFUL || !(this.m_5448_() instanceof Player))
            && this.getAnimationTick() > 5
            && this.getAnimationTick() < 9) {
            float f1 = this.m_146908_() * (float) (Math.PI / 180.0);
            this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f1) * 0.02F, 0.0, Mth.m_14089_(f1) * 0.02F));
            if (this.m_20270_(this.m_5448_()) < 3.5F && this.m_142582_(this.m_5448_())) {
               boolean flag = this.m_5448_().m_21254_();
               if (!flag && this.m_5448_().m_20205_() < this.m_20205_() && this.m_20197_().isEmpty() && !this.m_5448_().m_6144_()) {
                  this.m_5448_().m_7998_(this, true);
               }

               if (flag) {
                  if (this.m_5448_() instanceof Player player) {
                     this.damageShieldFor(player, (float)this.m_21051_(Attributes.f_22281_).m_22115_());
                  }

                  if (this.getStunTicks() == 0) {
                     this.setStunTicks(25 + this.f_19796_.m_188503_(20));
                  }
               } else {
                  this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
               }

               this.m_5496_((SoundEvent)AMSoundRegistry.CROCODILE_BITE.get(), this.m_6121_(), this.m_6100_());
            }
         }

         if (this.m_6084_()
            && this.m_5448_() != null
            && this.m_20069_()
            && (this.m_9236_().m_46791_() != Difficulty.PEACEFUL || !(this.m_5448_() instanceof Player))
            && this.m_5448_().m_20202_() != null
            && this.m_5448_().m_20202_() == this) {
            if (this.getAnimation() == NO_ANIMATION) {
               this.setAnimation(ANIMATION_DEATHROLL);
            }

            if (this.getAnimation() == ANIMATION_DEATHROLL && this.getAnimationTick() % 10 == 0 && this.m_20270_(this.m_5448_()) < 5.0) {
               this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), 5.0F);
            }
         }
      }

      if (this.getAnimation() == ANIMATION_DEATHROLL) {
         this.m_21573_().m_26573_();
      }

      if (this.m_27593_() && this.m_5448_() != null) {
         this.m_6710_(null);
      }

      if (this.getStunTicks() > 0) {
         this.setStunTicks(this.getStunTicks() - 1);
         if (this.m_9236_().f_46443_) {
            float angle = (float) (Math.PI / 180.0) * this.f_20883_;
            double headX = 1.5F * this.m_6134_() * Mth.m_14031_((float) Math.PI + angle);
            double headZ = 1.5F * this.m_6134_() * Mth.m_14089_(angle);

            for (int i = 0; i < 5; i++) {
               float innerAngle = (float) (Math.PI / 180.0) * (this.f_20883_ + this.f_19797_ * 5) * (i + 1);
               double extraX = 0.5F * Mth.m_14031_((float)(Math.PI + innerAngle));
               double extraZ = 0.5F * Mth.m_14089_(innerAngle);
               this.m_9236_()
                  .m_6493_(
                     ParticleTypes.f_123797_, true, this.m_20185_() + headX + extraX, this.m_20188_() + 0.5, this.m_20189_() + headZ + extraZ, 0.0, 0.0, 0.0
                  );
            }
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   protected void damageShieldFor(Player holder, float damage) {
      if (holder.m_21211_().canPerformAction(ToolActions.SHIELD_BLOCK)) {
         if (!this.m_9236_().f_46443_) {
            holder.m_36246_(Stats.f_12982_.m_12902_(holder.m_21211_().m_41720_()));
         }

         if (damage >= 3.0F) {
            int i = 1 + Mth.m_14143_(damage);
            InteractionHand hand = holder.m_7655_();
            holder.m_21211_().m_41622_(i, holder, p_213833_1_ -> {
               p_213833_1_.m_21190_(hand);
               ForgeEventFactory.onPlayerDestroyItem(holder, holder.m_21211_(), hand);
            });
            if (holder.m_21211_().m_41619_()) {
               if (hand == InteractionHand.MAIN_HAND) {
                  holder.m_8061_(EquipmentSlot.MAINHAND, ItemStack.f_41583_);
               } else {
                  holder.m_8061_(EquipmentSlot.OFFHAND, ItemStack.f_41583_);
               }

               holder.m_5496_(SoundEvents.f_12347_, 0.8F, 0.8F + this.m_9236_().f_46441_.m_188501_() * 0.4F);
            }
         }
      }
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.getStunTicks() > 0;
   }

   public boolean canRiderInteract() {
      return true;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean m_7307_(Entity entityIn) {
      if (this.m_21824_()) {
         LivingEntity livingentity = this.m_269323_();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).m_21830_(livingentity);
         }

         if (livingentity != null) {
            return livingentity.m_7307_(entityIn);
         }
      }

      return super.m_7307_(entityIn);
   }

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (!this.m_20197_().isEmpty()) {
         this.f_20883_ = Mth.m_14177_(this.m_146908_() - 180.0F);
      }

      if (this.m_20363_(passenger)) {
         float radius = 2.0F;
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = 2.0F * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = 2.0F * Mth.m_14089_(angle);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + 0.1F, this.m_20189_() + extraZ);
         this.passengerTimer++;
         if (this.m_6084_() && this.passengerTimer > 0 && this.passengerTimer % 40 == 0) {
            passenger.m_6469_(this.m_269291_().m_269333_(this), 2.0F);
         }
      }
   }

   @Nullable
   public LivingEntity m_6688_() {
      return null;
   }

   public boolean m_6147_() {
      return this.m_20069_() && this.isBesideClimbableBlock();
   }

   public boolean m_6063_() {
      return false;
   }

   public boolean m_6914_(LevelReader worldIn) {
      return worldIn.m_45784_(this);
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION && this.m_20197_().isEmpty() && this.getStunTicks() == 0) {
         this.setAnimation(ANIMATION_LUNGE);
      }

      return true;
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.isSitting()) {
         super.m_7023_(Vec3.f_82478_);
      } else if (this.m_21515_() && this.m_20069_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.9));
         if (this.m_5448_() == null) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
         }
      } else {
         super.m_7023_(travelVector);
      }
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268722_) || source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_6040_() {
      return true;
   }

   public float m_5610_(BlockPos pos, LevelReader worldIn) {
      return super.m_5610_(pos, worldIn);
   }

   @Override
   public boolean shouldLeaveWater() {
      if (!this.m_20197_().isEmpty()) {
         return false;
      } else {
         return this.m_5448_() != null && !this.m_5448_().m_20069_() ? true : this.swimTimer > 600;
      }
   }

   @Override
   public boolean shouldStopMoving() {
      return this.getAnimation() == ANIMATION_DEATHROLL || this.isSitting();
   }

   @Override
   public int getWaterSearchRange() {
      return this.m_20197_().isEmpty() ? 15 : 45;
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public boolean isDesert() {
      return (Boolean)this.f_19804_.m_135370_(DESERT);
   }

   public void setDesert(boolean desert) {
      this.f_19804_.m_135381_(DESERT, desert);
   }

   public boolean hasEgg() {
      return (Boolean)this.f_19804_.m_135370_(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.f_19804_.m_135381_(HAS_EGG, hasEgg);
   }

   public boolean isDigging() {
      return (Boolean)this.f_19804_.m_135370_(IS_DIGGING);
   }

   private void setDigging(boolean isDigging) {
      this.f_19804_.m_135381_(IS_DIGGING, isDigging);
   }

   public int getStunTicks() {
      return (Integer)this.f_19804_.m_135370_(STUN_TICKS);
   }

   private void setStunTicks(int stun) {
      this.f_19804_.m_135381_(STUN_TICKS, stun);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(1, new EntityCrocodile.MateGoal(this, 1.0));
      this.f_21345_.m_25352_(1, new EntityCrocodile.LayEggGoal(this, 1.0));
      this.f_21345_.m_25352_(2, new BreathAirGoal(this));
      this.f_21345_.m_25352_(2, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(2, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(4, new CrocodileAIMelee(this, 1.0, true));
      this.f_21345_.m_25352_(5, new CrocodileAIRandomSwimming(this, 1.0, 7));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this).m_26044_(new Class[0]));
      this.f_21346_.m_25352_(2, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(3, new OwnerHurtTargetGoal(this));
      this.f_21346_
         .m_25352_(
            4,
            new EntityAINearestTarget3D(this, Player.class, 80, false, true, null) {
               public boolean m_8036_() {
                  return !EntityCrocodile.this.m_6162_()
                     && !EntityCrocodile.this.m_21824_()
                     && EntityCrocodile.this.m_9236_().m_46791_() != Difficulty.PEACEFUL
                     && super.m_8036_();
               }
            }
         );
      this.f_21346_
         .m_25352_(
            5,
            new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CROCODILE_TARGETS)) {
               public boolean m_8036_() {
                  return !EntityCrocodile.this.m_6162_() && !EntityCrocodile.this.m_21824_() && super.m_8036_();
               }
            }
         );
      this.f_21346_.m_25352_(6, new EntityAINearestTarget3D(this, Monster.class, 180, false, true, NOT_CREEPER) {
         public boolean m_8036_() {
            return !EntityCrocodile.this.m_6162_() && EntityCrocodile.this.m_21824_() && super.m_8036_();
         }
      });
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         this.m_21839_(false);
         if (entity != null && this.m_21824_() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 3.0F;
         }

         return super.m_6469_(source, amount);
      }
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.CROCODILE.get()).m_20615_(p_241840_1_);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      if (item == Items.f_42656_) {
         return super.m_6071_(player, hand);
      } else if (this.m_21824_() && item.m_41472_() && item.m_41473_() != null && item.m_41473_().m_38746_() && this.m_21223_() < this.m_21233_()) {
         this.m_142075_(player, hand, itemstack);
         this.m_5634_(10.0F);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult type = super.m_6071_(player, hand);
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.m_21824_()
            && this.m_21830_(player)
            && !this.m_6898_(itemstack)) {
            if (this.isSitting()) {
               this.forcedSit = false;
               this.m_21839_(false);
            } else {
               this.forcedSit = true;
               this.m_21839_(true);
            }

            return InteractionResult.SUCCESS;
         } else {
            return type;
         }
      }
   }

   public void m_6710_(@Nullable LivingEntity entitylivingbaseIn) {
      if (!this.m_6162_()) {
         super.m_6710_(entitylivingbaseIn);
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.CROCODILE_BREEDABLES);
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.m_20197_().isEmpty()
         ? true
         : this.m_5448_() == null && !this.isSitting() && this.baskingTimer <= 0 && !this.shouldLeaveWater() && this.swimTimer <= -1000;
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
      return new Animation[]{ANIMATION_LUNGE, ANIMATION_DEATHROLL};
   }

   public boolean isCrowned() {
      String s = ChatFormatting.m_126649_(this.m_7755_().getString());
      return s != null && (s.toLowerCase().contains("crown") || s.toLowerCase().contains("king") || s.toLowerCase().contains("rool"));
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final EntityCrocodile turtle;
      private int digTime;

      LayEggGoal(EntityCrocodile turtle, double speedIn) {
         super(turtle, speedIn, 16);
         this.turtle = turtle;
      }

      public void m_8041_() {
         this.digTime = 0;
      }

      public boolean m_8036_() {
         return this.turtle.hasEgg() && super.m_8036_();
      }

      public boolean m_8045_() {
         return super.m_8045_() && this.turtle.hasEgg();
      }

      public double m_8052_() {
         return this.turtle.m_20205_() + 0.5;
      }

      public void m_8037_() {
         super.m_8037_();
         this.turtle.m_21839_(false);
         this.turtle.baskingTimer = -100;
         if (!this.turtle.m_20069_() && this.m_25625_()) {
            BlockPos blockpos = this.turtle.m_20183_();
            Level world = this.turtle.m_9236_();
            this.turtle.m_146850_(GameEvent.f_157797_);
            world.m_5594_(null, blockpos, SoundEvents.f_12486_, SoundSource.BLOCKS, 0.3F, 0.9F + world.f_46441_.m_188501_() * 0.2F);
            world.m_7731_(
               this.f_25602_.m_7494_(),
               (BlockState)((Block)AMBlockRegistry.CROCODILE_EGG.get()).m_49966_().m_61124_(BlockReptileEgg.EGGS, this.turtle.f_19796_.m_188503_(1) + 1),
               3
            );
            this.turtle.setHasEgg(false);
            this.turtle.setDigging(false);
            this.turtle.m_27601_(600);
         }
      }

      protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
         return worldIn.m_46859_(pos.m_7494_()) && BlockReptileEgg.isProperHabitat(worldIn, pos);
      }
   }

   static class MateGoal extends BreedGoal {
      private final EntityCrocodile crocodile;

      MateGoal(EntityCrocodile crocodile, double speedIn) {
         super(crocodile, speedIn);
         this.crocodile = crocodile;
      }

      public boolean m_8036_() {
         return super.m_8036_() && !this.crocodile.hasEgg();
      }

      protected void m_8026_() {
         ServerPlayer serverplayerentity = this.f_25113_.m_27592_();
         if (serverplayerentity == null && this.f_25115_.m_27592_() != null) {
            serverplayerentity = this.f_25115_.m_27592_();
         }

         if (serverplayerentity != null) {
            serverplayerentity.m_36220_(Stats.f_12937_);
            CriteriaTriggers.f_10581_.m_147278_(serverplayerentity, this.f_25113_, this.f_25115_, this.f_25113_);
         }

         this.crocodile.setHasEgg(true);
         this.f_25113_.m_27594_();
         this.f_25115_.m_27594_();
         this.f_25113_.m_146762_(6000);
         this.f_25115_.m_146762_(6000);
         if (this.f_25114_.m_46469_().m_46207_(GameRules.f_46135_)) {
            RandomSource random = this.f_25113_.m_217043_();
            this.f_25114_
               .m_7967_(new ExperienceOrb(this.f_25114_, this.f_25113_.m_20185_(), this.f_25113_.m_20186_(), this.f_25113_.m_20189_(), random.m_188503_(7) + 1));
         }
      }
   }
}
