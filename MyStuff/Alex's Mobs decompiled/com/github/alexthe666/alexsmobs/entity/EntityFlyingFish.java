package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SwimmerJumpPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityFlyingFish extends WaterAnimal implements FlyingAnimal, Bucketable {
   private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.m_135353_(EntityFlyingFish.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityFlyingFish.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityFlyingFish.class, EntityDataSerializers.f_135035_);
   public float prevOnLandProgress;
   public float onLandProgress;
   public float prevFlyProgress;
   public float flyProgress;
   private int glideIn = this.f_19796_.m_188503_(75) + 50;

   protected EntityFlyingFish(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.f_21342_ = new AquaticMoveController(this, 1.0F, 15.0F);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(2, new EntityFlyingFish.GlideGoal(this));
      this.f_21345_.m_25352_(3, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new AnimalAIRandomSwimming(this, 1.0, 12, 5));
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_11759_;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return SoundEvents.f_11761_;
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.m_27487_();
   }

   public boolean m_6785_(double p_27492_) {
      return !this.m_27487_() && !this.m_8077_();
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new SwimmerJumpPathNavigator(this, worldIn);
   }

   public int m_5792_() {
      return 8;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FROM_BUCKET, false);
      this.f_19804_.m_135372_(GLIDING, false);
      this.f_19804_.m_135372_(VARIANT, 0);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 6.0).m_22268_(Attributes.f_22279_, 0.3F);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.flyingFishSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev && source.m_7639_() != null) {
         double range = 15.0;
         this.glideIn = 0;

         for (EntityFlyingFish fsh : this.m_9236_().m_45976_(this.getClass(), this.m_20191_().m_82377_(range, range / 2.0, range))) {
            fsh.glideIn = 0;
         }
      }

      return prev;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevFlyProgress = this.flyProgress;
      boolean onLand = !this.m_20072_() && this.m_20096_();
      if (onLand && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (!onLand && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (this.isGliding()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }

         if (!this.m_20072_() && this.m_20184_().f_82480_ < 0.0) {
            this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.glideIn > 0) {
         this.glideIn--;
      }

      this.f_20883_ = this.m_146908_();
      float f2 = (float)(-((float)this.m_20184_().f_82480_ * 3.0F * 180.0F / (float)Math.PI));
      if (this.isGliding()) {
         f2 = -f2;
      }

      this.m_146926_(this.m_21376_(this.m_146909_(), f2, 9.0F));
      if (!this.m_20072_() && this.m_6084_() && this.m_20096_() && this.f_19796_.m_188501_() < 0.05F) {
         this.m_20256_(this.m_20184_().m_82520_((this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F, 0.5, (this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F));
         this.m_146922_(this.f_19796_.m_188501_() * 360.0F);
         this.m_5496_(SoundEvents.f_11760_, this.m_6121_(), this.m_6100_());
      }
   }

   protected float m_21376_(float current, float target, float maxChange) {
      float f = Mth.m_14177_(target - current);
      if (f > maxChange) {
         f = maxChange;
      }

      if (f < -maxChange) {
         f = -maxChange;
      }

      return current + f;
   }

   protected void m_6229_(int i) {
      if (this.m_6084_() && !this.m_20072_()) {
         this.m_20301_(i - 1);
         if (this.m_20146_() == -20) {
            this.m_20301_(0);
            this.m_6469_(this.m_269291_().m_269483_(), 2.0F);
         }
      } else {
         this.m_20301_(1000);
      }
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20069_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         float f = 0.6F;
         this.m_20256_(this.m_20184_().m_82542_(0.9, f, 0.9));
         if (this.m_5448_() == null) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
         }
      } else {
         super.m_7023_(travelVector);
      }
   }

   protected SoundEvent m_5501_() {
      return SoundEvents.f_11938_;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int variant) {
      this.f_19804_.m_135381_(VARIANT, variant);
   }

   public boolean isGliding() {
      return (Boolean)this.f_19804_.m_135370_(GLIDING);
   }

   public void setGliding(boolean flying) {
      this.f_19804_.m_135381_(GLIDING, flying);
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      Vec3 blockVec = Vec3.m_82512_(destinationBlock);
      BlockHitResult result = this.m_9236_().m_45547_(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.m_82425_().equals(destinationBlock);
   }

   public boolean m_29443_() {
      return true;
   }

   public boolean m_27487_() {
      return (Boolean)this.f_19804_.m_135370_(FROM_BUCKET);
   }

   public void m_27497_(boolean p_203706_1_) {
      this.f_19804_.m_135381_(FROM_BUCKET, p_203706_1_);
   }

   @Nonnull
   public SoundEvent m_142623_() {
      return SoundEvents.f_11782_;
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("FromBucket", this.m_27487_());
      compound.m_128405_("Variant", this.getVariant());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
      this.setVariant(compound.m_128451_("Variant"));
   }

   @Nonnull
   public ItemStack m_28282_() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.FLYING_FISH_BUCKET.get());
      if (this.m_8077_()) {
         stack.m_41714_(this.m_7770_());
      }

      return stack;
   }

   public void m_6872_(@Nonnull ItemStack bucket) {
      if (this.m_8077_()) {
         bucket.m_41714_(this.m_7770_());
      }

      Bucketable.m_148822_(this, bucket);
      CompoundTag compound = bucket.m_41784_();
      compound.m_128405_("Variant", this.getVariant());
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      Bucketable.m_148825_(this, compound);
      if (compound.m_128441_("Variant")) {
         this.setVariant(compound.m_128451_("Variant"));
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag
   ) {
      int i;
      if (data instanceof EntityFlyingFish.FlyingFishGroupData) {
         i = ((EntityFlyingFish.FlyingFishGroupData)data).variant;
      } else {
         i = this.f_19796_.m_188503_(3);
         data = new EntityFlyingFish.FlyingFishGroupData(i);
      }

      this.setVariant(i);
      return super.m_6518_(world, diff, spawnType, data, tag);
   }

   @Nonnull
   protected InteractionResult m_6071_(@Nonnull Player player, @Nonnull InteractionHand hand) {
      return Bucketable.m_148828_(player, hand, this).orElse(super.m_6071_(player, hand));
   }

   public static class FlyingFishGroupData extends AgeableMobGroupData {
      public final int variant;

      FlyingFishGroupData(int variant) {
         super(true);
         this.variant = variant;
      }
   }

   private class GlideGoal extends Goal {
      private final EntityFlyingFish fish;
      private final Level level;
      private BlockPos surface;
      private BlockPos glide;

      public GlideGoal(EntityFlyingFish fish) {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.fish = fish;
         this.level = fish.m_9236_();
      }

      public boolean m_8036_() {
         if (!this.fish.m_20072_()) {
            return false;
         } else {
            if (this.fish.glideIn == 0 || this.fish.m_217043_().m_188503_(80) == 0) {
               BlockPos found = this.findSurfacePos();
               if (found != null) {
                  BlockPos glideTo = this.findGlideToPos(this.fish.m_20183_(), found);
                  if (glideTo != null) {
                     this.surface = found;
                     this.glide = glideTo;
                     this.fish.glideIn = 0;
                     return true;
                  }
               }
            }

            return false;
         }
      }

      private BlockPos findSurfacePos() {
         BlockPos fishPos = this.fish.m_20183_();

         for (int i = 0; i < 15; i++) {
            BlockPos offset = fishPos.m_7918_(this.fish.f_19796_.m_188503_(16) - 8, 0, this.fish.f_19796_.m_188503_(16) - 8);

            while (this.level.m_46801_(offset) && offset.m_123342_() < this.level.m_151558_()) {
               offset = offset.m_7494_();
            }

            if (!this.level.m_46801_(offset) && this.level.m_46801_(offset.m_7495_()) && this.fish.canSeeBlock(offset)) {
               return offset;
            }
         }

         return null;
      }

      private BlockPos findGlideToPos(BlockPos fishPos, BlockPos surface) {
         Vec3 sub = Vec3.m_82528_(surface.m_121996_(fishPos)).m_82541_();

         for (double scale = EntityFlyingFish.this.f_19796_.m_188500_() * 8.0 + 1.0; scale > 2.0; scale--) {
            Vec3 scaled = sub.m_82490_(scale);
            BlockPos at = surface.m_7918_((int)scaled.f_82479_, 0, (int)scaled.f_82481_);
            if (!this.level.m_46801_(at) && this.level.m_46801_(at.m_7495_()) && this.fish.canSeeBlock(at)) {
               return at;
            }
         }

         return null;
      }

      public boolean m_8045_() {
         return this.surface != null && this.glide != null && (!this.fish.m_20096_() || this.fish.m_20072_());
      }

      public void m_8056_() {
      }

      public void m_8041_() {
         this.surface = null;
         this.glide = null;
         this.fish.glideIn = EntityFlyingFish.this.f_19796_.m_188503_(75) + 150;
         this.fish.setGliding(false);
      }

      public void m_8037_() {
         if (this.fish.m_20072_() && this.fish.m_20238_(Vec3.m_82512_(this.surface)) > 3.0) {
            this.fish.m_21573_().m_26519_(this.surface.m_123341_() + 0.5F, this.surface.m_123342_() + 1.0F, this.surface.m_123343_() + 0.5F, 1.2F);
            if (this.fish.isGliding()) {
               this.m_8041_();
            }
         } else {
            this.fish.m_21573_().m_26573_();
            Vec3 face = Vec3.m_82512_(this.glide).m_82546_(Vec3.m_82512_(this.surface));
            if (face.m_82553_() < 0.2F) {
               face = this.fish.m_20154_();
            }

            Vec3 target = face.m_82541_().m_82490_(0.1F);
            double y = 0.0;
            if (!this.fish.isGliding()) {
               y = 0.4F + EntityFlyingFish.this.f_19796_.m_188501_() * 0.2F;
            } else if (this.fish.isGliding() && this.fish.m_20072_()) {
               this.m_8041_();
            }

            Vec3 move = this.fish.m_20184_().m_82520_(target.f_82479_, y, target.f_82480_);
            this.fish.m_20256_(move);
            double d0 = move.m_165924_();
            this.fish.m_146926_((float)(-Mth.m_14136_(move.f_82480_, d0) * 180.0F / (float)Math.PI));
            this.fish.m_146922_((float)Mth.m_14136_(move.f_82481_, move.f_82479_) * (180.0F / (float)Math.PI) - 90.0F);
            this.fish.setGliding(true);
         }
      }
   }
}
