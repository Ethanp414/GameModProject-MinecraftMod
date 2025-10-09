package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIFollowCaravan;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIForageLeaves;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIVillagerRide;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityElephant extends TamableAnimal implements ITargetsDroppedItems, IAnimatedEntity {
   public static final Animation ANIMATION_TRUMPET_0 = Animation.create(20);
   public static final Animation ANIMATION_TRUMPET_1 = Animation.create(30);
   public static final Animation ANIMATION_CHARGE_PREPARE = Animation.create(25);
   public static final Animation ANIMATION_STOMP = Animation.create(20);
   public static final Animation ANIMATION_FLING = Animation.create(25);
   public static final Animation ANIMATION_EAT = Animation.create(30);
   public static final Animation ANIMATION_BREAKLEAVES = Animation.create(20);
   protected static final EntityDimensions TUSKED_SIZE = EntityDimensions.m_20398_(3.7F, 3.75F);
   private static final EntityDataAccessor<Boolean> TUSKED = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> TRADER = SynchedEntityData.m_135353_(EntityElephant.class, EntityDataSerializers.f_135035_);
   public static final Map<DyeColor, Item> DYE_COLOR_ITEM_MAP = (Map<DyeColor, Item>)Util.m_137469_(Maps.newHashMap(), map -> {
      map.put(DyeColor.WHITE, Items.f_42130_);
      map.put(DyeColor.ORANGE, Items.f_42131_);
      map.put(DyeColor.MAGENTA, Items.f_42132_);
      map.put(DyeColor.LIGHT_BLUE, Items.f_42133_);
      map.put(DyeColor.YELLOW, Items.f_42134_);
      map.put(DyeColor.LIME, Items.f_42135_);
      map.put(DyeColor.PINK, Items.f_42136_);
      map.put(DyeColor.GRAY, Items.f_42137_);
      map.put(DyeColor.LIGHT_GRAY, Items.f_42138_);
      map.put(DyeColor.CYAN, Items.f_42139_);
      map.put(DyeColor.PURPLE, Items.f_42140_);
      map.put(DyeColor.BLUE, Items.f_42141_);
      map.put(DyeColor.BROWN, Items.f_42142_);
      map.put(DyeColor.GREEN, Items.f_42143_);
      map.put(DyeColor.RED, Items.f_42197_);
      map.put(DyeColor.BLACK, Items.f_42198_);
   });
   private static final ResourceLocation TRADER_LOOT = new ResourceLocation("alexsmobs", "gameplay/trader_elephant_chest");
   public boolean forcedSit = false;
   public float prevSitProgress;
   public float sitProgress;
   public float prevStandProgress;
   public float standProgress;
   public int maxStandTime = 75;
   public boolean aiItemFlag = false;
   public SimpleContainer elephantInventory;
   private int animationTick;
   private Animation currentAnimation;
   private final boolean hasTuskedAttributes = false;
   private int standingTime = 0;
   @Nullable
   private EntityElephant caravanHead;
   @Nullable
   private EntityElephant caravanTail;
   private boolean hasChestVarChanged = false;
   private boolean hasChargedSpeed = false;
   private boolean charging;
   private int chargeCooldown = 0;
   private int chargingTicks = 0;
   @Nullable
   private UUID blossomThrowerUUID = null;
   private int despawnDelay = 47999;

   protected EntityElephant(EntityType type, Level world) {
      super(type, world);
      this.initElephantInventory();
      this.m_274367_(1.1F);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 85.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22278_, 0.9F)
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22279_, 0.35F);
   }

   @Nullable
   public static DyeColor getCarpetColor(ItemStack stack) {
      Block lvt_1_1_ = Block.m_49814_(stack.m_41720_());
      return lvt_1_1_ instanceof WoolCarpetBlock ? ((WoolCarpetBlock)lvt_1_1_).m_58309_() : null;
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.ELEPHANT_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.ELEPHANT_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.ELEPHANT_DIE.get();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.elephantSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   private void initElephantInventory() {
      SimpleContainer animalchest = this.elephantInventory;
      this.elephantInventory = new SimpleContainer(54) {
         public boolean m_6542_(Player player) {
            return EntityElephant.this.m_6084_() && !EntityElephant.this.f_19817_;
         }
      };
      if (animalchest != null) {
         int i = Math.min(animalchest.m_6643_(), this.elephantInventory.m_6643_());

         for (int j = 0; j < i; j++) {
            ItemStack itemstack = animalchest.m_8020_(j);
            if (!itemstack.m_41619_()) {
               this.elephantInventory.m_6836_(j, itemstack.m_41777_());
            }
         }
      }
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public int m_8085_() {
      return super.m_8085_();
   }

   protected boolean m_6107_() {
      return super.m_6107_() || this.isSitting() || this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getAnimationTick() < 10;
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(2, new MeleeAttackGoal(this, 1.0, true));
      this.f_21345_.m_25352_(2, new EntityElephant.PanicGoal());
      this.f_21345_.m_25352_(2, new ElephantAIVillagerRide(this, 1.0));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.0, Ingredient.m_204132_(AMTagRegistry.ELEPHANT_TAMEABLES), false));
      this.f_21345_.m_25352_(5, new ElephantAIForageLeaves(this));
      this.f_21345_.m_25352_(6, new FollowParentGoal(this, 1.0));
      this.f_21345_.m_25352_(7, new ElephantAIFollowCaravan(this, 0.5));
      this.f_21345_.m_25352_(8, new AvoidEntityGoal(this, Bee.class, 6.0F, 1.0, 1.2));
      this.f_21345_.m_25352_(9, new EntityElephant.AIWalkIdle(this, 0.5));
      this.f_21346_.m_25352_(1, new EntityElephant.HurtByTargetGoal().m_26044_(new Class[0]));
      this.f_21346_.m_25352_(2, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(3, new OwnerHurtTargetGoal(this));
      this.f_21346_.m_25352_(4, new CreatureAITargetItems(this, false));
   }

   public boolean m_6898_(ItemStack stack) {
      Item item = stack.m_41720_();
      return this.m_21824_() && stack.m_204117_(AMTagRegistry.ELEPHANT_BREEDABLES);
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
      if (!this.m_6162_()) {
         this.m_5496_((SoundEvent)AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.0F);
      } else {
         super.m_7355_(pos, state);
      }
   }

   @Nullable
   public LivingEntity m_6688_() {
      for (Entity passenger : this.m_20197_()) {
         if (passenger instanceof Player) {
            return (LivingEntity)passenger;
         }
      }

      return null;
   }

   @Nullable
   public AbstractVillager getControllingVillager() {
      for (Entity passenger : this.m_20197_()) {
         if (passenger instanceof AbstractVillager) {
            return (AbstractVillager)passenger;
         }
      }

      return null;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(TUSKED, false);
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(STANDING, false);
      this.f_19804_.m_135372_(CHESTED, false);
      this.f_19804_.m_135372_(TRADER, false);
      this.f_19804_.m_135372_(CARPET_COLOR, -1);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevSitProgress = this.sitProgress;
      this.prevStandProgress = this.standProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isStanding()) {
         if (this.standProgress < 5.0F) {
            this.standProgress += 0.5F;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress -= 0.5F;
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.f_19796_.m_188503_(50);
      }

      if (this.isSitting() && this.isStanding()) {
         this.setStanding(false);
      }

      if (this.hasChestVarChanged && this.elephantInventory != null && !this.isChested()) {
         for (int i = 3; i < 18; i++) {
            if (!this.elephantInventory.m_8020_(i).m_41619_()) {
               if (!this.m_9236_().f_46443_) {
                  this.m_5552_(this.elephantInventory.m_8020_(i), 1.0F);
               }

               this.elephantInventory.m_8016_(i);
            }
         }

         this.hasChestVarChanged = false;
      }

      if (this.isTusked() && !this.m_6162_()) {
         this.m_6210_();
      }

      if (this.charging) {
         this.chargingTicks++;
      }

      if (!this.m_21205_().m_41619_() && this.canTargetItem(this.m_21205_())) {
         if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_EAT);
         }

         if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 17) {
            this.eatItemEffect(this.m_21205_());
            if (this.m_21205_().m_204117_(AMTagRegistry.ELEPHANT_TAMEABLES)
               && !this.m_21824_()
               && (!this.isTusked() || this.m_6162_())
               && this.blossomThrowerUUID != null) {
               if (this.f_19796_.m_188503_(3) != 0) {
                  this.m_9236_().m_7605_(this, (byte)6);
               } else {
                  this.m_7105_(true);
                  this.m_21816_(this.blossomThrowerUUID);
                  Player player = this.m_9236_().m_46003_(this.blossomThrowerUUID);
                  if (player != null) {
                     this.m_21828_(player);
                  }

                  for (Entity passenger : this.m_20197_()) {
                     passenger.m_6038_();
                  }

                  this.m_9236_().m_7605_(this, (byte)7);
               }
            }

            this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
            this.m_5634_(10.0F);
         }
      }

      if (this.chargeCooldown > 0) {
         this.chargeCooldown--;
      }

      if (this.charging) {
         this.chargingTicks++;
      } else {
         this.chargingTicks = 0;
      }

      if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
         this.f_20883_ = this.m_146908_();
         if (this.getAnimationTick() == 20) {
            this.charging = true;
         }
      }

      if (this.m_6688_() != null && this.charging && this.chargingTicks > 100) {
         this.charging = false;
         this.chargeCooldown = 200;
      }

      LivingEntity target = this.m_5448_();
      double maxAttackMod = 0.0;
      if (this.m_6688_() != null && this.m_6688_() instanceof Player) {
         Player rider = (Player)this.m_6688_();
         if (rider.m_21214_() != null && !this.m_7307_(rider.m_21214_())) {
            UUID preyUUID = rider.m_21214_().m_20148_();
            if (!this.m_20148_().equals(preyUUID)) {
               target = rider.m_21214_();
               maxAttackMod = 4.0;
            }
         }
      }

      if (!this.m_9236_().f_46443_ && target != null) {
         if (this.m_20270_(target) > this.m_20205_() * 0.5F + 0.5F
            && this.m_6688_() == null
            && this.isTusked()
            && this.m_142582_(target)
            && this.getAnimation() == NO_ANIMATION
            && !this.charging
            && this.chargeCooldown == 0) {
            this.setAnimation(ANIMATION_CHARGE_PREPARE);
         }

         if (this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.m_6688_() == null) {
            this.m_21391_(target, 360.0F, 30.0F);
            this.f_20883_ = this.m_146908_();
            if (this.getAnimationTick() == 20) {
               this.charging = true;
            }
         }

         if (this.m_20270_(target) < 10.0 && this.charging) {
            this.setAnimation(ANIMATION_FLING);
         }

         if (this.m_20270_(target) < 2.1 && this.charging) {
            target.m_147240_(1.0, target.m_20185_() - this.m_20185_(), target.m_20189_() - this.m_20189_());
            target.f_19812_ = true;
            target.m_20256_(target.m_20184_().m_82520_(0.0, 0.7F, 0.0));
            target.m_6469_(this.m_269291_().m_269333_(this), 2.4F * (float)this.m_21051_(Attributes.f_22281_).m_22115_());
            this.launch(target, true);
            this.charging = false;
            this.chargeCooldown = 400;
         }

         double dist = this.m_20270_(target);
         if (dist < 4.5 + maxAttackMod && this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 15) {
            target.m_147240_(1.0, target.m_20185_() - this.m_20185_(), target.m_20189_() - this.m_20189_());
            target.m_20256_(target.m_20184_().m_82520_(0.0, 0.3F, 0.0));
            this.launch(target, false);
            target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
         }

         if (dist < 4.5 + maxAttackMod && this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 17) {
            target.m_147240_(0.3F, target.m_20185_() - this.m_20185_(), target.m_20189_() - this.m_20189_());
            target.m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
         }
      }

      if (!this.m_9236_().f_46443_ && this.m_5448_() == null && this.m_6688_() == null) {
         this.charging = false;
      }

      if (this.charging && !this.hasChargedSpeed) {
         this.m_21051_(Attributes.f_22279_).m_22100_(0.65F);
         this.hasChargedSpeed = true;
      }

      if (!this.charging && this.hasChargedSpeed) {
         this.m_21051_(Attributes.f_22279_).m_22100_(0.35F);
         this.hasChargedSpeed = false;
      }

      if (!this.m_9236_().f_46443_ && this.m_217043_().m_188503_(400) == 0 && this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(this.m_217043_().m_188499_() ? ANIMATION_TRUMPET_0 : ANIMATION_TRUMPET_1);
      }

      if (this.getAnimation() == ANIMATION_TRUMPET_0 && this.getAnimationTick() == 8
         || this.getAnimation() == ANIMATION_TRUMPET_1 && this.getAnimationTick() == 4) {
         this.m_146850_(GameEvent.f_223709_);
         this.m_5496_((SoundEvent)AMSoundRegistry.ELEPHANT_TRUMPET.get(), this.m_6121_(), this.m_6100_());
      }

      if (this.m_6084_() && this.charging) {
         for (Entity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(1.0))) {
            if ((!this.m_21824_() || !this.m_7307_(entity)) && (this.m_21824_() || !(entity instanceof EntityElephant)) && entity != this) {
               entity.m_6469_(this.m_269291_().m_269333_(this), 8.0F + this.f_19796_.m_188501_() * 8.0F);
               this.launch(entity, true);
            }
         }

         this.m_274367_(2.0F);
      } else {
         this.m_274367_(1.1F);
      }

      if (!this.m_21824_() && this.isTrader() && !this.m_9236_().f_46443_) {
         this.tryDespawn();
      }

      if (this.m_5448_() != null && !this.m_5448_().m_6084_()) {
         this.m_6710_(null);
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void m_8107_() {
      super.m_8107_();
      if (this.m_6162_() && this.m_20192_() > this.m_20206_()) {
         this.m_6210_();
      }
   }

   private boolean canDespawn() {
      return !this.m_21824_() && this.isTrader();
   }

   private void tryDespawn() {
      if (this.canDespawn()) {
         if (this.getControllingVillager() instanceof WanderingTrader) {
            int riderDelay = ((WanderingTrader)this.getControllingVillager()).m_35876_();
            if (riderDelay > 0) {
               this.despawnDelay = riderDelay;
            }
         }

         this.despawnDelay--;
         if (this.despawnDelay <= 0) {
            this.m_21455_(true, false);
            this.elephantInventory.m_6211_();
            if (this.getControllingVillager() != null) {
               this.getControllingVillager().m_142687_(RemovalReason.DISCARDED);
            }

            this.m_142687_(RemovalReason.DISCARDED);
         }
      }
   }

   private void launch(Entity e, boolean huge) {
      if (e.m_20096_()) {
         double d0 = e.m_20185_() - this.m_20185_();
         double d1 = e.m_20189_() - this.m_20189_();
         double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
         float f = huge ? 2.0F : 0.5F;
         e.m_5997_(d0 / d2 * f, huge ? 0.5 : 0.2F, d1 / d2 * f);
      }
   }

   private void eatItemEffect(ItemStack heldItemMainhand) {
      this.m_146850_(GameEvent.f_157806_);
      this.m_5496_(SoundEvents.f_12465_, this.m_6100_(), this.m_6121_());

      for (int i = 0; i < 8 + this.f_19796_.m_188503_(3); i++) {
         double d2 = this.f_19796_.m_188583_() * 0.02;
         double d0 = this.f_19796_.m_188583_() * 0.02;
         double d1 = this.f_19796_.m_188583_() * 0.02;
         float radius = this.m_20205_() * 0.65F;
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         ParticleOptions data = new ItemParticleOption(ParticleTypes.f_123752_, heldItemMainhand);
         if (heldItemMainhand.m_41720_() instanceof BlockItem) {
            data = new BlockParticleOption(ParticleTypes.f_123794_, ((BlockItem)heldItemMainhand.m_41720_()).m_40614_().m_49966_());
         }

         this.m_9236_().m_7106_(data, this.m_20185_() + extraX, this.m_20186_() + this.m_20206_() * 0.6F, this.m_20189_() + extraZ, d0, d1, d2);
      }
   }

   private boolean isChargePlayer(Entity controllingPassenger) {
      return true;
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION && !this.charging) {
         this.setAnimation(this.f_19796_.m_188499_() ? ANIMATION_FLING : ANIMATION_STOMP);
      }

      return true;
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      boolean owner = this.m_21824_() && this.m_21830_(player);
      InteractionResult type = super.m_6071_(player, hand);
      if (this.isChested() && player.m_6144_()) {
         this.openGUI(player);
         return InteractionResult.SUCCESS;
      } else if (this.canTargetItem(stack) && this.m_21205_().m_41619_()) {
         ItemStack rippedStack = stack.m_41777_();
         rippedStack.m_41764_(1);
         stack.m_41774_(1);
         this.m_21008_(InteractionHand.MAIN_HAND, rippedStack);
         if (rippedStack.m_204117_(AMTagRegistry.ELEPHANT_TAMEABLES)) {
            this.blossomThrowerUUID = player.m_20148_();
         }

         return InteractionResult.SUCCESS;
      } else if (owner && stack.m_204117_(ItemTags.f_215867_)) {
         DyeColor color = getCarpetColor(stack);
         if (color != this.getColor()) {
            if (this.getColor() != null) {
               this.m_19998_(this.getCarpetItemBeingWorn());
            }

            this.m_146850_(GameEvent.f_223708_);
            this.m_5496_(SoundEvents.f_12100_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
            if (!this.m_9236_().f_46443_ && player instanceof ServerPlayer serverPlayer) {
               AMAdvancementTriggerRegistry.ELEPHANT_SWAG.trigger(serverPlayer);
            }

            stack.m_41774_(1);
            this.setColor(color);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else if (owner && this.getColor() != null && stack.m_204117_(net.minecraftforge.common.Tags.Items.SHEARS)) {
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12344_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
         if (this.getColor() != null) {
            this.m_19998_(this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
         return InteractionResult.SUCCESS;
      } else if (owner && !this.isChested() && stack.m_204117_(net.minecraftforge.common.Tags.Items.CHESTS_WOODEN)) {
         this.setChested(true);
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_11811_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
         if (!player.m_150110_().f_35937_) {
            stack.m_41774_(1);
         }

         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      } else if (owner && this.isChested() && stack.m_204117_(net.minecraftforge.common.Tags.Items.SHEARS)) {
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12344_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
         this.m_19998_(Blocks.f_50087_);

         for (int i = 0; i < this.elephantInventory.m_6643_(); i++) {
            this.m_19983_(this.elephantInventory.m_8020_(i));
         }

         this.elephantInventory.m_6211_();
         this.setChested(false);
         return InteractionResult.SUCCESS;
      } else if (owner && !this.m_6162_() && type != InteractionResult.CONSUME) {
         if (!this.m_9236_().f_46443_) {
            player.m_20329_(this);
         }

         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isTusked() && !this.m_6162_() ? TUSKED_SIZE : super.m_6972_(poseIn);
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{
         ANIMATION_TRUMPET_0, ANIMATION_TRUMPET_1, ANIMATION_CHARGE_PREPARE, ANIMATION_STOMP, ANIMATION_FLING, ANIMATION_EAT, ANIMATION_BREAKLEAVES
      };
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Item getCarpetItemBeingWorn() {
      return this.getColor() != null ? DYE_COLOR_ITEM_MAP.get(this.getColor()) : Items.f_41852_;
   }

   protected void m_5907_() {
      super.m_5907_();
      if (this.isChested()) {
         if (!this.m_9236_().f_46443_) {
            this.m_19998_(Blocks.f_50087_);
         }

         for (int i = 0; i < this.elephantInventory.m_6643_(); i++) {
            this.m_19983_(this.elephantInventory.m_8020_(i));
         }

         this.elephantInventory.m_6211_();
         this.setChested(false);
      }

      if (!this.isTrader() && this.getColor() != null) {
         if (!this.m_9236_().f_46443_) {
            this.m_19998_(this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
      }
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      EntityElephant baby = (EntityElephant)((EntityType)AMEntityRegistry.ELEPHANT.get()).m_20615_(serverWorld);
      baby.setTusked(this.getNearestTusked(this.m_9236_(), 15.0) == null || this.f_19796_.m_188503_(2) == 0);
      return baby;
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Tusked", this.isTusked());
      compound.m_128379_("ElephantSitting", this.isSitting());
      compound.m_128379_("Standing", this.isStanding());
      compound.m_128379_("Chested", this.isChested());
      compound.m_128379_("Trader", this.isTrader());
      compound.m_128379_("ForcedToSit", this.forcedSit);
      compound.m_128379_("Tamed", this.m_21824_());
      compound.m_128405_("ChargeCooldown", this.chargeCooldown);
      compound.m_128405_("Carpet", (Integer)this.f_19804_.m_135370_(CARPET_COLOR));
      compound.m_128405_("DespawnDelay", this.despawnDelay);
      if (this.elephantInventory != null) {
         ListTag nbttaglist = new ListTag();

         for (int i = 0; i < this.elephantInventory.m_6643_(); i++) {
            ItemStack itemstack = this.elephantInventory.m_8020_(i);
            if (!itemstack.m_41619_()) {
               CompoundTag CompoundNBT = new CompoundTag();
               CompoundNBT.m_128344_("Slot", (byte)i);
               itemstack.m_41739_(CompoundNBT);
               nbttaglist.add(CompoundNBT);
            }
         }

         compound.m_128365_("Items", nbttaglist);
      }
   }

   public boolean m_7301_(MobEffectInstance potioneffectIn) {
      return potioneffectIn.m_19544_() == MobEffects.f_19615_ ? false : super.m_7301_(potioneffectIn);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_7105_(compound.m_128471_("Tamed"));
      this.setTusked(compound.m_128471_("Tusked"));
      this.setStanding(compound.m_128471_("Standing"));
      this.m_21839_(compound.m_128471_("ElephantSitting"));
      this.setChested(compound.m_128471_("Chested"));
      this.setTrader(compound.m_128471_("Trader"));
      this.forcedSit = compound.m_128471_("ForcedToSit");
      this.chargeCooldown = compound.m_128451_("ChargeCooldown");
      this.f_19804_.m_135381_(CARPET_COLOR, compound.m_128451_("Carpet"));
      if (this.elephantInventory != null) {
         ListTag nbttaglist = compound.m_128437_("Items", 10);
         this.initElephantInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = nbttaglist.m_128728_(i);
            int j = CompoundNBT.m_128445_("Slot") & 255;
            this.elephantInventory.m_6836_(j, ItemStack.m_41712_(CompoundNBT));
         }
      } else {
         ListTag nbttaglist = compound.m_128437_("Items", 10);
         this.initElephantInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = nbttaglist.m_128728_(i);
            int j = CompoundNBT.m_128445_("Slot") & 255;
            this.initElephantInventory();
            this.elephantInventory.m_6836_(j, ItemStack.m_41712_(CompoundNBT));
         }
      }

      if (compound.m_128425_("DespawnDelay", 99)) {
         this.despawnDelay = compound.m_128451_("DespawnDelay");
      }
   }

   public boolean isChested() {
      return Boolean.valueOf((Boolean)this.f_19804_.m_135370_(CHESTED));
   }

   public void setChested(boolean chested) {
      this.f_19804_.m_135381_(CHESTED, chested);
      this.hasChestVarChanged = true;
   }

   public boolean setSlot(int inventorySlot, @Nullable ItemStack itemStackIn) {
      int j = inventorySlot - 500 + 2;
      if (j >= 0 && j < this.elephantInventory.m_6643_()) {
         this.elephantInventory.m_6836_(j, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public void m_6667_(DamageSource cause) {
      super.m_6667_(cause);
      if (this.elephantInventory != null && !this.m_9236_().f_46443_) {
         for (int i = 0; i < this.elephantInventory.m_6643_(); i++) {
            ItemStack itemstack = this.elephantInventory.m_8020_(i);
            if (!itemstack.m_41619_()) {
               this.m_5552_(itemstack, 0.0F);
            }
         }
      }
   }

   public boolean isStanding() {
      return (Boolean)this.f_19804_.m_135370_(STANDING);
   }

   public void setStanding(boolean standing) {
      this.f_19804_.m_135381_(STANDING, standing);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   @Nullable
   public DyeColor getColor() {
      int lvt_1_1_ = (Integer)this.f_19804_.m_135370_(CARPET_COLOR);
      return lvt_1_1_ == -1 ? null : DyeColor.m_41053_(lvt_1_1_);
   }

   public void setColor(@Nullable DyeColor color) {
      this.f_19804_.m_135381_(CARPET_COLOR, color == null ? -1 : color.m_41060_());
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (spawnDataIn instanceof AgeableMobGroupData lvt_6_1_) {
         if (lvt_6_1_.m_146777_() == 0) {
            this.setTusked(true);
         }
      } else {
         this.setTusked(this.m_217043_().m_188499_());
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   @Nullable
   public EntityElephant getNearestTusked(LevelAccessor world, double dist) {
      List<? extends EntityElephant> list = world.m_45976_(this.getClass(), this.m_20191_().m_82377_(dist, dist / 2.0, dist));
      if (list.isEmpty()) {
         return null;
      } else {
         EntityElephant elephant1 = null;
         double d0 = Double.MAX_VALUE;

         for (EntityElephant elephant : list) {
            if (elephant.isTusked()) {
               double d1 = this.m_20280_(elephant);
               if (!(d1 > d0)) {
                  d0 = d1;
                  elephant1 = elephant;
               }
            }
         }

         return elephant1;
      }
   }

   public boolean isTusked() {
      return (Boolean)this.f_19804_.m_135370_(TUSKED);
   }

   public void setTusked(boolean tusked) {
      boolean prev = this.isTusked();
      if (!prev && tusked) {
         this.m_21051_(Attributes.f_22276_).m_22100_(110.0);
         this.m_21051_(Attributes.f_22281_).m_22100_(15.0);
         this.m_21153_(150.0F);
      } else {
         this.m_21051_(Attributes.f_22276_).m_22100_(85.0);
         this.m_21051_(Attributes.f_22281_).m_22100_(10.0);
      }

      this.f_19804_.m_135381_(TUSKED, tusked);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.ELEPHANT_FOODSTUFFS);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.m_32055_().m_41777_();
      duplicate.m_41764_(1);
      if (!this.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && !this.m_9236_().f_46443_) {
         this.m_5552_(this.m_21120_(InteractionHand.MAIN_HAND), 0.0F);
      }

      Entity itemThrower = e.m_19749_();
      if (duplicate.m_204117_(AMTagRegistry.ELEPHANT_TAMEABLES) && itemThrower != null) {
         this.blossomThrowerUUID = itemThrower.m_20148_();
      } else {
         this.blossomThrowerUUID = null;
      }

      this.m_21008_(InteractionHand.MAIN_HAND, duplicate);
      this.aiItemFlag = false;
   }

   @Override
   public void onFindTarget(ItemEntity e) {
      this.aiItemFlag = true;
   }

   public void addElephantLoot(@Nullable Player player, int seed) {
      if (this.m_9236_().m_7654_() != null) {
         LootTable loottable = this.m_9236_().m_7654_().m_278653_().m_278676_(TRADER_LOOT);
         net.minecraft.world.level.storage.loot.LootParams.Builder lootcontext$builder = new net.minecraft.world.level.storage.loot.LootParams.Builder(
            (ServerLevel)this.m_9236_()
         );
         loottable.m_287188_(this.elephantInventory, lootcontext$builder.m_287235_(LootContextParamSets.f_81410_), seed);
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(EntityElephant caravanHeadIn) {
      this.caravanHead = caravanHeadIn;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public EntityElephant getCaravanHead() {
      return this.caravanHead;
   }

   @Override
   public double getMaxDistToItem() {
      return Math.pow(this.m_20205_() + 3.0F, 2.0);
   }

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (this.m_20363_(passenger)) {
         float standAdd = -0.3F * this.standProgress;
         float scale = this.m_6162_() ? 0.5F : (this.isTusked() ? 1.1F : 1.0F);
         float sitAdd = -0.065F * this.sitProgress;
         float scaleY = scale * (2.4F * sitAdd - 0.4F * standAdd);
         if (passenger instanceof AbstractVillager villager) {
            scaleY -= 0.3F;
         }

         float radius = scale * (0.5F + standAdd);
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            float sinWave = Mth.m_14031_((float)(Math.PI * (this.getAnimationTick() / 25.0F)));
            radius += sinWave * 0.2F * scale;
         }

         if (this.getAnimation() == ANIMATION_STOMP) {
            float sinWave = Mth.m_14031_((float)(Math.PI * (this.getAnimationTick() / 20.0F)));
            radius -= sinWave * 1.0F * scale;
            scaleY += sinWave * 0.7F * scale;
         }

         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + this.m_6048_() + scaleY + passenger.m_6049_(), this.m_20189_() + extraZ);
      }
   }

   protected Vec3 m_274312_(Player player, Vec3 deltaIn) {
      if (player.f_20902_ != 0.0F) {
         float f = player.f_20902_ < 0.0F ? 0.5F : 1.0F;
         return new Vec3(player.f_20900_ * 0.25F, 0.0, player.f_20902_ * 0.5F * f);
      } else {
         this.m_6858_(false);
         return Vec3.f_82478_;
      }
   }

   protected void m_274498_(Player player, Vec3 vec3) {
      super.m_274498_(player, vec3);
      if (player.f_20902_ != 0.0F || player.f_20900_ != 0.0F) {
         this.m_19915_(player.m_146908_(), player.m_146909_() * 0.25F);
         this.f_19859_ = this.f_20883_ = this.f_20885_ = this.m_146908_();
         this.m_274367_(1.0F);
         this.m_21573_().m_26573_();
         this.m_6710_(null);
         this.m_6858_(true);
      }
   }

   protected float m_245547_(Player rider) {
      return (float)this.m_21133_(Attributes.f_22279_);
   }

   public double m_6048_() {
      float scale = this.m_6162_() ? 0.5F : (this.isTusked() ? 1.1F : 1.0F);
      float f = Math.min(0.25F, this.f_267362_.m_267731_());
      float f1 = this.f_267362_.m_267756_();
      float sitAdd = 0.0F;
      float standAdd = 0.0F;
      return this.m_20206_() - 0.05F - scale * ((double)(0.1F * Mth.m_14089_(f1 * 1.4F) * 1.4F * f) + sitAdd + standAdd);
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

   public void m_7023_(Vec3 vec3d) {
      if (this.isSitting()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         vec3d = Vec3.f_82478_;
      }

      super.m_7023_(vec3d);
   }

   public void openGUI(Player playerEntity) {
      if (!this.m_9236_().f_46443_ && !this.m_20363_(playerEntity)) {
         NetworkHooks.openScreen((ServerPlayer)playerEntity, new MenuProvider() {
            public AbstractContainerMenu m_7208_(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
               return ChestMenu.m_39246_(p_createMenu_1_, p_createMenu_2_, EntityElephant.this.elephantInventory);
            }

            public Component m_5446_() {
               return Component.m_237115_("entity.alexsmobs.elephant.chest");
            }
         });
      }
   }

   public boolean isTrader() {
      return (Boolean)this.f_19804_.m_135370_(TRADER);
   }

   public void setTrader(boolean trader) {
      this.f_19804_.m_135381_(TRADER, trader);
   }

   public boolean triggerCharge(ItemStack stack) {
      if (this.m_6688_() != null && this.chargeCooldown == 0 && !this.charging && this.getAnimation() == NO_ANIMATION && this.isTusked()) {
         this.setAnimation(ANIMATION_CHARGE_PREPARE);
         this.eatItemEffect(stack);
         this.m_5634_(2.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean canSpawnWithTraderHere() {
      return this.m_9236_().m_46749_(this.m_20183_()) && this.m_6914_(this.m_9236_()) && this.m_9236_().m_46859_(this.m_20183_().m_6630_(4));
   }

   private class AIWalkIdle extends RandomStrollGoal {
      public AIWalkIdle(EntityElephant e, double v) {
         super(e, v);
      }

      public boolean m_8036_() {
         this.f_25730_ = !EntityElephant.this.isTusked() && EntityElephant.this.inCaravan() ? 120 : 50;
         return super.m_8036_();
      }

      @Nullable
      protected Vec3 m_7037_() {
         return LandRandomPos.m_148488_(this.f_25725_, !EntityElephant.this.isTusked() && EntityElephant.this.inCaravan() ? 10 : 25, 7);
      }
   }

   class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(EntityElephant.this, new Class[0]);
      }

      public void m_8056_() {
         if (!EntityElephant.this.m_6162_() && EntityElephant.this.isTusked()) {
            super.m_8056_();
         } else {
            this.m_26047_();
            this.m_8041_();
         }
      }

      protected void m_5766_(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityElephant && (!mobIn.m_6162_() || !((EntityElephant)mobIn).isTusked())) {
            super.m_5766_(mobIn, targetIn);
         }
      }
   }

   class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
      public PanicGoal() {
         super(EntityElephant.this, 1.0);
      }

      public boolean m_8036_() {
         return (EntityElephant.this.m_6162_() || !EntityElephant.this.isTusked() || EntityElephant.this.m_6060_()) && super.m_8036_();
      }
   }
}
