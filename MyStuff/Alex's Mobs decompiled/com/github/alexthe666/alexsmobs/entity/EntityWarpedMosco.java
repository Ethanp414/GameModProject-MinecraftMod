package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityWarpedMosco extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_PUNCH_R = Animation.create(25);
   public static final Animation ANIMATION_PUNCH_L = Animation.create(25);
   public static final Animation ANIMATION_SLAM = Animation.create(35);
   public static final Animation ANIMATION_SUCK = Animation.create(60);
   public static final Animation ANIMATION_SPIT = Animation.create(60);
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.m_135353_(EntityWarpedMosco.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HAND_SIDE = SynchedEntityData.m_135353_(EntityWarpedMosco.class, EntityDataSerializers.f_135035_);
   public float flyLeftProgress;
   public float prevLeftFlyProgress;
   public float flyRightProgress;
   public float prevFlyRightProgress;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isLandNavigator;
   private int timeFlying;
   private int loopSoundTick = 0;

   protected EntityWarpedMosco(EntityType entityType, Level world) {
      super(entityType, world);
      this.f_21364_ = 30;
      this.switchNavigator(false);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 100.0)
         .m_22268_(Attributes.f_22277_, 128.0)
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22284_, 10.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_(Attributes.f_22285_, 2.0)
         .m_22268_(Attributes.f_22279_, 0.3);
   }

   public MobType m_6336_() {
      return MobType.f_21642_;
   }

   private static Animation getRandomAttack(RandomSource rand) {
      return switch (rand.m_188503_(4)) {
         case 0 -> ANIMATION_PUNCH_L;
         case 1 -> ANIMATION_PUNCH_R;
         case 2 -> ANIMATION_SLAM;
         case 3 -> ANIMATION_SUCK;
         default -> ANIMATION_SUCK;
      };
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.WARPED_MOSCO_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.WARPED_MOSCO_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.WARPED_MOSCO_HURT.get();
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(0, new EntityWarpedMosco.AttackGoal());
      this.f_21345_.m_25352_(4, new EntityWarpedMosco.AIWalkIdle());
      this.f_21345_.m_25352_(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
      this.f_21345_.m_25352_(5, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[]{EntityCrimsonMosquito.class, EntityWarpedMosco.class}));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, Player.class, true));
      this.f_21346_
         .m_25352_(
            2,
            new EntityAINearestTarget3D<LivingEntity>(
               this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)
            )
         );
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
      this.f_19804_.m_135372_(HAND_SIDE, true);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean isFlying() {
      return (Boolean)this.f_19804_.m_135370_(FLYING);
   }

   public void setFlying(boolean flying) {
      this.setDashRight(flying != this.isFlying() ? this.f_19796_.m_188499_() : this.isDashRight());
      this.f_19804_.m_135381_(FLYING, flying);
   }

   public boolean isDashRight() {
      return (Boolean)this.f_19804_.m_135370_(HAND_SIDE);
   }

   public void setDashRight(boolean right) {
      this.f_19804_.m_135381_(HAND_SIDE, right);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevFlyRightProgress = this.flyRightProgress;
      this.prevLeftFlyProgress = this.flyLeftProgress;
      boolean dashRight = this.isDashRight();
      boolean flying = this.isFlying();
      if (flying && dashRight && this.flyRightProgress < 5.0F) {
         this.flyRightProgress++;
      }

      if ((!flying || !dashRight) && this.flyRightProgress > 0.0F) {
         this.flyRightProgress--;
      }

      if (flying && !dashRight && this.flyLeftProgress < 5.0F) {
         this.flyLeftProgress++;
      }

      if ((!flying || dashRight) && this.flyLeftProgress > 0.0F) {
         this.flyLeftProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         if (flying) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }
      }

      if (flying) {
         if (this.loopSoundTick == 0) {
            this.m_5496_((SoundEvent)AMSoundRegistry.MOSQUITO_LOOP.get(), this.m_6121_(), this.m_6100_() * 0.3F);
         }

         this.loopSoundTick++;
         if (this.loopSoundTick > 100) {
            this.loopSoundTick = 0;
         }

         this.timeFlying++;
         this.m_20242_(true);
         if (this.m_20159_() || this.m_20160_()) {
            this.setFlying(false);
         }
      } else {
         this.timeFlying = 0;
         this.m_20242_(false);
      }

      if (this.f_19862_ && ForgeEventFactory.getMobGriefingEvent(this.m_9236_(), this)) {
         boolean flag = false;
         AABB axisalignedbb = this.m_20191_().m_82400_(0.2);

         for (BlockPos blockpos : BlockPos.m_121976_(
            Mth.m_14107_(axisalignedbb.f_82288_),
            Mth.m_14107_(axisalignedbb.f_82289_),
            Mth.m_14107_(axisalignedbb.f_82290_),
            Mth.m_14107_(axisalignedbb.f_82291_),
            Mth.m_14107_(axisalignedbb.f_82292_),
            Mth.m_14107_(axisalignedbb.f_82293_)
         )) {
            BlockState blockstate = this.m_9236_().m_8055_(blockpos);
            if (blockstate.m_204336_(AMTagRegistry.WARPED_MOSCO_BREAKABLES)) {
               flag = this.m_9236_().m_46953_(blockpos, true, this) || flag;
            }
         }

         if (!flag && this.m_20096_()) {
            this.m_6135_();
         }
      }

      LivingEntity target = this.m_5448_();
      if (target != null && this.m_6084_()) {
         if (this.getAnimation() == ANIMATION_SUCK && this.getAnimationTick() == 3 && this.m_20270_(target) < 4.7F) {
            target.m_7998_(this, true);
         }

         if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
            for (Entity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(5.0))) {
               if (!this.m_7307_(entity) && !(entity instanceof EntityWarpedMosco) && entity != this) {
                  entity.m_6469_(this.m_269291_().m_269333_(this), 10.0F + this.f_19796_.m_188501_() * 8.0F);
                  this.launch(entity, true);
               }
            }
         }

         if ((this.getAnimation() == ANIMATION_PUNCH_R || this.getAnimation() == ANIMATION_PUNCH_L)
            && this.getAnimationTick() == 13
            && this.m_20270_(target) < 4.7F) {
            target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
            this.knockbackRidiculous(target, 0.9F);
         }
      }

      if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
         this.spawnGroundEffects();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void spawnGroundEffects() {
      float radius = 2.3F;
      double extraY = 0.8F;

      for (int i = 0; i < 4; i++) {
         for (int i1 = 0; i1 < 20 + this.f_19796_.m_188503_(12); i1++) {
            double motionX = this.m_217043_().m_188583_() * 0.07;
            double motionY = this.m_217043_().m_188583_() * 0.07;
            double motionZ = this.m_217043_().m_188583_() * 0.07;
            float angle = (float) (Math.PI / 180.0) * this.f_20883_ + i1;
            double extraX = 2.3F * Mth.m_14031_((float) Math.PI + angle);
            double extraZ = 2.3F * Mth.m_14089_(angle);
            BlockPos ground = this.getMoscoGround(
               new BlockPos(Mth.m_14107_(this.m_20185_() + extraX), Mth.m_14107_(this.m_20186_() + 0.8F) - 1, Mth.m_14107_(this.m_20189_() + extraZ))
            );
            BlockState state = this.m_9236_().m_8055_(ground);
            if (state.m_280296_() && this.m_9236_().f_46443_) {
               this.m_9236_()
                  .m_6493_(
                     new BlockParticleOption(ParticleTypes.f_123794_, state),
                     true,
                     this.m_20185_() + extraX,
                     ground.m_123342_() + 0.8F,
                     this.m_20189_() + extraZ,
                     motionX,
                     motionY,
                     motionZ
                  );
            }
         }
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.m_20096_()) {
         double d0 = e.m_20185_() - this.m_20185_();
         double d1 = e.m_20189_() - this.m_20189_();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 2.0F : 0.5F;
         e.m_5997_(d0 / d2 * f, huge ? 0.5 : 0.2F, d1 / d2 * f);
      }
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

   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_PUNCH_L, ANIMATION_PUNCH_R, ANIMATION_SLAM, ANIMATION_SUCK, ANIMATION_SPIT};
   }

   private BlockPos getMoscoGround(BlockPos in) {
      BlockPos position = new BlockPos(in.m_123341_(), (int)this.m_20186_(), in.m_123343_());

      while (position.m_123342_() > -62 && !this.m_9236_().m_8055_(position).m_280296_() && this.m_9236_().m_6425_(position).m_76178_()) {
         position = position.m_7495_();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = -9.45F - this.m_217043_().m_188503_(24);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.m_7096_() + extraX, this.m_20186_(), fleePos.m_7094_() + extraZ);
      BlockPos ground = this.getMoscoGround(radialPos);
      if (ground.m_123342_() == -62) {
         return this.m_20182_();
      } else {
         ground = this.m_20183_();

         while (ground.m_123342_() > -62 && !this.m_9236_().m_8055_(ground).m_280296_()) {
            ground = ground.m_7495_();
         }

         return !this.isTargetBlocked(Vec3.m_82512_(ground.m_7494_())) ? Vec3.m_82512_(ground) : null;
      }
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = -9.45F - this.m_217043_().m_188503_(24) - radiusAdd;
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.m_7096_() + extraX), 0, (int)(fleePos.m_7094_() + extraZ));
      BlockPos ground = this.getMoscoGround(radialPos);
      int distFromGround = (int)this.m_20186_() - ground.m_123342_();
      int flightHeight = 4 + this.m_217043_().m_188503_(10);
      BlockPos newPos = ground.m_6630_(distFromGround > 8 ? flightHeight : this.m_217043_().m_188503_(6) + 1);
      return !this.isTargetBlocked(Vec3.m_82512_(newPos)) && this.m_20238_(Vec3.m_82512_(newPos)) > 1.0 ? Vec3.m_82512_(newPos) : null;
   }

   public void knockbackRidiculous(LivingEntity target, float power) {
      target.m_147240_(power, this.m_20185_() - target.m_20185_(), this.m_20189_() - target.m_20189_());
      float knockbackResist = (float)Mth.m_14008_(1.0 - this.m_21133_(Attributes.f_22278_), 0.0, 1.0);
      target.m_20256_(target.m_20184_().m_82520_(0.0, knockbackResist * power * 0.45F, 0.0));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   private boolean isOverLiquid() {
      BlockPos position = this.m_20183_();

      while (position.m_123342_() > 2 && this.m_9236_().m_46859_(position)) {
         position = position.m_7495_();
      }

      return !this.m_9236_().m_6425_(position).m_76178_();
   }

   public void m_7023_(Vec3 travelVector) {
      if ((this.getAnimation() == ANIMATION_SUCK || this.getAnimation() == ANIMATION_SLAM) && this.getAnimationTick() > 8) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         travelVector = Vec3.f_82478_;
         super.m_7023_(travelVector);
      } else {
         super.m_7023_(travelVector);
      }
   }

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      super.m_19956_(passenger, moveFunc);
      if (this.m_20363_(passenger)) {
         int tick = 5;
         if (this.getAnimation() == ANIMATION_SUCK) {
            tick = this.getAnimationTick();
         } else {
            passenger.m_8127_();
         }

         float radius = 2.0F;
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         double extraY = tick < 10 ? 0.0 : 0.15F * Mth.m_14045_(tick - 10, 0, 15);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + extraY + 0.1F, this.m_20189_() + extraZ);
         if ((tick - 10) % 4 == 0) {
            this.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 100, 1));
            passenger.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
         }
      }
   }

   public boolean canRiderInteract() {
      return true;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.warpedMoscoSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   private void spit(LivingEntity target) {
      if (this.getAnimation() == ANIMATION_SPIT) {
         this.m_21391_(target, 100.0F, 100.0F);
         this.f_20883_ = this.f_20885_;

         for (int i = 0; i < 2 + this.f_19796_.m_188503_(2); i++) {
            EntityHemolymph llamaspitentity = new EntityHemolymph(this.m_9236_(), this);
            double d0 = target.m_20185_() - this.m_20185_();
            double d1 = target.m_20227_(0.3333333333333333) - llamaspitentity.m_20186_();
            double d2 = target.m_20189_() - this.m_20189_();
            float f = Mth.m_14116_((float)(d0 * d0 + d2 * d2)) * 0.2F;
            llamaspitentity.shoot(d0, d1 + f, d2, 1.5F, 5.0F);
            if (!this.m_20067_()) {
               this.m_146850_(GameEvent.f_157778_);
               this.m_9236_()
                  .m_6263_(
                     null,
                     this.m_20185_(),
                     this.m_20186_(),
                     this.m_20189_(),
                     SoundEvents.f_12098_,
                     this.m_5720_(),
                     1.0F,
                     1.0F + (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F
                  );
            }

            this.m_9236_().m_7967_(llamaspitentity);
         }
      }
   }

   private boolean shouldRangeAttack(LivingEntity target) {
      return this.m_21223_() < Math.floor(this.m_21233_() * 0.25F) ? true : this.m_21223_() < this.m_21223_() * 0.5F && this.m_20270_(target) > 10.0F;
   }

   private class AIWalkIdle extends Goal {
      protected final EntityWarpedMosco mosco;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWalkIdle() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.mosco = EntityWarpedMosco.this;
      }

      public boolean m_8036_() {
         if (this.mosco.m_20160_() || this.mosco.m_5448_() != null && this.mosco.m_5448_().m_6084_() || this.mosco.m_20159_()) {
            return false;
         } else if (this.mosco.m_217043_().m_188503_(30) != 0 && !this.mosco.isFlying()) {
            return false;
         } else {
            if (this.mosco.m_20096_()) {
               this.flightTarget = EntityWarpedMosco.this.f_19796_.m_188503_(8) == 0;
            } else {
               this.flightTarget = EntityWarpedMosco.this.f_19796_.m_188503_(5) > 0 && this.mosco.timeFlying < 200;
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
            this.mosco.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
         } else {
            this.mosco.m_21573_().m_26519_(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityWarpedMosco.this.isFlying() && this.mosco.m_20096_()) {
            this.mosco.setFlying(false);
         }

         if (EntityWarpedMosco.this.isFlying() && this.mosco.m_20096_() && this.mosco.timeFlying > 10) {
            this.mosco.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.mosco.m_20182_();
         if (this.mosco.isOverLiquid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            return this.mosco.timeFlying >= 50 && !this.mosco.isOverLiquid()
               ? this.mosco.getBlockGrounding(vector3d)
               : this.mosco.getBlockInViewAway(vector3d, 0.0F);
         } else {
            return LandRandomPos.m_148488_(this.mosco, 20, 7);
         }
      }

      public boolean m_8045_() {
         return this.flightTarget
            ? this.mosco.isFlying() && this.mosco.m_20275_(this.x, this.y, this.z) > 20.0 && !this.mosco.f_19862_
            : !this.mosco.m_21573_().m_26571_() && !this.mosco.m_20160_();
      }

      public void m_8056_() {
         if (this.flightTarget) {
            this.mosco.setFlying(true);
            this.mosco.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
         } else {
            this.mosco.m_21573_().m_26519_(this.x, this.y, this.z, 1.0);
         }
      }

      public void m_8041_() {
         this.mosco.m_21573_().m_26573_();
         super.m_8041_();
      }
   }

   private class AttackGoal extends Goal {
      private int upTicks = 0;
      private int dashCooldown = 0;
      private boolean ranged = false;
      private BlockPos farTarget = null;

      public AttackGoal() {
      }

      public boolean m_8036_() {
         return EntityWarpedMosco.this.m_5448_() != null;
      }

      public void m_8037_() {
         if (this.dashCooldown > 0) {
            this.dashCooldown--;
         }

         if (EntityWarpedMosco.this.m_5448_() != null) {
            LivingEntity target = EntityWarpedMosco.this.m_5448_();
            this.ranged = EntityWarpedMosco.this.shouldRangeAttack(target);
            if (EntityWarpedMosco.this.isFlying()
               || this.ranged
               || EntityWarpedMosco.this.m_20270_(target) > 12.0F
                  && !EntityWarpedMosco.this.isTargetBlocked(target.m_20182_().m_82520_(0.0, target.m_20206_() * 0.6F, 0.0))) {
               float speedRush = 5.0F;
               this.upTicks++;
               EntityWarpedMosco.this.setFlying(true);
               if (!this.ranged) {
                  if (this.upTicks <= 20 && !(EntityWarpedMosco.this.m_20270_(target) < 6.0F)) {
                     EntityWarpedMosco.this.m_21566_()
                        .m_6849_(EntityWarpedMosco.this.m_20185_(), EntityWarpedMosco.this.m_20186_() + 3.0, EntityWarpedMosco.this.m_20189_(), 0.5);
                  } else {
                     EntityWarpedMosco.this.m_21566_().m_6849_(target.m_20185_(), target.m_20186_() + target.m_20192_() * 0.6F, target.m_20189_(), speedRush);
                  }
               } else {
                  if (this.farTarget == null || EntityWarpedMosco.this.m_20238_(Vec3.m_82512_(this.farTarget)) < 9.0) {
                     this.farTarget = this.getAvoidTarget(target);
                  }

                  if (this.farTarget != null) {
                     EntityWarpedMosco.this.m_21566_()
                        .m_6849_(this.farTarget.m_123341_(), this.farTarget.m_123342_() + target.m_20192_() * 0.6F, this.farTarget.m_123343_(), 3.0);
                  }

                  EntityWarpedMosco.this.setAnimation(EntityWarpedMosco.ANIMATION_SPIT);
                  if (this.upTicks % 30 == 0) {
                     EntityWarpedMosco.this.m_5634_(1.0F);
                  }

                  int tick = EntityWarpedMosco.this.getAnimationTick();
                  switch (tick) {
                     case 10:
                     case 20:
                     case 30:
                     case 40:
                        EntityWarpedMosco.this.spit(target);
                  }
               }
            } else {
               EntityWarpedMosco.this.m_21573_().m_5624_(EntityWarpedMosco.this.m_5448_(), 1.25);
            }

            if (EntityWarpedMosco.this.isFlying()) {
               if (EntityWarpedMosco.this.m_20270_(target) < 4.3F) {
                  if (this.dashCooldown == 0 || target.m_20096_() || target.m_20077_() || target.m_20069_()) {
                     target.m_6469_(EntityWarpedMosco.this.m_269291_().m_269333_(EntityWarpedMosco.this), 5.0F);
                     EntityWarpedMosco.this.knockbackRidiculous(target, 1.0F);
                     this.dashCooldown = 30;
                  }

                  float groundHeight = EntityWarpedMosco.this.getMoscoGround(EntityWarpedMosco.this.m_20183_()).m_123342_();
                  if (Math.abs(EntityWarpedMosco.this.m_20186_() - groundHeight) < 3.0 && !EntityWarpedMosco.this.isOverLiquid()) {
                     EntityWarpedMosco.this.timeFlying += 300;
                     EntityWarpedMosco.this.setFlying(false);
                  }
               }
            } else if (EntityWarpedMosco.this.m_20270_(target) < 4.0F && EntityWarpedMosco.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               Animation animation = EntityWarpedMosco.getRandomAttack(EntityWarpedMosco.this.f_19796_);
               if (animation == EntityWarpedMosco.ANIMATION_SUCK && target.m_20159_()) {
                  animation = EntityWarpedMosco.ANIMATION_SLAM;
               }

               EntityWarpedMosco.this.setAnimation(animation);
            }
         }
      }

      public BlockPos getAvoidTarget(LivingEntity target) {
         float radius = 10 + EntityWarpedMosco.this.m_217043_().m_188503_(8);
         float angle = (float) (Math.PI / 180.0) * (target.f_20885_ + 90.0F + EntityWarpedMosco.this.m_217043_().m_188503_(180));
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(target.m_20185_() + extraX, target.m_20186_() + 1.0, target.m_20189_() + extraZ);
         return EntityWarpedMosco.this.m_20238_(Vec3.m_82512_(radialPos)) > 30.0
               && !EntityWarpedMosco.this.isTargetBlocked(Vec3.m_82512_(radialPos))
               && EntityWarpedMosco.this.m_20238_(Vec3.m_82512_(radialPos)) > 6.0
            ? radialPos
            : EntityWarpedMosco.this.m_20183_();
      }

      public void m_8041_() {
         this.upTicks = 0;
         this.dashCooldown = 0;
         this.ranged = false;
      }
   }
}
