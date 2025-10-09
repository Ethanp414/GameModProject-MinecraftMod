package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILootChests;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.ILootsChests;
import com.github.alexthe666.alexsmobs.entity.ai.RaccoonAIBeg;
import com.github.alexthe666.alexsmobs.entity.ai.RaccoonAIWash;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIDestroyTurtleEggs;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;

public class EntityRaccoon extends TamableAnimal implements IAnimatedEntity, IFollower, ITargetsDroppedItems, ILootsChests {
   private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> WASHING = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Optional<BlockPos>> WASH_POS = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135039_);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> CARPET_COLOR = SynchedEntityData.m_135353_(EntityRaccoon.class, EntityDataSerializers.f_135028_);
   public float prevStandProgress;
   public float standProgress;
   public float prevBegProgress;
   public float begProgress;
   public float prevWashProgress;
   public float washProgress;
   public float prevSitProgress;
   public float sitProgress;
   public int maxStandTime = 75;
   private int standingTime = 0;
   private int stealCooldown = 0;
   public int lookForWaterBeforeEatingTimer = 0;
   private int animationTick;
   private Animation currentAnimation;
   private int pickupItemCooldown = 0;
   @Nullable
   private UUID eggThrowerUUID = null;
   public boolean forcedSit = false;
   public static final Animation ANIMATION_ATTACK = Animation.create(12);
   private static final TargetingConditions VILLAGER_STEAL_PREDICATE = TargetingConditions.m_148352_().m_26883_(20.0).m_148355_();
   private static final TargetingConditions IRON_GOLEM_PREDICATE = TargetingConditions.m_148352_().m_26883_(20.0).m_148355_();

   protected EntityRaccoon(EntityType type, Level world) {
      super(type, world);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
   }

   protected float m_6108_() {
      return 0.98F;
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.RACCOON_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.RACCOON_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.RACCOON_HURT.get();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.raccoonSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(1, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(2, new RaccoonAIWash(this));
      this.f_21345_.m_25352_(3, new TameableAIFollowOwner(this, 1.3, 10.0F, 2.0F, false));
      this.f_21345_.m_25352_(4, new FloatGoal(this));
      this.f_21345_.m_25352_(5, new LeapAtTargetGoal(this, 0.4F));
      this.f_21345_.m_25352_(6, new MeleeAttackGoal(this, 1.1, true));
      this.f_21345_.m_25352_(7, new AnimalAILootChests(this, 16));
      this.f_21345_.m_25352_(8, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(9, new RaccoonAIBeg(this, 0.65));
      this.f_21345_.m_25352_(10, new AnimalAIPanicBaby(this, 1.25));
      this.f_21345_.m_25352_(11, new EntityRaccoon.AIStealFromVillagers(this));
      this.f_21345_.m_25352_(12, new EntityRaccoon.StrollGoal(200));
      this.f_21345_.m_25352_(13, new TameableAIDestroyTurtleEggs(this, 1.0, 3));
      this.f_21345_.m_25352_(14, new AnimalAIWanderRanged(this, 120, 1.0, 14, 7));
      this.f_21345_.m_25352_(15, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(15, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this));
      this.f_21346_.m_25352_(1, new CreatureAITargetItems(this, false));
      this.f_21346_.m_25352_(3, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(4, new OwnerHurtTargetGoal(this));
   }

   public boolean m_7307_(Entity entityIn) {
      if (!(entityIn instanceof EntityBlueJay jay)) {
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
      } else {
         return jay.getRaccoonUUID() != null && jay.getRaccoonUUID().equals(this.m_20148_());
      }
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   protected void m_5907_() {
      super.m_5907_();
      if (this.getColor() != null) {
         if (!this.m_9236_().f_46443_) {
            this.m_19998_(this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
      }
   }

   @Nullable
   public DyeColor getColor() {
      int lvt_1_1_ = (Integer)this.f_19804_.m_135370_(CARPET_COLOR);
      return lvt_1_1_ == -1 ? null : DyeColor.m_41053_(lvt_1_1_);
   }

   public void setColor(@Nullable DyeColor color) {
      this.f_19804_.m_135381_(CARPET_COLOR, color == null ? -1 : color.m_41060_());
   }

   public Item getCarpetItemBeingWorn() {
      return this.getColor() != null ? EntityElephant.DYE_COLOR_ITEM_MAP.get(this.getColor()) : Items.f_41852_;
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.RACCOON_BREEDABLES);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      boolean owner = this.m_21824_() && this.m_21830_(player);
      if (itemstack.m_204117_(AMTagRegistry.RACCOON_TEAMING_FOODS) && this.bondWithBlueJays(player.m_20148_())) {
         this.m_142075_(player, hand, itemstack);
         this.m_9236_().m_7605_(this, (byte)93);
         return InteractionResult.SUCCESS;
      } else if (this.m_21824_() && !this.m_21205_().m_41619_()) {
         if (!this.m_9236_().f_46443_) {
            this.m_19983_(this.m_21205_().m_41777_());
         }

         this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
         this.pickupItemCooldown = 60;
         return InteractionResult.SUCCESS;
      } else if (owner && itemstack.m_204117_(ItemTags.f_215867_)) {
         DyeColor color = EntityElephant.getCarpetColor(itemstack);
         if (color != this.getColor()) {
            if (this.getColor() != null) {
               this.m_19998_(this.getCarpetItemBeingWorn());
            }

            this.m_146850_(GameEvent.f_223708_);
            this.m_5496_(SoundEvents.f_12100_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
            itemstack.m_41774_(1);
            this.setColor(color);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else if (owner && this.getColor() != null && itemstack.m_204117_(net.minecraftforge.common.Tags.Items.SHEARS)) {
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12344_, 1.0F, (this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F);
         if (this.getColor() != null) {
            this.m_19998_(this.getCarpetItemBeingWorn());
         }

         this.setColor(null);
         return InteractionResult.SUCCESS;
      } else if (this.m_21824_() && isRaccoonFood(itemstack) && !this.m_6898_(itemstack) && this.m_21223_() < this.m_21233_()) {
         if (this.m_21205_().m_41619_()) {
            ItemStack copy = itemstack.m_41777_();
            copy.m_41764_(1);
            this.m_21008_(InteractionHand.MAIN_HAND, copy);
            this.onEatItem();
            if (itemstack.hasCraftingRemainingItem()) {
               this.m_19983_(itemstack.getCraftingRemainingItem());
            }

            if (!player.m_7500_()) {
               itemstack.m_41774_(1);
            }

            this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
         } else {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
            this.m_5634_(5.0F);
         }

         this.m_142075_(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult != InteractionResult.SUCCESS
            && type != InteractionResult.SUCCESS
            && this.m_21824_()
            && this.m_21830_(player)
            && !isRaccoonFood(itemstack)
            && !player.m_6144_()) {
            this.setCommand(this.getCommand() + 1);
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
         } else {
            return type;
         }
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("RacSitting", this.isSitting());
      compound.m_128379_("ForcedToSit", this.forcedSit);
      compound.m_128405_("RacCommand", this.getCommand());
      compound.m_128405_("Carpet", (Integer)this.f_19804_.m_135370_(CARPET_COLOR));
      compound.m_128405_("StealCooldown", this.stealCooldown);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_21839_(compound.m_128471_("RacSitting"));
      this.forcedSit = compound.m_128471_("ForcedToSit");
      this.setCommand(compound.m_128451_("RacCommand"));
      this.f_19804_.m_135381_(CARPET_COLOR, compound.m_128451_("Carpet"));
      this.stealCooldown = compound.m_128451_("StealCooldown");
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public boolean isSitting() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public static boolean isRaccoonFood(ItemStack stack) {
      return stack.m_41614_() || stack.m_204117_(AMTagRegistry.RACCOON_FOODSTUFFS);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 9.0).m_22268_(Attributes.f_22281_, 2.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         this.m_21839_(false);
         if (entity != null && this.m_21824_() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 4.0F;
         }

         return super.m_6469_(source, amount);
      }
   }

   protected void m_8022_() {
      boolean flag = !(this.m_6688_() instanceof Mob);
      boolean flag1 = !(this.m_20202_() instanceof Boat);
      boolean flag2 = this.m_146895_() instanceof EntityBlueJay;
      this.f_21345_.m_25360_(Flag.MOVE, flag || flag2);
      this.f_21345_.m_25360_(Flag.JUMP, flag && flag1 || flag2);
      this.f_21345_.m_25360_(Flag.LOOK, flag || flag2);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevStandProgress = this.standProgress;
      this.prevBegProgress = this.begProgress;
      this.prevWashProgress = this.washProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.isStanding()) {
         if (this.standProgress < 5.0F) {
            this.standProgress++;
         }
      } else if (this.standProgress > 0.0F) {
         this.standProgress--;
      }

      if (this.isBegging()) {
         if (this.begProgress < 5.0F) {
            this.begProgress++;
         }
      } else if (this.begProgress > 0.0F) {
         this.begProgress--;
      }

      if (this.isWashing()) {
         if (this.washProgress < 5.0F) {
            this.washProgress++;
         }
      } else if (this.washProgress > 0.0F) {
         this.washProgress--;
      }

      if (this.isSitting()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
         this.setStanding(false);
         this.standingTime = 0;
         this.maxStandTime = 75 + this.f_19796_.m_188503_(50);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.lookForWaterBeforeEatingTimer > 0) {
            this.lookForWaterBeforeEatingTimer--;
         } else if (!this.isWashing() && this.canTargetItem(this.m_21205_())) {
            this.onEatItem();
            if (this.m_21205_().hasCraftingRemainingItem()) {
               this.m_19983_(this.m_21205_().getCraftingRemainingItem());
            }

            this.m_21205_().m_41774_(1);
         }
      }

      if (this.isWashing() && this.getWashPos() != null) {
         BlockPos washingPos = this.getWashPos();
         if (this.m_20275_(washingPos.m_123341_() + 0.5, washingPos.m_123342_() + 0.5, washingPos.m_123343_() + 0.5) < 3.0) {
            for (int j = 0; j < 4.0F; j++) {
               double d2 = this.f_19796_.m_188500_();
               double d3 = this.f_19796_.m_188500_();
               Vec3 vector3d = this.m_20184_();
               this.m_9236_()
                  .m_7106_(
                     ParticleTypes.f_123769_,
                     washingPos.m_123341_() + d2,
                     washingPos.m_123342_() + 0.8F,
                     washingPos.m_123343_() + d3,
                     vector3d.f_82479_,
                     vector3d.f_82480_,
                     vector3d.f_82481_
                  );
            }
         } else {
            this.setWashing(false);
         }
      }

      if (!this.m_9236_().f_46443_
         && this.m_5448_() != null
         && this.m_142582_(this.m_5448_())
         && this.m_20270_(this.m_5448_()) < 4.0F
         && this.getAnimation() == ANIMATION_ATTACK
         && this.getAnimationTick() == 5) {
         float f1 = this.m_146908_() * (float) (Math.PI / 180.0);
         this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f1) * -0.06F, 0.0, Mth.m_14089_(f1) * -0.06F));
         this.m_5448_().m_147240_(0.35F, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
         this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21051_(Attributes.f_22281_).m_22115_());
      }

      if (this.stealCooldown > 0) {
         this.stealCooldown--;
      }

      if (this.pickupItemCooldown > 0) {
         this.pickupItemCooldown--;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public void onEatItem() {
      this.m_5634_(10.0F);
      this.m_9236_().m_7605_(this, (byte)92);
      this.m_146850_(GameEvent.f_157806_);
      this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
   }

   public void postWashItem(ItemStack stack) {
      if (stack.m_204117_(AMTagRegistry.RACCOON_TAMEABLES) && this.eggThrowerUUID != null && !this.m_21824_()) {
         if (this.m_217043_().m_188501_() < 0.3F) {
            this.m_7105_(true);
            this.m_21816_(this.eggThrowerUUID);
            Player player = this.m_9236_().m_46003_(this.eggThrowerUUID);
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.f_10590_.m_68829_((ServerPlayer)player, this);
            }

            this.m_9236_().m_7605_(this, (byte)7);
         } else {
            this.m_9236_().m_7605_(this, (byte)6);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 92) {
         for (int i = 0; i < 6 + this.f_19796_.m_188503_(3); i++) {
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
      } else if (id == 93) {
         for (int i = 0; i < 6 + this.f_19796_.m_188503_(3); i++) {
            double d2 = this.f_19796_.m_188583_() * 0.02;
            double d0 = this.f_19796_.m_188583_() * 0.02;
            double d1 = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, new ItemStack(Items.f_151079_)),
                  this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                  this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }
      } else {
         super.m_7822_(id);
      }
   }

   public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
      return true;
   }

   public boolean isStanding() {
      return (Boolean)this.f_19804_.m_135370_(STANDING);
   }

   public void setStanding(boolean standing) {
      this.f_19804_.m_135381_(STANDING, standing);
   }

   public boolean isBegging() {
      return (Boolean)this.f_19804_.m_135370_(BEGGING);
   }

   public void setBegging(boolean begging) {
      this.f_19804_.m_135381_(BEGGING, begging);
   }

   public boolean isWashing() {
      return (Boolean)this.f_19804_.m_135370_(WASHING);
   }

   public void setWashing(boolean washing) {
      this.f_19804_.m_135381_(WASHING, washing);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(STANDING, false);
      this.f_19804_.m_135372_(SITTING, false);
      this.f_19804_.m_135372_(BEGGING, false);
      this.f_19804_.m_135372_(WASHING, false);
      this.f_19804_.m_135372_(CARPET_COLOR, -1);
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(WASH_POS, Optional.empty());
   }

   public BlockPos getWashPos() {
      return (BlockPos)((Optional)this.f_19804_.m_135370_(WASH_POS)).orElse(null);
   }

   public void setWashPos(BlockPos washingPos) {
      this.f_19804_.m_135381_(WASH_POS, Optional.ofNullable(washingPos));
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
      if (animation == ANIMATION_ATTACK) {
         this.maxStandTime = 15;
         this.setStanding(true);
      }
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK};
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return (AgeableMob)((EntityType)AMEntityRegistry.RACCOON.get()).m_20615_(serverWorld);
   }

   public void m_7023_(Vec3 vec3d) {
      if (this.isSitting() || this.isWashing()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         vec3d = Vec3.f_82478_;
      }

      super.m_7023_(vec3d);
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return isRaccoonFood(stack) && this.pickupItemCooldown == 0;
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.lookForWaterBeforeEatingTimer = 100;
      ItemStack duplicate = e.m_32055_().m_41777_();
      duplicate.m_41764_(1);
      if (!this.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && !this.m_9236_().f_46443_) {
         this.m_5552_(this.m_21120_(InteractionHand.MAIN_HAND), 0.0F);
      }

      Entity thrower = e.m_19749_();
      if (e.m_32055_().m_204117_(AMTagRegistry.RACCOON_TEAMING_FOODS) && thrower != null && this.bondWithBlueJays(thrower.m_20148_())) {
         this.m_9236_().m_7605_(this, (byte)93);
      } else {
         this.m_21008_(InteractionHand.MAIN_HAND, duplicate);
      }

      if (e.m_32055_().m_204117_(AMTagRegistry.RACCOON_TAMEABLES) && thrower != null) {
         this.eggThrowerUUID = thrower.m_20148_();
      } else {
         this.eggThrowerUUID = null;
      }
   }

   public double m_6048_() {
      return this.m_20206_() * 0.45;
   }

   private boolean bondWithBlueJays(UUID uuid) {
      AABB allyBox = this.m_20191_().m_82400_(48.0);
      boolean any = false;

      for (EntityBlueJay entity : this.m_9236_().m_45976_(EntityBlueJay.class, allyBox)) {
         if (entity.getFeedTime() > 0 && entity.getLastFeederUUID() != null && entity.getLastFeederUUID().equals(uuid)) {
            entity.setRaccoon(this);
            entity.setFeedTime(0);
            any = true;
         }
      }

      return any;
   }

   @Override
   public boolean isLootable(Container inventory) {
      for (int i = 0; i < inventory.m_6643_(); i++) {
         if (this.shouldLootItem(inventory.m_8020_(i))) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean shouldLootItem(ItemStack stack) {
      return isRaccoonFood(stack);
   }

   public boolean isHoldingSugar() {
      return this.m_21205_().m_204117_(AMTagRegistry.RACOON_DISSOLVES);
   }

   public BlockPos getLightPosition() {
      BlockPos pos = AMBlockPos.fromVec3(this.m_20182_());
      return !this.m_9236_().m_8055_(pos).m_60815_() ? pos.m_7494_() : pos;
   }

   public boolean isRigby() {
      String name = ChatFormatting.m_126649_(this.m_7755_().getString());
      if (name == null) {
         return false;
      } else {
         String lowercaseName = name.toLowerCase(Locale.ROOT);
         return lowercaseName.contains("rigby");
      }
   }

   private class AIStealFromVillagers extends Goal {
      EntityRaccoon raccoon;
      AbstractVillager target;
      int golemCheckTime = 0;
      int cooldown = 0;
      int fleeTime = 0;

      private AIStealFromVillagers(EntityRaccoon raccoon) {
         this.raccoon = raccoon;
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         if (this.cooldown > 0) {
            this.cooldown--;
            return false;
         } else if (this.raccoon != null && this.raccoon.stealCooldown == 0 && this.raccoon.m_21205_() != null && this.raccoon.m_21205_().m_41619_()) {
            AbstractVillager villager = this.getNearbyVillagers();
            if (!this.isGolemNearby() && villager != null) {
               this.target = villager;
            }

            this.cooldown = 150;
            return this.target != null;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.target != null && this.raccoon != null;
      }

      public void m_8041_() {
         this.target = null;
         this.cooldown = 200 + EntityRaccoon.this.f_19796_.m_188503_(200);
         this.golemCheckTime = 0;
         this.fleeTime = 0;
      }

      public void m_8037_() {
         if (this.target != null) {
            this.golemCheckTime++;
            if (this.fleeTime > 0) {
               this.fleeTime--;
               if (this.raccoon.m_21573_().m_26571_()) {
                  Vec3 fleevec = DefaultRandomPos.m_148407_(this.raccoon, 16, 7, this.raccoon.m_20182_());
                  if (fleevec != null) {
                     this.raccoon.m_21573_().m_26519_(fleevec.f_82479_, fleevec.f_82480_, fleevec.f_82481_, 1.3F);
                  }
               }

               if (this.fleeTime == 0) {
                  this.m_8041_();
               }
            } else {
               this.raccoon.m_21573_().m_5624_(this.target, 1.0);
               if (this.raccoon.m_20270_(this.target) < 1.7F) {
                  this.raccoon.setStanding(true);
                  this.raccoon.maxStandTime = 15;
                  MerchantOffers offers = this.target.m_6616_();
                  if (offers != null && !offers.isEmpty() && offers.size() >= 1) {
                     MerchantOffer offer = (MerchantOffer)offers.get(offers.size() <= 1 ? 0 : this.raccoon.m_217043_().m_188503_(offers.size() - 1));
                     if (offer != null) {
                        ItemStack stealStack = offer.m_45368_().m_41720_() == Items.f_42616_ ? offer.m_45352_() : offer.m_45368_();
                        if (stealStack.m_41619_()) {
                           this.m_8041_();
                        } else {
                           offer.m_45374_();
                           ItemStack copy = stealStack.m_41777_();
                           copy.m_41764_(1);
                           this.raccoon.m_21008_(InteractionHand.MAIN_HAND, copy);
                           this.fleeTime = 60 + EntityRaccoon.this.f_19796_.m_188503_(60);
                           this.raccoon.m_21573_().m_26573_();
                           EntityRaccoon.this.lookForWaterBeforeEatingTimer = 120 + EntityRaccoon.this.f_19796_.m_188503_(60);
                           this.target.m_6469_(EntityRaccoon.this.m_269291_().m_269264_(), 0.0F);
                           this.raccoon.stealCooldown = 24000 + EntityRaccoon.this.f_19796_.m_188503_(48000);
                        }
                     }
                  } else {
                     this.m_8041_();
                  }
               }

               if (this.golemCheckTime % 30 == 0 && EntityRaccoon.this.f_19796_.m_188499_() && this.isGolemNearby()) {
                  this.m_8041_();
               }
            }
         }
      }

      @Nullable
      private boolean isGolemNearby() {
         List<IronGolem> lvt_1_1_ = this.raccoon
            .m_9236_()
            .m_45971_(IronGolem.class, EntityRaccoon.IRON_GOLEM_PREDICATE, this.raccoon, this.raccoon.m_20191_().m_82400_(25.0));
         return !lvt_1_1_.isEmpty();
      }

      @Nullable
      private AbstractVillager getNearbyVillagers() {
         List<AbstractVillager> lvt_1_1_ = this.raccoon
            .m_9236_()
            .m_45971_(AbstractVillager.class, EntityRaccoon.VILLAGER_STEAL_PREDICATE, this.raccoon, this.raccoon.m_20191_().m_82400_(20.0));
         double lvt_2_1_ = 10000.0;
         AbstractVillager lvt_4_1_ = null;

         for (AbstractVillager lvt_6_1_ : lvt_1_1_) {
            if (lvt_6_1_.m_21223_() > 2.0F && !lvt_6_1_.m_6616_().isEmpty() && this.raccoon.m_20280_(lvt_6_1_) < lvt_2_1_) {
               lvt_4_1_ = lvt_6_1_;
               lvt_2_1_ = this.raccoon.m_20280_(lvt_6_1_);
            }
         }

         return lvt_4_1_;
      }
   }

   class StrollGoal extends MoveThroughVillageGoal {
      public StrollGoal(int p_i50726_3_) {
         super(EntityRaccoon.this, 1.0, true, p_i50726_3_, () -> false);
      }

      public void m_8056_() {
         super.m_8056_();
      }

      public boolean m_8036_() {
         return super.m_8036_() && this.canFoxMove();
      }

      public boolean m_8045_() {
         return super.m_8045_() && this.canFoxMove();
      }

      private boolean canFoxMove() {
         return !EntityRaccoon.this.isWashing() && !EntityRaccoon.this.isSitting() && EntityRaccoon.this.m_5448_() == null;
      }
   }
}
