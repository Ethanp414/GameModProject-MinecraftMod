package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityMurmurHead extends Monster implements FlyingAnimal {
   private static final EntityDataAccessor<Optional<UUID>> BODY_UUID = SynchedEntityData.m_135353_(EntityMurmurHead.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> BODY_ID = SynchedEntityData.m_135353_(EntityMurmurHead.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> PULLED_IN = SynchedEntityData.m_135353_(EntityMurmurHead.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.m_135353_(EntityMurmurHead.class, EntityDataSerializers.f_135035_);
   public double prevXHair;
   public double prevYHair;
   public double prevZHair;
   public double xHair;
   public double yHair;
   public double zHair;
   public float angerProgress;
   public float prevAngerProgress;
   private boolean prevLaunched = false;

   protected EntityMurmurHead(EntityType type, Level level) {
      super(type, level);
      this.f_21342_ = new EntityMurmurHead.MoveController();
   }

   protected EntityMurmurHead(EntityMurmur parent) {
      this((EntityType)AMEntityRegistry.MURMUR_HEAD.get(), parent.m_9236_());
      this.setBodyId(parent.m_20148_());
      this.doSpawnPositioning(parent);
   }

   protected PathNavigation m_6037_(Level level) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, this.m_9236_());
      flyingpathnavigation.m_26440_(false);
      flyingpathnavigation.m_7008_(true);
      flyingpathnavigation.m_26443_(true);
      return flyingpathnavigation;
   }

   public int m_213860_() {
      return 0;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new EntityMurmurHead.AttackGoal());
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, Player.class, 10, false, true, null));
      this.f_21346_.m_25352_(3, new EntityAINearestTarget3D(this, AbstractVillager.class, 30, false, true, null));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(BODY_UUID, Optional.empty());
      this.f_19804_.m_135372_(BODY_ID, -1);
      this.f_19804_.m_135372_(PULLED_IN, true);
      this.f_19804_.m_135372_(ANGRY, false);
   }

   private void doSpawnPositioning(EntityMurmur parent) {
      this.m_146884_(parent.getNeckBottom(1.0F).m_82520_(0.0, 0.5, 0.0));
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 48.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22279_, 0.2F);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean isPulledIn() {
      return (Boolean)this.f_19804_.m_135370_(PULLED_IN);
   }

   public void setPulledIn(boolean pulledIn) {
      this.f_19804_.m_135381_(PULLED_IN, pulledIn);
   }

   public boolean isAngry() {
      return (Boolean)this.f_19804_.m_135370_(ANGRY) || !this.m_6084_();
   }

   public void setAngry(boolean angry) {
      this.f_19804_.m_135381_(ANGRY, angry);
   }

   public Vec3 getNeckTop(float partialTick) {
      double d0 = Mth.m_14139_(partialTick, this.f_19854_, this.m_20185_());
      double d1 = Mth.m_14139_(partialTick, this.f_19855_, this.m_20186_());
      double d2 = Mth.m_14139_(partialTick, this.f_19856_, this.m_20189_());
      double bounce = 0.0;
      Entity body = this.getBody();
      if (body instanceof EntityMurmur) {
         bounce = ((EntityMurmur)body).calculateWalkBounce(partialTick);
      }

      return new Vec3(d0, d1 + bounce, d2);
   }

   public Vec3 getNeckBottom(float partialTick) {
      Entity body = this.getBody();
      Vec3 top = this.getNeckTop(partialTick);
      if (body instanceof EntityMurmur murmur) {
         Vec3 bodyBase = murmur.getNeckBottom(partialTick);
         double sub = top.m_82546_(bodyBase).m_165924_();
         return sub <= 0.06 ? new Vec3(top.f_82479_, bodyBase.f_82480_, top.f_82481_) : bodyBase;
      } else {
         return top.m_82520_(0.0, -0.5, 0.0);
      }
   }

   public boolean hasNeckBottom() {
      return true;
   }

   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   @Nullable
   public UUID getBodyId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(BODY_UUID)).orElse(null);
   }

   public void setBodyId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(BODY_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getBody() {
      if (!this.m_9236_().f_46443_) {
         UUID id = this.getBodyId();
         return id == null ? null : ((ServerLevel)this.m_9236_()).m_8791_(id);
      } else {
         int id = (Integer)this.f_19804_.m_135370_(BODY_ID);
         return id == -1 ? null : this.m_9236_().m_6815_(id);
      }
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128403_("BodyUUID")) {
         this.setBodyId(compound.m_128342_("BodyUUID"));
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      if (this.getBodyId() != null) {
         compound.m_128362_("BodyUUID", this.getBodyId());
      }
   }

   protected float m_6431_(Pose pose, EntityDimensions dimensions) {
      return dimensions.f_20378_ * 0.35F;
   }

   public void m_8119_() {
      super.m_8119_();
      this.f_20885_ = Mth.m_14036_(this.f_20885_, this.f_20883_ - 70.0F, this.f_20883_ + 70.0F);
      this.prevAngerProgress = this.angerProgress;
      if (this.isAngry() && this.angerProgress < 5.0F) {
         this.angerProgress++;
      }

      if (!this.isAngry() && this.angerProgress > 0.0F) {
         this.angerProgress--;
      }

      this.moveHair();
      Entity body = this.getBody();
      if (!this.m_9236_().f_46443_) {
         if (body instanceof EntityMurmur murmur) {
            this.f_19804_.m_135381_(BODY_ID, body.m_19879_());
            if (this.isPulledIn() && murmur.m_6084_()) {
               Vec3 base = murmur.getNeckBottom(1.0F).m_82520_(0.0, 0.55F, 0.0);
               Vec3 vec3 = base.m_82546_(this.m_20182_());
               if (vec3.m_82553_() < 1.0) {
                  this.m_6034_(base.f_82479_, base.f_82480_, base.f_82481_);
                  this.f_19794_ = false;
               } else {
                  this.f_19794_ = true;
                  vec3 = base.m_82546_(this.m_20182_()).m_82541_();
                  float f = this.m_5448_() != null && this.m_5448_().m_6084_() ? 0.3F : 0.15F;
                  this.m_20256_(vec3.m_82490_(f));
               }

               this.m_146922_(murmur.m_146908_());
               this.f_20883_ = murmur.m_146908_();
            } else {
               this.f_19794_ = false;
            }

            LivingEntity headTarget = this.m_5448_();
            LivingEntity bodyTarget = murmur.m_5448_();
            if (headTarget != null && headTarget.m_6084_()) {
               if (murmur.m_6779_(headTarget)) {
                  murmur.m_6710_(headTarget);
               } else {
                  this.m_6710_(null);
                  murmur.m_6710_(null);
               }
            } else if (bodyTarget != null && bodyTarget.m_6084_() && this.m_6779_(bodyTarget)) {
               this.m_6710_(bodyTarget);
            }

            if (body.m_213877_()) {
               this.m_142687_(RemovalReason.DISCARDED);
            }
         }

         if (body == null && this.f_19797_ > 20) {
            this.m_142687_(RemovalReason.DISCARDED);
         }
      } else if (body instanceof EntityMurmur murmur && (murmur.f_20916_ > 0 || murmur.f_20919_ > 0)) {
         this.f_20916_ = murmur.f_20916_;
         this.f_20919_ = murmur.f_20919_;
      }

      if (this.prevLaunched && !this.isPulledIn()) {
         this.m_5496_((SoundEvent)AMSoundRegistry.MURMUR_NECK.get(), 3.0F * this.m_6121_(), this.m_6100_());
      }

      this.prevLaunched = this.isPulledIn();
   }

   public boolean m_6063_() {
      return false;
   }

   public boolean m_6469_(DamageSource source, float damage) {
      Entity body = this.getBody();
      if (this.m_6673_(source)) {
         return false;
      } else {
         return body != null && body.m_6469_(source, 0.5F * damage) ? true : super.m_6469_(source, damage);
      }
   }

   public boolean m_6673_(DamageSource damageSource) {
      return super.m_6673_(damageSource) || damageSource.m_276093_(DamageTypes.f_268612_);
   }

   private void moveHair() {
      this.prevXHair = this.xHair;
      this.prevYHair = this.yHair;
      this.prevZHair = this.zHair;
      double d0 = this.m_20185_() - this.xHair;
      double d1 = this.m_20186_() - this.yHair;
      double d2 = this.m_20189_() - this.zHair;
      if (d0 > 10.0) {
         this.xHair = this.m_20185_();
         this.prevXHair = this.xHair;
      }

      if (d2 > 10.0) {
         this.zHair = this.m_20189_();
         this.prevZHair = this.zHair;
      }

      if (d1 > 10.0) {
         this.yHair = this.m_20186_();
         this.prevYHair = this.yHair;
      }

      if (d0 < -10.0) {
         this.xHair = this.m_20185_();
         this.prevXHair = this.xHair;
      }

      if (d2 < -10.0) {
         this.zHair = this.m_20189_();
         this.prevZHair = this.zHair;
      }

      if (d1 < -10.0) {
         this.yHair = this.m_20186_();
         this.prevYHair = this.yHair;
      }

      this.xHair += d0 * 0.25;
      this.zHair += d2 * 0.25;
      this.yHair += d1 * 0.25;
   }

   public boolean m_7307_(Entity entity) {
      return this.getBodyId() != null && entity.m_20148_().equals(this.getBodyId()) || super.m_7307_(entity);
   }

   public void m_8032_() {
      if (this.isPulledIn() && !this.isAngry()) {
         super.m_8032_();
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.MURMUR_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return this.getBody() == null ? (SoundEvent)AMSoundRegistry.MURMUR_HURT.get() : null;
   }

   protected SoundEvent m_5592_() {
      return this.getBody() == null ? (SoundEvent)AMSoundRegistry.MURMUR_HURT.get() : null;
   }

   public boolean m_29443_() {
      return true;
   }

   protected void m_7355_(BlockPos pos, BlockState blockIn) {
   }

   private class AttackGoal extends Goal {
      private int time;
      private int biteCooldown = 0;
      private Vec3 emergeFrom = Vec3.f_82478_;
      private float emergeAngle = 0.0F;

      public AttackGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         return EntityMurmurHead.this.m_5448_() != null && EntityMurmurHead.this.m_5448_().m_6084_();
      }

      public void m_8056_() {
         this.time = 0;
         this.biteCooldown = 0;
         EntityMurmurHead.this.setPulledIn(false);
      }

      public void m_8041_() {
         this.time = 0;
         EntityMurmurHead.this.setPulledIn(true);
         EntityMurmurHead.this.setAngry(false);
      }

      public void m_8037_() {
         LivingEntity target = EntityMurmurHead.this.m_5448_();
         Entity body = EntityMurmurHead.this.getBody();
         if (target != null) {
            double dist = Math.sqrt(EntityMurmurHead.this.m_20238_(target.m_146892_()));
            double bodyDist = body != null ? body.m_20270_(target) : 0.0;
            if (bodyDist > 16.0 && this.time > 30 && body instanceof EntityMurmur murmur) {
               murmur.m_6710_(target);
               murmur.m_21573_().m_5624_(target, 1.35);
            }

            if (bodyDist > 64.0) {
               EntityMurmurHead.this.setPulledIn(true);
            } else if (this.biteCooldown == 0) {
               EntityMurmurHead.this.setPulledIn(false);
               Vec3 moveTo = target.m_146892_();
               if (this.time > 30) {
                  if (!EntityMurmurHead.this.isAngry()) {
                     EntityMurmurHead.this.m_5496_(
                        (SoundEvent)AMSoundRegistry.MURMUR_ANGER.get(), 1.5F * EntityMurmurHead.this.m_6121_(), EntityMurmurHead.this.m_6100_()
                     );
                     EntityMurmurHead.this.m_146850_(GameEvent.f_223709_);
                  }

                  EntityMurmurHead.this.setAngry(true);
                  EntityMurmurHead.this.m_21573_().m_26519_(moveTo.f_82479_, moveTo.f_82480_, moveTo.f_82481_, 1.3);
               } else {
                  if (this.time == 0) {
                     this.emergeFrom = EntityMurmurHead.this.getNeckTop(1.0F).m_82520_(0.0, 0.5, 0.0);
                     Vec3 clockwise = moveTo.m_82546_(this.emergeFrom);
                  }

                  boolean clockwise = false;
                  float circleDistance = 2.5F;
                  float circlingTime = 30 * this.time;
                  float angle = (float) (Math.PI / 180.0) * (clockwise ? -circlingTime : circlingTime);
                  double extraX = circleDistance * Mth.m_14031_((float) Math.PI + angle);
                  double extraZ = circleDistance * Mth.m_14089_(angle);
                  double y = Math.max(this.emergeFrom.f_82480_ + 2.0, target.m_20188_());
                  Vec3 vec3 = new Vec3(this.emergeFrom.f_82479_ + extraX, y, this.emergeFrom.f_82481_ + extraZ);
                  EntityMurmurHead.this.m_21573_().m_26519_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 0.7);
               }

               EntityMurmurHead.this.m_7618_(Anchor.EYES, moveTo);
               if (dist < 1.5 && EntityMurmurHead.this.m_142582_(target)) {
                  EntityMurmurHead.this.m_5496_(
                     (SoundEvent)AMSoundRegistry.MURMUR_ATTACK.get(), EntityMurmurHead.this.m_6121_(), EntityMurmurHead.this.m_6100_()
                  );
                  this.biteCooldown = 5 + EntityMurmurHead.this.m_217043_().m_188503_(15);
                  target.m_6469_(EntityMurmurHead.this.m_269291_().m_269333_(EntityMurmurHead.this), 5.0F);
               }
            } else {
               EntityMurmurHead.this.setPulledIn(true);
               EntityMurmurHead.this.m_7618_(Anchor.EYES, target.m_146892_());
               EntityMurmurHead.this.setAngry(false);
            }

            this.time++;
         }

         if (this.biteCooldown > 0) {
            this.biteCooldown--;
         }
      }
   }

   class MoveController extends MoveControl {
      private final Mob parentEntity = EntityMurmurHead.this;

      public MoveController() {
         super(EntityMurmurHead.this);
      }

      public void m_8126_() {
         if (!EntityMurmurHead.this.isPulledIn()) {
            float angle = (float) (Math.PI / 180.0) * (this.parentEntity.f_20883_ + 90.0F);
            float radius = (float)Math.sin(this.parentEntity.f_19797_ * 0.2F) * 2.0F;
            double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
            double extraY = radius * -Math.cos(angle - (Math.PI / 2));
            double extraZ = radius * Mth.m_14089_(angle);
            Vec3 strafPlus = new Vec3(extraX, extraY, extraZ);
            if (this.f_24981_ == Operation.MOVE_TO) {
               Vec3 vector3d = new Vec3(
                  this.f_24975_ - this.parentEntity.m_20185_(), this.f_24976_ - this.parentEntity.m_20186_(), this.f_24977_ - this.parentEntity.m_20189_()
               );
               double d0 = vector3d.m_82553_();
               double width = this.parentEntity.m_20191_().m_82309_();
               Vec3 shimmy = Vec3.f_82478_;
               LivingEntity attackTarget = this.parentEntity.m_5448_();
               if (attackTarget != null && this.parentEntity.f_19862_) {
                  shimmy = new Vec3(0.0, 0.005, 0.0);
               }

               Vec3 vector3d1 = vector3d.m_82490_(this.f_24978_ * 0.05 / d0);
               this.parentEntity
                  .m_20256_(this.parentEntity.m_20184_().m_82549_(vector3d1.m_82549_(strafPlus.m_82490_(0.003 * Math.min(d0, 100.0)).m_82549_(shimmy))));
               if (attackTarget == null) {
                  if (d0 >= width) {
                     Vec3 deltaMovement = this.parentEntity.m_20184_();
                     this.parentEntity.m_146922_(-((float)Mth.m_14136_(deltaMovement.f_82479_, deltaMovement.f_82481_)) * (180.0F / (float)Math.PI));
                     this.parentEntity.f_20883_ = this.parentEntity.m_146908_();
                  }
               } else {
                  double d2 = attackTarget.m_20185_() - this.parentEntity.m_20185_();
                  double d1 = attackTarget.m_20189_() - this.parentEntity.m_20189_();
                  this.parentEntity.m_146922_(-((float)Mth.m_14136_(d2, d1)) * (180.0F / (float)Math.PI));
                  this.parentEntity.f_20883_ = this.parentEntity.m_146908_();
               }
            } else if (this.f_24981_ == Operation.WAIT) {
               this.parentEntity.m_20256_(this.parentEntity.m_20184_().m_82549_(strafPlus.m_82490_(0.003)));
            }
         }
      }
   }
}
