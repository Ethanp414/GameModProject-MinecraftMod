package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Zombie extends Monster {
   private static final ResourceLocation SPEED_MODIFIER_BABY_ID = ResourceLocation.withDefaultNamespace("baby");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(
      SPEED_MODIFIER_BABY_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
   );
   private static final ResourceLocation REINFORCEMENT_CALLER_CHARGE_ID = ResourceLocation.withDefaultNamespace("reinforcement_caller_charge");
   private static final AttributeModifier ZOMBIE_REINFORCEMENT_CALLEE_CHARGE = new AttributeModifier(
      ResourceLocation.withDefaultNamespace("reinforcement_callee_charge"), -0.05F, AttributeModifier.Operation.ADD_VALUE
   );
   private static final ResourceLocation LEADER_ZOMBIE_BONUS_ID = ResourceLocation.withDefaultNamespace("leader_zombie_bonus");
   private static final ResourceLocation ZOMBIE_RANDOM_SPAWN_BONUS_ID = ResourceLocation.withDefaultNamespace("zombie_random_spawn_bonus");
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
   public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
   public static final int REINFORCEMENT_ATTEMPTS = 50;
   public static final int REINFORCEMENT_RANGE_MAX = 40;
   public static final int REINFORCEMENT_RANGE_MIN = 7;
   private static final int NOT_CONVERTING = -1;
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.ZOMBIE.getDimensions().scale(0.5F).withEyeHeight(0.93F);
   private static final float BREAK_DOOR_CHANCE = 0.1F;
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = $$0 -> $$0 == Difficulty.HARD;
   private static final boolean DEFAULT_BABY = false;
   private static final boolean DEFAULT_CAN_BREAK_DOORS = false;
   private static final int DEFAULT_IN_WATER_TIME = 0;
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
   private boolean canBreakDoors = false;
   private int inWaterTime = 0;
   private int conversionTime;

   public Zombie(EntityType<? extends Zombie> $$0, Level $$1) {
      super($$0, $$1);
   }

   public Zombie(Level $$0) {
      this(EntityType.ZOMBIE, $$0);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(4, new Zombie.ZombieAttackTurtleEggGoal(this, 1.0, 3));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23F)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder $$0) {
      super.defineSynchedData($$0);
      $$0.define(DATA_BABY_ID, false);
      $$0.define(DATA_SPECIAL_TYPE_ID, 0);
      $$0.define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean isUnderWaterConverting() {
      return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean $$0) {
      if (this.navigation.canNavigateGround()) {
         if (this.canBreakDoors != $$0) {
            this.canBreakDoors = $$0;
            this.navigation.setCanOpenDoors($$0);
            if ($$0) {
               this.goalSelector.addGoal(1, this.breakDoorGoal);
            } else {
               this.goalSelector.removeGoal(this.breakDoorGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.removeGoal(this.breakDoorGoal);
         this.canBreakDoors = false;
      }
   }

   @Override
   public boolean isBaby() {
      return this.getEntityData().get(DATA_BABY_ID);
   }

   @Override
   protected int getBaseExperienceReward(ServerLevel $$0) {
      if (this.isBaby()) {
         this.xpReward = (int)((double)this.xpReward * 2.5);
      }

      return super.getBaseExperienceReward($$0);
   }

   @Override
   public void setBaby(boolean $$0) {
      this.getEntityData().set(DATA_BABY_ID, $$0);
      if (this.level() != null && !this.level().isClientSide()) {
         AttributeInstance $$1 = this.getAttribute(Attributes.MOVEMENT_SPEED);
         $$1.removeModifier(SPEED_MODIFIER_BABY_ID);
         if ($$0) {
            $$1.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
      if (DATA_BABY_ID.equals($$0)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated($$0);
   }

   protected boolean convertsInWater() {
      return true;
   }

   @Override
   public void tick() {
      if (!this.level().isClientSide() && this.isAlive() && !this.isNoAi()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;
            if (this.conversionTime < 0) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isEyeInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startUnderWaterConversion(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   @Override
   public void aiStep() {
      if (this.isAlive()) {
         boolean $$0 = this.isSunSensitive() && this.isSunBurnTick();
         if ($$0) {
            ItemStack $$1 = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!$$1.isEmpty()) {
               if ($$1.isDamageableItem()) {
                  Item $$2 = $$1.getItem();
                  $$1.setDamageValue($$1.getDamageValue() + this.random.nextInt(2));
                  if ($$1.getDamageValue() >= $$1.getMaxDamage()) {
                     this.onEquippedItemBroken($$2, EquipmentSlot.HEAD);
                     this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               $$0 = false;
            }

            if ($$0) {
               this.igniteForSeconds(8.0F);
            }
         }
      }

      super.aiStep();
   }

   private void startUnderWaterConversion(int $$0) {
      this.conversionTime = $$0;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.DROWNED);
      if (!this.isSilent()) {
         this.level().levelEvent(null, 1040, this.blockPosition(), 0);
      }
   }

   protected void convertToZombieType(EntityType<? extends Zombie> $$0) {
      this.convertTo(
         $$0,
         ConversionParams.single(this, true, true),
         $$0x -> $$0x.handleAttributes($$0x.level().getCurrentDifficultyAt($$0x.blockPosition()).getSpecialMultiplier())
      );
   }

   @VisibleForTesting
   public boolean convertVillagerToZombieVillager(ServerLevel $$0, Villager $$1) {
      ZombieVillager $$2 = $$1.convertTo(EntityType.ZOMBIE_VILLAGER, ConversionParams.single($$1, true, true), $$2x -> {
         $$2x.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$2x.blockPosition()), EntitySpawnReason.CONVERSION, new Zombie.ZombieGroupData(false, true));
         $$2x.setVillagerData($$1.getVillagerData());
         $$2x.setGossips($$1.getGossips().copy());
         $$2x.setTradeOffers($$1.getOffers().copy());
         $$2x.setVillagerXp($$1.getVillagerXp());
         if (!this.isSilent()) {
            $$0.levelEvent(null, 1026, this.blockPosition(), 0);
         }
      });
      return $$2 != null;
   }

   protected boolean isSunSensitive() {
      return true;
   }

   @Override
   public boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
      if (!super.hurtServer($$0, $$1, $$2)) {
         return false;
      } else {
         LivingEntity $$3 = this.getTarget();
         if ($$3 == null && $$1.getEntity() instanceof LivingEntity) {
            $$3 = (LivingEntity)$$1.getEntity();
         }

         if ($$3 != null
            && $$0.getDifficulty() == Difficulty.HARD
            && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
            && $$0.isSpawningMonsters()) {
            int $$4 = Mth.floor(this.getX());
            int $$5 = Mth.floor(this.getY());
            int $$6 = Mth.floor(this.getZ());
            EntityType<? extends Zombie> $$7 = this.getType();
            Zombie $$8 = $$7.create($$0, EntitySpawnReason.REINFORCEMENT);
            if ($$8 == null) {
               return true;
            }

            for(int $$9 = 0; $$9 < 50; ++$$9) {
               int $$10 = $$4 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int $$11 = $$5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int $$12 = $$6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               BlockPos $$13 = new BlockPos($$10, $$11, $$12);
               if (SpawnPlacements.isSpawnPositionOk($$7, $$0, $$13)
                  && SpawnPlacements.checkSpawnRules($$7, $$0, EntitySpawnReason.REINFORCEMENT, $$13, $$0.random)) {
                  $$8.setPos((double)$$10, (double)$$11, (double)$$12);
                  if (!$$0.hasNearbyAlivePlayer((double)$$10, (double)$$11, (double)$$12, 7.0)
                     && $$0.isUnobstructed($$8)
                     && $$0.noCollision($$8)
                     && ($$8.canSpawnInLiquids() || !$$0.containsAnyLiquid($$8.getBoundingBox()))) {
                     $$8.setTarget($$3);
                     $$8.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$8.blockPosition()), EntitySpawnReason.REINFORCEMENT, null);
                     $$0.addFreshEntityWithPassengers($$8);
                     AttributeInstance $$14 = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                     AttributeModifier $$15 = $$14.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                     double $$16 = $$15 != null ? $$15.amount() : 0.0;
                     $$14.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                     $$14.addPermanentModifier(new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, $$16 - 0.05, AttributeModifier.Operation.ADD_VALUE));
                     $$8.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(ZOMBIE_REINFORCEMENT_CALLEE_CHARGE);
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   @Override
   public boolean doHurtTarget(ServerLevel $$0, Entity $$1) {
      boolean $$2 = super.doHurtTarget($$0, $$1);
      if ($$2) {
         float $$3 = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < $$3 * 0.3F) {
            $$1.igniteForSeconds((float)(2 * (int)$$3));
         }
      }

      return $$2;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource $$0) {
      return SoundEvents.ZOMBIE_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   @Override
   protected void playStepSound(BlockPos $$0, BlockState $$1) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   @Override
   public EntityType<? extends Zombie> getType() {
      return super.getType();
   }

   protected boolean canSpawnInLiquids() {
      return false;
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource $$0, DifficultyInstance $$1) {
      super.populateDefaultEquipmentSlots($$0, $$1);
      if ($$0.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int $$2 = $$0.nextInt(3);
         if ($$2 == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput $$0) {
      super.addAdditionalSaveData($$0);
      $$0.putBoolean("IsBaby", this.isBaby());
      $$0.putBoolean("CanBreakDoors", this.canBreakDoors());
      $$0.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      $$0.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput $$0) {
      super.readAdditionalSaveData($$0);
      this.setBaby($$0.getBooleanOr("IsBaby", false));
      this.setCanBreakDoors($$0.getBooleanOr("CanBreakDoors", false));
      this.inWaterTime = $$0.getIntOr("InWaterTime", 0);
      int $$1 = $$0.getIntOr("DrownedConversionTime", -1);
      if ($$1 != -1) {
         this.startUnderWaterConversion($$1);
      } else {
         this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, false);
      }
   }

   @Override
   public boolean killedEntity(ServerLevel $$0, LivingEntity $$1, DamageSource $$2) {
      boolean $$3 = super.killedEntity($$0, $$1, $$2);
      if (($$0.getDifficulty() == Difficulty.NORMAL || $$0.getDifficulty() == Difficulty.HARD) && $$1 instanceof Villager $$4) {
         if ($$0.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return $$3;
         }

         if (this.convertVillagerToZombieVillager($$0, $$4)) {
            $$3 = false;
         }
      }

      return $$3;
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose $$0) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions($$0);
   }

   @Override
   public boolean canHoldItem(ItemStack $$0) {
      return $$0.is(ItemTags.EGGS) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem($$0);
   }

   @Override
   public boolean wantsToPickUp(ServerLevel $$0, ItemStack $$1) {
      return $$1.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp($$0, $$1);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, EntitySpawnReason $$2, @Nullable SpawnGroupData $$3) {
      RandomSource $$4 = $$0.getRandom();
      $$3 = super.finalizeSpawn($$0, $$1, $$2, $$3);
      float $$5 = $$1.getSpecialMultiplier();
      if ($$2 != EntitySpawnReason.CONVERSION) {
         this.setCanPickUpLoot($$4.nextFloat() < 0.55F * $$5);
      }

      if ($$3 == null) {
         $$3 = new Zombie.ZombieGroupData(getSpawnAsBabyOdds($$4), true);
      }

      if ($$3 instanceof Zombie.ZombieGroupData $$6) {
         if ($$6.isBaby) {
            this.setBaby(true);
            if ($$6.canSpawnJockey) {
               if ((double)$$4.nextFloat() < 0.05) {
                  List<Chicken> $$7 = $$0.getEntitiesOfClass(
                     Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN
                  );
                  if (!$$7.isEmpty()) {
                     Chicken $$8 = (Chicken)$$7.get(0);
                     $$8.setChickenJockey(true);
                     this.startRiding($$8, false, false);
                  }
               } else if ((double)$$4.nextFloat() < 0.05) {
                  Chicken $$9 = EntityType.CHICKEN.create(this.level(), EntitySpawnReason.JOCKEY);
                  if ($$9 != null) {
                     $$9.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                     $$9.finalizeSpawn($$0, $$1, EntitySpawnReason.JOCKEY, null);
                     $$9.setChickenJockey(true);
                     this.startRiding($$9, false, false);
                     $$0.addFreshEntity($$9);
                  }
               }
            }
         }

         this.setCanBreakDoors($$4.nextFloat() < $$5 * 0.1F);
         if ($$2 != EntitySpawnReason.CONVERSION) {
            this.populateDefaultEquipmentSlots($$4, $$1);
            this.populateDefaultEquipmentEnchantments($$0, $$4, $$1);
         }
      }

      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate $$10 = LocalDate.now();
         int $$11 = $$10.get(ChronoField.DAY_OF_MONTH);
         int $$12 = $$10.get(ChronoField.MONTH_OF_YEAR);
         if ($$12 == 10 && $$11 == 31 && $$4.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack($$4.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.setDropChance(EquipmentSlot.HEAD, 0.0F);
         }
      }

      this.handleAttributes($$5);
      return $$3;
   }

   @VisibleForTesting
   public void setInWaterTime(int $$0) {
      this.inWaterTime = $$0;
   }

   @VisibleForTesting
   public void setConversionTime(int $$0) {
      this.conversionTime = $$0;
   }

   public static boolean getSpawnAsBabyOdds(RandomSource $$0) {
      return $$0.nextFloat() < 0.05F;
   }

   protected void handleAttributes(float $$0) {
      this.randomizeReinforcementsChance();
      this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)
         .addOrReplacePermanentModifier(new AttributeModifier(RANDOM_SPAWN_BONUS_ID, this.random.nextDouble() * 0.05F, AttributeModifier.Operation.ADD_VALUE));
      double $$1 = this.random.nextDouble() * 1.5 * (double)$$0;
      if ($$1 > 1.0) {
         this.getAttribute(Attributes.FOLLOW_RANGE)
            .addOrReplacePermanentModifier(new AttributeModifier(ZOMBIE_RANDOM_SPAWN_BONUS_ID, $$1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }

      if (this.random.nextFloat() < $$0 * 0.05F) {
         this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
            .addOrReplacePermanentModifier(
               new AttributeModifier(LEADER_ZOMBIE_BONUS_ID, this.random.nextDouble() * 0.25 + 0.5, AttributeModifier.Operation.ADD_VALUE)
            );
         this.getAttribute(Attributes.MAX_HEALTH)
            .addOrReplacePermanentModifier(
               new AttributeModifier(LEADER_ZOMBIE_BONUS_ID, this.random.nextDouble() * 3.0 + 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            );
         this.setCanBreakDoors(true);
      }
   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.1F);
   }

   class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
      ZombieAttackTurtleEggGoal(final PathfinderMob param2, final double param3, final int param5) {
         super(Blocks.TURTLE_EGG, $$0, $$1, $$2);
      }

      @Override
      public void playDestroyProgressSound(LevelAccessor $$0, BlockPos $$1) {
         $$0.playSound(null, $$1, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + Zombie.this.random.nextFloat() * 0.2F);
      }

      @Override
      public void playBreakSound(Level $$0, BlockPos $$1) {
         $$0.playSound(null, $$1, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + $$0.random.nextFloat() * 0.2F);
      }

      @Override
      public double acceptedDistance() {
         return 1.14;
      }
   }

   public static class ZombieGroupData implements SpawnGroupData {
      public final boolean isBaby;
      public final boolean canSpawnJockey;

      public ZombieGroupData(boolean $$0, boolean $$1) {
         this.isBaby = $$0;
         this.canSpawnJockey = $$1;
      }
   }
}
