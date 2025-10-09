package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.MooseAIJostle;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class EntityMoose extends Animal implements IAnimatedEntity {
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
   public static final Animation ANIMATION_ATTACK = Animation.create(15);
   private static final int DAY = 24000;
   private static final EntityDataAccessor<Boolean> ANTLERED = SynchedEntityData.m_135353_(EntityMoose.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> JOSTLING = SynchedEntityData.m_135353_(EntityMoose.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> JOSTLE_ANGLE = SynchedEntityData.m_135353_(EntityMoose.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID = SynchedEntityData.m_135353_(EntityMoose.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.m_135353_(EntityMoose.class, EntityDataSerializers.f_135035_);
   public float prevJostleAngle;
   public float prevJostleProgress;
   public float jostleProgress;
   public boolean jostleDirection;
   public int jostleTimer = 0;
   public boolean instantlyTriggerJostleAI = false;
   public int jostleCooldown = 100 + this.f_19796_.m_188503_(40);
   public int timeUntilAntlerDrop = 168000 + this.f_19796_.m_188503_(3) * 24000;
   private int animationTick;
   private Animation currentAnimation;
   private int snowTimer = 0;
   private boolean permSnow = false;

   protected EntityMoose(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.m_274367_(1.1F);
   }

   public static boolean canMooseSpawn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      BlockState blockstate = worldIn.m_8055_(pos.m_7495_());
      return blockstate.m_60713_(Blocks.f_50440_)
         || blockstate.m_60713_(Blocks.f_50125_)
         || blockstate.m_60713_(Blocks.f_50127_) && worldIn.m_45524_(pos, 0) > 8;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 55.0)
         .m_22268_(Attributes.f_22281_, 7.5)
         .m_22268_(Attributes.f_22279_, 0.25)
         .m_22268_(Attributes.f_22278_, 0.5);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mooseSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected float m_6108_() {
      return 0.98F;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new MooseAIJostle(this));
      this.f_21345_.m_25352_(3, new AnimalAIPanicBaby(this, 1.25));
      this.f_21345_.m_25352_(4, new MeleeAttackGoal(this, 1.1, true));
      this.f_21345_.m_25352_(5, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(7, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.MOOSE_BREEDABLES), false));
      this.f_21345_.m_25352_(7, new AnimalAIWanderRanged(this, 120, 1.0, 14, 7));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new AnimalAIHurtByTargetNotBaby(this));
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 6) {
         for (int lvt_3_1_ = 0; lvt_3_1_ < 7; lvt_3_1_++) {
            double lvt_4_1_ = this.f_19796_.m_188583_() * 0.02;
            double lvt_6_1_ = this.f_19796_.m_188583_() * 0.02;
            double lvt_8_1_ = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_().m_7106_(ParticleTypes.f_123762_, this.m_20208_(1.0), this.m_20187_() + 0.5, this.m_20262_(1.0), lvt_4_1_, lvt_6_1_, lvt_8_1_);
         }
      } else {
         super.m_7822_(id);
      }
   }

   public boolean m_6898_(ItemStack stack) {
      if (stack.m_204117_(AMTagRegistry.MOOSE_BREEDABLES) && !this.m_27593_() && this.m_146764_() == 0) {
         if (this.m_217043_().m_188503_(5) == 0) {
            return true;
         } else {
            this.m_9236_().m_7605_(this, (byte)6);
            return false;
         }
      } else {
         return false;
      }
   }

   public void m_6710_(@Nullable LivingEntity entitylivingbaseIn) {
      if (!this.m_6162_()) {
         super.m_6710_(entitylivingbaseIn);
      }
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_ATTACK);
      }

      return true;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(ANTLERED, true);
      this.f_19804_.m_135372_(JOSTLING, false);
      this.f_19804_.m_135372_(SNOWY, false);
      this.f_19804_.m_135372_(JOSTLE_ANGLE, 0.0F);
      this.f_19804_.m_135372_(JOSTLER_UUID, Optional.empty());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setSnowy(compound.m_128471_("Snowy"));
      if (compound.m_128441_("AntlerTime")) {
         this.timeUntilAntlerDrop = compound.m_128451_("AntlerTime");
      }

      this.setAntlered(compound.m_128471_("Antlered"));
      this.jostleCooldown = compound.m_128451_("JostlingCooldown");
      this.permSnow = compound.m_128471_("SnowPerm");
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Snowy", this.isSnowy());
      compound.m_128379_("SnowPerm", this.permSnow);
      compound.m_128405_("AntlerTime", this.timeUntilAntlerDrop);
      compound.m_128379_("Antlered", this.isAntlered());
      compound.m_128405_("JostlingCooldown", this.jostleCooldown);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevJostleProgress = this.jostleProgress;
      this.prevJostleAngle = this.getJostleAngle();
      if (this.isJostling()) {
         if (this.jostleProgress < 5.0F) {
            this.jostleProgress++;
         }
      } else if (this.jostleProgress > 0.0F) {
         this.jostleProgress--;
      }

      if (this.jostleCooldown > 0) {
         this.jostleCooldown--;
      }

      if (!this.m_9236_().f_46443_
         && this.getAnimation() == NO_ANIMATION
         && this.m_217043_().m_188503_(120) == 0
         && (this.m_5448_() == null || !this.m_5448_().m_6084_())
         && !this.isJostling()
         && this.getJostlingPartnerUUID() == null
         && this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_)
         && this.m_217043_().m_188503_(3) == 0) {
         this.setAnimation(ANIMATION_EAT_GRASS);
      }

      if (this.timeUntilAntlerDrop > 0) {
         this.timeUntilAntlerDrop--;
      }

      if (this.timeUntilAntlerDrop == 0) {
         if (this.isAntlered()) {
            this.setAntlered(false);
            this.m_19983_(new ItemStack((ItemLike)AMItemRegistry.MOOSE_ANTLER.get()));
            this.timeUntilAntlerDrop = 48000 + this.f_19796_.m_188503_(3) * 24000;
         } else {
            this.setAntlered(true);
            this.timeUntilAntlerDrop = 168000 + this.f_19796_.m_188503_(3) * 24000;
         }
      }

      if (this.m_5448_() != null && this.m_5448_().m_6084_()) {
         if (this.isJostling()) {
            this.setJostling(false);
         }

         if (!this.m_9236_().f_46443_ && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 8) {
            float dmg = (float)this.m_21051_(Attributes.f_22281_).m_22115_();
            if (!this.isAntlered()) {
               dmg = 3.0F;
            }

            if (this.m_5448_() instanceof Wolf || this.m_5448_() instanceof EntityOrca) {
               dmg = 2.0F;
            }

            this.m_5448_().m_147240_(1.0, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), dmg);
         }
      }

      if (this.snowTimer > 0) {
         this.snowTimer--;
      }

      if (this.snowTimer == 0 && !this.m_9236_().f_46443_) {
         this.snowTimer = 200 + this.f_19796_.m_188503_(400);
         if (this.isSnowy()) {
            if (!this.permSnow
               && (
                  !this.m_9236_().f_46443_
                     || this.m_20094_() > 0
                     || this.m_20072_()
                     || !EntityGrizzlyBear.isSnowingAt(this.m_9236_(), this.m_20183_().m_7494_())
               )) {
               this.setSnowy(false);
            }
         } else if (!this.m_9236_().f_46443_ && EntityGrizzlyBear.isSnowingAt(this.m_9236_(), this.m_20183_())) {
            this.setSnowy(true);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (this.m_6673_(source)) {
         return false;
      } else {
         Entity entity = source.m_7639_();
         if (entity instanceof EntityOrca || entity instanceof Wolf) {
            amount = (amount + 1.0F) * 3.0F;
         }

         return super.m_6469_(source, amount);
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.MOOSE_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.MOOSE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.MOOSE_HURT.get();
   }

   public boolean isAntlered() {
      return (Boolean)this.f_19804_.m_135370_(ANTLERED);
   }

   public void setAntlered(boolean anters) {
      this.f_19804_.m_135381_(ANTLERED, anters);
   }

   public boolean isJostling() {
      return (Boolean)this.f_19804_.m_135370_(JOSTLING);
   }

   public void setJostling(boolean jostle) {
      this.f_19804_.m_135381_(JOSTLING, jostle);
   }

   public float getJostleAngle() {
      return (Float)this.f_19804_.m_135370_(JOSTLE_ANGLE);
   }

   public void setJostleAngle(float scale) {
      this.f_19804_.m_135381_(JOSTLE_ANGLE, scale);
   }

   @Nullable
   public UUID getJostlingPartnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(JOSTLER_UUID)).orElse(null);
   }

   public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(JOSTLER_UUID, Optional.ofNullable(uniqueId));
   }

   public boolean isSnowy() {
      return (Boolean)this.f_19804_.m_135370_(SNOWY);
   }

   public void setSnowy(boolean honeyed) {
      this.f_19804_.m_135381_(SNOWY, honeyed);
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
         return type;
      }
   }

   @Nullable
   public Entity getJostlingPartner() {
      UUID id = this.getJostlingPartnerUUID();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   public void setJostlingPartner(@Nullable Entity jostlingPartner) {
      if (jostlingPartner == null) {
         this.setJostlingPartnerUUID(null);
      } else {
         this.setJostlingPartnerUUID(jostlingPartner.m_20148_());
      }
   }

   public void pushBackJostling(EntityMoose entityMoose, float strength) {
      this.applyKnockbackFromMoose(strength, entityMoose.m_20185_() - this.m_20185_(), entityMoose.m_20189_() - this.m_20189_());
   }

   private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
      LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
      if (!event.isCanceled()) {
         strength = event.getStrength();
         ratioX = event.getRatioX();
         ratioZ = event.getRatioZ();
         if (!(strength <= 0.0F)) {
            this.f_19812_ = true;
            Vec3 vector3d = this.m_20184_();
            Vec3 vector3d1 = new Vec3(ratioX, 0.0, ratioZ).m_82541_().m_82490_(strength);
            this.m_20334_(vector3d.f_82479_ / 2.0 - vector3d1.f_82479_, 0.3F, vector3d.f_82481_ / 2.0 - vector3d1.f_82481_);
         }
      }
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
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_ATTACK, ANIMATION_EAT_GRASS};
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return (AgeableMob)((EntityType)AMEntityRegistry.MOOSE.get()).m_20615_(serverWorld);
   }

   public boolean canJostleWith(EntityMoose moose) {
      return !moose.isJostling()
         && moose.isAntlered()
         && moose.getAnimation() == NO_ANIMATION
         && !moose.m_6162_()
         && moose.getJostlingPartnerUUID() == null
         && moose.jostleCooldown == 0;
   }

   public void playJostleSound() {
      this.m_5496_((SoundEvent)AMSoundRegistry.MOOSE_JOSTLE.get(), this.m_6100_(), this.m_6121_());
   }
}
