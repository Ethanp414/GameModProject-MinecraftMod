package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.CaimanAIBellow;
import com.github.alexthe666.alexsmobs.entity.ai.CaimanAIMelee;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwnerWater;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class EntityCaiman extends TamableAnimal implements ISemiAquatic, IFollower {
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityCaiman.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityCaiman.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> BELLOWING = SynchedEntityData.m_135353_(EntityCaiman.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.m_135353_(EntityCaiman.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.m_135353_(EntityCaiman.class, EntityDataSerializers.f_135035_);
   public float prevSitProgress;
   public float sitProgress;
   public float prevHoldProgress;
   public float holdProgress;
   public float prevSwimProgress;
   public float swimProgress;
   public float prevVibrateProgress;
   public float vibrateProgress;
   private int swimTimer = -1000;
   public int bellowCooldown = 100 + this.f_19796_.m_188503_(1000);
   private boolean isLandNavigator;
   public boolean tameAttackFlag = false;

   public EntityCaiman(EntityType type, Level level) {
      super(type, level);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(BELLOWING, false);
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(HAS_EGG, false);
      this.f_19804_.m_135372_(HELD_MOB_ID, -1);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(1, new EntityCaiman.MateGoal(this, 1.0));
      this.f_21345_.m_25352_(1, new EntityCaiman.LayEggGoal(this, 1.0));
      this.f_21345_.m_25352_(2, new CaimanAIMelee(this));
      this.f_21345_.m_25352_(3, new BreathAirGoal(this));
      this.f_21345_.m_25352_(4, new TameableAIFollowOwnerWater(this, 1.1, 4.0F, 2.0F, false));
      this.f_21345_.m_25352_(5, new MeleeAttackGoal(this, 1.2F, false));
      this.f_21345_.m_25352_(6, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.CAIMAN_BREEDABLES), false));
      this.f_21345_.m_25352_(7, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(7, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(8, new CaimanAIBellow(this));
      this.f_21345_.m_25352_(9, new SemiAquaticAIRandomSwimming(this, 1.0, 30));
      this.f_21345_.m_25352_(10, new RandomStrollGoal(this, 1.0, 60));
      this.f_21345_.m_25352_(11, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this).m_26044_(new Class[0]));
      this.f_21346_.m_25352_(2, new OwnerHurtByTargetGoal(this) {
         public void m_8056_() {
            super.m_8056_();
            EntityCaiman.this.tameAttackFlag = true;
         }

         public void m_8041_() {
            super.m_8056_();
            EntityCaiman.this.tameAttackFlag = false;
         }
      });
      this.f_21346_.m_25352_(3, new OwnerHurtTargetGoal(this) {
         public void m_8056_() {
            super.m_8056_();
            EntityCaiman.this.tameAttackFlag = true;
         }

         public void m_8041_() {
            super.m_8056_();
            EntityCaiman.this.tameAttackFlag = false;
         }
      });
      this.f_21346_
         .m_25352_(
            5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CAIMAN_TARGETS)) {
               public boolean m_8036_() {
                  return !EntityCaiman.this.m_6162_() && !EntityCaiman.this.m_21824_() && super.m_8036_();
               }
            }
         );
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.caimanSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static <T extends Mob> boolean canCaimanSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
      BlockState blockstate = worldIn.m_8055_(p_223317_3_.m_7495_());
      return blockstate.m_60713_(Blocks.f_220864_) || blockstate.m_60713_(Blocks.f_220834_) || blockstate.m_204336_(AMTagRegistry.CAIMAN_SPAWNS);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.CAIMAN_BREEDABLES);
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigation(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new AquaticMoveController(this, 1.1F);
         this.f_21344_ = new SemiAquaticPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   public int m_5792_() {
      return 2;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   protected SoundEvent m_7515_() {
      return this.m_6162_() ? (SoundEvent)AMSoundRegistry.CROCODILE_BABY.get() : (SoundEvent)AMSoundRegistry.CAIMAN_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.CAIMAN_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.CAIMAN_HURT.get();
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevHoldProgress = this.holdProgress;
      this.prevSwimProgress = this.swimProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevVibrateProgress = this.vibrateProgress;
      boolean ground = !this.m_20072_();
      boolean bellowing = this.isBellowing();
      boolean grabbing = this.getHeldMobId() != -1;
      boolean sitting = this.isSitting() && ground;
      if (!ground && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (ground && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if (ground && this.swimProgress > 0.0F) {
         this.swimProgress--;
      }

      if (!ground && this.swimProgress < 5.0F) {
         this.swimProgress++;
      }

      if (bellowing && this.vibrateProgress < 5.0F) {
         this.vibrateProgress++;
      }

      if (!bellowing && this.vibrateProgress > 0.0F) {
         this.vibrateProgress--;
      }

      if (sitting && this.sitProgress < 5.0F) {
         this.sitProgress++;
      }

      if (!sitting && this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (grabbing && this.holdProgress < 5.0F) {
         this.holdProgress += 2.5F;
      }

      if (!grabbing && this.holdProgress > 0.0F) {
         this.holdProgress -= 2.5F;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_20069_()) {
            this.swimTimer++;
         } else {
            if (this.isBellowing()) {
               this.setBellowing(false);
            }

            this.swimTimer--;
         }

         if (this.m_5448_() instanceof WaterAnimal && !this.m_21824_()) {
            WaterAnimal fish = (WaterAnimal)this.m_5448_();
            CompoundTag fishNbt = new CompoundTag();
            fish.m_7380_(fishNbt);
            fishNbt.m_128359_("DeathLootTable", BuiltInLootTables.f_78712_.toString());
            fish.m_7378_(fishNbt);
         }
      } else if (this.m_20072_() && this.isBellowing()) {
         int particles = 4 + this.m_217043_().m_188503_(3);

         for (int i = 0; i <= particles; i++) {
            Vec3 particleVec = new Vec3(0.0, 0.0, 1.0).m_82524_((float)i / particles * (float) Math.PI * 2.0F).m_82549_(this.m_20182_());
            double particleY = this.m_20191_().f_82289_ + this.getFluidTypeHeight((FluidType)ForgeMod.WATER_TYPE.get());
            this.m_9236_().m_7106_(ParticleTypes.f_123769_, particleVec.f_82479_, particleY, particleVec.f_82481_, 0.0, 0.3F, 0.0);
         }
      }

      if (this.bellowCooldown > 0) {
         this.bellowCooldown--;
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (this.m_21824_() && itemstack.m_204117_(AMTagRegistry.CAIMAN_FOODSTUFFS) && this.m_21223_() < this.m_21233_()) {
         this.m_142075_(player, hand, itemstack);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11788_, this.m_6121_(), this.m_6100_());
         this.m_5634_(5.0F);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.m_21824_()
            && this.m_21830_(player)
            && !this.m_6898_(itemstack)) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.m_5661_(Component.m_237110_("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.m_7755_()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.m_21839_(true);
               return InteractionResult.SUCCESS;
            } else {
               this.m_21839_(false);
               return InteractionResult.SUCCESS;
            }
         } else {
            return type;
         }
      }
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22284_, 8.0)
         .m_22268_(Attributes.f_22279_, 0.2F);
   }

   public boolean m_6063_() {
      return false;
   }

   public boolean m_6914_(LevelReader worldIn) {
      return worldIn.m_45784_(this);
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public void setHeldMobId(int i) {
      this.f_19804_.m_135381_(HELD_MOB_ID, i);
   }

   public int getHeldMobId() {
      return (Integer)this.f_19804_.m_135370_(HELD_MOB_ID);
   }

   public boolean hasEgg() {
      return (Boolean)this.f_19804_.m_135370_(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.f_19804_.m_135381_(HAS_EGG, hasEgg);
   }

   public Entity getHeldMob() {
      int id = this.getHeldMobId();
      return id == -1 ? null : this.m_9236_().m_6815_(id);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public boolean isBellowing() {
      return (Boolean)this.f_19804_.m_135370_(BELLOWING);
   }

   public void setBellowing(boolean bellowing) {
      this.f_19804_.m_135381_(BELLOWING, bellowing);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.isSitting()) {
         super.m_7023_(Vec3.f_82478_);
      } else if (this.m_21515_() && this.m_20069_()) {
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

   public void calculateEntityAnimation(LivingEntity living, boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 0.0, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   public boolean m_6040_() {
      return true;
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.shouldLeaveWater() && this.swimTimer <= -1000 || this.bellowCooldown == 0;
   }

   @Override
   public boolean shouldLeaveWater() {
      LivingEntity target = this.m_5448_();
      return target != null && !target.m_20069_() ? true : this.swimTimer > 600 && !this.isBellowing();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isSitting();
   }

   @Override
   public int getWaterSearchRange() {
      return 12;
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverLevel, AgeableMob ageableMob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.CAIMAN.get()).m_20615_(serverLevel);
   }

   public Vec3 getShakePreyPos() {
      Vec3 jaw = new Vec3(0.0, -0.1, 1.0);
      Vec3 head = jaw.m_82496_(-this.m_146909_() * (float) (Math.PI / 180.0)).m_82524_(-this.m_6080_() * (float) (Math.PI / 180.0));
      return this.m_146892_().m_82549_(head);
   }

   public void m_5997_(double x, double y, double z) {
      if (this.getHeldMobId() == -1) {
         super.m_5997_(x, y, z);
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("HasEgg", this.hasEgg());
      compound.m_128379_("Bellowing", this.isBellowing());
      compound.m_128405_("CaimanCommand", this.getCommand());
      compound.m_128379_("CaimanSitting", this.m_21827_());
      compound.m_128405_("BellowCooldown", this.bellowCooldown);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setHasEgg(compound.m_128471_("HasEgg"));
      this.setBellowing(compound.m_128471_("Bellowing"));
      this.bellowCooldown = compound.m_128451_("BellowCooldown");
      this.setCommand(compound.m_128451_("CaimanCommand"));
      this.m_21839_(compound.m_128471_("CaimanSitting"));
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   static class LayEggGoal extends MoveToBlockGoal {
      private final EntityCaiman caiman;
      private int digTime;

      LayEggGoal(EntityCaiman caiman, double speedIn) {
         super(caiman, speedIn, 16);
         this.caiman = caiman;
      }

      public void m_8041_() {
         this.digTime = 0;
      }

      public boolean m_8036_() {
         return this.caiman.hasEgg() && super.m_8036_();
      }

      public boolean m_8045_() {
         return super.m_8045_() && this.caiman.hasEgg();
      }

      public double m_8052_() {
         return this.caiman.m_20205_() + 0.5;
      }

      public void m_8037_() {
         super.m_8037_();
         BlockPos blockpos = this.caiman.m_20183_();
         this.caiman.swimTimer = 1000;
         if (!this.caiman.m_20069_() && this.m_25625_()) {
            Level world = this.caiman.m_9236_();
            this.caiman.m_146850_(GameEvent.f_157797_);
            world.m_5594_(null, blockpos, SoundEvents.f_12486_, SoundSource.BLOCKS, 0.3F, 0.9F + world.f_46441_.m_188501_() * 0.2F);
            world.m_7731_(
               this.f_25602_.m_7494_(),
               (BlockState)((Block)AMBlockRegistry.CAIMAN_EGG.get()).m_49966_().m_61124_(BlockReptileEgg.EGGS, this.caiman.f_19796_.m_188503_(1) + 3),
               3
            );
            this.caiman.setHasEgg(false);
            this.caiman.m_27601_(600);
         }
      }

      protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
         return worldIn.m_46859_(pos.m_7494_()) && BlockReptileEgg.isProperHabitat(worldIn, pos);
      }
   }

   static class MateGoal extends BreedGoal {
      private final EntityCaiman caiman;

      MateGoal(EntityCaiman caiman, double speedIn) {
         super(caiman, speedIn);
         this.caiman = caiman;
      }

      public boolean m_8036_() {
         return super.m_8036_() && !this.caiman.hasEgg();
      }

      protected void m_8026_() {
         ServerPlayer serverplayerentity = this.f_25113_.m_27592_();
         if (serverplayerentity == null && this.f_25115_.m_27592_() != null) {
            serverplayerentity = this.f_25115_.m_27592_();
         }

         if (serverplayerentity != null) {
            serverplayerentity.m_36220_(Stats.f_12937_);
            CriteriaTriggers.f_10581_.m_147278_(serverplayerentity, this.f_25113_, this.f_25115_, this.f_25113_);
         }

         this.caiman.setHasEgg(true);
         this.f_25113_.m_27594_();
         this.f_25115_.m_27594_();
         this.f_25113_.m_146762_(6000);
         this.f_25115_.m_146762_(6000);
         RandomSource random = this.f_25113_.m_217043_();
         if (this.f_25114_.m_46469_().m_46207_(GameRules.f_46135_)) {
            this.f_25114_
               .m_7967_(new ExperienceOrb(this.f_25114_, this.f_25113_.m_20185_(), this.f_25113_.m_20186_(), this.f_25113_.m_20189_(), random.m_188503_(7) + 1));
         }
      }
   }
}
