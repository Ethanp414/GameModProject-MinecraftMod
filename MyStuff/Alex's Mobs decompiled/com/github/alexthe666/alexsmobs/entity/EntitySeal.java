package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SealAIBask;
import com.github.alexthe666.alexsmobs.entity.ai.SealAIDiveForItems;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class EntitySeal extends Animal implements ISemiAquatic, IHerdPanic, ITargetsDroppedItems {
   private static final EntityDataAccessor<Float> SWIM_ANGLE = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> BASKING = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> ARCTIC = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> BOB_TICKS = SynchedEntityData.m_135353_(EntitySeal.class, EntityDataSerializers.f_135028_);
   public float prevSwimAngle;
   public float prevBaskProgress;
   public float baskProgress;
   public float prevDigProgress;
   public float digProgress;
   public float prevBobbingProgress;
   public float bobbingProgress;
   public int revengeCooldown = 0;
   public UUID feederUUID = null;
   private int baskingTimer = 0;
   private int swimTimer = -1000;
   private int ticksSinceInWater = 0;
   private boolean isLandNavigator;
   public int fishFeedings = 0;

   protected EntitySeal(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SEAL_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SEAL_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SEAL_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22281_, 2.0).m_22268_(Attributes.f_22279_, 0.18F);
   }

   public static boolean canSealSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      Holder<Biome> holder = worldIn.m_204166_(pos);
      if (!holder.m_203565_(Biomes.f_48211_) && !holder.m_203565_(Biomes.f_48172_)) {
         boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.SEAL_SPAWNS);
         return spawnBlock && worldIn.m_45524_(pos, 0) > 8;
      } else {
         return worldIn.m_45524_(pos, 0) > 8 && worldIn.m_8055_(pos.m_7495_()).m_60713_(Blocks.f_50126_);
      }
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new SealAIBask(this));
      this.f_21345_.m_25352_(1, new BreathAirGoal(this));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(3, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(3, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(4, new AnimalAIHerdPanic(this, 1.6));
      this.f_21345_.m_25352_(5, new MeleeAttackGoal(this, 1.0, true));
      this.f_21345_.m_25352_(6, new SealAIDiveForItems(this));
      this.f_21345_.m_25352_(7, new RandomSwimmingGoal(this, 1.0, 7));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(9, new AvoidEntityGoal(this, EntityOrca.class, 20.0F, 1.3, 1.0));
      this.f_21345_
         .m_25352_(
            10,
            new TemptGoal(
               this, 1.1, Ingredient.m_43938_(Stream.of(new TagValue(AMTagRegistry.SEAL_BREEDABLES), new TagValue(AMTagRegistry.SEAL_OFFERINGS))), false
            )
         );
      this.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(this, EntityFlyingFish.class, 55, true, true, null));
      this.f_21346_.m_25352_(2, new CreatureAITargetItems(this, false));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new AquaticMoveController(this, 1.5F);
         this.f_21344_ = new SemiAquaticPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         double range = 15.0;
         int fleeTime = 100 + this.m_217043_().m_188503_(150);
         this.revengeCooldown = fleeTime;

         for (EntitySeal gaz : this.m_9236_().m_45976_(this.getClass(), this.m_20191_().m_82377_(15.0, 7.5, 15.0))) {
            gaz.revengeCooldown = fleeTime;
            gaz.setBasking(false);
         }

         this.setBasking(false);
      }

      return prev;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(SWIM_ANGLE, 0.0F);
      this.f_19804_.m_135372_(BASKING, false);
      this.f_19804_.m_135372_(DIGGING, false);
      this.f_19804_.m_135372_(ARCTIC, false);
      this.f_19804_.m_135372_(VARIANT, 0);
      this.f_19804_.m_135372_(BOB_TICKS, 0);
   }

   public boolean isTearsEasterEgg() {
      String s = ChatFormatting.m_126649_(this.m_7755_().getString());
      return s != null && s.toLowerCase().contains("he was");
   }

   public void m_267651_(boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 0.0, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * (this.m_20069_() ? 4.0F : 48.0F), 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   public float getSwimAngle() {
      return (Float)this.f_19804_.m_135370_(SWIM_ANGLE);
   }

   public void setSwimAngle(float progress) {
      this.f_19804_.m_135381_(SWIM_ANGLE, progress);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevBaskProgress = this.baskProgress;
      this.prevDigProgress = this.digProgress;
      this.prevBobbingProgress = this.bobbingProgress;
      this.prevSwimAngle = this.getSwimAngle();
      boolean dig = this.isDigging() && this.m_20072_();
      float f2 = (float)(-((float)this.m_20184_().f_82480_ * 180.0F / (float)Math.PI));
      if (this.m_20069_()) {
         this.m_146926_(f2 * 2.5F);
         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else if (!this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (this.isBasking()) {
         if (this.baskProgress < 5.0F) {
            this.baskProgress++;
         }
      } else if (this.baskProgress > 0.0F) {
         this.baskProgress--;
      }

      if (dig) {
         if (this.digProgress < 5.0F) {
            this.digProgress++;
         }
      } else if (this.digProgress > 0.0F) {
         this.digProgress--;
      }

      if (dig && this.m_9236_().m_8055_(this.m_20099_()).m_60815_()) {
         BlockPos posit = this.m_20099_();
         BlockState understate = this.m_9236_().m_8055_(posit);

         for (int i = 0; i < 4 + this.f_19796_.m_188503_(2); i++) {
            double particleX = posit.m_123341_() + this.f_19796_.m_188501_();
            double particleY = posit.m_123342_() + 1.0F;
            double particleZ = posit.m_123343_() + this.f_19796_.m_188501_();
            double motX = this.f_19796_.m_188583_() * 0.02;
            double motY = 0.1F + this.f_19796_.m_188501_() * 0.2F;
            double motZ = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_().m_7106_(new BlockParticleOption(ParticleTypes.f_123794_, understate), particleX, particleY, particleZ, motX, motY, motZ);
         }
      }

      if (!this.m_9236_().f_46443_) {
         if (this.isBasking()) {
            if (this.m_21188_() != null
               || this.m_27593_()
               || this.revengeCooldown > 0
               || this.m_20072_()
               || this.m_5448_() != null
               || this.baskingTimer > 1000 && this.m_217043_().m_188503_(100) == 0) {
               this.setBasking(false);
            }
         } else if (this.m_5448_() == null
            && !this.m_27593_()
            && this.m_21188_() == null
            && this.revengeCooldown == 0
            && !this.isBasking()
            && this.baskingTimer == 0
            && this.m_217043_().m_188503_(15) == 0
            && !this.m_20072_()) {
            this.setBasking(true);
         }

         if (this.revengeCooldown > 0) {
            this.revengeCooldown--;
         }

         if (this.revengeCooldown == 0 && this.m_21188_() != null) {
            this.m_6703_(null);
         }

         float threshold = 0.05F;
         if (this.m_20069_() && this.f_19859_ - this.m_146908_() > threshold) {
            this.setSwimAngle(this.getSwimAngle() + 2.0F);
         } else if (this.m_20069_() && this.f_19859_ - this.m_146908_() < -threshold) {
            this.setSwimAngle(this.getSwimAngle() - 2.0F);
         } else if (this.getSwimAngle() > 0.0F) {
            this.setSwimAngle(Math.max(this.getSwimAngle() - 10.0F, 0.0F));
         } else if (this.getSwimAngle() < 0.0F) {
            this.setSwimAngle(Math.min(this.getSwimAngle() + 10.0F, 0.0F));
         }

         this.setSwimAngle(Mth.m_14036_(this.getSwimAngle(), -70.0F, 70.0F));
         if (this.isBasking()) {
            this.baskingTimer++;
         } else {
            this.baskingTimer = 0;
         }

         if (this.m_20069_()) {
            this.swimTimer++;
            this.ticksSinceInWater = 0;
         } else {
            this.ticksSinceInWater++;
            this.swimTimer--;
         }
      }

      int bob = (Integer)this.f_19804_.m_135370_(BOB_TICKS);
      if (bob > 0) {
         bob--;
         if (this.bobbingProgress < 5.0F) {
            this.bobbingProgress++;
         }

         this.f_19804_.m_135381_(BOB_TICKS, bob);
      } else {
         if (this.bobbingProgress > 0.0F) {
            this.bobbingProgress--;
         }

         if (!this.m_9236_().f_46443_ && this.f_19796_.m_188503_(300) == 0 && !this.m_20069_() && this.revengeCooldown == 0) {
            bob = 20 + this.f_19796_.m_188503_(20);
            this.f_19804_.m_135381_(BOB_TICKS, bob);
         }
      }
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int variant) {
      this.f_19804_.m_135381_(VARIANT, variant);
   }

   public boolean isBasking() {
      return (Boolean)this.f_19804_.m_135370_(BASKING);
   }

   public void setBasking(boolean basking) {
      this.f_19804_.m_135381_(BASKING, basking);
   }

   public boolean isDigging() {
      return (Boolean)this.f_19804_.m_135370_(DIGGING);
   }

   public void setDigging(boolean digging) {
      this.f_19804_.m_135381_(DIGGING, digging);
   }

   public boolean isArctic() {
      return (Boolean)this.f_19804_.m_135370_(ARCTIC);
   }

   public void setArctic(boolean arctic) {
      this.f_19804_.m_135381_(ARCTIC, arctic);
   }

   public int m_6062_() {
      return 4800;
   }

   protected int m_7305_(int currentAir) {
      return this.m_6062_();
   }

   public int m_8132_() {
      return 1;
   }

   public int m_8085_() {
      return 1;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag dataTag
   ) {
      this.setArctic(this.isBiomeArctic(worldIn, this.m_20183_()));
      int i;
      if (data instanceof EntitySeal.SealGroupData) {
         i = ((EntitySeal.SealGroupData)data).variant;
      } else {
         i = this.f_19796_.m_188503_(2);
         data = new EntitySeal.SealGroupData(i);
      }

      this.setVariant(i);
      this.m_20301_(this.m_6062_());
      this.m_146926_(0.0F);
      return super.m_6518_(worldIn, difficultyIn, reason, data, dataTag);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Arctic", this.isArctic());
      compound.m_128379_("Basking", this.isBasking());
      compound.m_128405_("BaskingTimer", this.baskingTimer);
      compound.m_128405_("SwimTimer", this.swimTimer);
      compound.m_128405_("FishFeedings", this.fishFeedings);
      compound.m_128405_("Variant", this.getVariant());
      if (this.feederUUID != null) {
         compound.m_128362_("FeederUUID", this.feederUUID);
      }
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setArctic(compound.m_128471_("Arctic"));
      this.setBasking(compound.m_128471_("Basking"));
      this.baskingTimer = compound.m_128451_("BaskingTimer");
      this.swimTimer = compound.m_128451_("SwimTimer");
      this.fishFeedings = compound.m_128451_("FishFeedings");
      if (compound.m_128403_("FeederUUID")) {
         this.feederUUID = compound.m_128342_("FeederUUID");
      }

      this.setVariant(compound.m_128451_("Variant"));
   }

   private boolean isBiomeArctic(LevelAccessor worldIn, BlockPos position) {
      return worldIn.m_204166_(position).m_203656_(AMTagRegistry.SPAWNS_WHITE_SEALS);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20069_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.9));
         if (this.m_5448_() == null) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
         }

         if (this.isDigging()) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.02, 0.0));
         }
      } else {
         super.m_7023_(travelVector);
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SEAL_BREEDABLES);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntitySeal seal = (EntitySeal)((EntityType)AMEntityRegistry.SEAL.get()).m_20615_(serverWorld);
      seal.setArctic(this.isBiomeArctic(serverWorld, this.m_20183_()));
      return seal;
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.shouldLeaveWater() && this.swimTimer <= -1000;
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
      return this.isBasking();
   }

   @Override
   public int getWaterSearchRange() {
      return 32;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SEAL_OFFERINGS) || stack.m_204117_(AMTagRegistry.SEAL_BREEDABLES);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      if (e.m_32055_().m_204117_(AMTagRegistry.SEAL_OFFERINGS)) {
         this.fishFeedings++;
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11788_, this.m_6121_(), this.m_6100_());
         Entity itemThrower = e.m_19749_();
         if (this.fishFeedings >= 3) {
            if (itemThrower != null) {
               this.feederUUID = itemThrower.m_20148_();
            }

            this.fishFeedings = 0;
         }
      } else {
         this.feederUUID = null;
      }

      this.m_5634_(10.0F);
   }

   @Override
   public void onPanic() {
   }

   @Override
   public boolean canPanic() {
      return !this.isBasking();
   }

   public static class SealGroupData extends AgeableMobGroupData {
      public final int variant;

      SealGroupData(int variant) {
         super(true);
         this.variant = variant;
      }
   }
}
