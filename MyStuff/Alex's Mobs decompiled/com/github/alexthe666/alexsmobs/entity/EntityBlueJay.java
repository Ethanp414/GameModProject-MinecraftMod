package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.BlueJayAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAITempt;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityBlueJay extends Animal implements ITargetsDroppedItems {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> CREST_TARGET = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> LAST_FEEDER_UUID = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> RACCOON_UUID = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> FEED_TIME = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> SING_TIME = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> BLUE_VISUAL_FLAG = SynchedEntityData.m_135353_(EntityBlueJay.class, EntityDataSerializers.f_135035_);
   private static final Predicate<Entity> HIGHLIGHTS_WITH_SONG = entity -> entity instanceof Enemy;
   public float prevFlyProgress;
   public float flyProgress;
   public float prevFlapAmount;
   public float flapAmount;
   public float attackProgress;
   public float prevAttackProgress;
   public float prevCrestAmount;
   public float crestAmount;
   private boolean isLandNavigator;
   private int timeFlying;
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   public boolean aiItemFlag = false;
   private int prevSingTime = 0;
   private int blueTime = 0;
   private int raiseCrestOverrideTicks;

   protected EntityBlueJay(EntityType<? extends Animal> animal, Level level) {
      super(animal, level);
      this.m_21441_(BlockPathTypes.DANGER_FIRE, -1.0F);
      this.m_21441_(BlockPathTypes.WATER, -1.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 16.0F);
      this.m_21441_(BlockPathTypes.COCOA, -1.0F);
      this.m_21441_(BlockPathTypes.FENCE, -1.0F);
      this.switchNavigator(false);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(2, new BlueJayAIMelee(this));
      this.f_21345_.m_25352_(3, new FollowParentGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new FlyingAITempt(this, 1.0, Ingredient.m_204132_(AMTagRegistry.BLUE_JAY_FOODSTUFFS), false));
      this.f_21345_.m_25352_(5, new EntityBlueJay.AIFollowFeederOrRaccoon());
      this.f_21345_.m_25352_(6, new EntityBlueJay.AIFlyIdle());
      this.f_21345_.m_25352_(7, new EntityBlueJay.AIScatter());
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
      this.f_21345_.m_25352_(10, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new EntityBlueJay.AITargetItems(this, false, false, 40, 16));
      this.f_21346_.m_25352_(4, new HurtByTargetGoal(this, new Class[]{Player.class}).m_26044_(new Class[0]));
   }

   public static boolean checkBlueJaySpawnRules(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return m_186209_(worldIn, pos);
   }

   public boolean m_6914_(LevelReader reader) {
      if (reader.m_45784_(this) && !reader.m_46855_(this.m_20191_())) {
         BlockPos blockpos = this.m_20183_();
         BlockState blockstate2 = reader.m_8055_(blockpos.m_7495_());
         return blockstate2.m_204336_(BlockTags.f_13035_) || blockstate2.m_204336_(BlockTags.f_13106_) || blockstate2.m_60713_(Blocks.f_50440_);
      } else {
         return false;
      }
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.blueJaySpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.BLUE_JAY_BREEDABLES);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FLYING, false);
      this.f_19804_.m_135372_(ATTACK_TICK, 0);
      this.f_19804_.m_135372_(FEED_TIME, 0);
      this.f_19804_.m_135372_(SING_TIME, 0);
      this.f_19804_.m_135372_(CREST_TARGET, 0.0F);
      this.f_19804_.m_135372_(BLUE_VISUAL_FLAG, false);
      this.f_19804_.m_135372_(RACCOON_UUID, Optional.empty());
      this.f_19804_.m_135372_(LAST_FEEDER_UUID, Optional.empty());
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigation(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new FlightMoveController(this, 1.0F, false);
         this.f_21344_ = new DirectPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevCrestAmount = this.crestAmount;
      this.prevAttackProgress = this.attackProgress;
      this.prevFlapAmount = this.flapAmount;
      this.prevFlyProgress = this.flyProgress;
      this.prevBirdPitch = this.birdPitch;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if ((Integer)this.f_19804_.m_135370_(ATTACK_TICK) > 0) {
         this.f_19804_.m_135381_(ATTACK_TICK, (Integer)this.f_19804_.m_135370_(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      float yMov = (float)this.m_20184_().f_82480_;
      this.birdPitch = yMov * 2.0F * (-180.0F / (float)Math.PI);
      if (yMov >= 0.0F) {
         if (this.flapAmount < 1.0F) {
            this.flapAmount += 0.25F;
         }
      } else if (yMov < -0.07F && this.flapAmount > 0.0F) {
         this.flapAmount -= 0.25F;
      }

      if (this.raiseCrestOverrideTicks > 0) {
         this.raiseCrestOverrideTicks--;
         this.crestAmount = 0.75F;
      } else {
         this.crestAmount = Mth.m_14121_(this.crestAmount, this.getTargetCrest(), 0.3F);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.isFlying()) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            this.timeFlying++;
            this.m_20242_(true);
            if (this.m_20159_() || this.m_27593_()) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
            this.m_20242_(false);
         }

         if (this.m_5448_() != null) {
            this.setCrestTarget(1.0F);
         } else if (this.getRaccoonUUID() != null) {
            this.setCrestTarget(0.5F);
         } else {
            this.setCrestTarget(0.0F);
         }
      }

      if (this.getFeedTime() > 0) {
         this.setFeedTime(this.getFeedTime() - 1);
         if (this.getFeedTime() == 0) {
            this.setLastFeeder(null);
         }
      }

      if (this.m_20202_() instanceof EntityRaccoon riddenRaccoon) {
         this.f_20883_ = riddenRaccoon.f_20883_;
      }

      if (this.getRaccoon() instanceof EntityRaccoon raccoon) {
         LivingEntity jayTarget = this.m_5448_();
         LivingEntity raccoonTarget = raccoon.m_5448_();
         if (jayTarget != null && jayTarget.m_6084_()) {
            if (this.m_20159_()) {
               this.m_8127_();
            }
         } else if (raccoonTarget != null && raccoonTarget.m_6084_() && this.m_6779_(raccoonTarget)) {
            this.m_6710_(raccoonTarget);
         }
      }

      if (this.getSingTime() > 0) {
         this.setSingTime(this.getSingTime() - 1);
         if (this.prevSingTime % 15 == 0) {
            this.m_5496_((SoundEvent)AMSoundRegistry.BLUE_JAY_SONG.get(), this.m_6121_(), this.m_6100_());
         }

         if (this.m_9236_().f_46443_ && this.getSingTime() % 5 == 0 && this.m_9236_().f_46443_) {
            Vec3 modelFront = new Vec3(0.0, 0.2F, 0.3F)
               .m_82490_(this.m_6134_())
               .m_82496_(-this.m_146909_() * (float) (Math.PI / 180.0))
               .m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0));
            Vec3 particleFrom = this.m_20182_().m_82549_(modelFront);
            this.m_9236_()
               .m_7106_(
                  (ParticleOptions)AMParticleRegistry.BIRD_SONG.get(),
                  particleFrom.f_82479_,
                  particleFrom.f_82480_,
                  particleFrom.f_82481_,
                  modelFront.f_82479_,
                  modelFront.f_82480_,
                  modelFront.f_82481_
               );
         }
      }

      if (this.prevSingTime < this.getSingTime() && !this.m_9236_().f_46443_) {
         this.blueTime = 1200;
         this.f_19804_.m_135381_(BLUE_VISUAL_FLAG, true);
         this.highlightMonsters();
      }

      if (this.blueTime > 0) {
         this.blueTime--;
         if (this.blueTime == 0) {
            this.f_19804_.m_135381_(BLUE_VISUAL_FLAG, false);
            this.m_9236_().m_7605_(this, (byte)68);
         } else {
            this.m_9236_().m_7605_(this, (byte)67);
         }
      }

      this.prevSingTime = this.getSingTime();
   }

   public void m_8032_() {
      super.m_8032_();
      this.raiseCrestOverrideTicks = 15;
   }

   private boolean highlightMonsters() {
      AABB allyBox = this.m_20191_().m_82400_(64.0);
      allyBox = allyBox.m_165887_(-64.0);
      allyBox = allyBox.m_165893_(320.0);
      boolean any = false;

      for (LivingEntity entity : this.m_9236_().m_6443_(LivingEntity.class, allyBox, HIGHLIGHTS_WITH_SONG)) {
         entity.m_7292_(new MobEffectInstance(MobEffects.f_19619_, this.blueTime, 0, true, false));
      }

      return any;
   }

   public boolean isMakingMonstersBlue() {
      return (Boolean)this.f_19804_.m_135370_(BLUE_VISUAL_FLAG);
   }

   public void m_142687_(RemovalReason removalReason) {
      if (this.getSingTime() > 0 && !this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(BLUE_VISUAL_FLAG, false);
         this.m_9236_().m_7605_(this, (byte)68);
      }

      super.m_142687_(removalReason);
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public void m_7023_(Vec3 vec3d) {
      if (this.m_20069_() && this.m_20184_().f_82480_ > 0.0) {
         this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
      }

      super.m_7023_(vec3d);
   }

   public BlockPos getBlueJayGround(BlockPos in) {
      BlockPos position = new BlockPos(in.m_123341_(), (int)this.m_20186_(), in.m_123343_());

      while (position.m_123342_() < 320 && !this.m_9236_().m_6425_(position).m_76178_()) {
         position = position.m_7494_();
      }

      while (position.m_123342_() > -64 && !this.m_9236_().m_8055_(position).m_280296_() && this.m_9236_().m_6425_(position).m_76178_()) {
         position = position.m_7495_();
      }

      return position;
   }

   public boolean m_7307_(Entity entityIn) {
      if (this.getRaccoonUUID() != null) {
         if (entityIn instanceof EntityRaccoon && this.getRaccoonUUID().equals(entityIn.m_20148_())) {
            return true;
         }

         Entity raccoon = this.getRaccoon();
         if (raccoon != null && (raccoon.m_7307_(entityIn) || entityIn.m_7307_(raccoon))) {
            return true;
         }
      }

      return super.m_7307_(entityIn);
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.m_217043_().m_188503_(15);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.m_7096_() + extraX), (int)this.m_20186_(), (int)(fleePos.m_7094_() + extraZ));
      BlockPos ground = this.getBlueJayGround(radialPos);
      if (ground.m_123342_() < -64) {
         return null;
      } else {
         ground = this.m_20183_();

         while (ground.m_123342_() > -64 && !this.m_9236_().m_8055_(ground).m_280296_()) {
            ground = ground.m_7495_();
         }

         return !this.isTargetBlocked(Vec3.m_82512_(ground.m_7494_())) ? Vec3.m_82512_(ground.m_7495_()) : null;
      }
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 5.0F + radiusAdd + this.m_217043_().m_188503_(5);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.m_7096_() + extraX), 0, (int)(fleePos.m_7094_() + extraZ));
      BlockPos ground = this.getBlueJayGround(radialPos);
      int distFromGround = (int)this.m_20186_() - ground.m_123342_();
      int flightHeight = 5 + this.m_217043_().m_188503_(5);
      int j = this.m_217043_().m_188503_(5) + 5;
      BlockPos newPos = ground.m_6630_(distFromGround > 5 ? flightHeight : j);
      if (this.m_9236_().m_8055_(ground).m_204336_(BlockTags.f_13035_)) {
         newPos = ground.m_6630_(1 + this.m_217043_().m_188503_(3));
      }

      return !this.isTargetBlocked(Vec3.m_82512_(newPos)) && this.m_20238_(Vec3.m_82512_(newPos)) > 1.0 ? Vec3.m_82512_(newPos) : null;
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.BLUE_JAY_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.BLUE_JAY_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.BLUE_JAY_HURT.get();
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22281_, 1.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.f_19804_.m_135370_(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      if (flying && this.m_6162_()) {
         flying = false;
      }

      this.f_19804_.m_135381_(FLYING, flying);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setFlying(compound.m_128471_("Flying"));
      this.blueTime = compound.m_128451_("BlueTime");
      if (compound.m_128403_("FeederUUID")) {
         this.setLastFeederUUID(compound.m_128342_("FeederUUID"));
      }

      if (compound.m_128403_("RaccoonUUID")) {
         this.setRaccoonUUID(compound.m_128342_("RaccoonUUID"));
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Flying", this.isFlying());
      compound.m_128405_("BlueTime", this.blueTime);
      if (this.getLastFeederUUID() != null) {
         compound.m_128362_("FeederUUID", this.getLastFeederUUID());
      }

      if (this.getRaccoonUUID() != null) {
         compound.m_128362_("RaccoonUUID", this.getRaccoonUUID());
      }
   }

   public int getFeedTime() {
      return (Integer)this.f_19804_.m_135370_(FEED_TIME);
   }

   public void setFeedTime(int feedTime) {
      this.f_19804_.m_135381_(FEED_TIME, feedTime);
   }

   public int getSingTime() {
      return (Integer)this.f_19804_.m_135370_(SING_TIME);
   }

   public void setSingTime(int singTime) {
      this.f_19804_.m_135381_(SING_TIME, singTime);
   }

   public float getTargetCrest() {
      return (Float)this.f_19804_.m_135370_(CREST_TARGET);
   }

   public void setCrestTarget(float crestTarget) {
      this.f_19804_.m_135381_(CREST_TARGET, crestTarget);
   }

   @Nullable
   public UUID getLastFeederUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(LAST_FEEDER_UUID)).orElse(null);
   }

   public void setLastFeederUUID(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(LAST_FEEDER_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getLastFeeder() {
      UUID id = this.getLastFeederUUID();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   public void setLastFeeder(@Nullable Entity feeder) {
      if (feeder == null) {
         this.setLastFeederUUID(null);
      } else {
         this.setLastFeederUUID(feeder.m_20148_());
      }
   }

   @Nullable
   public UUID getRaccoonUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(RACCOON_UUID)).orElse(null);
   }

   public void setRaccoonUUID(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(RACCOON_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getRaccoon() {
      UUID id = this.getRaccoonUUID();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   public void setRaccoon(@Nullable Entity feeder) {
      if (feeder == null) {
         this.setRaccoonUUID(null);
      } else {
         this.setRaccoonUUID(feeder.m_20148_());
      }
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.m_20183_();

      while (position.m_123342_() > -65 && this.m_9236_().m_46859_(position)) {
         position = position.m_7495_();
      }

      return !this.m_9236_().m_6425_(position).m_76178_() || this.m_9236_().m_8055_(position).m_60713_(Blocks.f_50191_) || position.m_123342_() <= -65;
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob m_142606_(ServerLevel level, AgeableMob mob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.BLUE_JAY.get()).m_20615_(this.m_9236_());
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_41720_().m_41472_() || stack.m_204117_(AMTagRegistry.BLUE_JAY_FOODSTUFFS);
   }

   @Override
   public double getMaxDistToItem() {
      return 1.0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      if (!this.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && !this.m_9236_().f_46443_) {
         this.m_5552_(this.m_21120_(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.m_5634_(3.0F);
      Entity itemThrower = e.m_19749_();
      if (itemThrower != null && e.m_32055_().m_204117_(AMTagRegistry.BLUE_JAY_TEAMING_FOODS)) {
         this.setLastFeederUUID(itemThrower.m_20148_());
         this.setFeedTime(1200);
         this.m_8127_();
      }

      if (e.m_19749_() != null && e.m_32055_().m_204117_(AMTagRegistry.BLUE_JAY_ALERT_FOODS)) {
         this.setSingTime(40);
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (!type.m_19077_()) {
         if (itemstack.m_204117_(AMTagRegistry.BLUE_JAY_TEAMING_FOODS) && this.getFeedTime() <= 0) {
            this.m_5634_(3.0F);
            this.m_142075_(player, hand, itemstack);
            this.setRaccoonUUID(null);
            this.m_8127_();
            this.setLastFeeder(player);
            this.setFeedTime(1200);
            return InteractionResult.SUCCESS;
         }

         if (itemstack.m_204117_(AMTagRegistry.BLUE_JAY_ALERT_FOODS) && this.getSingTime() <= 0) {
            this.m_5634_(3.0F);
            this.setSingTime(40);
            this.m_142075_(player, hand, itemstack);
            return InteractionResult.SUCCESS;
         }
      }

      return type;
   }

   @Override
   public void peck() {
      this.f_19804_.m_135381_(ATTACK_TICK, 7);
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id != 67 && id != 68) {
         super.m_7822_(id);
      } else {
         AlexsMobs.PROXY.onEntityStatus(this, id);
      }
   }

   private boolean isTrusting() {
      return this.getFeedTime() > 0 || this.getSingTime() > 0 || this.getRaccoonUUID() != null || this.aiItemFlag;
   }

   private class AIFlyIdle extends Goal {
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget;

      public AIFlyIdle() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         if (EntityBlueJay.this.m_20160_()
            || EntityBlueJay.this.m_5448_() != null && EntityBlueJay.this.m_5448_().m_6084_()
            || EntityBlueJay.this.m_20159_()
            || EntityBlueJay.this.aiItemFlag
            || EntityBlueJay.this.getSingTime() > 0) {
            return false;
         } else if (EntityBlueJay.this.m_217043_().m_188503_(45) != 0 && !EntityBlueJay.this.isFlying()) {
            return false;
         } else {
            if (EntityBlueJay.this.m_20096_()) {
               this.flightTarget = EntityBlueJay.this.f_19796_.m_188499_();
            } else {
               this.flightTarget = EntityBlueJay.this.f_19796_.m_188503_(5) > 0 && EntityBlueJay.this.timeFlying < 200;
            }

            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.f_82479_;
               this.y = lvt_1_1_.f_82480_;
               this.z = lvt_1_1_.f_82481_;
               return true;
            }
         }
      }

      public void m_8037_() {
         if (this.flightTarget) {
            EntityBlueJay.this.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
         } else {
            EntityBlueJay.this.m_21573_().m_26519_(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityBlueJay.this.isFlying() && EntityBlueJay.this.m_20096_()) {
            EntityBlueJay.this.setFlying(false);
         }

         if (EntityBlueJay.this.isFlying() && EntityBlueJay.this.m_20096_() && EntityBlueJay.this.timeFlying > 10) {
            EntityBlueJay.this.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = EntityBlueJay.this.m_20182_();
         if (EntityBlueJay.this.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return EntityBlueJay.this.timeFlying >= 200 && !EntityBlueJay.this.isOverWaterOrVoid()
               ? EntityBlueJay.this.getBlockGrounding(vector3d)
               : EntityBlueJay.this.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.m_148488_(EntityBlueJay.this, 10, 7);
         }
      }

      public boolean m_8045_() {
         return this.flightTarget
            ? EntityBlueJay.this.isFlying() && EntityBlueJay.this.m_20275_(this.x, this.y, this.z) > 5.0
            : !EntityBlueJay.this.m_21573_().m_26571_() && !EntityBlueJay.this.m_20160_();
      }

      public void m_8056_() {
         if (this.flightTarget) {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
         } else {
            EntityBlueJay.this.m_21573_().m_26519_(this.x, this.y, this.z, 1.0);
         }
      }

      public void m_8041_() {
         EntityBlueJay.this.m_21573_().m_26573_();
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         super.m_8041_();
      }
   }

   private class AIFollowFeederOrRaccoon extends Goal {
      private Entity following;

      AIFollowFeederOrRaccoon() {
         this.m_7021_(EnumSet.of(Flag.LOOK, Flag.MOVE));
      }

      public boolean m_8036_() {
         if (!EntityBlueJay.this.m_20159_() && (EntityBlueJay.this.m_5448_() == null || !EntityBlueJay.this.m_5448_().m_6084_())) {
            if (EntityBlueJay.this.getRaccoonUUID() != null) {
               Entity raccoon = EntityBlueJay.this.getRaccoon();
               if (raccoon != null) {
                  this.following = raccoon;
                  return true;
               }
            }

            if (EntityBlueJay.this.getFeedTime() > 0) {
               Entity feeder = EntityBlueJay.this.getLastFeeder();
               if (feeder != null) {
                  this.following = feeder;
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         LivingEntity target = EntityBlueJay.this.m_5448_();
         return this.following != null
            && this.following.m_6084_()
            && (target == null || !target.m_6084_())
            && (this.following instanceof EntityRaccoon || EntityBlueJay.this.getFeedTime() > 0)
            && !EntityBlueJay.this.m_20159_();
      }

      public void m_8037_() {
         double dist = EntityBlueJay.this.m_20270_(this.following);
         if (!(dist > 6.0) && !EntityBlueJay.this.isFlying()) {
            EntityBlueJay.this.m_21573_().m_26519_(this.following.m_20185_(), this.following.m_20186_(), this.following.m_20189_(), 1.0);
         } else {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.m_21566_().m_6849_(this.following.m_20185_(), this.following.m_20186_(), this.following.m_20189_(), 1.0);
         }

         if (EntityBlueJay.this.isFlying() && EntityBlueJay.this.m_20096_() && dist < 3.0) {
            EntityBlueJay.this.setFlying(false);
         }

         if (this.following instanceof EntityRaccoon raccoon) {
            if (dist > 40.0) {
               EntityBlueJay.this.m_6021_(this.following.m_20185_(), this.following.m_20186_(), this.following.m_20189_());
            }

            if (dist < 2.5) {
               EntityBlueJay.this.m_21566_().m_6849_(this.following.m_20185_(), this.following.m_20186_(), this.following.m_20189_(), 1.0);
            }

            if (dist < 1.0 && raccoon.m_20197_().isEmpty()) {
               EntityBlueJay.this.m_7998_(raccoon, false);
            }
         }
      }
   }

   private class AIScatter extends Goal {
      protected final EntityBlueJay.AIScatter.Sorter theNearestAttackableTargetSorter;
      protected final com.google.common.base.Predicate<? super Entity> targetEntitySelector;
      protected int executionChance = 8;
      protected boolean mustUpdate;
      private Entity targetEntity;
      private Vec3 flightTarget = null;
      private int cooldown = 0;

      AIScatter() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.theNearestAttackableTargetSorter = new EntityBlueJay.AIScatter.Sorter(EntityBlueJay.this);
         this.targetEntitySelector = new com.google.common.base.Predicate<Entity>() {
            public boolean apply(@Nullable Entity e) {
               return e.m_6084_() && e.m_6095_().m_204039_(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player)e).m_7500_();
            }
         };
      }

      public boolean m_8036_() {
         Entity entity = EntityBlueJay.this.m_5448_();
         if (!EntityBlueJay.this.m_20159_() && !EntityBlueJay.this.m_20160_() && (entity == null || !entity.m_6084_()) && !EntityBlueJay.this.isTrusting()) {
            if (!this.mustUpdate) {
               long worldTime = EntityBlueJay.this.m_9236_().m_46467_() % 10L;
               if (EntityBlueJay.this.m_21216_() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (EntityBlueJay.this.m_217043_().m_188503_(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<Entity> list = EntityBlueJay.this.m_9236_().m_6443_(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               return true;
            }
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.targetEntity != null;
      }

      public void m_8041_() {
         this.flightTarget = null;
         this.targetEntity = null;
      }

      public void m_8037_() {
         if (this.cooldown > 0) {
            this.cooldown--;
         }

         if (this.flightTarget != null) {
            EntityBlueJay.this.setFlying(true);
            EntityBlueJay.this.m_21566_().m_6849_(this.flightTarget.f_82479_, this.flightTarget.f_82480_, this.flightTarget.f_82481_, 1.0);
            if (this.cooldown == 0 && EntityBlueJay.this.isTargetBlocked(this.flightTarget)) {
               this.cooldown = 30;
               this.flightTarget = null;
            }
         }

         if (this.targetEntity != null) {
            if (EntityBlueJay.this.m_20096_() || this.flightTarget == null || this.flightTarget != null && EntityBlueJay.this.m_20238_(this.flightTarget) < 3.0
               )
             {
               Vec3 vec = EntityBlueJay.this.getBlockInViewAway(this.targetEntity.m_20182_(), 0.0F);
               if (vec != null && vec.m_7098_() > EntityBlueJay.this.m_20186_()) {
                  this.flightTarget = vec;
               }
            }

            if (EntityBlueJay.this.m_20270_(this.targetEntity) > 20.0F) {
               this.m_8041_();
            }
         }
      }

      protected double getTargetDistance() {
         return 4.0;
      }

      protected AABB getTargetableArea(double targetDistance) {
         Vec3 renderCenter = new Vec3(EntityBlueJay.this.m_20185_(), EntityBlueJay.this.m_20186_() + 0.5, EntityBlueJay.this.m_20189_());
         AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
         return aabb.m_82383_(renderCenter);
      }

      public record Sorter(Entity theEntity) implements Comparator<Entity> {
         public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.m_20280_(p_compare_1_);
            double d1 = this.theEntity.m_20280_(p_compare_2_);
            return Double.compare(d0, d1);
         }
      }
   }

   private static class AITargetItems extends CreatureAITargetItems {
      public AITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
         super(creature, checkSight, onlyNearby, tickThreshold, radius);
         this.executionChance = 1;
      }

      @Override
      public void m_8041_() {
         super.m_8041_();
         ((EntityBlueJay)this.f_26135_).aiItemFlag = false;
      }

      @Override
      public boolean m_8036_() {
         return super.m_8036_() && (this.f_26135_.m_5448_() == null || !this.f_26135_.m_5448_().m_6084_());
      }

      @Override
      public boolean m_8045_() {
         return super.m_8045_() && (this.f_26135_.m_5448_() == null || !this.f_26135_.m_5448_().m_6084_());
      }

      @Override
      protected void moveTo() {
         EntityBlueJay jay = (EntityBlueJay)this.f_26135_;
         if (this.targetEntity != null) {
            jay.aiItemFlag = true;
            if (this.f_26135_.m_20270_(this.targetEntity) < 2.0F) {
               jay.m_21566_().m_6849_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
               jay.peck();
            }

            if (!(this.f_26135_.m_20270_(this.targetEntity) > 8.0F) && !jay.isFlying()) {
               this.f_26135_.m_21573_().m_26519_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
            } else {
               jay.setFlying(true);
               float f = (float)(jay.m_20185_() - this.targetEntity.m_20185_());
               float f1 = 1.8F;
               float f2 = (float)(jay.m_20189_() - this.targetEntity.m_20189_());
               float xzDist = Mth.m_14116_(f * f + f2 * f2);
               if (!jay.m_142582_(this.targetEntity)) {
                  jay.m_21566_().m_6849_(this.targetEntity.m_20185_(), 1.0 + jay.m_20186_(), this.targetEntity.m_20189_(), 1.0);
               } else {
                  if (xzDist < 5.0F) {
                     f1 = 0.0F;
                  }

                  jay.m_21566_().m_6849_(this.targetEntity.m_20185_(), f1 + this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
               }
            }
         }
      }

      @Override
      public void m_8037_() {
         super.m_8037_();
         this.moveTo();
      }
   }
}
