package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

public class EntitySkelewag extends Monster implements IAnimatedEntity {
   public static final Animation ANIMATION_STAB = Animation.create(10);
   public static final Animation ANIMATION_SLASH = Animation.create(25);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntitySkelewag.class, EntityDataSerializers.f_135028_);
   private int animationTick;
   private Animation currentAnimation;
   public float prevOnLandProgress;
   public float onLandProgress;

   protected EntitySkelewag(EntityType<? extends Monster> monster, Level level) {
      super(monster, level);
      this.f_21364_ = 10;
      this.f_21342_ = new AquaticMoveController(this, 1.0F, 15.0F);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn);
   }

   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.skelewagSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static boolean canSkelewagSpawn(
      EntityType<EntitySkelewag> type, ServerLevelAccessor levelAccessor, MobSpawnType p_32352_, BlockPos below, RandomSource random
   ) {
      return !levelAccessor.m_6425_(below.m_7495_()).m_205070_(FluidTags.f_13131_)
         ? false
         : levelAccessor.m_46791_() != Difficulty.PEACEFUL
            && m_219009_(levelAccessor, below, random)
            && (p_32352_ == MobSpawnType.SPAWNER || random.m_188503_(40) == 0 && levelAccessor.m_6425_(below).m_205070_(FluidTags.f_13131_));
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SKELEWAG_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SKELEWAG_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SKELEWAG_HURT.get();
   }

   public float m_5610_(BlockPos pos, LevelReader level) {
      return this.m_9236_().m_6425_(pos).m_205070_(FluidTags.f_13131_) ? 10.0F + level.m_220417_(pos) - 0.5F : super.m_5610_(pos, this.m_9236_());
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(2, new EntitySkelewag.AttackGoal(this));
      this.f_21345_.m_25352_(3, new AnimalAIRandomSwimming(this, 1.0, 12, 5));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[]{Drowned.class, EntitySkelewag.class}));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, Player.class, true));
      this.f_21346_.m_25352_(3, new EntityAINearestTarget3D(this, Dolphin.class, true));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(VARIANT, 0);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22281_, 3.0).m_22268_(Attributes.f_22279_, 0.45).m_22268_(Attributes.f_22276_, 20.0);
   }

   public int m_5792_() {
      return 1;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevOnLandProgress = this.onLandProgress;
      boolean onLand = !this.m_20072_() && this.m_20096_();
      if (onLand && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (!onLand && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      float targetXRot = 0.0F;
      if (this.m_20184_().m_82553_() > 0.09) {
         targetXRot = -((float)(Mth.m_14136_(this.m_20184_().f_82480_, this.m_20184_().m_165924_()) * 180.0F / (float)Math.PI));
      }

      if (targetXRot < this.m_146909_() - 5.0F) {
         targetXRot = this.m_146909_() - 5.0F;
      }

      if (targetXRot > this.m_146909_() + 5.0F) {
         targetXRot = this.m_146909_() + 5.0F;
      }

      this.m_146926_(targetXRot);
      if (!this.m_9236_().f_46443_ && this.m_5448_() != null && this.m_20270_(this.m_5448_()) < 2.0F + this.m_5448_().m_20205_()) {
         this.m_21391_(this.m_5448_(), 350.0F, 200.0F);
         if (this.getAnimation() == ANIMATION_STAB && this.getAnimationTick() == 7 && this.m_142582_(this.m_5448_())) {
            float f1 = this.m_146908_() * (float) (Math.PI / 180.0);
            this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f1) * 0.02F, 0.0, Mth.m_14089_(f1) * 0.02F));
            this.m_5448_().m_147240_(1.0, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
         }

         if (this.getAnimation() == ANIMATION_SLASH
            && this.getAnimationTick() % 5 == 0
            && this.getAnimationTick() > 0
            && this.getAnimationTick() < 25
            && this.m_142582_(this.m_5448_())) {
            for (LivingEntity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_5448_().m_20191_().m_82400_(2.0))) {
               if (!entity.m_20365_(this) && entity != this && !entity.m_7307_(this)) {
                  entity.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_() * 0.5F);
               }
            }
         }
      }

      if (this.onLandProgress >= 5.0F && this.m_20160_()) {
         this.m_20153_();
      }

      if (!this.m_20072_() && this.m_20096_() && this.f_19796_.m_188501_() < 0.2F) {
         this.m_20256_(this.m_20184_().m_82520_((this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F, 0.5, (this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F));
         this.m_146922_(this.f_19796_.m_188501_() * 360.0F);
         this.m_5496_((SoundEvent)AMSoundRegistry.SKELEWAG_HURT.get(), this.m_6121_(), this.m_6100_());
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int command) {
      this.f_19804_.m_135381_(VARIANT, command);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128405_("Variant", this.getVariant());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setVariant(compound.m_128451_("Variant"));
   }

   public boolean m_6063_() {
      return false;
   }

   public boolean m_6914_(LevelReader worldIn) {
      return worldIn.m_45784_(this);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20069_()) {
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

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (this.m_20363_(passenger)) {
         passenger.m_5618_(this.f_20883_);
         Vec3 vec = new Vec3(0.0, this.m_20206_() * 0.4F, this.m_20205_() * -0.2F)
            .m_82496_(-this.m_146909_() * (float) (Math.PI / 180.0))
            .m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0));
         passenger.m_6034_(this.m_20185_() + vec.f_82479_, this.m_20186_() + vec.f_82480_ + passenger.m_6049_(), this.m_20189_() + vec.f_82481_);
      }
   }

   public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
      return true;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setVariant(this.m_217043_().m_188501_() < 0.3F ? 1 : 0);
      if (this.f_19796_.m_188501_() < 0.2F) {
         Drowned drowned = (Drowned)EntityType.f_20562_.m_20615_(this.m_9236_());
         drowned.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
         drowned.m_20359_(this);
         drowned.m_20329_(this);
         worldIn.m_47205_(drowned);
      }

      if (reason == MobSpawnType.STRUCTURE) {
         this.m_21446_(this.m_20183_(), 15);
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public boolean m_6040_() {
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
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_SLASH, ANIMATION_STAB};
   }

   private class AttackGoal extends Goal {
      private final EntitySkelewag fish;
      private boolean isCharging = false;

      public AttackGoal(EntitySkelewag skelewag) {
         this.fish = skelewag;
      }

      public boolean m_8036_() {
         return this.fish.m_5448_() != null;
      }

      public void m_8037_() {
         LivingEntity target = this.fish.m_5448_();
         if (target != null) {
            double dist = this.fish.m_20270_(target);
            if (dist > 5.0) {
               this.isCharging = true;
            }

            this.fish.m_21573_().m_5624_(target, this.isCharging ? 1.3F : 0.8F);
            if (dist < 5.0F + target.m_20205_() / 2.0F) {
               this.fish
                  .setAnimation(
                     this.isCharging
                        ? EntitySkelewag.ANIMATION_STAB
                        : (EntitySkelewag.this.f_19796_.m_188499_() ? EntitySkelewag.ANIMATION_SLASH : EntitySkelewag.ANIMATION_STAB)
                  );
               this.isCharging = false;
            }
         }
      }

      public void m_8041_() {
         this.isCharging = false;
      }
   }
}
