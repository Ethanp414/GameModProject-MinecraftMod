package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntitySunbird extends Animal implements FlyingAnimal {
   public static final Predicate<? super Entity> SCORCH_PRED = new com.google.common.base.Predicate<Entity>() {
      public boolean apply(@Nullable Entity e) {
         return e.m_6084_() && e.m_6095_().m_204039_(AMTagRegistry.SUNBIRD_SCORCH_TARGETS);
      }
   };
   private static final EntityDataAccessor<Boolean> SCORCHING = SynchedEntityData.m_135353_(EntitySunbird.class, EntityDataSerializers.f_135035_);
   public float birdPitch = 0.0F;
   public float prevBirdPitch = 0.0F;
   private int beaconSearchCooldown = 50;
   private BlockPos beaconPos = null;
   private boolean orbitClockwise = false;
   private float prevScorchProgress;
   private float scorchProgress;
   private int fullScorchTime;

   protected EntitySunbird(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.f_21342_ = new EntitySunbird.MoveHelperController(this);
      this.orbitClockwise = new Random().nextBoolean();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(SCORCHING, false);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22277_, 64.0)
         .m_22268_(Attributes.f_22281_, 2.0)
         .m_22268_(Attributes.f_22279_, 1.0);
   }

   public static boolean canSunbirdSpawn(EntityType<? extends Mob> typeIn, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return true;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.sunbirdSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SUNBIRD_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SUNBIRD_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SUNBIRD_HURT.get();
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(3, new EntitySunbird.RandomFlyGoal(this));
      this.f_21345_.m_25352_(4, new LookAtPlayerGoal(this, Player.class, 32.0F));
      this.f_21345_.m_25352_(5, new RandomLookAroundGoal(this));
   }

   public float getBrightness() {
      return 1.0F;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         if (source.m_7639_() != null && source.m_7639_() instanceof LivingEntity) {
            LivingEntity hurter = (LivingEntity)source.m_7639_();
            if (hurter.m_21023_((MobEffect)AMEffectRegistry.SUNBIRD_BLESSING.get())) {
               hurter.m_21195_((MobEffect)AMEffectRegistry.SUNBIRD_BLESSING.get());
            }

            hurter.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.SUNBIRD_CURSE.get(), 600, 0));
         }

         return prev;
      } else {
         return prev;
      }
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_20069_()) {
         this.m_19920_(0.02F, travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.8F));
      } else if (this.m_20077_()) {
         this.m_19920_(0.02F, travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.5));
      } else {
         BlockPos ground = AMBlockPos.fromCoords(this.m_20185_(), this.m_20186_() - 1.0, this.m_20189_());
         float f = 0.91F;
         if (this.m_20096_()) {
            f = this.m_9236_().m_8055_(ground).getFriction(this.m_9236_(), ground, this) * 0.91F;
         }

         f = 0.91F;
         if (this.m_20096_()) {
            f = this.m_9236_().m_8055_(ground).getFriction(this.m_9236_(), ground, this) * 0.91F;
         }

         this.m_267651_(true);
         this.m_19920_(0.2F, travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(f));
      }

      this.m_267651_(false);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevBirdPitch = this.birdPitch;
      this.prevScorchProgress = this.scorchProgress;
      float f2 = (float)(-((float)this.m_20184_().f_82480_ * 180.0F / (float)Math.PI));
      this.birdPitch = f2;
      if (this.m_9236_().f_46443_) {
         float radius = 0.35F + this.f_19796_.m_188501_() * 3.5F;
         float angle = (float) (Math.PI / 180.0) * ((this.f_19796_.m_188499_() ? -85.0F : 85.0F) + this.f_20883_);
         float angleMotion = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         double extraXMotion = -0.2F * Mth.m_14031_((float)(Math.PI + angleMotion));
         double extraZMotion = -0.2F * Mth.m_14089_(angleMotion);
         double yRandom = 0.2F + this.f_19796_.m_188501_() * 0.3F;
         this.m_9236_()
            .m_7106_(
               (ParticleOptions)AMParticleRegistry.SUNBIRD_FEATHER.get(),
               this.m_20185_() + extraX,
               this.m_20186_() + yRandom,
               this.m_20189_() + extraZ,
               extraXMotion,
               0.0,
               extraZMotion
            );
      } else {
         if (this.f_19797_ % 100 == 0) {
            if (!this.isScorching() && !this.getScorchingMobs().isEmpty()) {
               this.setScorching(true);
            }

            for (Player e : this.m_9236_().m_6443_(Player.class, this.getScorchArea(), Predicates.alwaysTrue())) {
               if (!e.m_21023_((MobEffect)AMEffectRegistry.SUNBIRD_BLESSING.get()) && !e.m_21023_((MobEffect)AMEffectRegistry.SUNBIRD_CURSE.get())) {
                  e.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.SUNBIRD_BLESSING.get(), 600, 0));
               }
            }
         }

         if (this.beaconSearchCooldown > 0) {
            this.beaconSearchCooldown--;
         }

         if (this.beaconSearchCooldown <= 0) {
            this.beaconSearchCooldown = 100 + this.f_19796_.m_188503_(200);
            if (this.m_9236_() instanceof ServerLevel) {
               List<BlockPos> beacons = this.getNearbyBeacons(this.m_20183_(), (ServerLevel)this.m_9236_(), 64);
               BlockPos closest = null;

               for (BlockPos pos : beacons) {
                  if ((
                        closest == null
                           || this.m_20275_(closest.m_123341_(), closest.m_123342_(), closest.m_123343_())
                              > this.m_20275_(pos.m_123341_(), pos.m_123342_(), pos.m_123343_())
                     )
                     && this.isValidBeacon(pos)) {
                     closest = pos;
                  }
               }

               if (closest != null && this.isValidBeacon(closest)) {
                  this.beaconPos = closest;
               }
            }

            if (this.beaconPos != null && !this.isValidBeacon(this.beaconPos) && this.f_19797_ > 40) {
               this.beaconPos = null;
            }
         }
      }

      boolean scorching = this.isScorching();
      if (scorching) {
         if (this.scorchProgress < 20.0F) {
            this.scorchProgress++;
         }
      } else if (this.scorchProgress > 0.0F) {
         this.scorchProgress--;
      }

      if (scorching && this.scorchProgress == 20.0F && !this.m_9236_().f_46443_) {
         if (this.fullScorchTime > 30) {
            this.setScorching(false);
         } else if (this.fullScorchTime % 5 == 0) {
            for (Entity ex : this.getScorchingMobs()) {
               ex.m_20254_(4);
               if (ex instanceof Phantom) {
                  ((Phantom)ex).m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.SUNBIRD_CURSE.get(), 200, 0));
               }
            }
         }

         this.fullScorchTime++;
      } else {
         this.fullScorchTime = 0;
      }
   }

   private List<LivingEntity> getScorchingMobs() {
      return this.m_9236_().m_6443_(LivingEntity.class, this.getScorchArea(), SCORCH_PRED);
   }

   public boolean isScorching() {
      return (Boolean)this.f_19804_.m_135370_(SCORCHING);
   }

   public void setScorching(boolean scorching) {
      this.f_19804_.m_135381_(SCORCHING, scorching);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128441_("BeaconPosX")) {
         int i = compound.m_128451_("BeaconPosX");
         int j = compound.m_128451_("BeaconPosY");
         int k = compound.m_128451_("BeaconPosZ");
         this.beaconPos = new BlockPos(i, j, k);
      } else {
         this.beaconPos = null;
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      BlockPos blockpos = this.beaconPos;
      if (blockpos != null) {
         compound.m_128405_("BeaconPosX", blockpos.m_123341_());
         compound.m_128405_("BeaconPosY", blockpos.m_123342_());
         compound.m_128405_("BeaconPosZ", blockpos.m_123343_());
      }
   }

   private AABB getScorchArea() {
      return this.m_20191_().m_82377_(15.0, 32.0, 15.0);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return null;
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   private List<BlockPos> getNearbyBeacons(BlockPos blockpos, ServerLevel world, int range) {
      PoiManager pointofinterestmanager = world.m_8904_();
      Stream<BlockPos> stream = pointofinterestmanager.m_27138_(
         poiTypeHolder -> poiTypeHolder.m_203565_(AMPointOfInterestRegistry.BEACON.getKey()), Predicates.alwaysTrue(), blockpos, range, Occupancy.ANY
      );
      return stream.collect(Collectors.toList());
   }

   private boolean isValidBeacon(BlockPos pos) {
      BlockEntity te = this.m_9236_().m_7702_(pos);
      return te instanceof BeaconBlockEntity && !((BeaconBlockEntity)te).m_58702_().isEmpty();
   }

   public boolean m_29443_() {
      return true;
   }

   public float getScorchProgress(float partialTick) {
      return (this.prevScorchProgress + (this.scorchProgress - this.prevScorchProgress) * partialTick) / 20.0F;
   }

   static class MoveHelperController extends MoveControl {
      private final EntitySunbird parentEntity;

      public MoveHelperController(EntitySunbird sunbird) {
         super(sunbird);
         this.parentEntity = sunbird;
      }

      public void m_8126_() {
         if (this.f_24981_ == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(
               this.f_24975_ - this.parentEntity.m_20185_(), this.f_24976_ - this.parentEntity.m_20186_(), this.f_24977_ - this.parentEntity.m_20189_()
            );
            double d0 = vector3d.m_82553_();
            if (d0 < this.parentEntity.m_20191_().m_82309_()) {
               this.f_24981_ = Operation.WAIT;
               this.parentEntity.m_20256_(this.parentEntity.m_20184_().m_82490_(0.5));
            } else {
               this.parentEntity.m_20256_(this.parentEntity.m_20184_().m_82549_(vector3d.m_82490_(this.f_24978_ * 0.05 / d0)));
               if (this.parentEntity.m_5448_() == null) {
                  Vec3 vector3d1 = this.parentEntity.m_20184_();
                  this.parentEntity.m_146922_(-((float)Mth.m_14136_(vector3d1.f_82479_, vector3d1.f_82481_)) * (180.0F / (float)Math.PI));
                  this.parentEntity.f_20883_ = this.parentEntity.m_146908_();
               } else {
                  double d2 = this.parentEntity.m_5448_().m_20185_() - this.parentEntity.m_20185_();
                  double d1 = this.parentEntity.m_5448_().m_20189_() - this.parentEntity.m_20189_();
                  this.parentEntity.m_146922_(-((float)Mth.m_14136_(d2, d1)) * (180.0F / (float)Math.PI));
                  this.parentEntity.f_20883_ = this.parentEntity.m_146908_();
               }
            }
         }
      }

      private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
         AABB axisalignedbb = this.parentEntity.m_20191_();

         for (int i = 1; i < p_220673_2_; i++) {
            axisalignedbb = axisalignedbb.m_82383_(p_220673_1_);
            if (!this.parentEntity.m_9236_().m_45756_(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }

   static class RandomFlyGoal extends Goal {
      private final EntitySunbird parentEntity;
      private BlockPos target = null;

      public RandomFlyGoal(EntitySunbird sunbird) {
         this.parentEntity = sunbird;
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         MoveControl movementcontroller = this.parentEntity.m_21566_();
         if (movementcontroller.m_24995_() && this.target != null) {
            return false;
         } else {
            if (this.parentEntity.beaconPos != null) {
               this.target = this.getBlockInViewBeacon(this.parentEntity.beaconPos, 5 + this.parentEntity.f_19796_.m_188503_(1));
            } else {
               this.target = this.getBlockInViewSunbird();
            }

            if (this.target != null) {
               this.parentEntity
                  .m_21566_()
                  .m_6849_(
                     this.target.m_123341_() + 0.5,
                     this.target.m_123342_() + 0.5,
                     this.target.m_123343_() + 0.5,
                     this.parentEntity.beaconPos != null ? 0.8 : 1.0
                  );
            }

            return true;
         }
      }

      public boolean m_8045_() {
         return this.target != null
            && this.parentEntity.m_20238_(Vec3.m_82512_(this.target)) > 2.4
            && this.parentEntity.m_21566_().m_24995_()
            && !this.parentEntity.f_19862_;
      }

      public void m_8041_() {
         this.target = null;
      }

      public void m_8037_() {
         if (this.target == null) {
            if (this.parentEntity.beaconPos != null) {
               this.target = this.getBlockInViewBeacon(this.parentEntity.beaconPos, 5 + this.parentEntity.f_19796_.m_188503_(1));
            } else {
               this.target = this.getBlockInViewSunbird();
            }
         }

         if (this.parentEntity.beaconPos != null && this.parentEntity.f_19796_.m_188503_(100) == 0) {
            this.parentEntity.orbitClockwise = this.parentEntity.f_19796_.m_188499_();
         }

         if (this.target != null) {
            this.parentEntity
               .m_21566_()
               .m_6849_(
                  this.target.m_123341_() + 0.5, this.target.m_123342_() + 0.5, this.target.m_123343_() + 0.5, this.parentEntity.beaconPos != null ? 0.8 : 1.0
               );
            if (this.parentEntity.m_20238_(Vec3.m_82512_(this.target)) < 2.5) {
               this.target = null;
            }
         }
      }

      private BlockPos getBlockInViewBeacon(BlockPos orbitPos, float gatheringCircleDist) {
         float angle = (float) (Math.PI / 20) * (this.parentEntity.orbitClockwise ? -this.parentEntity.f_19797_ : this.parentEntity.f_19797_);
         double extraX = gatheringCircleDist * Mth.m_14031_(angle);
         double extraZ = gatheringCircleDist * Mth.m_14089_(angle);
         if (orbitPos != null) {
            BlockPos pos = AMBlockPos.fromCoords(
               orbitPos.m_123341_() + extraX, orbitPos.m_123342_() + this.parentEntity.f_19796_.m_188503_(2) + 2, orbitPos.m_123343_() + extraZ
            );
            if (this.parentEntity.m_9236_().m_46859_(new BlockPos(pos))) {
               return pos;
            }
         }

         return null;
      }

      public BlockPos getBlockInViewSunbird() {
         float radius = -9.45F - this.parentEntity.m_217043_().m_188503_(24);
         float neg = this.parentEntity.m_217043_().m_188499_() ? 1.0F : -1.0F;
         float renderYawOffset = this.parentEntity.f_20883_;
         float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.parentEntity.m_217043_().m_188501_() * neg;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.m_20185_() + extraX, 0.0, this.parentEntity.m_20189_() + extraZ);
         BlockPos ground = this.parentEntity.m_9236_().m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
         int distFromGround = (int)this.parentEntity.m_20186_() - ground.m_123342_();
         int flightHeight = Math.max(ground.m_123342_(), 230 + this.parentEntity.m_217043_().m_188503_(40)) - ground.m_123342_();
         BlockPos newPos = radialPos.m_6630_(
            distFromGround > 16 ? flightHeight : (int)this.parentEntity.m_20186_() + this.parentEntity.m_217043_().m_188503_(16) + 1
         );
         return !this.parentEntity.isTargetBlocked(Vec3.m_82512_(newPos)) && this.parentEntity.m_20238_(Vec3.m_82512_(newPos)) > 6.0 ? newPos : null;
      }
   }
}
