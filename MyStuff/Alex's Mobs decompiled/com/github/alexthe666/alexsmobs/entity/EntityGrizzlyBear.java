package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIAprilFools;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIBeehive;
import com.github.alexthe666.alexsmobs.entity.ai.GrizzlyBearAIFleeBees;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
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
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityGrizzlyBear extends TamableAnimal implements NeutralMob, IAnimatedEntity, ITargetsDroppedItems, IFollower {
   public static final Animation ANIMATION_MAUL = Animation.create(20);
   public static final Animation ANIMATION_SNIFF = Animation.create(12);
   public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
   public static final Animation ANIMATION_SWIPE_L = Animation.create(20);
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HONEYED = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> APRIL_FOOLS_MODE = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityGrizzlyBear.class, EntityDataSerializers.f_135028_);
   private static final UniformInt angerLogic = TimeUtil.m_145020_(20, 39);
   public float prevStandProgress;
   public float prevSitProgress;
   public float standProgress;
   public float sitProgress;
   public int maxStandTime = 75;
   public boolean forcedSit = false;
   private int animationTick;
   private Animation currentAnimation;
   private int standingTime = 0;
   private int sittingTime = 0;
   private int maxSitTime = 75;
   private int eatingTime = 0;
   private int angerTime;
   private UUID angerTarget;
   private int honeyedTime;
   @Nullable
   private UUID salmonThrowerID = null;
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.m_204132_(AMTagRegistry.GORILLA_FOODSTUFFS);
   public int timeUntilNextFur = this.f_19796_.m_188503_(24000) + 24000;
   protected static final EntityDimensions STANDING_SIZE = EntityDimensions.m_20395_(1.7F, 2.75F);
   private boolean recalcSize = false;
   private int snowTimer = 0;
   private boolean permSnow = false;

   protected EntityGrizzlyBear(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 55.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22278_, 0.6F)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isStanding() ? STANDING_SIZE.m_20388_(this.m_6134_()) : super.m_6972_(poseIn);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.grizzlyBearSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         this.m_21839_(false);
         if (entity != null && this.m_21824_() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 3.0F;
         }

         return super.m_6469_(source, amount);
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_DIE.get();
   }

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (this.m_20363_(passenger)) {
         float sitAdd = -0.065F * this.sitProgress;
         float standAdd = -0.07F * this.standProgress;
         float radius = standAdd + sitAdd;
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + this.m_6048_() + passenger.m_6049_(), this.m_20189_() + extraZ);
      }
   }

   public double m_6048_() {
      float f = Math.min(0.25F, this.f_267362_.m_267731_());
      float f1 = this.f_267362_.m_267756_();
      float sitAdd = 0.01F * this.sitProgress;
      float standAdd = 0.07F * this.standProgress;
      return this.m_20206_() - 0.3 + 0.12F * Mth.m_14089_(f1 * 0.7F) * 0.7F * f + sitAdd + standAdd;
   }

   public void m_8032_() {
      if (!this.isFreddy()) {
         super.m_8032_();
      }
   }

   protected float m_6108_() {
      return this.m_20160_() ? 0.9F : 0.98F;
   }

   public void m_6825_() {
      this.m_7870_(angerLogic.m_214085_(this.f_19796_));
   }

   public int m_6784_() {
      return this.angerTime;
   }

   public void m_7870_(int time) {
      this.angerTime = time;
   }

   public UUID m_6120_() {
      return this.angerTarget;
   }

   public void m_6925_(@Nullable UUID target) {
      this.angerTarget = target;
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_19385_() != null && source.m_19385_().equals("sting") || source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(2, new TameableAIFollowOwner(this, 1.2, 5.0F, 2.0F, false));
      this.f_21345_.m_25352_(3, new GrizzlyBearAIAprilFools(this));
      this.f_21345_.m_25352_(4, new EntityGrizzlyBear.MeleeAttackGoal());
      this.f_21345_.m_25352_(4, new EntityGrizzlyBear.PanicGoal());
      this.f_21345_.m_25352_(5, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS, false));
      this.f_21345_.m_25352_(5, new FollowParentGoal(this, 1.25));
      this.f_21345_.m_25352_(5, new GrizzlyBearAIBeehive(this));
      this.f_21345_.m_25352_(6, new GrizzlyBearAIFleeBees(this, 14.0F, 1.0, 1.0));
      this.f_21345_.m_25352_(6, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(7, new RandomStrollGoal(this, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new OwnerHurtTargetGoal(this));
      this.f_21346_.m_25352_(3, new EntityGrizzlyBear.HurtByTargetGoal());
      this.f_21346_.m_25352_(4, new CreatureAITargetItems(this, false));
      this.f_21346_.m_25352_(5, new EntityGrizzlyBear.AttackPlayerGoal());
      this.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::m_21674_));
      this.f_21346_.m_25352_(7, new NonTameRandomTargetGoal(this, Fox.class, false, (Predicate)null));
      this.f_21346_.m_25352_(8, new NonTameRandomTargetGoal(this, Wolf.class, false, (Predicate)null));
      this.f_21346_.m_25352_(7, new ResetUniversalAngerTargetGoal(this, false));
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Honeyed", this.isHoneyed());
      compound.m_128379_("Snowy", this.isSnowy());
      compound.m_128379_("Standing", this.isStanding());
      compound.m_128379_("BearSitting", this.isSitting());
      compound.m_128379_("ForcedToSit", this.forcedSit);
      compound.m_128379_("SnowPerm", this.permSnow);
      compound.m_128405_("FurTime", this.timeUntilNextFur);
      compound.m_128405_("BearCommand", this.getCommand());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setHoneyed(compound.m_128471_("Honeyed"));
      this.setSnowy(compound.m_128471_("Snowy"));
      this.setStanding(compound.m_128471_("Standing"));
      this.m_21839_(compound.m_128471_("BearSitting"));
      this.setCommand(compound.m_128451_("BearCommand"));
      this.forcedSit = compound.m_128471_("ForcedToSit");
      this.permSnow = compound.m_128471_("SnowPerm");
      this.timeUntilNextFur = compound.m_128451_("FurTime");
   }

   public boolean m_6898_(ItemStack stack) {
      Item item = stack.m_41720_();
      return this.m_21824_() && stack.m_204117_(AMTagRegistry.GRIZZLY_BREEDABLES);
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 67) {
         AlexsMobs.PROXY.onEntityStatus(this, id);
      } else if (id == 68) {
         AlexsMobs.PROXY.spawnSpecialParticle(0);
      } else {
         super.m_7822_(id);
      }
   }

   @Nullable
   public LivingEntity m_6688_() {
      for (Entity passenger : this.m_20197_()) {
         if (passenger instanceof Player player) {
            return player;
         }
      }

      return null;
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (item == Items.f_41979_ && !this.isSnowy() && !this.m_9236_().f_46443_) {
         this.m_142075_(player, hand, itemstack);
         this.permSnow = true;
         this.setSnowy(true);
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12482_, this.m_6121_(), this.m_6100_());
         return InteractionResult.SUCCESS;
      } else if (item instanceof ShovelItem && this.isSnowy() && !this.m_9236_().f_46443_) {
         this.permSnow = false;
         if (!player.m_7500_()) {
            itemstack.m_220157_(1, this.m_217043_(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
         }

         this.setSnowy(false);
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12474_, this.m_6121_(), this.m_6100_());
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.m_21824_()
            || !this.m_21830_(player)
            || this.m_6898_(itemstack)) {
            return type;
         } else if (!player.m_6144_() && !this.m_6162_()) {
            player.m_20329_(this);
            return InteractionResult.SUCCESS;
         } else {
            this.setCommand((this.getCommand() + 1) % 3);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.m_5661_(Component.m_237110_("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.m_7755_()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.forcedSit = true;
               this.m_21839_(true);
               return InteractionResult.SUCCESS;
            } else {
               this.forcedSit = false;
               this.m_21839_(false);
               return InteractionResult.SUCCESS;
            }
         }
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

   public void m_7023_(Vec3 vec3d) {
      if (!this.shouldMove()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         vec3d = Vec3.f_82478_;
      }

      super.m_7023_(vec3d);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_6162_() || this.m_20192_() > this.m_20206_()) {
         this.m_6210_();
      }

      if (!this.isStanding() && this.m_20206_() >= 2.75F) {
         this.m_6210_();
      }

      this.prevStandProgress = this.standProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.isSitting()) {
         if (this.sitProgress < 10.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isStanding()) {
         if (this.standProgress < 10.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      if (!this.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && this.canTargetItem(this.m_21120_(InteractionHand.MAIN_HAND))) {
         this.setEating(true);
         this.m_21839_(true);
         this.setStanding(false);
      }

      if (this.recalcSize) {
         this.recalcSize = false;
         this.m_6210_();
      }

      if (this.isEating() && !this.canTargetItem(this.m_21120_(InteractionHand.MAIN_HAND))) {
         this.setEating(false);
         this.eatingTime = 0;
         if (!this.forcedSit) {
            this.m_21839_(true);
         }
      }

      if (this.isEating()) {
         this.eatingTime++;

         for (int i = 0; i < 3; i++) {
            double d2 = this.f_19796_.m_188583_() * 0.02;
            double d0 = this.f_19796_.m_188583_() * 0.02;
            double d1 = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, this.m_21120_(InteractionHand.MAIN_HAND)),
                  this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                  this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }

         if (this.eatingTime % 5 == 0) {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
         }

         if (this.eatingTime > 100) {
            ItemStack stack = this.m_21120_(InteractionHand.MAIN_HAND);
            if (!stack.m_41619_()) {
               if (stack.m_204117_(AMTagRegistry.GRIZZLY_HONEY)) {
                  this.setHoneyed(true);
                  this.m_5634_(10.0F);
                  this.honeyedTime = 700;
               } else {
                  this.m_5634_(4.0F);
               }

               if (stack.m_204117_(AMTagRegistry.GRIZZLY_TAMEABLES) && !this.m_21824_() && this.salmonThrowerID != null) {
                  if (this.m_217043_().m_188501_() < 0.3F) {
                     this.m_7105_(true);
                     this.m_21816_(this.salmonThrowerID);
                     Player player = this.m_9236_().m_46003_(this.salmonThrowerID);
                     if (player instanceof ServerPlayer) {
                        CriteriaTriggers.f_10590_.m_68829_((ServerPlayer)player, this);
                     }

                     this.m_9236_().m_7605_(this, (byte)7);
                  } else {
                     this.m_9236_().m_7605_(this, (byte)6);
                  }
               }

               if (stack.hasCraftingRemainingItem()) {
                  this.m_19983_(stack.getCraftingRemainingItem());
               }

               stack.m_41774_(1);
            }

            this.eatingTime = 0;
         }
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.f_19796_.m_188503_(50);
      }

      if (this.isSitting() && !this.forcedSit && ++this.sittingTime > this.maxSitTime) {
         this.m_21839_(false);
         this.sittingTime = 0;
         this.maxSitTime = 75 + this.f_19796_.m_188503_(50);
      }

      if (!this.m_9236_().f_46443_ && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && this.f_19796_.m_188503_(1500) == 0) {
         this.maxSitTime = 300 + this.f_19796_.m_188503_(250);
         this.m_21839_(true);
      }

      if (!this.forcedSit && this.isSitting() && (this.m_5448_() != null || this.isStanding()) && !this.isEating()) {
         this.m_21839_(false);
      }

      if (this.getAnimation() == NO_ANIMATION && this.getAprilFoolsFlag() < 1 && this.f_19796_.m_188503_(this.isStanding() ? 350 : 2500) == 0) {
         this.setAnimation(ANIMATION_SNIFF);
      }

      if (this.isSitting()) {
         this.m_21573_().m_26573_();
      }

      LivingEntity attackTarget = this.m_5448_();
      if (this.m_6688_() != null && this.m_6688_() instanceof Player) {
         Player rider = (Player)this.m_6688_();
         if (rider.m_21214_() != null && this.m_20270_(rider.m_21214_()) < this.m_20205_() + 3.0F && !this.m_7307_(rider.m_21214_())) {
            UUID preyUUID = rider.m_21214_().m_20148_();
            if (!this.m_20148_().equals(preyUUID)) {
               attackTarget = rider.m_21214_();
               if (this.getAnimation() == NO_ANIMATION || this.getAnimation() == ANIMATION_SNIFF) {
                  this.setAnimation(this.f_19796_.m_188499_() ? ANIMATION_MAUL : (this.f_19796_.m_188499_() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R));
               }
            }
         }
      }

      if (attackTarget != null) {
         if (!this.m_9236_().f_46443_) {
            this.m_6858_(true);
         }

         if (this.m_20270_(attackTarget) < attackTarget.m_20205_() + this.m_20205_() + 2.5F) {
            if (this.getAnimation() == ANIMATION_MAUL && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 3) {
               this.m_7327_(attackTarget);
            }

            if (this.getAnimation() == ANIMATION_SWIPE_L && this.getAnimationTick() == 7) {
               this.m_7327_(attackTarget);
               float rot = this.m_146908_() + 90.0F;
               attackTarget.m_147240_(0.5, Mth.m_14031_(rot * (float) (Math.PI / 180.0)), -Mth.m_14089_(rot * (float) (Math.PI / 180.0)));
            }

            if (this.getAnimation() == ANIMATION_SWIPE_R && this.getAnimationTick() == 7) {
               this.m_7327_(attackTarget);
               float rot = this.m_146908_() - 90.0F;
               attackTarget.m_147240_(0.5, Mth.m_14031_(rot * (float) (Math.PI / 180.0)), -Mth.m_14089_(rot * (float) (Math.PI / 180.0)));
            }
         }
      } else if (!this.m_9236_().f_46443_ && this.m_6688_() == null) {
         this.m_6858_(false);
      }

      if (!this.m_9236_().f_46443_ && this.isHoneyed() && --this.honeyedTime <= 0) {
         this.setHoneyed(false);
         this.honeyedTime = 0;
      }

      if (this.forcedSit && !this.m_20160_() && this.m_21824_()) {
         this.m_21839_(true);
      }

      if (this.m_20160_() && this.isSitting()) {
         this.m_21839_(false);
      }

      if (!this.m_9236_().f_46443_ && this.m_6084_() && this.m_21824_() && !this.m_6162_() && --this.timeUntilNextFur <= 0) {
         this.m_19998_((ItemLike)AMItemRegistry.BEAR_FUR.get());
         this.timeUntilNextFur = this.f_19796_.m_188503_(24000) + 24000;
      }

      if (this.snowTimer > 0) {
         this.snowTimer--;
      }

      if (this.snowTimer == 0 && !this.m_9236_().f_46443_) {
         this.snowTimer = 200 + this.f_19796_.m_188503_(400);
         if (this.isSnowy()) {
            if (!this.permSnow
               && (!this.m_9236_().f_46443_ || this.m_20094_() > 0 || this.m_20072_() || !isSnowingAt(this.m_9236_(), this.m_20183_().m_7494_()))) {
               this.setSnowy(false);
            }
         } else if (!this.m_9236_().f_46443_ && isSnowingAt(this.m_9236_(), this.m_20183_())) {
            this.setSnowy(true);
         }
      }

      if (this.isFreddy()) {
         this.setStanding(true);
         this.standingTime = 0;
         this.maxStandTime = 40;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public static boolean isSnowingAt(Level world, BlockPos position) {
      if (!world.m_46471_()) {
         return false;
      } else if (!world.m_45527_(position)) {
         return false;
      } else {
         return world.m_5452_(Types.MOTION_BLOCKING, position).m_123342_() > position.m_123342_()
            ? false
            : ((Biome)world.m_204166_(position).m_203334_()).m_264600_(position) == Precipitation.SNOW;
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

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(STANDING, false);
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(HONEYED, false);
      this.f_19804_.m_135372_(SNOWY, false);
      this.f_19804_.m_135372_(EATING, false);
      this.f_19804_.m_135372_(APRIL_FOOLS_MODE, 0);
      this.f_19804_.m_135372_(COMMAND, 0);
   }

   public boolean isEating() {
      return (Boolean)this.f_19804_.m_135370_(EATING);
   }

   public void setEating(boolean eating) {
      this.f_19804_.m_135381_(EATING, eating);
   }

   public boolean isHoneyed() {
      return (Boolean)this.f_19804_.m_135370_(HONEYED);
   }

   public void setHoneyed(boolean honeyed) {
      this.f_19804_.m_135381_(HONEYED, honeyed);
   }

   public boolean isSnowy() {
      return (Boolean)this.f_19804_.m_135370_(SNOWY);
   }

   public void setSnowy(boolean honeyed) {
      this.f_19804_.m_135381_(SNOWY, honeyed);
   }

   public boolean isStanding() {
      return (Boolean)this.f_19804_.m_135370_(STANDING);
   }

   public void setStanding(boolean standing) {
      this.f_19804_.m_135381_(STANDING, standing);
      this.recalcSize = true;
   }

   public int getAprilFoolsFlag() {
      return (Integer)this.f_19804_.m_135370_(APRIL_FOOLS_MODE);
   }

   public void setAprilFoolsFlag(int i) {
      this.f_19804_.m_135381_(APRIL_FOOLS_MODE, i);
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel world, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.GRIZZLY_BEAR.get()).m_20615_(world);
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
      if (animation == ANIMATION_MAUL) {
         this.maxStandTime = 21;
         this.setStanding(true);
      }

      if (animation == ANIMATION_SWIPE_R || animation == ANIMATION_SWIPE_L) {
         this.maxStandTime = 2 + this.f_19796_.m_188503_(5);
         this.setStanding(true);
      }
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_MAUL, ANIMATION_SNIFF, ANIMATION_SWIPE_R, ANIMATION_SWIPE_L};
   }

   public boolean shouldMove() {
      return !this.isSitting();
   }

   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (spawnDataIn == null) {
         spawnDataIn = new AgeableMobGroupData(1.0F);
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.GRIZZLY_FOODSTUFFS);
   }

   @Override
   public void onGetItem(ItemEntity targetEntity) {
      ItemStack duplicate = targetEntity.m_32055_().m_41777_();
      duplicate.m_41764_(1);
      if (!this.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && !this.m_9236_().f_46443_) {
         this.m_5552_(this.m_21120_(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.m_21008_(InteractionHand.MAIN_HAND, duplicate);
      Entity thrower = targetEntity.m_19749_();
      if (targetEntity.m_32055_().m_204117_(AMTagRegistry.GRIZZLY_TAMEABLES) && thrower != null && this.isHoneyed()) {
         this.salmonThrowerID = thrower.m_20148_();
      } else {
         this.salmonThrowerID = null;
      }
   }

   public boolean isEatingHeldItem() {
      return false;
   }

   public boolean isFreddy() {
      return this.getAprilFoolsFlag() > 1;
   }

   @Override
   public boolean shouldFollow() {
      return this.getAprilFoolsFlag() == 0 && this.getCommand() == 1;
   }

   class AttackPlayerGoal extends NearestAttackableTargetGoal<Player> {
      public AttackPlayerGoal() {
         super(EntityGrizzlyBear.this, Player.class, 3, true, true, null);
      }

      public boolean m_8036_() {
         return !EntityGrizzlyBear.this.m_6162_() && EntityGrizzlyBear.this.getAprilFoolsFlag() < 1 && !EntityGrizzlyBear.this.isHoneyed()
            ? super.m_8036_()
            : false;
      }

      protected double m_7623_() {
         return 5.0;
      }
   }

   class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
      public HurtByTargetGoal() {
         super(EntityGrizzlyBear.this, new Class[0]);
      }

      public void m_8056_() {
         super.m_8056_();
         if (EntityGrizzlyBear.this.m_6162_()) {
            this.m_26047_();
            this.m_8041_();
         }
      }

      protected void m_5766_(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityGrizzlyBear && !mobIn.m_6162_()) {
            super.m_5766_(mobIn, targetIn);
         }
      }
   }

   class MeleeAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
      public MeleeAttackGoal() {
         super(EntityGrizzlyBear.this, 1.25, true);
      }

      protected void m_6739_(LivingEntity enemy, double distToEnemySqr) {
         double d0 = this.m_6639_(enemy);
         if (distToEnemySqr <= d0
            && (
               EntityGrizzlyBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION
                  || EntityGrizzlyBear.this.getAnimation() == EntityGrizzlyBear.ANIMATION_SNIFF
            )) {
            EntityGrizzlyBear.this.setAnimation(
               EntityGrizzlyBear.this.f_19796_.m_188499_()
                  ? EntityGrizzlyBear.ANIMATION_MAUL
                  : (EntityGrizzlyBear.this.f_19796_.m_188499_() ? EntityGrizzlyBear.ANIMATION_SWIPE_L : EntityGrizzlyBear.ANIMATION_SWIPE_R)
            );
         }
      }

      public void m_8041_() {
         EntityGrizzlyBear.this.setStanding(false);
         super.m_8041_();
      }

      protected double m_6639_(LivingEntity attackTarget) {
         return 3.0F + attackTarget.m_20205_();
      }
   }

   class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
      public PanicGoal() {
         super(EntityGrizzlyBear.this, 2.0);
      }

      public boolean m_8036_() {
         return (EntityGrizzlyBear.this.m_6162_() || EntityGrizzlyBear.this.m_6060_()) && super.m_8036_();
      }
   }
}
