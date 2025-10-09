package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityEnderiophage extends Animal implements Enemy, FlyingAnimal {
   private static final EntityDataAccessor<Float> PHAGE_PITCH = SynchedEntityData.m_135353_(EntityEnderiophage.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.m_135353_(EntityEnderiophage.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> MISSING_EYE = SynchedEntityData.m_135353_(EntityEnderiophage.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> PHAGE_SCALE = SynchedEntityData.m_135353_(EntityEnderiophage.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityEnderiophage.class, EntityDataSerializers.f_135028_);
   private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = entity -> entity instanceof EntityEndergrade
      || entity.m_21023_((MobEffect)AMEffectRegistry.ENDER_FLU.get());
   public float prevPhagePitch;
   public float tentacleAngle;
   public float lastTentacleAngle;
   public float phageRotation;
   public float prevFlyProgress;
   public float flyProgress;
   public int passengerIndex = 0;
   public float prevEnderiophageScale = 1.0F;
   private float rotationVelocity;
   private int slowDownTicks = 0;
   private float randomMotionSpeed;
   private boolean isLandNavigator;
   private int timeFlying = 0;
   private int fleeAfterStealTime = 0;
   private int attachTime = 0;
   private int dismountCooldown = 0;
   private int squishCooldown = 0;
   private PathfinderMob angryEnderman = null;

   protected EntityEnderiophage(EntityType type, Level world) {
      super(type, world);
      this.rotationVelocity = 1.0F / (this.f_19796_.m_188501_() + 1.0F) * 0.2F;
      this.switchNavigator(false);
      this.f_21364_ = 5;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22277_, 16.0)
         .m_22268_(Attributes.f_22279_, 0.15F)
         .m_22268_(Attributes.f_22281_, 2.0);
   }

   public static boolean canEnderiophageSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return true;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.enderiophageSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   private void doInitialPosing(LevelAccessor world) {
      BlockPos down = this.getPhageGround(this.m_20183_());
      this.m_6034_(down.m_123341_() + 0.5F, down.m_123342_() + 1, down.m_123343_() + 0.5F);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (reason == MobSpawnType.NATURAL) {
         this.doInitialPosing(worldIn);
      }

      this.setSkinForDimension();
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public int m_5792_() {
      return 2;
   }

   public float getPhageScale() {
      return (Float)this.f_19804_.m_135370_(PHAGE_SCALE);
   }

   public void setPhageScale(float scale) {
      this.f_19804_.m_135381_(PHAGE_SCALE, scale);
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int variant) {
      this.f_19804_.m_135381_(VARIANT, variant);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new EntityEnderiophage.FlyTowardsTarget(this));
      this.f_21345_.m_25352_(2, new EntityEnderiophage.AIWalkIdle());
      this.f_21346_.m_25352_(1, new EntityAINearestTarget3D(this, EnderMan.class, 15, true, true, null) {
         public boolean m_8036_() {
            return EntityEnderiophage.this.isMissingEye() && super.m_8036_();
         }

         public boolean m_8045_() {
            return EntityEnderiophage.this.isMissingEye() && super.m_8045_();
         }
      });
      this.f_21346_.m_25352_(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
         public boolean m_8036_() {
            return !EntityEnderiophage.this.isMissingEye() && EntityEnderiophage.this.fleeAfterStealTime == 0 && super.m_8036_();
         }

         public boolean m_8045_() {
            return !EntityEnderiophage.this.isMissingEye() && super.m_8045_();
         }
      });
      this.f_21346_.m_25352_(3, new HurtByTargetGoal(this, new Class[]{EnderMan.class}));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new FlightMoveController(this, 1.0F, false, true);
         this.f_21344_ = new DirectPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(VARIANT, 0);
      this.f_19804_.m_135372_(PHAGE_PITCH, 0.0F);
      this.f_19804_.m_135372_(PHAGE_SCALE, 1.0F);
      this.f_19804_.m_135372_(FLYING, false);
      this.f_19804_.m_135372_(MISSING_EYE, false);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean isInOverworld() {
      return this.m_9236_().m_46472_() == Level.f_46428_ && !this.m_21525_();
   }

   public boolean isInNether() {
      return this.m_9236_().m_46472_() == Level.f_46429_ && !this.m_21525_();
   }

   public void setStandardFleeTime() {
      this.fleeAfterStealTime = 20;
   }

   public void m_6083_() {
      Entity entity = this.m_20202_();
      if (this.m_20159_() && !entity.m_6084_()) {
         this.m_8127_();
      } else {
         this.m_20334_(0.0, 0.0, 0.0);
         this.m_8119_();
         if (this.m_20159_()) {
            this.attachTime++;
            Entity mount = this.m_20202_();
            if (mount instanceof LivingEntity) {
               this.passengerIndex = mount.m_20197_().indexOf(this);
               this.f_20883_ = ((LivingEntity)mount).f_20883_;
               this.m_146922_(((LivingEntity)mount).m_146908_());
               this.f_20885_ = ((LivingEntity)mount).f_20885_;
               this.f_19859_ = ((LivingEntity)mount).f_20885_;
               float radius = mount.m_20205_();
               float angle = (float) (Math.PI / 180.0) * (((LivingEntity)mount).f_20883_ + this.passengerIndex * 90.0F);
               double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
               double extraZ = radius * Mth.m_14089_(angle);
               this.m_6034_(mount.m_20185_() + extraX, Math.max(mount.m_20186_() + mount.m_20192_() * 0.25F, mount.m_20186_()), mount.m_20189_() + extraZ);
               if (!mount.m_6084_() || mount instanceof Player && ((Player)mount).m_7500_()) {
                  this.m_6038_();
               }

               this.setPhagePitch(0.0F);
               if (!this.m_9236_().f_46443_ && this.attachTime > 15) {
                  LivingEntity target = (LivingEntity)mount;
                  float dmg = 1.0F;
                  if (target.m_21223_() > target.m_21233_() * 0.2F) {
                     dmg = 6.0F;
                  }

                  if ((target.m_21223_() < 1.5 || mount.m_6469_(this.m_269291_().m_269333_(this), dmg)) && mount instanceof LivingEntity) {
                     this.dismountCooldown = 100;
                     if (mount instanceof EnderMan) {
                        this.setMissingEye(false);
                        this.m_146850_(GameEvent.f_157806_);
                        this.m_5496_(SoundEvents.f_11897_, this.m_6121_(), this.m_6100_());
                        this.m_5634_(5.0F);
                        ((EnderMan)mount).m_7292_(new MobEffectInstance(MobEffects.f_19610_, 400));
                        this.fleeAfterStealTime = 400;
                        this.setFlying(true);
                        this.angryEnderman = (PathfinderMob)mount;
                     } else if (this.f_19796_.m_188503_(3) == 0) {
                        if (!this.isMissingEye()) {
                           if (target.m_21124_((MobEffect)AMEffectRegistry.ENDER_FLU.get()) == null) {
                              target.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.ENDER_FLU.get(), 12000));
                           } else {
                              MobEffectInstance inst = target.m_21124_((MobEffect)AMEffectRegistry.ENDER_FLU.get());
                              int duration = 12000;
                              int level = 0;
                              if (inst != null) {
                                 duration = inst.m_19557_();
                                 level = inst.m_19564_();
                              }

                              target.m_21195_((MobEffect)AMEffectRegistry.ENDER_FLU.get());
                              target.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.ENDER_FLU.get(), duration, Math.min(level + 1, 4)));
                           }

                           this.m_5634_(5.0F);
                           this.m_146850_(GameEvent.f_223709_);
                           this.m_5496_(SoundEvents.f_12018_, this.m_6121_(), this.m_6100_());
                           this.setMissingEye(true);
                        }

                        if (!this.m_9236_().f_46443_) {
                           this.m_6710_(null);
                           this.m_21335_(null);
                           this.m_6703_(null);
                           this.f_21345_.m_25386_().forEach(Goal::m_8041_);
                           this.f_21346_.m_25386_().forEach(Goal::m_8041_);
                        }
                     }
                  }

                  if (((LivingEntity)mount).m_21223_() <= 0.0F
                     || this.fleeAfterStealTime > 0
                     || this.isMissingEye() && !(mount instanceof EnderMan)
                     || !this.isMissingEye() && mount instanceof EnderMan) {
                     this.m_6038_();
                     this.m_6710_(null);
                     this.dismountCooldown = 100;
                     AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.m_19879_(), mount.m_19879_()));
                     this.setFlying(true);
                  }
               }
            }
         }
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   public void onSpawnFromEffect() {
      this.prevEnderiophageScale = 0.2F;
      this.setPhageScale(0.2F);
   }

   public void setSkinForDimension() {
      if (this.isInNether()) {
         this.setVariant(2);
      } else if (this.isInOverworld()) {
         this.setVariant(1);
      } else {
         this.setVariant(0);
      }
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.ENDERIOPHAGE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.ENDERIOPHAGE_HURT.get();
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
      this.m_5496_((SoundEvent)AMSoundRegistry.ENDERIOPHAGE_WALK.get(), 0.4F, 1.0F);
   }

   protected float m_6059_() {
      return this.f_19788_ + 0.3F;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevEnderiophageScale = this.getPhageScale();
      float extraMotionSlow = 1.0F;
      float extraMotionSlowY = 1.0F;
      if (this.slowDownTicks > 0) {
         this.slowDownTicks--;
         extraMotionSlow = 0.33F;
         extraMotionSlowY = 0.1F;
      }

      if (this.dismountCooldown > 0) {
         this.dismountCooldown--;
      }

      if (this.squishCooldown > 0) {
         this.squishCooldown--;
      }

      if (!this.m_9236_().f_46443_) {
         if (!this.m_20159_() && this.attachTime != 0) {
            this.attachTime = 0;
         }

         if (this.fleeAfterStealTime > 0) {
            if (this.angryEnderman != null) {
               Vec3 vec = this.getBlockInViewAway(this.angryEnderman.m_20182_(), 10.0F);
               if (this.fleeAfterStealTime < 5) {
                  if (this.angryEnderman instanceof NeutralMob) {
                     ((NeutralMob)this.angryEnderman).m_21662_();
                  }

                  try {
                     this.angryEnderman.f_21345_.m_25386_().forEach(Goal::m_8041_);
                     this.angryEnderman.f_21346_.m_25386_().forEach(Goal::m_8041_);
                  } catch (Exception var18) {
                     var18.printStackTrace();
                  }

                  this.angryEnderman = null;
               }

               if (vec != null) {
                  this.setFlying(true);
                  this.m_21566_().m_6849_(vec.f_82479_, vec.f_82480_, vec.f_82481_, 1.3F);
               }
            }

            this.fleeAfterStealTime--;
         }
      }

      this.f_20883_ = this.m_146908_();
      this.f_20885_ = this.m_146908_();
      this.setPhagePitch(-90.0F);
      if (this.m_6084_() && this.m_29443_() && this.randomMotionSpeed > 0.75F && this.m_20184_().m_82556_() > 0.02 && this.m_9236_().f_46443_) {
         float pitch = -this.getPhagePitch() / 90.0F;
         float radius = this.m_20205_() * 0.2F * -pitch;
         float angle = (float) (Math.PI / 180.0) * this.m_146908_();
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraY = 0.2F - (1.0F - pitch) * 0.15F;
         double extraZ = radius * Mth.m_14089_(angle);
         double motX = extraX * 8.0 + this.f_19796_.m_188583_() * 0.05F;
         double motY = -0.1F;
         double motZ = extraZ + this.f_19796_.m_188583_() * 0.05F;
         this.m_9236_()
            .m_7106_(
               (ParticleOptions)AMParticleRegistry.DNA.get(), this.m_20185_() + extraX, this.m_20186_() + extraY, this.m_20189_() + extraZ, motX, motY, motZ
            );
      }

      this.prevPhagePitch = this.getPhagePitch();
      this.prevFlyProgress = this.flyProgress;
      if (this.m_29443_()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      this.lastTentacleAngle = this.tentacleAngle;
      this.phageRotation = this.phageRotation + this.rotationVelocity;
      if (this.phageRotation > Math.PI * 2) {
         if (this.m_9236_().f_46443_) {
            this.phageRotation = (float) (Math.PI * 2);
         } else {
            this.phageRotation = (float)(this.phageRotation - (Math.PI * 2));
            if (this.f_19796_.m_188503_(10) == 0) {
               this.rotationVelocity = 1.0F / (this.f_19796_.m_188501_() + 1.0F) * 0.2F;
            }

            this.m_9236_().m_7605_(this, (byte)19);
         }
      }

      if (this.phageRotation < (float) Math.PI) {
         float f = this.phageRotation / (float) Math.PI;
         this.tentacleAngle = Mth.m_14031_(f * f * (float) Math.PI) * 4.275F;
         if (f > 0.75) {
            if (this.squishCooldown == 0 && this.m_29443_()) {
               this.squishCooldown = 20;
               this.m_5496_((SoundEvent)AMSoundRegistry.ENDERIOPHAGE_SQUISH.get(), 3.0F, this.m_6100_());
            }

            this.randomMotionSpeed = 1.0F;
         } else {
            this.randomMotionSpeed = 0.01F;
         }
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_29443_() && this.isLandNavigator) {
            this.switchNavigator(false);
         }

         if (!this.m_29443_() && !this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.m_29443_()) {
            this.m_20334_(
               this.m_20184_().f_82479_ * this.randomMotionSpeed * extraMotionSlow,
               this.m_20184_().f_82480_ * this.randomMotionSpeed * extraMotionSlowY,
               this.m_20184_().f_82481_ * this.randomMotionSpeed * extraMotionSlow
            );
            this.timeFlying++;
            if (this.m_20096_() && this.timeFlying > 100) {
               this.setFlying(false);
            }
         } else {
            this.timeFlying = 0;
         }

         if (this.isMissingEye() && this.m_5448_() != null && !(this.m_5448_() instanceof EnderMan)) {
            this.m_6710_(null);
         }
      }

      if (!this.m_20096_() && this.m_20184_().f_82480_ < 0.0) {
         this.m_20256_(this.m_20184_().m_82542_(1.0, 0.6, 1.0));
      }

      if (this.m_29443_()) {
         float phageDist = -((float)((Math.abs(this.m_20184_().m_7096_()) + Math.abs(this.m_20184_().m_7094_())) * 6.0));
         this.incrementPhagePitch(phageDist * 1.0F);
         this.setPhagePitch(Mth.m_14036_(this.getPhagePitch(), -90.0F, 10.0F));
         float plateau = 2.0F;
         if (this.getPhagePitch() > plateau) {
            this.decrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90.0F);
         }

         if (this.getPhagePitch() < -plateau) {
            this.incrementPhagePitch(phageDist * Math.abs(this.getPhagePitch()) / 90.0F);
         }

         if (this.getPhagePitch() > 2.0F) {
            this.decrementPhagePitch(1.0F);
         } else if (this.getPhagePitch() < -2.0F) {
            this.incrementPhagePitch(1.0F);
         }

         if (this.f_19862_) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, 0.2F, 0.0));
         }
      } else {
         if (this.getPhagePitch() > 0.0F) {
            float decrease = Math.min(2.0F, this.getPhagePitch());
            this.decrementPhagePitch(decrease);
         }

         if (this.getPhagePitch() < 0.0F) {
            float decrease = Math.min(2.0F, -this.getPhagePitch());
            this.incrementPhagePitch(decrease);
         }
      }

      if (this.getPhageScale() < 1.0F) {
         this.setPhageScale(this.getPhageScale() + 0.05F);
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Flying", this.m_29443_());
      compound.m_128379_("MissingEye", this.isMissingEye());
      compound.m_128405_("Variant", this.getVariant());
      compound.m_128405_("SlowDownTicks", this.slowDownTicks);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setFlying(compound.m_128471_("Flying"));
      this.setMissingEye(compound.m_128471_("MissingEye"));
      this.setVariant(compound.m_128451_("Variant"));
      this.slowDownTicks = compound.m_128451_("SlowDownTicks");
   }

   public boolean isMissingEye() {
      return (Boolean)this.f_19804_.m_135370_(MISSING_EYE);
   }

   public void setMissingEye(boolean missingEye) {
      this.f_19804_.m_135381_(MISSING_EYE, missingEye);
   }

   public boolean m_29443_() {
      return (Boolean)this.f_19804_.m_135370_(FLYING);
   }

   public void setFlying(boolean flying) {
      this.f_19804_.m_135381_(FLYING, flying);
   }

   public float getPhagePitch() {
      return (Float)this.f_19804_.m_135370_(PHAGE_PITCH);
   }

   public void setPhagePitch(float pitch) {
      this.f_19804_.m_135381_(PHAGE_PITCH, pitch);
   }

   public void incrementPhagePitch(float pitch) {
      this.f_19804_.m_135381_(PHAGE_PITCH, this.getPhagePitch() + pitch);
   }

   public void decrementPhagePitch(float pitch) {
      this.f_19804_.m_135381_(PHAGE_PITCH, this.getPhagePitch() - pitch);
   }

   protected float m_6431_(Pose poseIn, EntityDimensions sizeIn) {
      return 1.8F;
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return null;
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.m_20183_();

      while (position.m_123342_() > -63 && !this.m_9236_().m_8055_(position).m_280296_()) {
         position = position.m_7495_();
      }

      return !this.m_9236_().m_6425_(position).m_76178_() || position.m_123342_() < -63;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.m_217043_().m_188503_(24) - radiusAdd;
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.m_7096_() + extraX), 0, (int)(fleePos.m_7094_() + extraZ));
      BlockPos ground = this.getPhageGround(radialPos);
      int distFromGround = (int)this.m_20186_() - ground.m_123342_();
      int flightHeight = 6 + this.m_217043_().m_188503_(10);
      BlockPos newPos = ground.m_6630_(distFromGround <= 8 && this.fleeAfterStealTime <= 0 ? this.m_217043_().m_188503_(6) + 5 : flightHeight);
      return !this.isTargetBlocked(Vec3.m_82512_(newPos)) && this.m_20238_(Vec3.m_82512_(newPos)) > 1.0 ? Vec3.m_82512_(newPos) : null;
   }

   private BlockPos getPhageGround(BlockPos in) {
      BlockPos position = new BlockPos(in.m_123341_(), (int)this.m_20186_(), in.m_123343_());

      while (position.m_123342_() > -63 && !this.m_9236_().m_8055_(position).m_280296_()) {
         position = position.m_7495_();
      }

      return position.m_123342_() < -62 ? position.m_6630_(120 + this.f_19796_.m_188503_(5)) : position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.m_217043_().m_188503_(24);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.m_7096_() + extraX, this.m_20186_(), fleePos.m_7094_() + extraZ);
      BlockPos ground = this.getPhageGround(radialPos);
      if (ground.m_123342_() <= -63) {
         return Vec3.m_82514_(ground, 110 + this.f_19796_.m_188503_(20));
      } else {
         ground = this.m_20183_();

         while (ground.m_123342_() > -63 && !this.m_9236_().m_8055_(ground).m_280296_()) {
            ground = ground.m_7495_();
         }

         return !this.isTargetBlocked(Vec3.m_82512_(ground.m_7494_())) ? Vec3.m_82512_(ground) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         if (entity instanceof EnderMan) {
            amount = (amount + 1.0F) * 0.35F;
            this.angryEnderman = (EnderMan)entity;
         }

         return super.m_6469_(source, amount);
      }
   }

   private class AIWalkIdle extends Goal {
      protected final EntityEnderiophage phage;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.phage = EntityEnderiophage.this;
      }

      public boolean m_8036_() {
         if (this.phage.m_20160_() || this.phage.m_5448_() != null && this.phage.m_5448_().m_6084_() || this.phage.m_20159_()) {
            return false;
         } else if (this.phage.m_217043_().m_188503_(30) != 0 && !this.phage.m_29443_() && this.phage.fleeAfterStealTime == 0) {
            return false;
         } else {
            if (this.phage.m_20096_()) {
               this.flightTarget = EntityEnderiophage.this.f_19796_.m_188503_(12) == 0;
            } else {
               this.flightTarget = EntityEnderiophage.this.f_19796_.m_188503_(5) > 0 && this.phage.timeFlying < 100;
            }

            if (this.phage.fleeAfterStealTime > 0) {
               this.flightTarget = true;
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
            this.phage.m_21566_().m_6849_(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.3F : 1.0);
         } else {
            this.phage.m_21573_().m_26519_(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.3F : 1.0);
         }

         if (!this.flightTarget && EntityEnderiophage.this.m_29443_() && this.phage.m_20096_()) {
            this.phage.setFlying(false);
         }

         if (EntityEnderiophage.this.m_29443_() && this.phage.m_20096_() && this.phage.timeFlying > 100 && this.phage.fleeAfterStealTime == 0) {
            this.phage.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.phage.m_20182_();
         if (this.phage.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.phage.timeFlying >= 50 && EntityEnderiophage.this.fleeAfterStealTime <= 0 && !this.phage.isOverWaterOrVoid()
               ? this.phage.getBlockGrounding(vector3d)
               : this.phage.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.m_148488_(this.phage, 10, 7);
         }
      }

      public boolean m_8045_() {
         return this.flightTarget
            ? this.phage.m_29443_() && this.phage.m_20275_(this.x, this.y, this.z) > 2.0
            : !this.phage.m_21573_().m_26571_() && !this.phage.m_20160_();
      }

      public void m_8056_() {
         if (this.flightTarget) {
            this.phage.setFlying(true);
            this.phage.m_21566_().m_6849_(this.x, this.y, this.z, EntityEnderiophage.this.fleeAfterStealTime == 0 ? 1.3F : 1.0);
         } else {
            this.phage.m_21573_().m_26519_(this.x, this.y, this.z, 1.0);
         }
      }

      public void m_8041_() {
         this.phage.m_21573_().m_26573_();
         super.m_8041_();
      }
   }

   public static class FlyTowardsTarget extends Goal {
      private final EntityEnderiophage parentEntity;

      public FlyTowardsTarget(EntityEnderiophage phage) {
         this.parentEntity = phage;
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         return !this.parentEntity.m_20159_()
            && this.parentEntity.m_5448_() != null
            && !this.isBittenByPhage(this.parentEntity.m_5448_())
            && this.parentEntity.fleeAfterStealTime == 0;
      }

      public boolean m_8045_() {
         return this.parentEntity.m_5448_() != null
            && !this.isBittenByPhage(this.parentEntity.m_5448_())
            && !this.parentEntity.f_19862_
            && !this.parentEntity.m_20159_()
            && this.parentEntity.m_29443_()
            && this.parentEntity.m_21566_().m_24995_()
            && this.parentEntity.fleeAfterStealTime == 0
            && (this.parentEntity.m_5448_() instanceof EnderMan || !this.parentEntity.isMissingEye());
      }

      public boolean isBittenByPhage(Entity entity) {
         int phageCount = 0;

         for (Entity e : entity.m_20197_()) {
            if (e instanceof EntityEnderiophage) {
               phageCount++;
            }
         }

         return phageCount > 3;
      }

      public void m_8041_() {
      }

      public void m_8037_() {
         if (this.parentEntity.m_5448_() != null) {
            float width = this.parentEntity.m_5448_().m_20205_() + this.parentEntity.m_20205_() + 2.0F;
            boolean isWithinReach = this.parentEntity.m_20280_(this.parentEntity.m_5448_()) < width * width;
            if (!this.parentEntity.m_29443_() && !isWithinReach) {
               this.parentEntity
                  .m_21573_()
                  .m_26519_(this.parentEntity.m_5448_().m_20185_(), this.parentEntity.m_5448_().m_20186_(), this.parentEntity.m_5448_().m_20189_(), 1.2);
            } else {
               this.parentEntity
                  .m_21566_()
                  .m_6849_(
                     this.parentEntity.m_5448_().m_20185_(),
                     this.parentEntity.m_5448_().m_20186_(),
                     this.parentEntity.m_5448_().m_20189_(),
                     isWithinReach ? 1.6 : 1.0
                  );
            }

            if (this.parentEntity.m_5448_().m_20186_() > this.parentEntity.m_20186_() + 1.2F) {
               this.parentEntity.setFlying(true);
            }

            if (this.parentEntity.dismountCooldown == 0
               && this.parentEntity.m_20191_().m_82377_(0.3, 0.3, 0.3).m_82381_(this.parentEntity.m_5448_().m_20191_())
               && !this.isBittenByPhage(this.parentEntity.m_5448_())) {
               this.parentEntity.m_7998_(this.parentEntity.m_5448_(), true);
               if (!this.parentEntity.m_9236_().f_46443_) {
                  AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.parentEntity.m_19879_(), this.parentEntity.m_5448_().m_19879_()));
               }
            }
         }
      }
   }
}
