package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.MantisShrimpAIBreakBlocks;
import com.github.alexthe666.alexsmobs.entity.ai.MantisShrimpAIFryRice;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class EntityMantisShrimp extends TamableAnimal implements ISemiAquatic, IFollower {
   private static final EntityDataAccessor<Float> RIGHT_EYE_PITCH = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> RIGHT_EYE_YAW = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> LEFT_EYE_PITCH = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> LEFT_EYE_YAW = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> PUNCH_TICK = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.m_135353_(EntityMantisShrimp.class, EntityDataSerializers.f_135028_);
   public float prevRightPitch;
   public float prevRightYaw;
   public float prevLeftPitch;
   public float prevLeftYaw;
   public float prevInWaterProgress;
   public float inWaterProgress;
   public float prevPunchProgress;
   public float punchProgress;
   private int leftLookCooldown = 0;
   private int rightLookCooldown = 0;
   private float targetRightPitch;
   private float targetRightYaw;
   private float targetLeftPitch;
   private float targetLeftYaw;
   private boolean isLandNavigator;
   private int fishFeedings;
   private int moistureAttackTime = 0;

   protected EntityMantisShrimp(EntityType type, Level world) {
      super(type, world);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(false);
      this.m_274367_(1.0F);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.MANTIS_SHRIMP_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.MANTIS_SHRIMP_HURT.get();
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         if (entity instanceof Shulker || entity instanceof ShulkerBullet) {
            amount = (amount + 1.0F) * 0.33F;
         }

         return super.m_6469_(source, amount);
      }
   }

   public void m_5993_(Entity entity, int score, DamageSource src) {
      if (entity instanceof LivingEntity living && living.m_6095_() == EntityType.f_20521_) {
         CompoundTag fishNbt = new CompoundTag();
         living.m_7380_(fishNbt);
         fishNbt.m_128359_("DeathLootTable", BuiltInLootTables.f_78712_.toString());
         living.m_7378_(fishNbt);
         living.m_19998_(Items.f_42748_);
      }

      super.m_5993_(entity, score, src);
   }

   public static boolean canMantisShrimpSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      BlockPos downPos = pos;

      while (downPos.m_123342_() > 1 && !worldIn.m_6425_(downPos).m_76178_()) {
         downPos = downPos.m_7495_();
      }

      boolean spawnBlock = worldIn.m_8055_(downPos).m_204336_(AMTagRegistry.MANTIS_SHRIMP_SPAWNS);
      return worldIn.m_204166_(pos).m_203656_(AMTagRegistry.SPAWNS_WHITE_MANTIS_SHRIMP) && randomIn.m_188501_() < 0.5F
         ? false
         : spawnBlock && downPos.m_123342_() < worldIn.m_5736_() + 1;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22278_, 0.1)
         .m_22268_(Attributes.f_22284_, 8.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22279_, 0.3F);
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return !this.m_21824_();
   }

   public MobType m_6336_() {
      return MobType.f_21642_;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mantisShrimpSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new MantisShrimpAIFryRice(this));
      this.f_21345_.m_25352_(0, new MantisShrimpAIBreakBlocks(this));
      this.f_21345_.m_25352_(1, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(2, new EntityMantisShrimp.FollowOwner(this, 1.3, 4.0F, 2.0F, false));
      this.f_21345_.m_25352_(3, new MeleeAttackGoal(this, 1.2F, false));
      this.f_21345_.m_25352_(4, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(4, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(5, new BreedGoal(this, 0.8));
      this.f_21345_
         .m_25352_(
            6,
            new TemptGoal(
               this,
               1.0,
               Ingredient.m_43938_(Stream.of(new TagValue(AMTagRegistry.MANTIS_SHRIMP_BREEDABLES), new TagValue(AMTagRegistry.MANTIS_SHRIMP_TAMEABLES))),
               false
            )
         );
      this.f_21345_.m_25352_(7, new SemiAquaticAIRandomSwimming(this, 1.0, 30));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new OwnerHurtTargetGoal(this));
      this.f_21346_
         .m_25352_(
            3,
            new EntityAINearestTarget3D(this, LivingEntity.class, 120, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.MANTIS_SHRIMP_TARGETS)) {
               public boolean m_8036_() {
                  return EntityMantisShrimp.this.getCommand() != 3 && !EntityMantisShrimp.this.isSitting() && super.m_8036_();
               }
            }
         );
      this.f_21346_.m_25352_(4, new HurtByTargetGoal(this, new Class[0]));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new AnimalSwimMoveControllerSink(this, 1.0F, 1.0F);
         this.f_21344_ = new SemiAquaticPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.isSitting()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         travelVector = Vec3.f_82478_;
         super.m_7023_(travelVector);
      } else {
         if (this.m_21515_() && this.m_20069_()) {
            this.m_19920_(this.m_6113_(), travelVector);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            this.m_20256_(this.m_20184_().m_82490_(0.9));
         } else {
            super.m_7023_(travelVector);
         }
      }
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268722_) || source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_6040_() {
      return true;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(RIGHT_EYE_PITCH, 0.0F);
      this.f_19804_.m_135372_(RIGHT_EYE_YAW, 0.0F);
      this.f_19804_.m_135372_(LEFT_EYE_PITCH, 0.0F);
      this.f_19804_.m_135372_(LEFT_EYE_YAW, 0.0F);
      this.f_19804_.m_135372_(PUNCH_TICK, 0);
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(VARIANT, 0);
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(MOISTNESS, 60000);
   }

   public boolean m_6898_(ItemStack stack) {
      Item item = stack.m_41720_();
      return this.m_21824_() && stack.m_204117_(AMTagRegistry.MANTIS_SHRIMP_BREEDABLES);
   }

   public boolean m_7327_(Entity entityIn) {
      this.punch();
      return true;
   }

   public void punch() {
      this.f_19804_.m_135381_(PUNCH_TICK, 4);
   }

   public float getEyeYaw(boolean left) {
      return (Float)this.f_19804_.m_135370_(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW);
   }

   public float getEyePitch(boolean left) {
      return (Float)this.f_19804_.m_135370_(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH);
   }

   public void setEyePitch(boolean left, float pitch) {
      this.f_19804_.m_135381_(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH, pitch);
   }

   public void setEyeYaw(boolean left, float yaw) {
      this.f_19804_.m_135381_(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW, yaw);
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int command) {
      this.f_19804_.m_135381_(VARIANT, command);
   }

   public int getMoistness() {
      return (Integer)this.f_19804_.m_135370_(MOISTNESS);
   }

   public void setMoistness(int p_211137_1_) {
      this.f_19804_.m_135381_(MOISTNESS, p_211137_1_);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_21525_()) {
         this.m_20301_(this.m_6062_());
      } else if (!this.m_20071_() && this.m_21205_().m_41720_() != Items.f_42447_) {
         this.setMoistness(this.getMoistness() - 1);
         if (this.getMoistness() <= 0 && this.moistureAttackTime-- <= 0) {
            this.setCommand(0);
            this.m_21839_(false);
            this.m_6469_(this.m_269291_().m_269483_(), this.f_19796_.m_188503_(2) == 0 ? 1.0F : 0.0F);
            this.moistureAttackTime = 20;
         }
      } else {
         this.setMoistness(60000);
      }

      if (this.m_21023_(MobEffects.f_19620_)) {
         this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (!this.m_21824_() && itemstack.m_204117_(AMTagRegistry.MANTIS_SHRIMP_TAMEABLES)) {
         this.m_142075_(player, hand, itemstack);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_12465_, this.m_6121_(), this.m_6100_());
         this.fishFeedings++;
         if ((this.fishFeedings <= 10 || this.m_217043_().m_188503_(6) != 0) && this.fishFeedings <= 30) {
            this.m_9236_().m_7605_(this, (byte)6);
         } else {
            this.m_21828_(player);
            this.m_9236_().m_7605_(this, (byte)7);
         }

         return InteractionResult.SUCCESS;
      } else if (this.m_21824_() && itemstack.m_204117_(ItemTags.f_13156_)) {
         if (this.m_21223_() < this.m_21233_()) {
            this.m_142075_(player, hand, itemstack);
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_12465_, this.m_6121_(), this.m_6100_());
            this.m_5634_(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && this.m_21824_() && this.m_21830_(player)) {
            if (player.m_6144_() || itemstack.m_204117_(AMTagRegistry.SHRIMP_RICE_FRYABLES)) {
               if (this.m_21205_().m_41619_()) {
                  ItemStack cop = itemstack.m_41777_();
                  cop.m_41764_(1);
                  this.m_21008_(InteractionHand.MAIN_HAND, cop);
                  itemstack.m_41774_(1);
                  return InteractionResult.SUCCESS;
               }

               this.m_19983_(this.m_21205_().m_41777_());
               this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
               return InteractionResult.SUCCESS;
            }

            if (!this.m_6898_(itemstack)) {
               this.setCommand(this.getCommand() + 1);
               if (this.getCommand() == 4) {
                  this.setCommand(0);
               }

               if (this.getCommand() == 3) {
                  player.m_5661_(Component.m_237110_("entity.alexsmobs.mantis_shrimp.command_3", new Object[]{this.m_7755_()}), true);
               } else {
                  player.m_5661_(Component.m_237110_("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.m_7755_()}), true);
               }

               boolean sit = this.getCommand() == 2;
               if (sit) {
                  this.m_21839_(true);
                  return InteractionResult.SUCCESS;
               }

               this.m_21839_(false);
               return InteractionResult.SUCCESS;
            }
         }

         return type;
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("MantisShrimpSitting", this.isSitting());
      compound.m_128405_("Command", this.getCommand());
      compound.m_128405_("Moisture", this.getMoistness());
      compound.m_128405_("Variant", this.getVariant());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_21839_(compound.m_128471_("MantisShrimpSitting"));
      this.setCommand(compound.m_128451_("Command"));
      this.setVariant(compound.m_128451_("Variant"));
      this.setMoistness(compound.m_128451_("Moisture"));
   }

   public void m_8107_() {
      super.m_8107_();
      if (this.m_6162_() && this.m_20192_() > this.m_20206_()) {
         this.m_6210_();
      }

      this.prevLeftPitch = this.getEyePitch(true);
      this.prevRightPitch = this.getEyePitch(false);
      this.prevLeftYaw = this.getEyeYaw(true);
      this.prevRightYaw = this.getEyeYaw(false);
      this.prevInWaterProgress = this.inWaterProgress;
      this.prevPunchProgress = this.punchProgress;
      this.updateEyes();
      if (this.isSitting() && this.m_21573_().m_26571_()) {
         this.m_21573_().m_26573_();
      }

      if (this.m_20069_()) {
         if (this.inWaterProgress < 5.0F) {
            this.inWaterProgress++;
         }

         if (this.isLandNavigator) {
            this.switchNavigator(false);
         }
      } else {
         if (this.inWaterProgress > 0.0F) {
            this.inWaterProgress--;
         }

         if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }
      }

      if ((Integer)this.f_19804_.m_135370_(PUNCH_TICK) > 0) {
         if ((Integer)this.f_19804_.m_135370_(PUNCH_TICK) == 2 && this.m_5448_() != null && this.m_20270_(this.m_5448_()) < 2.8) {
            if (this.m_5448_() instanceof AbstractFish && !this.m_21824_()) {
               AbstractFish fish = (AbstractFish)this.m_5448_();
               CompoundTag fishNbt = new CompoundTag();
               fish.m_7380_(fishNbt);
               fishNbt.m_128359_("DeathLootTable", BuiltInLootTables.f_78712_.toString());
               fish.m_7378_(fishNbt);
            }

            this.m_5448_().m_147240_(1.7F, this.m_20185_() - this.m_5448_().m_20185_(), this.m_20189_() - this.m_5448_().m_20189_());
            float knockbackResist = (float)Mth.m_14008_(1.0 - this.m_21133_(Attributes.f_22278_), 0.0, 1.0);
            this.m_5448_().m_20256_(this.m_5448_().m_20184_().m_82520_(0.0, knockbackResist * 0.8F, 0.0));
            if (!this.m_5448_().m_20069_()) {
               this.m_5448_().m_20254_(2);
            }

            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
         }

         if (this.punchProgress == 1.0F) {
            this.m_5496_((SoundEvent)AMSoundRegistry.MANTIS_SHRIMP_SNAP.get(), this.m_6100_(), this.m_6121_());
         }

         if (this.punchProgress == 2.0F && this.m_9236_().f_46443_ && this.m_20069_()) {
            for (int i = 0; i < 10 + this.f_19796_.m_188503_(8); i++) {
               double d2 = this.f_19796_.m_188583_() * 0.6;
               double d0 = this.f_19796_.m_188583_() * 0.2;
               double d1 = this.f_19796_.m_188583_() * 0.6;
               float radius = this.m_20205_() * 0.85F;
               float angle = (float) (Math.PI / 180.0) * this.f_20883_;
               double extraX = radius * Mth.m_14031_((float) Math.PI + angle) + this.f_19796_.m_188501_() * 0.5F - 0.25F;
               double extraZ = radius * Mth.m_14089_(angle) + this.f_19796_.m_188501_() * 0.5F - 0.25F;
               ParticleOptions data = ParticleTypes.f_123795_;
               this.m_9236_()
                  .m_7106_(
                     data,
                     this.m_20185_() + extraX,
                     this.m_20186_() + this.m_20206_() * 0.3F + this.f_19796_.m_188501_() * 0.15F,
                     this.m_20189_() + extraZ,
                     d0,
                     d1,
                     d2
                  );
            }
         }

         if (this.punchProgress < 2.0F) {
            this.punchProgress++;
         }

         this.f_19804_.m_135381_(PUNCH_TICK, (Integer)this.f_19804_.m_135370_(PUNCH_TICK) - 1);
      } else if (this.punchProgress > 0.0F) {
         this.punchProgress -= 0.25F;
      }
   }

   public boolean m_7307_(Entity entityIn) {
      if (this.m_21824_()) {
         LivingEntity livingentity = this.m_269323_();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).m_21830_(livingentity);
         }

         if (livingentity != null) {
            return livingentity.m_7307_(entityIn);
         }
      }

      return super.m_7307_(entityIn);
   }

   private void updateEyes() {
      float leftPitchDist = Math.abs(this.getEyePitch(true) - this.targetLeftPitch);
      float rightPitchDist = Math.abs(this.getEyePitch(false) - this.targetRightPitch);
      float leftYawDist = Math.abs(this.getEyeYaw(true) - this.targetLeftYaw);
      float rightYawDist = Math.abs(this.getEyeYaw(false) - this.targetRightYaw);
      if (this.rightLookCooldown == 0 && this.f_19796_.m_188503_(20) == 0 && rightPitchDist < 0.5F && rightYawDist < 0.5F) {
         this.targetRightPitch = Mth.m_14036_(this.f_19796_.m_188501_() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.targetRightYaw = Mth.m_14036_(this.f_19796_.m_188501_() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.rightLookCooldown = 3 + this.f_19796_.m_188503_(15);
      }

      if (this.leftLookCooldown == 0 && this.f_19796_.m_188503_(20) == 0 && leftPitchDist < 0.5F && leftYawDist < 0.5F) {
         this.targetLeftPitch = Mth.m_14036_(this.f_19796_.m_188501_() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.targetLeftYaw = Mth.m_14036_(this.f_19796_.m_188501_() * 60.0F - 30.0F, -30.0F, 30.0F);
         this.leftLookCooldown = 3 + this.f_19796_.m_188503_(15);
      }

      if (leftPitchDist > 0.5F) {
         if (this.getEyePitch(true) < this.targetLeftPitch) {
            this.setEyePitch(true, this.getEyePitch(true) + Math.min(leftPitchDist, 4.0F));
         }

         if (this.getEyePitch(true) > this.targetLeftPitch) {
            this.setEyePitch(true, this.getEyePitch(true) - Math.min(leftPitchDist, 4.0F));
         }
      }

      if (rightPitchDist > 0.5F) {
         if (this.getEyePitch(false) < this.targetRightPitch) {
            this.setEyePitch(false, this.getEyePitch(false) + Math.min(rightPitchDist, 4.0F));
         }

         if (this.getEyePitch(false) > this.targetRightPitch) {
            this.setEyePitch(false, this.getEyePitch(false) - Math.min(rightPitchDist, 4.0F));
         }
      }

      if (leftYawDist > 0.5F) {
         if (this.getEyeYaw(true) < this.targetLeftYaw) {
            this.setEyeYaw(true, this.getEyeYaw(true) + Math.min(leftYawDist, 4.0F));
         }

         if (this.getEyeYaw(true) > this.targetLeftYaw) {
            this.setEyeYaw(true, this.getEyeYaw(true) - Math.min(leftYawDist, 4.0F));
         }
      }

      if (rightYawDist > 0.5F) {
         if (this.getEyeYaw(false) < this.targetRightYaw) {
            this.setEyeYaw(false, this.getEyeYaw(false) + Math.min(rightYawDist, 4.0F));
         }

         if (this.getEyeYaw(false) > this.targetRightYaw) {
            this.setEyeYaw(false, this.getEyeYaw(false) - Math.min(rightYawDist, 4.0F));
         }
      }

      if (this.rightLookCooldown > 0) {
         this.rightLookCooldown--;
      }

      if (this.leftLookCooldown > 0) {
         this.leftLookCooldown--;
      }
   }

   public boolean m_6063_() {
      return false;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      int i;
      if (reason == MobSpawnType.SPAWN_EGG) {
         i = this.m_217043_().m_188503_(4);
      } else if (worldIn.m_204166_(this.m_20183_()).m_203656_(AMTagRegistry.SPAWNS_WHITE_MANTIS_SHRIMP)) {
         i = 3;
      } else {
         i = this.m_217043_().m_188503_(3);
      }

      this.setVariant(i);
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntityMantisShrimp shrimp = (EntityMantisShrimp)((EntityType)AMEntityRegistry.MANTIS_SHRIMP.get()).m_20615_(serverWorld);
      shrimp.setVariant(this.m_217043_().m_188503_(3));
      return shrimp;
   }

   @Override
   public boolean shouldEnterWater() {
      return (this.m_21205_().m_41619_() || this.m_21205_().m_41720_() != Items.f_42447_) && !this.isSitting();
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.m_21205_().m_41720_() == Items.f_42447_;
   }

   @Override
   public boolean shouldStopMoving() {
      return this.isSitting();
   }

   @Override
   public int getWaterSearchRange() {
      return 16;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public boolean m_6914_(LevelReader worldIn) {
      return worldIn.m_45784_(this);
   }

   protected void updateAir(int p_209207_1_) {
   }

   public static class FollowOwner extends Goal {
      private final EntityMantisShrimp tameable;
      private final LevelReader world;
      private final double followSpeed;
      private final float maxDist;
      private final float minDist;
      private final boolean teleportToLeaves;
      private LivingEntity owner;
      private int timeToRecalcPath;
      private float oldWaterCost;

      public FollowOwner(EntityMantisShrimp p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
         this.tameable = p_i225711_1_;
         this.world = p_i225711_1_.m_9236_();
         this.followSpeed = p_i225711_2_;
         this.minDist = p_i225711_4_;
         this.maxDist = p_i225711_5_;
         this.teleportToLeaves = p_i225711_6_;
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         LivingEntity lvt_1_1_ = this.tameable.m_269323_();
         if (lvt_1_1_ == null) {
            return false;
         } else if (lvt_1_1_.m_5833_()) {
            return false;
         } else if (this.tameable.isSitting() || this.tameable.getCommand() != 1) {
            return false;
         } else if (this.tameable.m_20280_(lvt_1_1_) < this.minDist * this.minDist) {
            return false;
         } else if (this.tameable.m_5448_() != null && this.tameable.m_5448_().m_6084_()) {
            return false;
         } else {
            this.owner = lvt_1_1_;
            return true;
         }
      }

      public boolean m_8045_() {
         if (this.tameable.m_21573_().m_26571_()) {
            return false;
         } else if (this.tameable.isSitting() || this.tameable.getCommand() != 1) {
            return false;
         } else {
            return this.tameable.m_5448_() != null && this.tameable.m_5448_().m_6084_()
               ? false
               : this.tameable.m_20280_(this.owner) > this.maxDist * this.maxDist;
         }
      }

      public void m_8056_() {
         this.timeToRecalcPath = 0;
         this.oldWaterCost = this.tameable.m_21439_(BlockPathTypes.WATER);
         this.tameable.m_21441_(BlockPathTypes.WATER, 0.0F);
      }

      public void m_8041_() {
         this.owner = null;
         this.tameable.m_21573_().m_26573_();
         this.tameable.m_21441_(BlockPathTypes.WATER, this.oldWaterCost);
      }

      public void m_8037_() {
         this.tameable.m_21563_().m_24960_(this.owner, 10.0F, this.tameable.m_8132_());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.tameable.m_21523_() && !this.tameable.m_20159_()) {
               if (this.tameable.m_20280_(this.owner) >= 144.0) {
                  this.tryToTeleportNearEntity();
               } else {
                  this.tameable.m_21573_().m_5624_(this.owner, this.followSpeed);
               }
            }
         }
      }

      private void tryToTeleportNearEntity() {
         BlockPos lvt_1_1_ = this.owner.m_20183_();

         for (int lvt_2_1_ = 0; lvt_2_1_ < 10; lvt_2_1_++) {
            int lvt_3_1_ = this.getRandomNumber(-3, 3);
            int lvt_4_1_ = this.getRandomNumber(-1, 1);
            int lvt_5_1_ = this.getRandomNumber(-3, 3);
            boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.m_123341_() + lvt_3_1_, lvt_1_1_.m_123342_() + lvt_4_1_, lvt_1_1_.m_123343_() + lvt_5_1_);
            if (lvt_6_1_) {
               return;
            }
         }
      }

      private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
         if (Math.abs(p_226328_1_ - this.owner.m_20185_()) < 2.0 && Math.abs(p_226328_3_ - this.owner.m_20189_()) < 2.0) {
            return false;
         } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
            return false;
         } else {
            this.tameable.m_7678_(p_226328_1_ + 0.5, p_226328_2_, p_226328_3_ + 0.5, this.tameable.m_146908_(), this.tameable.m_146909_());
            this.tameable.m_21573_().m_26573_();
            return true;
         }
      }

      private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
         BlockPathTypes lvt_2_1_ = WalkNodeEvaluator.m_77604_(this.world, p_226329_1_.m_122032_());
         if (!this.world.m_6425_(p_226329_1_).m_205070_(FluidTags.f_13131_)
            && (this.world.m_6425_(p_226329_1_).m_205070_(FluidTags.f_13131_) || !this.world.m_6425_(p_226329_1_.m_7495_()).m_205070_(FluidTags.f_13131_))) {
            if (lvt_2_1_ == BlockPathTypes.WALKABLE && this.tameable.getMoistness() >= 2000) {
               BlockState lvt_3_1_ = this.world.m_8055_(p_226329_1_.m_7495_());
               if (!this.teleportToLeaves && lvt_3_1_.m_60734_() instanceof LeavesBlock) {
                  return false;
               } else {
                  BlockPos lvt_4_1_ = p_226329_1_.m_121996_(this.tameable.m_20183_());
                  return this.world.m_45756_(this.tameable, this.tameable.m_20191_().m_82338_(lvt_4_1_));
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }

      private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
         return this.tameable.m_217043_().m_188503_(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
      }
   }
}
