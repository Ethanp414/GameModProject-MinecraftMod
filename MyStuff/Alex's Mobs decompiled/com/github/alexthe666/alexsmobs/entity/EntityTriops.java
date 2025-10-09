package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTriops extends WaterAnimal implements ITargetsDroppedItems, Bucketable {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityTriops.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> TRIOPS_SCALE = SynchedEntityData.m_135353_(EntityTriops.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> BABY_AGE = SynchedEntityData.m_135353_(EntityTriops.class, EntityDataSerializers.f_135028_);
   public float prevOnLandProgress;
   public float onLandProgress;
   public float prevSwimRot;
   public float swimRot;
   public boolean fedCarrot = false;
   public int breedCooldown = 0;
   public float tail1Yaw;
   public float prevTail1Yaw;
   public float tail2Yaw;
   public float prevTail2Yaw;
   public float moveDistance;
   private EntityTriops breedWith;
   private boolean pregnant;

   public EntityTriops(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.f_21342_ = new AquaticMoveController(this, 1.0F, 15.0F);
      this.tail1Yaw = this.m_146908_();
      this.prevTail1Yaw = this.m_146908_();
      this.tail2Yaw = this.m_146908_();
      this.prevTail2Yaw = this.m_146908_();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FROM_BUCKET, false);
      this.f_19804_.m_135372_(TRIOPS_SCALE, 1.0F);
      this.f_19804_.m_135372_(BABY_AGE, 0);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new EntityTriops.BreedGoal());
      this.f_21345_.m_25352_(1, new EntityTriops.LayEggGoal());
      this.f_21345_.m_25352_(2, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(3, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new AnimalAISwimBottom(this, 1.0, 7));
      this.f_21346_.m_25352_(1, new CreatureAITargetItems(this, false, 10));
   }

   public int m_5792_() {
      return 5;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20072_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82542_(0.9, 0.8, 0.9));
         if (this.m_5448_() == null) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
         }

         this.moveDistance = (float)(this.moveDistance + travelVector.m_165924_());
      } else {
         super.m_7023_(travelVector);
      }
   }

   protected void m_5625_(float f) {
      if (this.f_19796_.m_188503_(2) == 0) {
         this.m_5496_(this.m_5501_(), 0.2F, 1.3F + (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.4F);
      }
   }

   protected SoundEvent m_5501_() {
      return SoundEvents.f_11938_;
   }

   public boolean m_27487_() {
      return (Boolean)this.f_19804_.m_135370_(FROM_BUCKET);
   }

   public void m_27497_(boolean sit) {
      this.f_19804_.m_135381_(FROM_BUCKET, sit);
   }

   @Nonnull
   public SoundEvent m_142623_() {
      return SoundEvents.f_11782_;
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.m_27487_() || this.m_6162_() || this.fedCarrot;
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return !this.m_6162_() && !this.m_27487_() && !this.fedCarrot;
   }

   protected void m_6229_(int i) {
      if (this.m_6084_() && !this.m_20072_()) {
         this.m_20301_(i - 1);
         if (this.m_20146_() == -20) {
            this.m_20301_(0);
            this.m_6469_(this.m_269291_().m_269483_(), this.f_19796_.m_188503_(2) == 0 ? 1.0F : 0.0F);
         }
      } else {
         this.m_20301_(2000);
      }
   }

   public int getBabyAge() {
      return (Integer)this.f_19804_.m_135370_(BABY_AGE);
   }

   public void setBabyAge(int babyAge) {
      this.f_19804_.m_135381_(BABY_AGE, babyAge);
   }

   public float getTriopsScale() {
      return (Float)this.f_19804_.m_135370_(TRIOPS_SCALE);
   }

   public void setTriopsScale(float scale) {
      this.f_19804_.m_135381_(TRIOPS_SCALE, scale);
   }

   public boolean m_6162_() {
      return this.getBabyAge() < 0;
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("FromBucket", this.m_27487_());
      compound.m_128379_("FedCarrot", this.fedCarrot);
      compound.m_128379_("Pregnant", this.pregnant);
      compound.m_128405_("BreedCooldown", this.breedCooldown);
      compound.m_128350_("TriopsScale", this.getTriopsScale());
      compound.m_128405_("BabyAge", this.getBabyAge());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
      this.fedCarrot = compound.m_128471_("FedCarrot");
      this.pregnant = compound.m_128471_("Pregnant");
      this.breedCooldown = compound.m_128451_("BreedCooldown");
      this.setTriopsScale(compound.m_128457_("TriopsScale"));
      this.setBabyAge(compound.m_128451_("BabyAge"));
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 4.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setTriopsScale(0.9F + this.f_19796_.m_188501_() * 0.2F);
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevOnLandProgress = this.onLandProgress;
      this.prevSwimRot = this.swimRot;
      this.prevTail1Yaw = this.tail1Yaw;
      this.prevTail2Yaw = this.tail2Yaw;
      boolean onLand = !this.m_20072_() && this.m_20096_();
      this.m_146926_(-((float)this.m_20184_().f_82480_ * 2.2F * (180.0F / (float)Math.PI)));
      if (onLand && this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (!onLand && this.onLandProgress > 0.0F) {
         this.onLandProgress--;
      }

      if (this.breedCooldown > 0) {
         this.breedCooldown--;
      }

      this.tail1Yaw = Mth.m_14148_(this.tail1Yaw, this.f_20883_, 7.0F);
      this.tail2Yaw = Mth.m_14148_(this.tail2Yaw, this.tail1Yaw, 7.0F);
      if (this.onLandProgress == 0.0F) {
         float f = (float)(20.0 * Math.sin(this.f_267362_.m_267756_()) * this.f_267362_.m_267731_());
         this.swimRot = Mth.m_14148_(this.swimRot, f, 2.0F);
      }
   }

   public void m_267651_(boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, this.m_20186_() - this.f_19855_, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 6.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   public MobType m_6336_() {
      return MobType.f_21642_;
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 67) {
         for (int i = 0; i < 5; i++) {
            this.m_9236_().m_7106_(ParticleTypes.f_123748_, this.m_20208_(0.5), this.m_20227_(0.8F), this.m_20262_(0.5), 0.0, 0.0, 0.0);
         }
      } else if (id == 68) {
         this.m_9236_().m_7106_(ParticleTypes.f_123750_, this.m_20185_(), this.m_20227_(0.8F), this.m_20189_(), 0.0, 0.0, 0.0);
      } else {
         super.m_7822_(id);
      }
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return (stack.m_204117_(AMTagRegistry.TRIOPS_BREEDABLES) || stack.m_150930_((Item)AMItemRegistry.MOSQUITO_LARVA.get())) && !this.fedCarrot;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack stack = e.m_32055_();
      if (stack.m_41720_().m_41472_() && stack.m_41720_().m_41473_() != null) {
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11788_, this.m_6100_(), this.m_6121_());
         this.m_5634_(5.0F);
         if (!this.m_9236_().f_46443_ && this.breedCooldown == 0 && !this.fedCarrot) {
            this.fedCarrot = true;
            this.m_9236_().m_7605_(this, (byte)67);
         }
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (!type.m_19077_() && this.canTargetItem(itemstack) && !this.fedCarrot) {
         if (!player.m_150110_().f_35937_) {
            itemstack.m_41774_(1);
         }

         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11788_, this.m_6100_(), this.m_6121_());
         this.m_5634_(5.0F);
         if (itemstack.m_204117_(AMTagRegistry.TRIOPS_BREEDABLES)) {
            if (!this.m_9236_().f_46443_ && this.breedCooldown == 0) {
               this.m_9236_().m_7605_(this, (byte)67);
            }

            this.fedCarrot = true;
         }

         return InteractionResult.SUCCESS;
      } else {
         return Bucketable.m_148828_(player, hand, this).orElse(type);
      }
   }

   public boolean isSearchingForMate() {
      return this.m_6084_() && this.m_20072_() && this.fedCarrot && this.breedCooldown <= 0;
   }

   public void m_6872_(@Nonnull ItemStack bucket) {
      if (this.m_8077_()) {
         bucket.m_41714_(this.m_7770_());
      }

      CompoundTag platTag = new CompoundTag();
      this.m_7380_(platTag);
      CompoundTag compound = bucket.m_41784_();
      compound.m_128365_("TriopsTag", platTag);
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      if (compound.m_128441_("TriopsTag")) {
         this.m_7378_(compound.m_128469_("TriopsTag"));
      }

      this.m_20301_(2000);
   }

   public ItemStack m_28282_() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.TRIOPS_BUCKET.get());
      if (this.m_8077_()) {
         stack.m_41714_(this.m_7770_());
      }

      return stack;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.TRIOPS_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.TRIOPS_HURT.get();
   }

   private class BreedGoal extends Goal {
      private final Predicate<Entity> validBreedPartner;
      private EntityTriops breedPartner;
      private int executionCooldown = 50;

      public BreedGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.validBreedPartner = shrimp -> shrimp instanceof EntityTriops otherFish
            && otherFish.m_19879_() != EntityTriops.this.m_19879_()
            && otherFish.isSearchingForMate();
      }

      public boolean m_8036_() {
         if (EntityTriops.this.m_20072_() && EntityTriops.this.fedCarrot && EntityTriops.this.breedCooldown <= 0 && EntityTriops.this.breedWith == null) {
            if (this.executionCooldown > 0) {
               this.executionCooldown--;
            } else {
               this.executionCooldown = 50 + EntityTriops.this.f_19796_.m_188503_(50);
               List<EntityTriops> list = EntityTriops.this.m_9236_()
                  .m_6443_(EntityTriops.class, EntityTriops.this.m_20191_().m_82377_(10.0, 8.0, 10.0), EntitySelector.f_20408_.and(this.validBreedPartner));
               list.sort(Comparator.comparingDouble(EntityTriops.this::m_20280_));
               if (!list.isEmpty()) {
                  EntityTriops closestPupfish = list.get(0);
                  if (closestPupfish != null) {
                     this.breedPartner = closestPupfish;
                     this.breedPartner.breedWith = EntityTriops.this;
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.breedPartner != null
            && !EntityTriops.this.pregnant
            && !this.breedPartner.pregnant
            && EntityTriops.this.breedWith == null
            && this.breedPartner.isSearchingForMate()
            && EntityTriops.this.isSearchingForMate();
      }

      public void m_8056_() {
      }

      public void m_8041_() {
         EntityTriops.this.fedCarrot = false;
         EntityTriops.this.breedCooldown = 1200 + EntityTriops.this.f_19796_.m_188503_(3600);
      }

      public void m_8037_() {
         EntityTriops.this.m_21573_().m_5624_(this.breedPartner, 1.0);
         this.breedPartner.m_21573_().m_5624_(EntityTriops.this, 1.0);
         if (EntityTriops.this.m_20270_(this.breedPartner) < 1.2F) {
            EntityTriops.this.m_9236_().m_7605_(EntityTriops.this, (byte)68);
            EntityTriops.this.pregnant = true;
         }
      }
   }

   class LayEggGoal extends Goal {
      private BlockPos eggPos;

      LayEggGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public void m_8041_() {
         this.eggPos = null;
      }

      public boolean m_8036_() {
         if (EntityTriops.this.pregnant && EntityTriops.this.m_217043_().m_188503_(30) == 0) {
            BlockPos egg = this.getEggLayPos();
            if (egg != null) {
               this.eggPos = egg;
               return true;
            }
         }

         return false;
      }

      public boolean m_8045_() {
         return this.eggPos != null && EntityTriops.this.pregnant && EntityTriops.this.m_9236_().m_8055_(this.eggPos).m_60795_();
      }

      public boolean isValidPos(BlockPos pos) {
         BlockState state = EntityTriops.this.m_9236_().m_8055_(pos);
         FluidState stateBelow = EntityTriops.this.m_9236_().m_6425_(pos.m_7495_());
         return stateBelow.m_205070_(FluidTags.f_13131_) && state.m_60795_();
      }

      public BlockPos getEggLayPos() {
         for (int i = 0; i < 10; i++) {
            BlockPos offset = EntityTriops.this.m_20183_()
               .m_7918_(EntityTriops.this.m_217043_().m_188503_(10) - 5, 10, EntityTriops.this.m_217043_().m_188503_(10) - 5);

            while (EntityTriops.this.m_9236_().m_8055_(offset.m_7495_()).m_60795_() && offset.m_123342_() > EntityTriops.this.m_9236_().m_141937_()) {
               offset = offset.m_7495_();
            }

            if (this.isValidPos(offset)) {
               return offset;
            }
         }

         return null;
      }

      public void m_8037_() {
         super.m_8037_();
         EntityTriops.this.m_21573_().m_26519_(this.eggPos.m_123341_(), this.eggPos.m_123342_(), this.eggPos.m_123343_(), 1.0);
         if (EntityTriops.this.m_20238_(Vec3.m_82539_(this.eggPos)) < 2.0) {
            EntityTriops.this.pregnant = false;
            EntityTriops.this.m_9236_().m_46597_(this.eggPos, ((Block)AMBlockRegistry.TRIOPS_EGGS.get()).m_49966_());
         }
      }
   }
}
