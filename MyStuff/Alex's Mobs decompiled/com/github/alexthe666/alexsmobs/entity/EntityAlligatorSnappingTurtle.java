package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;

public class EntityAlligatorSnappingTurtle extends Animal implements ISemiAquatic, Shearable, IForgeShearable {
   public static final Predicate<LivingEntity> TARGET_PRED = animal -> !(animal instanceof EntityAlligatorSnappingTurtle)
      && !(animal instanceof ArmorStand)
      && EntitySelector.f_20406_.test(animal)
      && animal.m_6084_();
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.m_135353_(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Integer> MOSS = SynchedEntityData.m_135353_(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> WAITING = SynchedEntityData.m_135353_(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> ATTACK_TARGET_FLAG = SynchedEntityData.m_135353_(
      EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Boolean> LUNGE_FLAG = SynchedEntityData.m_135353_(
      EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Float> TURTLE_SCALE = SynchedEntityData.m_135353_(
      EntityAlligatorSnappingTurtle.class, EntityDataSerializers.f_135029_
   );
   public float openMouthProgress;
   public float prevOpenMouthProgress;
   public float attackProgress;
   public float prevAttackProgress;
   public int chaseTime = 0;
   private int biteTick = 0;
   private int waitTime = 0;
   private int timeUntilWait = 0;
   private int mossTime = 0;

   protected EntityAlligatorSnappingTurtle(EntityType<? extends Animal> type, Level worldIn) {
      super(type, worldIn);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.m_274367_(1.0F);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.ALLIGATOR_SNAPPING_TURTLE_HURT.get();
   }

   public static boolean canTurtleSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_SPAWNS);
      return spawnBlock && pos.m_123342_() < worldIn.m_5736_() + 4;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.alligatorSnappingTurtleSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 18.0)
         .m_22268_(Attributes.f_22278_, 0.7)
         .m_22268_(Attributes.f_22284_, 8.0)
         .m_22268_(Attributes.f_22277_, 16.0)
         .m_22268_(Attributes.f_22281_, 4.0)
         .m_22268_(Attributes.f_22279_, 0.2F);
   }

   public float m_6134_() {
      return this.m_6162_() ? 0.3F : 1.0F;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new MeleeAttackGoal(this, 1.3, false));
      this.f_21345_.m_25352_(2, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(2, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(3, new BottomFeederAIWander(this, 1.0, 120, 150, 10));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(5, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this) {
         public boolean m_8045_() {
            return EntityAlligatorSnappingTurtle.this.chaseTime >= 0 && super.m_8045_();
         }
      });
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, LivingEntity.class, 2, false, true, TARGET_PRED) {
         @Override
         protected AABB m_7255_(double targetDistance) {
            return this.f_26135_.m_20191_().m_82377_(0.5, 2.0, 0.5);
         }
      });
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.ALLIGATOR_SNAPPING_TURTLE_BREEDABLES);
   }

   public boolean m_6147_() {
      return this.isBesideClimbableBlock();
   }

   public boolean m_7327_(Entity entityIn) {
      return true;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(CLIMBING, (byte)0);
      this.f_19804_.m_135372_(MOSS, 0);
      this.f_19804_.m_135372_(TURTLE_SCALE, 1.0F);
      this.f_19804_.m_135372_(WAITING, false);
      this.f_19804_.m_135372_(ATTACK_TARGET_FLAG, false);
      this.f_19804_.m_135372_(LUNGE_FLAG, false);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevOpenMouthProgress = this.openMouthProgress;
      this.prevAttackProgress = this.attackProgress;
      boolean attack = (Boolean)this.f_19804_.m_135370_(LUNGE_FLAG);
      boolean open = this.isWaiting() || (Boolean)this.f_19804_.m_135370_(ATTACK_TARGET_FLAG) && !attack;
      if (attack) {
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }

      if (open) {
         if (this.openMouthProgress < 5.0F) {
            this.openMouthProgress++;
         }
      } else if (this.openMouthProgress > 0.0F) {
         this.openMouthProgress--;
      }

      if (this.attackProgress == 4.0F && this.m_5448_() != null && this.m_6084_() && this.m_142582_(this.m_5448_()) && this.m_20270_(this.m_5448_()) < 2.3F) {
         float dmg = this.m_6162_() ? 1.0F : (float)this.m_21051_(Attributes.f_22281_).m_22115_();
         this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), dmg);
      }

      if (this.attackProgress > 4.0F) {
         this.biteTick = 5;
      }

      if (this.biteTick > 0) {
         this.biteTick--;
      }

      if (this.chaseTime < 0) {
         this.chaseTime++;
      }

      if (!this.m_9236_().f_46443_) {
         this.setBesideClimbableBlock(this.f_19862_ && this.m_20069_());
         if (this.isWaiting()) {
            this.waitTime++;
            this.timeUntilWait = 1500;
            if (this.waitTime > 1500 || this.m_5448_() != null) {
               this.setWaiting(false);
            }
         } else {
            this.timeUntilWait--;
            this.waitTime = 0;
         }

         if ((this.m_5448_() == null || !this.m_5448_().m_6084_()) && this.timeUntilWait <= 0 && this.m_20069_()) {
            this.setWaiting(true);
         }

         if (this.m_5448_() != null && this.biteTick == 0) {
            this.setWaiting(false);
            this.chaseTime++;
            this.f_19804_.m_135381_(ATTACK_TARGET_FLAG, true);
            this.m_21391_(this.m_5448_(), 360.0F, 40.0F);
            this.f_20883_ = this.m_146908_();
            if (this.openMouthProgress > 4.0F && this.m_142582_(this.m_5448_()) && this.m_20270_(this.m_5448_()) < 2.3F) {
               this.f_19804_.m_135381_(LUNGE_FLAG, true);
            }

            if (this.chaseTime > 40 && this.m_20270_(this.m_5448_()) > (this.m_5448_() instanceof Player ? 5 : 10)) {
               this.chaseTime = -50;
               this.m_6710_(null);
               this.m_6703_(null);
               this.m_21335_(null);
               this.f_20888_ = null;
            }
         } else {
            this.f_19804_.m_135381_(ATTACK_TARGET_FLAG, false);
            this.f_19804_.m_135381_(LUNGE_FLAG, false);
         }

         this.mossTime++;
         if (this.m_20069_() && this.mossTime > 12000) {
            this.mossTime = 0;
            this.setMoss(Math.min(10, this.getMoss() + 1));
         }
      }
   }

   @Nullable
   public LivingEntity m_5448_() {
      return this.chaseTime < 0 ? null : super.m_5448_();
   }

   public void m_6710_(@Nullable LivingEntity entitylivingbaseIn) {
      if (this.chaseTime >= 0) {
         super.m_6710_(entitylivingbaseIn);
      } else {
         super.m_6710_(null);
      }
   }

   @Nullable
   public LivingEntity m_21188_() {
      return this.chaseTime < 0 ? null : super.m_21188_();
   }

   public void m_6703_(@Nullable LivingEntity entitylivingbaseIn) {
      if (this.chaseTime >= 0) {
         super.m_6703_(entitylivingbaseIn);
      } else {
         super.m_6703_(null);
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setMoss(this.f_19796_.m_188503_(6));
      this.setTurtleScale(0.8F + this.f_19796_.m_188501_() * 0.2F);
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public float getTurtleScale() {
      return (Float)this.f_19804_.m_135370_(TURTLE_SCALE);
   }

   public void setTurtleScale(float scale) {
      this.f_19804_.m_135381_(TURTLE_SCALE, scale);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn) {
         @Override
         public boolean m_6342_(BlockPos pos) {
            return this.f_26495_.m_8055_(pos).m_60819_().m_76178_();
         }
      };
   }

   public boolean isWaiting() {
      return (Boolean)this.f_19804_.m_135370_(WAITING);
   }

   public void setWaiting(boolean sit) {
      this.f_19804_.m_135381_(WAITING, sit);
   }

   public int getMoss() {
      return (Integer)this.f_19804_.m_135370_(MOSS);
   }

   public void setMoss(int moss) {
      this.f_19804_.m_135381_(MOSS, moss);
   }

   protected void updateAir(int air) {
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Waiting", this.isWaiting());
      compound.m_128405_("MossLevel", this.getMoss());
      compound.m_128350_("TurtleScale", this.getTurtleScale());
      compound.m_128405_("MossTime", this.mossTime);
      compound.m_128405_("WaitTime", this.waitTime);
      compound.m_128405_("WaitTime2", this.timeUntilWait);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setWaiting(compound.m_128471_("Waiting"));
      this.setMoss(compound.m_128451_("MossLevel"));
      this.setTurtleScale(compound.m_128457_("TurtleScale"));
      this.mossTime = compound.m_128451_("MossTime");
      this.waitTime = compound.m_128451_("WaitTime");
      this.timeUntilWait = compound.m_128451_("WaitTime2");
   }

   @Override
   public boolean shouldEnterWater() {
      return true;
   }

   @Override
   public boolean shouldLeaveWater() {
      return false;
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isWaiting();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   public boolean m_6040_() {
      return true;
   }

   public float m_5610_(BlockPos pos, LevelReader worldIn) {
      return worldIn.m_6425_(pos.m_7495_()).m_76178_() && worldIn.m_6425_(pos).m_205070_(FluidTags.f_13131_) ? 10.0F : super.m_5610_(pos, worldIn);
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.f_19804_.m_135370_(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = (Byte)this.f_19804_.m_135370_(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.f_19804_.m_135381_(CLIMBING, b0);
   }

   public boolean m_6914_(LevelReader worldIn) {
      return worldIn.m_45784_(this);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20069_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         if (this.f_20899_) {
            this.m_20256_(this.m_20184_().m_82490_(1.0));
            this.m_20256_(this.m_20184_().m_82520_(0.0, 0.72, 0.0));
         } else {
            this.m_20256_(this.m_20184_().m_82490_(0.4));
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.08, 0.0));
         }
      } else {
         super.m_7023_(travelVector);
      }
   }

   public boolean m_6220_() {
      return this.m_6084_() && this.getMoss() > 0;
   }

   public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos) {
      return this.m_6220_();
   }

   public void m_5851_(SoundSource category) {
      this.m_9236_().m_6269_(null, this, SoundEvents.f_12344_, category, 1.0F, 1.0F);
      this.m_146850_(GameEvent.f_223708_);
      if (!this.m_9236_().m_5776_()) {
         if (this.f_19796_.m_188501_() < this.getMoss() * 0.05F) {
            this.m_19998_((ItemLike)AMItemRegistry.SPIKED_SCUTE.get());
         } else {
            this.m_19998_(Items.f_41867_);
         }

         this.setMoss(0);
      }
   }

   @Nonnull
   public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
      world.m_6269_(null, this, SoundEvents.f_12344_, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      this.m_146850_(GameEvent.f_223708_);
      if (!world.m_5776_()) {
         if (this.f_19796_.m_188501_() < this.getMoss() * 0.05F) {
            this.setMoss(0);
            return Collections.singletonList(new ItemStack((ItemLike)AMItemRegistry.SPIKED_SCUTE.get()));
         } else {
            this.setMoss(0);
            return Collections.singletonList(new ItemStack(Items.f_41867_));
         }
      } else {
         return Collections.emptyList();
      }
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get()).m_20615_(p_241840_1_);
   }
}
