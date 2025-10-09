package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class EntitySkreecher extends Monster {
   public static final float MAX_DIST_TO_CEILING = 4.0F;
   private static final EntityDataAccessor<Boolean> CLINGING = SynchedEntityData.m_135353_(EntitySkreecher.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> JUMPING_UP = SynchedEntityData.m_135353_(EntitySkreecher.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> CLAPPING = SynchedEntityData.m_135353_(EntitySkreecher.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> DIST_TO_CEILING = SynchedEntityData.m_135353_(EntitySkreecher.class, EntityDataSerializers.f_135029_);
   protected static final EntityDimensions GROUND_SIZE = EntityDimensions.m_20395_(0.99F, 1.35F);
   public float prevClingProgress;
   public float clingProgress;
   public float prevClapProgress;
   public float clapProgress;
   public float prevDistanceToCeiling;
   private int clapTick = 0;
   private int clingCooldown = 0;
   private boolean isUpsideDownNavigator;
   private boolean hasAttemptedWardenSpawning;
   private boolean hasGroundSize = false;

   protected EntitySkreecher(EntityType<? extends Monster> type, Level level) {
      super(type, level);
      this.switchNavigator(false);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(3, new AvoidEntityGoal(this, Warden.class, 6.0F, 1.0, 1.2));
      this.f_21345_.m_25352_(2, new EntitySkreecher.FollowTargetGoal());
      this.f_21345_.m_25352_(3, new EntitySkreecher.WanderUpsideDownGoal());
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, LivingEntity.class, 30.0F));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, Player.class, true) {
         @Override
         protected AABB m_7255_(double targetDistance) {
            AABB bb = this.f_26135_.m_20191_().m_82377_(16.0, 1.0, 16.0);
            return new AABB(bb.f_82288_, -64.0, bb.f_82290_, bb.f_82291_, 320.0, bb.f_82293_);
         }
      });
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.skreecherSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static boolean checkSkreecherSpawnRules(
      EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean isOnSculk = worldIn.m_8055_(pos.m_7495_()).m_60713_(Blocks.f_220855_);
      return worldIn.m_46791_() != Difficulty.PEACEFUL && m_219009_(worldIn, pos, random) && isOnSculk;
   }

   public int m_5792_() {
      return 1;
   }

   private void switchNavigator(boolean clinging) {
      if (clinging) {
         this.f_21342_ = new EntitySkreecher.MoveController();
         this.f_21344_ = this.createScreecherNavigation(this.m_9236_());
         this.isUpsideDownNavigator = true;
      } else {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigation(this, this.m_9236_());
         this.isUpsideDownNavigator = false;
      }
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 2.0)
         .m_22268_(Attributes.f_22281_, 1.0)
         .m_22268_(Attributes.f_22279_, 0.2F)
         .m_22268_(Attributes.f_22277_, 64.0);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DIST_TO_CEILING, 0.0F);
      this.f_19804_.m_135372_(CLINGING, false);
      this.f_19804_.m_135372_(JUMPING_UP, false);
      this.f_19804_.m_135372_(CLAPPING, false);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SKREECHER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SKREECHER_HURT.get();
   }

   public boolean m_6469_(DamageSource source, float value) {
      this.setClinging(false);
      this.setClapping(false);
      this.clingCooldown = 200 + this.f_19796_.m_188503_(200);
      return super.m_6469_(source, value);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevClapProgress = this.clapProgress;
      this.prevClingProgress = this.clingProgress;
      this.prevDistanceToCeiling = this.getDistanceToCeiling();
      boolean clingVisually = this.isClinging() || this.isJumpingUp() || this.f_20899_;
      if (clingVisually && this.clingProgress < 5.0F) {
         this.clingProgress++;
      }

      if (!clingVisually && this.clingProgress > 0.0F && this.getDistanceToCeiling() == 0.0F) {
         this.clingProgress--;
      }

      boolean clapping = this.isClapping();
      if (clapping) {
         if (this.clapProgress < 5.0F) {
            this.clapProgress++;
         }
      } else if (this.clapProgress > 0.0F) {
         this.clapProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         float technicalDistToCeiling = this.calculateDistanceToCeiling();
         float gap = Math.max(technicalDistToCeiling - this.getDistanceToCeiling(), 0.0F);
         if (this.isClinging()) {
            this.m_20242_(true);
            if (technicalDistToCeiling > 4.0F || !this.m_6084_() || this.clingCooldown > 0 || this.isInFluidType()) {
               this.setClinging(false);
            }

            float goal = Math.min(technicalDistToCeiling, 4.0F);
            if (this.getDistanceToCeiling() < goal) {
               this.setDistanceToCeiling(Math.min(goal, this.prevDistanceToCeiling + 0.15F));
            }

            if (this.getDistanceToCeiling() > goal) {
               this.setDistanceToCeiling(Math.max(goal, this.prevDistanceToCeiling - 0.15F));
            }

            if (this.getDistanceToCeiling() < 1.0F) {
               gap = -0.03F;
            }

            this.m_20256_(this.m_20184_().m_82520_(0.0, gap * 0.5F, 0.0));
         } else {
            this.m_20242_(false);
            if (technicalDistToCeiling < 4.0F && this.clingCooldown <= 0) {
               this.setClinging(true);
            }

            this.setDistanceToCeiling(Math.max(0.0F, this.prevDistanceToCeiling - 0.5F));
            if (this.m_20096_()
               && this.clingCooldown <= 0
               && !this.isJumpingUp()
               && this.m_6084_()
               && this.f_19796_.m_188501_() < 0.0085F
               && technicalDistToCeiling > 4.0F
               && !this.m_9236_().m_45527_(this.m_20183_())) {
               this.setJumpingUp(true);
            }
         }
      }

      if (this.isJumpingUp()) {
         if (this.m_6084_() && !this.m_9236_().m_45527_(this.m_20183_()) && (!this.f_19863_ || this.m_20096_())) {
            this.setDistanceToCeiling(1.5F);
            this.m_20256_(this.m_20184_().m_82520_(0.0, 0.2F, 0.0));

            for (int i = 0; i < 3; i++) {
               this.m_9236_().m_7106_(ParticleTypes.f_235900_, this.m_20208_(0.5), this.m_20186_() - 0.2F, this.m_20262_(0.5), 0.0, -0.2F, 0.0);
            }
         } else {
            this.setJumpingUp(false);
         }
      }

      if (this.clingCooldown > 0) {
         this.clingCooldown--;
      }

      if (!this.m_6084_() || this.clingCooldown > 0 && this.isClinging()) {
         this.m_20256_(this.m_20184_().m_82520_(0.0, -0.25, 0.0));
      }

      if (this.isClinging() && !this.isUpsideDownNavigator) {
         this.switchNavigator(true);
      }

      if (!this.isClinging() && this.isUpsideDownNavigator) {
         this.switchNavigator(false);
      }

      if (this.isClapping() && this.m_6084_() && this.clingCooldown <= 0) {
         float dir = this.isClinging() ? -0.5F : 0.1F;
         if (this.clapTick % 8 == 0) {
            this.m_5496_((SoundEvent)AMSoundRegistry.SKREECHER_CLAP.get(), this.m_6121_() * 3.0F, this.m_6100_());
            this.m_146850_(GameEvent.f_223709_);
            this.angerAllNearbyWardens();
            this.m_9236_().m_7106_((ParticleOptions)AMParticleRegistry.SKULK_BOOM.get(), this.m_20185_(), this.m_20188_(), this.m_20189_(), 0.0, dir, 0.0);
         } else if (this.clapTick % 15 == 0) {
            this.m_5496_((SoundEvent)AMSoundRegistry.SKREECHER_CALL.get(), this.m_6121_() * 4.0F, this.m_6100_());
         }

         if (this.clapTick >= 100 && !this.hasAttemptedWardenSpawning && AMConfig.skreechersSummonWarden) {
            this.hasAttemptedWardenSpawning = true;
            BlockPos spawnAt = this.m_20183_().m_7495_();

            while (spawnAt.m_123342_() > -64 && !this.m_9236_().m_8055_(spawnAt).m_60783_(this.m_9236_(), spawnAt, Direction.UP)) {
               spawnAt = spawnAt.m_7495_();
            }

            Holder<Biome> holder = this.m_9236_().m_204166_(spawnAt);
            if (!this.m_9236_().f_46443_ && this.getNearbyWardens().isEmpty() && holder.m_203656_(AMTagRegistry.SKREECHERS_CAN_SPAWN_WARDENS)) {
               Warden warden = (Warden)EntityType.f_217015_.m_20615_(this.m_9236_());
               warden.m_7678_(this.m_20185_(), spawnAt.m_123342_() + 1, this.m_20189_(), this.m_146908_(), 0.0F);
               warden.m_6518_(
                  (ServerLevel)this.m_9236_(), this.m_9236_().m_6436_(this.m_20183_()), MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null
               );
               warden.m_219459_(this);
               warden.m_219387_(this, 79, false);
               this.m_9236_().m_7967_(warden);
            }
         }

         this.clapTick++;
         if (!this.m_9236_().f_46443_) {
            if (this.m_5448_() != null
               && this.m_5448_().m_6084_()
               && this.m_142582_(this.m_5448_())
               && !this.m_5448_().m_21023_(MobEffects.f_19609_)
               && !this.m_21023_(MobEffects.f_19610_)) {
               double horizDist = this.m_5448_().m_20182_().m_82546_(this.m_20182_()).m_165924_();
               if (horizDist > 20.0) {
                  this.setClapping(false);
               }
            } else {
               this.setClapping(false);
            }
         }
      }

      if (!this.isClinging() && !this.hasGroundSize) {
         this.m_6210_();
         this.hasGroundSize = true;
      }

      if (this.isClinging() && this.hasGroundSize) {
         this.m_6210_();
         this.hasGroundSize = false;
      }
   }

   public boolean m_213854_() {
      return true;
   }

   public void angerAllNearbyWardens() {
      for (Warden warden : this.getNearbyWardens()) {
         if (warden.m_142582_(this)) {
            warden.m_219387_(this, 100, false);
         }
      }
   }

   private List<Warden> getNearbyWardens() {
      AABB angerBox = new AABB(
         this.m_20185_() - 35.0,
         this.m_20186_() + (this.isClinging() ? 5.0F : 25.0F),
         this.m_20189_() - 35.0,
         this.m_20185_() + 35.0,
         -64.0,
         this.m_20189_() + 35.0
      );
      return this.m_9236_().m_45976_(Warden.class, angerBox);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Clinging", this.isClinging());
      compound.m_128347_("CeilDist", this.getDistanceToCeiling());
      compound.m_128379_("SummonedWarden", this.hasAttemptedWardenSpawning);
      compound.m_128405_("ClingCooldown", this.clingCooldown);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setClinging(compound.m_128471_("Clinging"));
      this.setDistanceToCeiling((float)compound.m_128459_("CeilDist"));
      this.hasAttemptedWardenSpawning = compound.m_128471_("SummonedWarden");
      this.clingCooldown = compound.m_128451_("ClingCooldown");
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isClinging() ? super.m_6972_(poseIn) : GROUND_SIZE.m_20388_(this.m_6134_());
   }

   public boolean isClinging() {
      return (Boolean)this.f_19804_.m_135370_(CLINGING);
   }

   public void setClinging(boolean upsideDown) {
      this.f_19804_.m_135381_(CLINGING, upsideDown);
   }

   public boolean isClapping() {
      return (Boolean)this.f_19804_.m_135370_(CLAPPING);
   }

   public void setClapping(boolean clapping) {
      this.f_19804_.m_135381_(CLAPPING, clapping);
      if (!clapping) {
         this.clapTick = 0;
      }
   }

   public boolean isJumpingUp() {
      return (Boolean)this.f_19804_.m_135370_(JUMPING_UP);
   }

   public void setJumpingUp(boolean jumping) {
      this.f_19804_.m_135381_(JUMPING_UP, jumping);
   }

   protected BlockPos getPositionAbove(float height) {
      return AMBlockPos.fromCoords(this.m_20182_().f_82479_, this.m_20191_().f_82292_ + height + 0.5000001, this.m_20182_().f_82481_);
   }

   protected PathNavigation createScreecherNavigation(Level level) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
         public boolean m_6342_(BlockPos pos) {
            int airAbove;
            for (airAbove = 0; EntitySkreecher.this.m_9236_().m_8055_(pos).m_60795_() && airAbove < 6.0F; airAbove++) {
               pos = pos.m_7494_();
            }

            return airAbove < Math.min(4.0F, (float)EntitySkreecher.this.f_19796_.m_188503_(4));
         }
      };
      flyingpathnavigation.m_26440_(false);
      flyingpathnavigation.m_7008_(false);
      return flyingpathnavigation;
   }

   private float calculateDistanceToCeiling() {
      BlockPos ceiling = this.getCeilingOf(this.m_20183_());
      return (float)(ceiling.m_123342_() - this.m_20191_().f_82292_);
   }

   private boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.f_19794_) {
         return false;
      } else {
         double d = 0.3F;
         Vec3 vec3 = new Vec3(x, y, z);
         AABB axisAlignedBB = AABB.m_165882_(vec3, 0.3F, 1.0E-6, 0.3F);
         return this.m_9236_()
            .m_45556_(axisAlignedBB)
            .filter(Predicate.not(BlockStateBase::m_60795_))
            .anyMatch(
               p_185969_ -> {
                  BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                  return p_185969_.m_60828_(this.m_9236_(), blockpos)
                     && Shapes.m_83157_(
                        p_185969_.m_60812_(this.m_9236_(), blockpos).m_83216_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_),
                        Shapes.m_83064_(axisAlignedBB),
                        BooleanOp.f_82689_
                     );
               }
            );
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public float getDistanceToCeiling() {
      return (Float)this.f_19804_.m_135370_(DIST_TO_CEILING);
   }

   public void setDistanceToCeiling(float dist) {
      this.f_19804_.m_135381_(DIST_TO_CEILING, dist);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.isClinging() && !this.isInFluidType()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.75));
      } else {
         super.m_7023_(travelVector);
      }
   }

   public BlockPos getCeilingOf(BlockPos usPos) {
      while (!this.m_9236_().m_8055_(usPos).m_60783_(this.m_9236_(), usPos, Direction.DOWN) && usPos.m_123342_() < this.m_9236_().m_151558_()) {
         usPos = usPos.m_7494_();
      }

      return usPos;
   }

   private class FollowTargetGoal extends Goal {
      public FollowTargetGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         return EntitySkreecher.this.m_5448_() != null && EntitySkreecher.this.m_5448_().m_6084_() && EntitySkreecher.this.clingCooldown <= 0;
      }

      public void m_8056_() {
         EntitySkreecher.this.m_5496_((SoundEvent)AMSoundRegistry.SKREECHER_DETECT.get(), EntitySkreecher.this.m_6121_() * 6.0F, EntitySkreecher.this.m_6100_());
      }

      public void m_8037_() {
         LivingEntity target = EntitySkreecher.this.m_5448_();
         if (target != null) {
            if (EntitySkreecher.this.isClinging()) {
               BlockPos ceilAbove = EntitySkreecher.this.getCeilingOf(target.m_20183_().m_7494_());
               EntitySkreecher.this.m_21573_()
                  .m_26519_(target.m_20185_(), ceilAbove.m_123342_() - EntitySkreecher.this.f_19796_.m_188501_() * 4.0F, target.m_20189_(), 1.2F);
            } else {
               EntitySkreecher.this.m_21573_().m_26519_(target.m_20185_(), target.m_20186_(), target.m_20189_(), 1.0);
            }

            Vec3 vec = target.m_20182_().m_82546_(EntitySkreecher.this.m_20182_());
            EntitySkreecher.this.m_21563_().m_24960_(target, 360.0F, 180.0F);
            if (vec.m_165924_() < 2.5 && EntitySkreecher.this.clingCooldown == 0) {
               EntitySkreecher.this.setClapping(true);
            }
         }
      }
   }

   class MoveController extends MoveControl {
      private final Mob parentEntity = EntitySkreecher.this;

      public MoveController() {
         super(EntitySkreecher.this);
      }

      public void m_8126_() {
         if (this.f_24981_ == Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(
               this.f_24975_ - this.parentEntity.m_20185_(), this.f_24976_ - this.parentEntity.m_20186_(), this.f_24977_ - this.parentEntity.m_20189_()
            );
            double d0 = vector3d.m_82553_();
            double width = this.parentEntity.m_20191_().m_82309_();
            Vec3 vector3d1 = vector3d.m_82490_(this.f_24978_ * 0.035 / d0);
            float verticalSpeed = 0.15F;
            this.parentEntity.m_20256_(this.parentEntity.m_20184_().m_82549_(vector3d1.m_82542_(1.0, verticalSpeed, 1.0)));
            if (this.parentEntity.m_5448_() != null) {
               double d1 = this.parentEntity.m_5448_().m_20189_() - this.parentEntity.m_20189_();
               double d3 = this.parentEntity.m_5448_().m_20186_() - this.parentEntity.m_20186_();
               double d2 = this.parentEntity.m_5448_().m_20185_() - this.parentEntity.m_20185_();
               float f = Mth.m_14116_((float)(d2 * d2 + d1 * d1));
               this.parentEntity.m_146922_(-((float)Mth.m_14136_(d2, d1)) * (180.0F / (float)Math.PI));
               this.parentEntity.m_146926_((float)(Mth.m_14136_(d3, f) * 180.0F / (float)Math.PI));
               this.parentEntity.f_20883_ = this.parentEntity.m_146908_();
            } else if (d0 >= width) {
               this.parentEntity.m_146922_(-((float)Mth.m_14136_(vector3d1.f_82479_, vector3d1.f_82481_)) * (180.0F / (float)Math.PI));
            }
         }
      }
   }

   class WanderUpsideDownGoal extends RandomStrollGoal {
      private int stillTicks = 0;

      public WanderUpsideDownGoal() {
         super(EntitySkreecher.this, 1.0, 25);
      }

      @Nullable
      protected Vec3 m_7037_() {
         if (EntitySkreecher.this.isClinging()) {
            int distance = 16;
            int i = 0;
            if (i < 15) {
               Random rand = new Random();
               BlockPos randPos = EntitySkreecher.this.m_20183_().m_7918_(rand.nextInt(distance * 2) - distance, -4, rand.nextInt(distance * 2) - distance);
               BlockPos lowestPos = EntitySkreecher.this.getCeilingOf(randPos).m_6625_(rand.nextInt(4));
               return Vec3.m_82512_(lowestPos);
            } else {
               return null;
            }
         } else {
            return super.m_7037_();
         }
      }

      public boolean m_8036_() {
         return super.m_8036_();
      }

      public boolean m_8045_() {
         return super.m_8045_();
      }

      public void m_8041_() {
         super.m_8041_();
         this.f_25726_ = 0.0;
         this.f_25727_ = 0.0;
         this.f_25728_ = 0.0;
      }

      public void m_8056_() {
         this.stillTicks = 0;
         this.f_25725_.m_21573_().m_26519_(this.f_25726_, this.f_25727_, this.f_25728_, this.f_25729_);
      }
   }
}
