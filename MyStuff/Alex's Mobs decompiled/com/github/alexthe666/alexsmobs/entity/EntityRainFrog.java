package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.message.MessageStartDancing;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityRainFrog extends Animal implements ITargetsDroppedItems, IDancingMob {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> STANCE_TIME = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> DANCE_TIME = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DISTURBED = SynchedEntityData.m_135353_(EntityRainFrog.class, EntityDataSerializers.f_135035_);
   public float burrowProgress;
   public float prevBurrowProgress;
   public float danceProgress;
   public float prevDanceProgress;
   public float attackProgress;
   public float prevAttackProgress;
   public float stanceProgress;
   public float prevStanceProgress;
   private int burrowCooldown = 0;
   private int weatherCooldown = 0;
   private boolean isJukeboxing;
   private BlockPos jukeboxPosition;

   protected EntityRainFrog(EntityType<? extends Animal> rainFrog, Level lvl) {
      super(rainFrog, lvl);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 6.0).m_22268_(Attributes.f_22279_, 0.2F);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new TemptGoal(this, 1.0, Ingredient.m_204132_(AMTagRegistry.RAIN_FROG_BREEDABLES), false));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(3, new AvoidEntityGoal(this, EntityRattlesnake.class, 9.0F, 1.3, 1.0));
      this.f_21345_.m_25352_(5, new EntityRainFrog.AIBurrow());
      this.f_21345_.m_25352_(6, new AnimalAIWanderRanged(this, 20, 1.0, 10, 7));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new CreatureAITargetItems(this, false));
   }

   public static boolean canRainFrogSpawn(EntityType animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.RAIN_FROG_SPAWNS);
      return spawnBlock && worldIn.m_6106_() != null && (worldIn.m_6106_().m_6534_() || worldIn.m_6106_().m_6533_());
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.rainFrogSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public boolean isBurrowed() {
      return (Boolean)this.f_19804_.m_135370_(BURROWED);
   }

   public void setBurrowed(boolean burrowed) {
      this.f_19804_.m_135381_(BURROWED, burrowed);
   }

   public boolean isDisturbed() {
      return (Boolean)this.f_19804_.m_135370_(DISTURBED);
   }

   public void setDisturbed(boolean burrowed) {
      this.f_19804_.m_135381_(DISTURBED, burrowed);
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int variant) {
      this.f_19804_.m_135381_(VARIANT, variant);
   }

   public int getStanceTime() {
      return (Integer)this.f_19804_.m_135370_(STANCE_TIME);
   }

   public void setStanceTime(int stanceTime) {
      this.f_19804_.m_135381_(STANCE_TIME, stanceTime);
   }

   public int getAttackTime() {
      return (Integer)this.f_19804_.m_135370_(ATTACK_TIME);
   }

   public void setAttackTime(int attackTime) {
      this.f_19804_.m_135381_(ATTACK_TIME, attackTime);
   }

   public int getDanceTime() {
      return (Integer)this.f_19804_.m_135370_(DANCE_TIME);
   }

   public void setDanceTime(int danceTime) {
      this.f_19804_.m_135381_(DANCE_TIME, danceTime);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.RAIN_FROG_BREEDABLES);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      EntityRainFrog frog = (EntityRainFrog)((EntityType)AMEntityRegistry.RAIN_FROG.get()).m_20615_(p_241840_1_);
      frog.setVariant(this.getVariant());
      frog.setDisturbed(true);
      return frog;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(VARIANT, 0);
      this.f_19804_.m_135372_(STANCE_TIME, 0);
      this.f_19804_.m_135372_(ATTACK_TIME, 0);
      this.f_19804_.m_135372_(DANCE_TIME, 0);
      this.f_19804_.m_135372_(BURROWED, false);
      this.f_19804_.m_135372_(DISTURBED, false);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevBurrowProgress = this.burrowProgress;
      this.prevDanceProgress = this.danceProgress;
      this.prevAttackProgress = this.attackProgress;
      this.prevStanceProgress = this.stanceProgress;
      if (this.isBurrowed()) {
         if (this.burrowProgress < 5.0F) {
            this.burrowProgress += 0.5F;
         }
      } else if (this.burrowProgress > 0.0F) {
         this.burrowProgress -= 0.5F;
      }

      if (this.burrowCooldown > 0) {
         this.burrowCooldown--;
      }

      if (this.getStanceTime() > 0) {
         this.setStanceTime(this.getStanceTime() - 1);
         if (this.stanceProgress < 5.0F) {
            this.stanceProgress++;
         }
      } else if (this.stanceProgress > 0.0F) {
         this.stanceProgress--;
      }

      if (this.getAttackTime() > 0) {
         this.setAttackTime(this.getAttackTime() - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress += 2.5F;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress -= 0.5F;
      }

      boolean dancing = this.getDanceTime() > 0 || this.isJukeboxing;
      if (dancing) {
         if (this.danceProgress < 5.0F) {
            this.danceProgress++;
         }
      } else if (this.danceProgress > 0.0F) {
         this.danceProgress--;
      }

      if (this.getDanceTime() > 0) {
         this.setBurrowed(false);
         this.setDanceTime(this.getDanceTime() - 1);
         if (this.getDanceTime() == 1 && this.weatherCooldown <= 0 && this.m_9236_().m_46469_().m_46207_(GameRules.f_46150_)) {
            this.changeWeather();
         }
      }

      if (this.weatherCooldown > 0) {
         this.weatherCooldown--;
      }

      if (this.jukeboxPosition == null
         || !this.jukeboxPosition.m_203195_(this.m_20182_(), 15.0)
         || !this.m_9236_().m_8055_(this.jukeboxPosition).m_60713_(Blocks.f_50131_)) {
         this.isJukeboxing = false;
         this.setDanceTime(0);
         this.jukeboxPosition = null;
      }
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev && source.m_7640_() instanceof LivingEntity) {
         if (this.getStanceTime() <= 0) {
            this.setStanceTime(30 + this.f_19796_.m_188503_(20));
         }

         this.setBurrowed(false);
      }

      return prev;
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return !this.m_8023_();
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.isDisturbed();
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_5803_() {
      return this.isBurrowed();
   }

   public void calculateEntityAnimation(LivingEntity mob, boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 0.0, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 128.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setVariant(this.f_19796_.m_188503_(3));
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setDisturbed(compound.m_128471_("Disturbed"));
      this.setVariant(compound.m_128451_("Variant"));
      this.weatherCooldown = compound.m_128451_("WeatherCooldown");
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Disturbed", this.isDisturbed());
      compound.m_128405_("Variant", this.getVariant());
      compound.m_128405_("WeatherCooldown", this.weatherCooldown);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (item instanceof ShovelItem && (this.isBurrowed() || !this.isDisturbed()) && !this.m_9236_().f_46443_) {
         this.f_21363_ = 1000;
         if (!player.m_7500_()) {
            itemstack.m_220157_(1, this.m_217043_(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
         }

         this.setStanceTime(20 + this.f_19796_.m_188503_(30));
         this.setBurrowed(false);
         this.setDisturbed(true);
         this.burrowCooldown = this.burrowCooldown + 150 + this.f_19796_.m_188503_(120);
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(SoundEvents.f_12331_, this.m_6121_(), this.m_6100_());
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public void m_7023_(Vec3 travelVector) {
      if (!this.isBurrowed() && this.getDanceTime() <= 0) {
         if (this.m_21515_() && this.m_20069_()) {
            this.m_19920_(this.m_6113_(), travelVector);
            this.m_6478_(MoverType.SELF, this.m_20184_());
            this.m_20256_(this.m_20184_().m_82490_(0.9));
            if (this.m_5448_() == null) {
               this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
            }
         } else {
            super.m_7023_(travelVector);
         }
      } else {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         travelVector = Vec3.f_82478_;
         super.m_7023_(travelVector);
      }
   }

   @Override
   public void onFindTarget(ItemEntity e) {
      this.setBurrowed(false);
      this.burrowCooldown += 50;
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.INSECT_ITEMS);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.setAttackTime(10);
      this.m_5634_(2.0F);
   }

   private void changeWeather() {
      int time = 24000 + 1200 * this.f_19796_.m_188503_(10);
      int type = 0;
      if (!this.m_9236_().m_46471_()) {
         type = this.f_19796_.m_188503_(1) + 1;
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (type == 0) {
            serverLevel.m_8606_(time, 0, false, false);
         } else {
            serverLevel.m_8606_(0, time, true, type == 2);
         }
      }

      this.weatherCooldown = time + 24000;
   }

   public void m_6818_(BlockPos pos, boolean isPartying) {
      AlexsMobs.sendMSGToServer(new MessageStartDancing(this.m_19879_(), isPartying, pos));
      if (isPartying) {
         this.setJukeboxPos(pos);
      } else {
         this.setJukeboxPos(null);
      }
   }

   @Override
   public void setDancing(boolean dancing) {
      this.setDanceTime(dancing && this.weatherCooldown == 0 ? 240 + this.f_19796_.m_188503_(200) : 0);
   }

   @Override
   public void setJukeboxPos(BlockPos pos) {
      this.jukeboxPosition = pos;
   }

   protected SoundEvent m_7515_() {
      return this.getStanceTime() > 0 ? (SoundEvent)AMSoundRegistry.RAIN_FROG_HURT.get() : (SoundEvent)AMSoundRegistry.RAIN_FROG_IDLE.get();
   }

   public int m_8100_() {
      return this.getStanceTime() > 0 ? 10 : 80;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.RAIN_FROG_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.RAIN_FROG_HURT.get();
   }

   private class AIBurrow extends Goal {
      private BlockPos sand = null;
      private int burrowedTime = 0;

      public AIBurrow() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         if (!EntityRainFrog.this.isBurrowed() && EntityRainFrog.this.burrowCooldown == 0 && EntityRainFrog.this.f_19796_.m_188503_(200) == 0) {
            this.burrowedTime = 0;
            this.sand = this.findSand();
            return this.sand != null;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.burrowedTime < 300;
      }

      public BlockPos findSand() {
         BlockPos blockpos = null;

         for (BlockPos blockpos1 : BlockPos.m_121976_(
            Mth.m_14107_(EntityRainFrog.this.m_20185_() - 4.0),
            Mth.m_14107_(EntityRainFrog.this.m_20186_() - 1.0),
            Mth.m_14107_(EntityRainFrog.this.m_20189_() - 4.0),
            Mth.m_14107_(EntityRainFrog.this.m_20185_() + 4.0),
            EntityRainFrog.this.m_146904_(),
            Mth.m_14107_(EntityRainFrog.this.m_20189_() + 4.0)
         )) {
            if (EntityRainFrog.this.m_9236_().m_8055_(blockpos1).m_204336_(BlockTags.f_13029_)) {
               blockpos = blockpos1;
               break;
            }
         }

         return blockpos;
      }

      public void m_8037_() {
         if (EntityRainFrog.this.isBurrowed()) {
            this.burrowedTime++;
            if (!EntityRainFrog.this.m_20075_().m_204336_(BlockTags.f_13029_)) {
               EntityRainFrog.this.setBurrowed(false);
            }
         } else if (this.sand != null) {
            EntityRainFrog.this.m_21573_().m_26519_(this.sand.m_123341_() + 0.5F, this.sand.m_123342_() + 1.0F, this.sand.m_123343_() + 0.5F, 1.0);
            if (EntityRainFrog.this.m_20075_().m_204336_(BlockTags.f_13029_)) {
               EntityRainFrog.this.setBurrowed(true);
               EntityRainFrog.this.m_21573_().m_26573_();
               this.sand = null;
            } else {
               EntityRainFrog.this.setBurrowed(false);
            }
         }
      }

      public void m_8041_() {
         EntityRainFrog.this.setBurrowed(false);
         EntityRainFrog.this.burrowCooldown = 120 + EntityRainFrog.this.f_19796_.m_188503_(1200);
         this.sand = null;
      }
   }
}
