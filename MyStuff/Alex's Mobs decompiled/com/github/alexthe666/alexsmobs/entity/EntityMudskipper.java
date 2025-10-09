package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalSwimMoveControllerSink;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.MudskipperAIAttack;
import com.github.alexthe666.alexsmobs.entity.ai.MudskipperAIDisplay;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwnerWater;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class EntityMudskipper extends TamableAnimal implements IFollower, ISemiAquatic, Bucketable {
   public float prevSitProgress;
   public float sitProgress;
   public float prevSwimProgress;
   public float swimProgress;
   public float prevDisplayProgress;
   public float displayProgress;
   public float prevMudProgress;
   public float mudProgress;
   public float nextDisplayAngleFromServer;
   public float prevDisplayAngle;
   public boolean displayDirection;
   public int displayTimer = 0;
   public boolean instantlyTriggerDisplayAI = false;
   public int displayCooldown = 100 + this.f_19796_.m_188503_(100);
   private static final EntityDataAccessor<Boolean> DISPLAYING = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> DISPLAY_ANGLE = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> DISPLAYER_UUID = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> MOUTH_TICKS = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityMudskipper.class, EntityDataSerializers.f_135028_);
   private boolean isLandNavigator;
   private int swimTimer = -1000;

   public EntityMudskipper(EntityType type, Level level) {
      super(type, level);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.switchNavigator(true);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21827_()) {
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

   public static <T extends Mob> boolean canMudskipperSpawn(
      EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random
   ) {
      BlockState blockstate = worldIn.m_8055_(p_223317_3_.m_7495_());
      return blockstate.m_60713_(Blocks.f_220864_) || blockstate.m_60713_(Blocks.f_220834_);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mudskipperSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public boolean m_6914_(LevelReader worldIn) {
      BlockPos pos = AMBlockPos.fromCoords(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return !worldIn.m_8055_(pos).m_60828_(worldIn, pos);
   }

   public boolean m_6040_() {
      return true;
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(1, new TameableAIFollowOwnerWater(this, 1.3, 4.0F, 2.0F, false));
      this.f_21345_.m_25352_(2, new MudskipperAIAttack(this));
      this.f_21345_.m_25352_(3, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(3, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.MUDSKIPPER_TAMEABLES), false));
      this.f_21345_.m_25352_(5, new BreedGoal(this, 0.8));
      this.f_21345_.m_25352_(6, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(7, new MudskipperAIDisplay(this));
      this.f_21345_.m_25352_(8, new SemiAquaticAIRandomSwimming(this, 1.0, 80));
      this.f_21345_.m_25352_(9, new RandomStrollGoal(this, 1.0, 120));
      this.f_21345_.m_25352_(10, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21346_.m_25352_(1, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new OwnerHurtTargetGoal(this));
      this.f_21346_.m_25352_(3, new HurtByTargetGoal(this) {
         public boolean m_8036_() {
            return EntityMudskipper.this.m_21824_() && super.m_8036_();
         }
      });
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new GroundPathNavigatorWide(this, this.m_9236_());
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new AnimalSwimMoveControllerSink(this, 1.3F, 1.0F);
         this.f_21344_ = new SemiAquaticPathNavigator(this, this.m_9236_());
         this.isLandNavigator = false;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DISPLAYING, false);
      this.f_19804_.m_135372_(FROM_BUCKET, false);
      this.f_19804_.m_135372_(DISPLAY_ANGLE, 0.0F);
      this.f_19804_.m_135372_(DISPLAYER_UUID, Optional.empty());
      this.f_19804_.m_135372_(MOUTH_TICKS, 0);
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(SITTING, false);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 12.0).m_22268_(Attributes.f_22281_, 2.0).m_22268_(Attributes.f_22279_, 0.2F);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("FromBucket", this.m_27487_());
      compound.m_128405_("DisplayCooldown", this.displayCooldown);
      compound.m_128405_("MudskipperCommand", this.getCommand());
      compound.m_128379_("MudskipperSitting", this.m_21827_());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
      this.displayCooldown = compound.m_128451_("DisplayCooldown");
      this.setCommand(compound.m_128451_("MudskipperCommand"));
      this.m_21839_(compound.m_128471_("MudskipperSitting"));
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevSwimProgress = this.swimProgress;
      this.prevSitProgress = this.sitProgress;
      this.prevDisplayProgress = this.displayProgress;
      this.prevMudProgress = this.mudProgress;
      if (this.displayProgress < 5.0F && this.isDisplaying()) {
         this.displayProgress++;
      }

      if (this.displayProgress > 0.0F && !this.isDisplaying()) {
         this.displayProgress--;
      }

      if (this.sitProgress < 5.0F && this.m_21827_()) {
         this.sitProgress++;
      }

      if (this.sitProgress > 0.0F && !this.m_21827_()) {
         this.sitProgress--;
      }

      boolean mud = this.onMud();
      if (mud) {
         if (this.mudProgress < 1.0F) {
            this.mudProgress += 0.5F;
         }
      } else if (this.mudProgress > 0.0F) {
         this.mudProgress -= 0.5F;
      }

      boolean swim = !this.m_20096_() && this.m_20072_();
      if (this.swimProgress < 5.0F && swim) {
         this.swimProgress++;
      }

      if (this.swimProgress > 0.0F && !swim) {
         this.swimProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.m_20072_()) {
            this.swimTimer++;
         } else {
            this.swimTimer--;
         }
      }

      if (this.displayCooldown > 0) {
         this.displayCooldown--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.getDisplayAngle() < this.nextDisplayAngleFromServer) {
            this.setDisplayAngle(this.getDisplayAngle() + 1.0F);
         }

         if (this.getDisplayAngle() > this.nextDisplayAngleFromServer) {
            this.setDisplayAngle(this.getDisplayAngle() - 1.0F);
         }
      }

      if (this.isMouthOpen()) {
         this.openMouth(this.getMouthTicks() - 1);
      }

      if (this.m_20069_() && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (!this.m_20069_() && !this.isLandNavigator) {
         this.switchNavigator(true);
      }
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev && source.m_7640_() instanceof LivingEntity) {
         this.openMouth(10);
      }

      return prev;
   }

   public boolean isDisplaying() {
      return (Boolean)this.f_19804_.m_135370_(DISPLAYING);
   }

   public void setDisplaying(boolean display) {
      this.f_19804_.m_135381_(DISPLAYING, display);
   }

   public float getDisplayAngle() {
      return (Float)this.f_19804_.m_135370_(DISPLAY_ANGLE);
   }

   public void setDisplayAngle(float scale) {
      this.f_19804_.m_135381_(DISPLAY_ANGLE, scale);
   }

   public int getMouthTicks() {
      return (Integer)this.f_19804_.m_135370_(MOUTH_TICKS);
   }

   public void openMouth(int time) {
      this.f_19804_.m_135381_(MOUTH_TICKS, time);
   }

   @Nullable
   public UUID getDisplayingPartnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DISPLAYER_UUID)).orElse(null);
   }

   public void setDisplayingPartnerUUID(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(DISPLAYER_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getDisplayingPartner() {
      UUID id = this.getDisplayingPartnerUUID();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   public void setDisplayingPartner(@Nullable Entity jostlingPartner) {
      if (jostlingPartner == null) {
         this.setDisplayingPartnerUUID(null);
      } else {
         this.setDisplayingPartnerUUID(jostlingPartner.m_20148_());
      }
   }

   public boolean canDisplayWith(EntityMudskipper mudskipper) {
      return !mudskipper.m_6162_()
         && !mudskipper.m_21827_()
         && !mudskipper.shouldFollow()
         && mudskipper.m_20096_()
         && mudskipper.getDisplayingPartnerUUID() == null
         && mudskipper.displayCooldown == 0;
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob m_142606_(ServerLevel serverLevel, AgeableMob ageableMob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.MUDSKIPPER.get()).m_20615_(serverLevel);
   }

   public boolean isMouthOpen() {
      return this.getMouthTicks() > 0;
   }

   public boolean onMud() {
      BlockState below = this.m_9236_().m_8055_(this.m_20099_());
      return below.m_60713_(Blocks.f_220864_);
   }

   public void m_267651_(boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 0.0, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 8.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   protected void m_7355_(BlockPos pos, BlockState blockIn) {
      this.m_5496_((SoundEvent)AMSoundRegistry.MUDSKIPPER_WALK.get(), 1.0F, 1.0F);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.MUDSKIPPER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.MUDSKIPPER_HURT.get();
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public boolean m_21827_() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   @Override
   public boolean shouldEnterWater() {
      return (this.m_21188_() != null || this.swimTimer <= -1000) && !this.isDisplaying();
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.swimTimer > 200 || this.isDisplaying();
   }

   @Override
   public boolean shouldStopMoving() {
      return this.m_21827_();
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   public boolean m_27487_() {
      return (Boolean)this.f_19804_.m_135370_(FROM_BUCKET);
   }

   public void m_27497_(boolean bucket) {
      this.f_19804_.m_135381_(FROM_BUCKET, bucket);
   }

   @Nonnull
   public ItemStack m_28282_() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.MUDSKIPPER_BUCKET.get());
      if (this.m_8077_()) {
         stack.m_41714_(this.m_7770_());
      }

      return stack;
   }

   public void m_6872_(@Nonnull ItemStack bucket) {
      if (this.m_8077_()) {
         bucket.m_41714_(this.m_7770_());
      }

      CompoundTag platTag = new CompoundTag();
      this.m_7380_(platTag);
      CompoundTag compound = bucket.m_41784_();
      compound.m_128365_("MudskipperData", platTag);
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      if (compound.m_128441_("MudskipperData")) {
         this.m_7378_(compound.m_128469_("MudskipperData"));
      }
   }

   @Nonnull
   public SoundEvent m_142623_() {
      return SoundEvents.f_11782_;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.MUDSKIPPER_BREEDABLES);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (!this.m_21824_() && itemstack.m_204117_(AMTagRegistry.MUDSKIPPER_TAMEABLES)) {
         this.m_142075_(player, hand, itemstack);
         this.openMouth(10);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_12465_, this.m_6121_(), this.m_6100_());
         if (this.m_217043_().m_188503_(2) == 0) {
            this.m_21828_(player);
            this.m_9236_().m_7605_(this, (byte)7);
         } else {
            this.m_9236_().m_7605_(this, (byte)6);
         }

         return InteractionResult.SUCCESS;
      } else if (this.m_21824_() && itemstack.m_204117_(AMTagRegistry.MUDSKIPPER_FOODSTUFFS)) {
         if (this.m_21223_() < this.m_21233_()) {
            this.m_142075_(player, hand, itemstack);
            this.openMouth(10);
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_12465_, this.m_6121_(), this.m_6100_());
            this.m_5634_(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (item != Items.f_42447_
            && interactionresult != InteractionResult.SUCCESS
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
            return Bucketable.m_148828_(player, hand, this).orElse(type);
         }
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
}
