package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityBlobfish extends WaterAnimal implements FlyingAnimal, Bucketable {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityBlobfish.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> BLOBFISH_SCALE = SynchedEntityData.m_135353_(EntityBlobfish.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> DEPRESSURIZED = SynchedEntityData.m_135353_(EntityBlobfish.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SLIMED = SynchedEntityData.m_135353_(EntityBlobfish.class, EntityDataSerializers.f_135035_);
   public float squishFactor;
   public float prevSquishFactor;
   public float squishAmount;
   private boolean wasOnGround;

   protected EntityBlobfish(EntityType type, Level world) {
      super(type, world);
      this.f_21342_ = new AquaticMoveController(this, 1.0F);
   }

   public static Builder createAttributes() {
      return Mob.m_21552_().m_22268_(Attributes.f_22276_, 3.0);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.blobfishSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 8.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected void m_6229_(int p_209207_1_) {
      if (this.m_6084_() && !this.m_20072_() && !this.isSlimed()) {
         this.m_20301_(p_209207_1_ - 1);
         if (this.m_20146_() == -20) {
            this.m_20301_(0);
            this.m_6469_(this.m_269291_().m_269063_(), this.f_19796_.m_188503_(2) == 0 ? 1.0F : 0.0F);
         }
      } else {
         this.m_20301_(2000);
      }
   }

   protected float m_6431_(Pose p_213348_1_, EntityDimensions p_213348_2_) {
      return p_213348_2_.f_20378_ * 0.65F;
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.m_8077_() || this.m_27487_() || this.isSlimed();
   }

   public boolean m_6785_(double p_213397_1_) {
      return !this.m_27487_() && !this.m_8077_();
   }

   public int m_5792_() {
      return 4;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FROM_BUCKET, false);
      this.f_19804_.m_135372_(BLOBFISH_SCALE, 1.0F);
      this.f_19804_.m_135372_(DEPRESSURIZED, false);
      this.f_19804_.m_135372_(SLIMED, false);
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return super.m_6972_(poseIn).m_20388_(this.getBlobfishScale());
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
      compound.m_128379_("Depressurized", this.isDepressurized());
      compound.m_128379_("Slimed", this.isSlimed());
      compound.m_128350_("BlobfishScale", this.getBlobfishScale());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
      this.setDepressurized(compound.m_128471_("Depressurized"));
      this.setSlimed(compound.m_128471_("Slimed"));
      this.setBlobfishScale(compound.m_128457_("BlobfishScale"));
   }

   private boolean hasClearance() {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int l1 = 0; l1 < 10; l1++) {
         BlockState blockstate = this.m_9236_().m_8055_(blockpos$mutable.m_122169_(this.m_20185_(), this.m_20186_() + l1, this.m_20189_()));
         if (!blockstate.m_60819_().m_205070_(FluidTags.f_13131_) && !blockstate.m_280296_()) {
            return false;
         }
      }

      return true;
   }

   public float getBlobfishScale() {
      return (Float)this.f_19804_.m_135370_(BLOBFISH_SCALE);
   }

   public void setBlobfishScale(float scale) {
      this.f_19804_.m_135381_(BLOBFISH_SCALE, scale);
   }

   public boolean isDepressurized() {
      return (Boolean)this.f_19804_.m_135370_(DEPRESSURIZED);
   }

   public void setDepressurized(boolean depressurized) {
      this.f_19804_.m_135381_(DEPRESSURIZED, depressurized);
   }

   public boolean isSlimed() {
      return (Boolean)this.f_19804_.m_135370_(SLIMED);
   }

   public void setSlimed(boolean slimed) {
      this.f_19804_.m_135381_(SLIMED, slimed);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(2, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(3, new AnimalAISwimBottom(this, 1.0, 7));
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

   @Nonnull
   protected InteractionResult m_6071_(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack lvt_3_1_ = player.m_21120_(hand);
      if (lvt_3_1_.m_41720_() == Items.f_42518_ && this.m_6084_() && !this.isSlimed()) {
         this.setSlimed(true);

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
         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      } else {
         return Bucketable.m_148828_(player, hand, this).orElse(super.m_6071_(player, hand));
      }
   }

   protected SoundEvent m_5501_() {
      return SoundEvents.f_11938_;
   }

   protected void m_7355_(BlockPos p_180429_1_, BlockState p_180429_2_) {
   }

   @Nonnull
   public ItemStack m_28282_() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.BLOBFISH_BUCKET.get());
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
      compound.m_128350_("BucketScale", this.getBlobfishScale());
      compound.m_128379_("Slimed", this.isSlimed());
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      Bucketable.m_148825_(this, compound);
      if (compound.m_128441_("BucketScale")) {
         this.setBlobfishScale(compound.m_128457_("BucketScale"));
      }

      if (compound.m_128441_("Slimed")) {
         this.setSlimed(compound.m_128471_("Slimed"));
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setBlobfishScale(0.75F + this.f_19796_.m_188501_() * 0.5F);
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevSquishFactor = this.squishFactor;
      this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
      float f2 = -((float)this.m_20184_().f_82480_ * 2.2F * (180.0F / (float)Math.PI));
      this.m_146926_(f2);
      if (!this.m_20069_()) {
         if (this.m_20096_()) {
            if (!this.wasOnGround) {
               this.squishAmount = -0.35F;
            }
         } else if (this.wasOnGround) {
            this.squishAmount = 2.0F;
         }
      }

      this.wasOnGround = this.m_20096_();
      this.alterSquishAmount();
      boolean clear = this.hasClearance();
      if (clear) {
         if (this.isDepressurized()) {
            this.setDepressurized(false);
         }
      } else if (!this.isDepressurized()) {
         this.setDepressurized(true);
      }
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_11759_;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return SoundEvents.f_11761_;
   }

   public static boolean canBlobfishSpawn(
      EntityType<EntityBlobfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER
         || pos.m_123342_() <= AMConfig.blobfishSpawnHeight
            && iServerWorld.m_6425_(pos).m_205070_(FluidTags.f_13131_)
            && iServerWorld.m_6425_(pos.m_7494_()).m_205070_(FluidTags.f_13131_);
   }

   public boolean m_29443_() {
      return false;
   }
}
