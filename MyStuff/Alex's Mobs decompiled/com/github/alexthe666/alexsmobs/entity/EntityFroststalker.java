package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FroststalkerAIMelee;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

public class EntityFroststalker extends Animal implements IAnimatedEntity, ISemiAquatic {
   public static final ResourceLocation SPIKED_LOOT = new ResourceLocation("alexsmobs", "entities/froststalker_spikes");
   public static final Animation ANIMATION_BITE = Animation.create(13);
   public static final Animation ANIMATION_SPEAK = Animation.create(11);
   public static final Animation ANIMATION_SLASH_L = Animation.create(12);
   public static final Animation ANIMATION_SLASH_R = Animation.create(12);
   public static final Animation ANIMATION_SHOVE = Animation.create(12);
   private static final EntityDataAccessor<Boolean> SPIKES = SynchedEntityData.m_135353_(EntityFroststalker.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.m_135353_(EntityFroststalker.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SPIKE_SHAKING = SynchedEntityData.m_135353_(EntityFroststalker.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> BIPEDAL = SynchedEntityData.m_135353_(EntityFroststalker.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> TURN_ANGLE = SynchedEntityData.m_135353_(EntityFroststalker.class, EntityDataSerializers.f_135029_);
   public static final Predicate<Player> VALID_LEADER_PLAYERS = player -> player.m_6844_(EquipmentSlot.HEAD)
      .m_150930_((Item)AMItemRegistry.FROSTSTALKER_HELMET.get());
   public float bipedProgress;
   public float prevBipedProgress;
   public float tackleProgress;
   public float prevTackleProgress;
   public float spikeShakeProgress;
   public float prevSpikeShakeProgress;
   public float prevTurnAngle;
   private int animationTick;
   private Animation currentAnimation;
   private int standingTime = 400 - this.f_19796_.m_188503_(700);
   private int currentSpeedMode = -1;
   private LivingEntity leader;
   private int packSize = 1;
   private int shakeTime = 0;
   private boolean hasSpikedArmor = false;
   private int fleeFireFlag;
   private int resetLeaderCooldown = 100;

   protected EntityFroststalker(EntityType<? extends Animal> type, Level level) {
      super(type, level);
      this.m_21441_(BlockPathTypes.LAVA, -1.0F);
      this.m_21441_(BlockPathTypes.DANGER_FIRE, -1.0F);
      this.m_21441_(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.FROSTSTALKER_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.FROSTSTALKER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.FROSTSTALKER_HURT.get();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.froststalkerSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public static boolean canFroststalkerSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return worldIn.m_45524_(pos, 0) > 8
         && (worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.FROSTSTALKER_SPAWNS) || worldIn.m_8055_(pos.m_7495_()).m_280296_());
   }

   @Nullable
   protected ResourceLocation m_7582_() {
      return this.hasSpikes() ? SPIKED_LOOT : super.m_7582_();
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 24.0)
         .m_22268_(Attributes.f_22284_, 2.0)
         .m_22268_(Attributes.f_22281_, 4.5)
         .m_22268_(Attributes.f_22279_, 0.3F);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean m_6469_(DamageSource source, float amount) {
      if (source.m_269533_(DamageTypeTags.f_268745_)) {
         amount *= 2.0F;
      }

      boolean prev = super.m_6469_(source, amount);
      if (prev && this.hasSpikes() && !this.isSpikeShaking() && source.m_7639_() != null && source.m_7639_().m_20270_(this) < 10.0F) {
         this.setSpikeShaking(true);
         this.shakeTime = 20 + this.f_19796_.m_188503_(60);
         this.standFor(this.shakeTime + 10);
      }

      return prev;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this) {
         public void m_8037_() {
            if (EntityFroststalker.this.m_217043_().m_188501_() < 0.8F) {
               if (EntityFroststalker.this.hasSpikes()) {
                  EntityFroststalker.this.jumpUnderwater();
               } else {
                  EntityFroststalker.this.m_21569_().m_24901_();
               }
            }
         }
      });
      this.f_21345_.m_25352_(1, new EntityFroststalker.AIAvoidFire());
      this.f_21345_.m_25352_(2, new FroststalkerAIMelee(this));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(5, new FroststalkerAIFollowLeader(this));
      this.f_21345_.m_25352_(6, new AnimalAIFindWater(this));
      this.f_21345_.m_25352_(7, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(8, new AnimalAIWanderRanged(this, 90, 1.0, 7, 7));
      this.f_21345_.m_25352_(9, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
      this.f_21345_.m_25352_(9, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[]{EntityFroststalker.class}).m_26044_(new Class[0]));
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 40, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.FROSTSTALKER_TARGETS)
            )
         );
      this.f_21346_
         .m_25352_(
            3,
            new NearestAttackableTargetGoal(
               this,
               Player.class,
               80,
               false,
               true,
               livingEntity -> !livingEntity.m_6844_(EquipmentSlot.HEAD).m_150930_((Item)AMItemRegistry.FROSTSTALKER_HELMET.get())
            )
         );
   }

   private void jumpUnderwater() {
      BlockPos pos = this.m_20097_();
      if (this.m_9236_().m_46801_(pos) && !this.m_9236_().m_46801_(pos.m_7494_())) {
         this.m_6034_(this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_());
         this.m_9236_().m_46597_(pos, Blocks.f_50449_.m_49966_());
         this.m_9236_().m_186460_(pos, Blocks.f_50449_, Mth.m_216271_(this.m_217043_(), 60, 120));
      }

      double d0 = 0.2F;
      Vec3 vec3 = this.m_20184_();
      this.m_20334_(vec3.f_82479_, d0, vec3.f_82481_);
   }

   public void m_27595_(@Nullable Player player) {
      if (player != null && this.isValidLeader(player)) {
         super.m_27595_(player);
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(TURN_ANGLE, 0.0F);
      this.f_19804_.m_135372_(SPIKES, true);
      this.f_19804_.m_135372_(BIPEDAL, false);
      this.f_19804_.m_135372_(SPIKE_SHAKING, false);
      this.f_19804_.m_135372_(TACKLING, false);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Spiked", this.hasSpikes());
      compound.m_128379_("Bipedal", this.isBipedal());
      compound.m_128379_("SpikeShaking", this.isSpikeShaking());
      compound.m_128405_("StandingTime", this.standingTime);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setSpiked(compound.m_128471_("Spiked"));
      this.setBipedal(compound.m_128471_("Bipedal"));
      this.setSpikeShaking(compound.m_128471_("SpikeShaking"));
      this.standingTime = compound.m_128451_("StandingTime");
   }

   public BlockPos m_21534_() {
      return this.leader == null ? super.m_21534_() : this.leader.m_20097_();
   }

   public boolean m_21536_() {
      return this.isFollower();
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.FROSTSTALKER_BREEDABLES);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevBipedProgress = this.bipedProgress;
      this.prevTackleProgress = this.tackleProgress;
      this.prevSpikeShakeProgress = this.spikeShakeProgress;
      this.prevTurnAngle = this.getTurnAngle();
      if (this.isBipedal()) {
         if (this.bipedProgress < 5.0F) {
            this.bipedProgress++;
         }
      } else if (this.bipedProgress > 0.0F) {
         this.bipedProgress--;
      }

      if (this.isTackling()) {
         if (this.tackleProgress < 5.0F) {
            this.tackleProgress++;
         }
      } else if (this.tackleProgress > 0.0F) {
         this.tackleProgress--;
      }

      if (this.isSpikeShaking()) {
         if (this.spikeShakeProgress < 5.0F) {
            this.spikeShakeProgress++;
         }

         if (this.currentSpeedMode != 2) {
            this.currentSpeedMode = 2;
            this.m_21051_(Attributes.f_22279_).m_22100_(0.1F);
         }
      } else {
         if (this.spikeShakeProgress > 0.0F) {
            this.spikeShakeProgress--;
         }

         if (this.isBipedal()) {
            if (this.currentSpeedMode != 0) {
               this.currentSpeedMode = 0;
               this.m_21051_(Attributes.f_22279_).m_22100_(0.35F);
            }
         } else if (this.currentSpeedMode != 1) {
            this.currentSpeedMode = 1;
            this.m_21051_(Attributes.f_22279_).m_22100_(0.25);
         }
      }

      if (this.hasSpikes()) {
         if (!this.hasSpikedArmor) {
            this.hasSpikedArmor = true;
            this.m_21051_(Attributes.f_22284_).m_22100_(12.0);
         }
      } else if (this.hasSpikedArmor) {
         this.hasSpikedArmor = false;
         this.m_21051_(Attributes.f_22284_).m_22100_(0.0);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ % 200 == 0) {
            if (this.m_20071_() && !this.hasSpikes()) {
               this.setSpiked(true);
            }

            if (this.isHotBiome() && !this.m_20071_()) {
               this.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 400));
               if (this.f_19796_.m_188503_(2) == 0 && !this.m_20071_()) {
                  this.setSpiked(false);
               }
            }
         }

         float threshold = 1.0F;
         boolean flag = false;
         if (this.isBipedal() && this.f_19859_ - this.m_146908_() > threshold) {
            this.setTurnAngle(this.getTurnAngle() + 5.0F);
            flag = true;
         }

         if (this.isBipedal() && this.f_19859_ - this.m_146908_() < -threshold) {
            this.setTurnAngle(this.getTurnAngle() - 5.0F);
            flag = true;
         }

         if (!flag) {
            if (this.getTurnAngle() > 0.0F) {
               this.setTurnAngle(Math.max(this.getTurnAngle() - 10.0F, 0.0F));
            }

            if (this.getTurnAngle() < 0.0F) {
               this.setTurnAngle(Math.min(this.getTurnAngle() + 10.0F, 0.0F));
            }
         }

         this.setTurnAngle(Mth.m_14036_(this.getTurnAngle(), -60.0F, 60.0F));
         if (this.standingTime > 0) {
            this.standingTime--;
         }

         if (this.standingTime < 0) {
            this.standingTime++;
         }

         if (this.standingTime <= 0 && this.isBipedal()) {
            this.standingTime = -200 - this.f_19796_.m_188503_(400);
            this.setBipedal(false);
         }

         if (this.standingTime == 0 && !this.isBipedal() && this.m_20184_().m_82556_() >= 0.03) {
            this.standingTime = 200 + this.f_19796_.m_188503_(600);
            this.setBipedal(true);
         }

         if (this.shakeTime > 0) {
            if (this.shakeTime % 5 == 0) {
               int spikeCount = 2 + this.f_19796_.m_188503_(4);

               for (int i = 0; i < spikeCount; i++) {
                  float f = (float)(i + 1) / spikeCount * 360.0F;
                  EntityIceShard shard = new EntityIceShard(this.m_9236_(), this);
                  shard.shootFromRotation(this, this.m_146909_() - this.f_19796_.m_188503_(40), f, 0.0F, 0.15F + this.f_19796_.m_188501_() * 0.2F, 1.0F);
                  this.m_9236_().m_7967_(shard);
               }
            }

            this.shakeTime--;
         }

         if (this.shakeTime == 0 && this.isSpikeShaking()) {
            this.setSpikeShaking(false);
            if (this.f_19796_.m_188503_(2) == 0) {
               this.setSpiked(false);
            }
         }

         if (this.m_5448_() != null && this.isValidLeader(this.m_5448_())) {
            this.m_6710_(null);
         }

         if (this.m_5448_() != null
            && !this.isValidLeader(this.m_5448_())
            && this.m_5448_().m_6084_()
            && (this.m_21188_() == null || !this.m_21188_().m_6084_())) {
            this.m_6703_(this.m_5448_());
         }

         LivingEntity playerTarget = null;
         if (this.leader instanceof Player) {
            playerTarget = this.leader.m_21214_();
            if (playerTarget == null || !playerTarget.m_6084_() || playerTarget instanceof EntityFroststalker) {
               playerTarget = this.leader.m_21188_();
            }
         }

         if (playerTarget != null && playerTarget.m_6084_() && !(playerTarget instanceof EntityFroststalker)) {
            this.m_6710_(playerTarget);
         }

         boolean attackAnim = this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5
            || this.getAnimation() == ANIMATION_SHOVE && this.getAnimationTick() == 8
            || this.getAnimation() == ANIMATION_SLASH_L && this.getAnimationTick() == 7
            || this.getAnimation() == ANIMATION_SLASH_R && this.getAnimationTick() == 7;
         if (this.m_5448_() != null && attackAnim) {
            this.m_5448_().m_147240_(0.2F, this.m_5448_().m_20185_() - this.m_20185_(), this.m_5448_().m_20189_() - this.m_20189_());
            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), (float)this.m_21133_(Attributes.f_22281_));
         }
      }

      if (this.fleeFireFlag > 0) {
         this.fleeFireFlag--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.resetLeaderCooldown > 0) {
            this.resetLeaderCooldown--;
         } else {
            this.resetLeaderCooldown = 200 + this.m_217043_().m_188503_(200);
            this.lookForPlayerLeader();
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void lookForPlayerLeader() {
      if (!(this.leader instanceof Player)) {
         float range = 10.0F;
         List<Player> playerList = this.m_9236_().m_6443_(Player.class, this.m_20191_().m_82377_(range, range, range), VALID_LEADER_PLAYERS);
         Player closestPlayer = null;

         for (Player player : playerList) {
            if (closestPlayer == null || player.m_20270_(this) < closestPlayer.m_20270_(this)) {
               closestPlayer = player;
            }
         }

         if (closestPlayer != null) {
            this.stopFollowing();
            this.startFollowing(closestPlayer);
         }
      }
   }

   public boolean isFleeingFire() {
      return this.fleeFireFlag > 0;
   }

   public boolean isHotBiome() {
      if (this.m_21525_()) {
         return false;
      } else if (this.m_9236_().m_46472_() == Level.f_46429_) {
         return true;
      } else {
         int i = Mth.m_14107_(this.m_20185_());
         int k = Mth.m_14107_(this.m_20189_());
         return this.m_9236_().m_204166_(new BlockPos(i, 0, k)).m_203656_(BiomeTags.f_263828_);
      }
   }

   public void standFor(int time) {
      this.setBipedal(true);
      this.standingTime = time;
   }

   protected float m_6118_() {
      return 0.52F * this.m_20098_();
   }

   protected void m_6135_() {
      double d0 = (double)this.m_6118_() + this.m_285755_();
      Vec3 vec3 = this.m_20184_();
      this.m_20334_(vec3.f_82479_, d0, vec3.f_82481_);
      float f = this.m_146908_() * (float) (Math.PI / 180.0);
      this.m_20256_(this.m_20184_().m_82520_(-Mth.m_14031_(f) * 0.2F, 0.0, Mth.m_14089_(f) * 0.2F));
      this.f_19812_ = true;
      ForgeHooks.onLivingJump(this);
   }

   public void frostJump() {
      this.m_6135_();
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
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_SHOVE};
   }

   public float getTurnAngle() {
      return (Float)this.f_19804_.m_135370_(TURN_ANGLE);
   }

   public void setTurnAngle(float progress) {
      this.f_19804_.m_135381_(TURN_ANGLE, progress);
   }

   public boolean hasSpikes() {
      return (Boolean)this.f_19804_.m_135370_(SPIKES);
   }

   public void setSpiked(boolean bar) {
      this.f_19804_.m_135381_(SPIKES, bar);
   }

   public boolean isTackling() {
      return (Boolean)this.f_19804_.m_135370_(TACKLING);
   }

   public void setTackling(boolean bar) {
      this.f_19804_.m_135381_(TACKLING, bar);
   }

   public boolean isBipedal() {
      return (Boolean)this.f_19804_.m_135370_(BIPEDAL);
   }

   public void setBipedal(boolean bar) {
      this.f_19804_.m_135381_(BIPEDAL, bar);
   }

   public boolean isSpikeShaking() {
      return (Boolean)this.f_19804_.m_135370_(SPIKE_SHAKING);
   }

   public void setSpikeShaking(boolean bar) {
      this.f_19804_.m_135381_(SPIKE_SHAKING, bar);
   }

   public boolean isFollower() {
      return this.leader != null && this.isValidLeader(this.leader);
   }

   public boolean isValidLeader(LivingEntity leader) {
      if (leader instanceof Player) {
         return this.m_21188_() != null && this.m_21188_().equals(leader)
            ? false
            : leader.m_6844_(EquipmentSlot.HEAD).m_150930_((Item)AMItemRegistry.FROSTSTALKER_HELMET.get());
      } else {
         return leader.m_6084_() && leader instanceof EntityFroststalker;
      }
   }

   public boolean m_7327_(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         int anim = this.f_19796_.m_188503_(4);
         switch (anim) {
            case 0:
               this.setAnimation(ANIMATION_SHOVE);
               break;
            case 1:
               this.setAnimation(ANIMATION_BITE);
               break;
            case 2:
               this.setAnimation(ANIMATION_SLASH_L);
               break;
            case 3:
               this.setAnimation(ANIMATION_SLASH_R);
         }
      }

      return true;
   }

   public LivingEntity startFollowing(LivingEntity leader) {
      this.leader = leader;
      if (leader instanceof EntityFroststalker) {
         ((EntityFroststalker)leader).addFollower();
      }

      return leader;
   }

   public void stopFollowing() {
      if (this.leader instanceof EntityFroststalker) {
         ((EntityFroststalker)this.leader).removeFollower();
      }

      this.leader = null;
   }

   private void addFollower() {
      this.packSize++;
   }

   private void removeFollower() {
      this.packSize--;
   }

   public boolean canBeFollowed() {
      return this.hasFollowers() && this.packSize < this.getMaxPackSize() && this.isValidLeader(this);
   }

   public boolean hasFollowers() {
      return this.packSize > 1;
   }

   public int m_5792_() {
      return 6;
   }

   public int getMaxPackSize() {
      return this.m_5792_();
   }

   public void addFollowers(Stream<EntityFroststalker> p_27534_) {
      p_27534_.limit(this.getMaxPackSize() - this.packSize).filter(p_27538_ -> p_27538_ != this).forEach(p_27536_ -> p_27536_.startFollowing(this));
   }

   public boolean inRangeOfLeader() {
      return this.m_20270_(this.leader) <= 60.0;
   }

   public void pathToLeader() {
      if (this.isFollower()) {
         double speed = 1.0;
         if (this.leader instanceof Player) {
            speed = 1.3;
            if (this.m_20270_(this.leader) > 24.0F) {
               speed = 1.4F;
               this.standFor(20);
            }
         }

         if (this.m_20270_(this.leader) > 6.0F && this.m_21573_().m_26571_()) {
            this.m_21573_().m_5624_(this.leader, speed);
         }
      }
   }

   protected void m_5806_(BlockPos pos) {
      int i = EnchantmentHelper.m_44836_(Enchantments.f_44974_, this);
      if (i > 0 || this.hasSpikes()) {
         FrostWalkerEnchantment.m_45018_(this, this.m_9236_(), pos, i == 0 ? -1 : i);
      }

      if (this.m_6757_(this.m_20075_())) {
         this.m_21185_();
      }

      this.m_21186_();
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor p_27528_, DifficultyInstance p_27529_, MobSpawnType p_27530_, @Nullable SpawnGroupData p_27531_, @Nullable CompoundTag p_27532_
   ) {
      this.m_21051_(Attributes.f_22277_).m_22125_(new AttributeModifier("Random spawn bonus", this.f_19796_.m_188583_() * 0.05, Operation.MULTIPLY_BASE));
      if (p_27531_ == null) {
         p_27531_ = new EntityFroststalker.SchoolSpawnGroupData(this);
      } else {
         this.startFollowing(((EntityFroststalker.SchoolSpawnGroupData)p_27531_).leader);
      }

      return p_27531_;
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_146743_, AgeableMob p_146744_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.FROSTSTALKER.get()).m_20615_(p_146743_);
   }

   @Override
   public boolean shouldEnterWater() {
      return !this.hasSpikes() && (this.m_5448_() == null || !this.m_5448_().m_6084_());
   }

   @Override
   public boolean shouldLeaveWater() {
      return this.hasSpikes() || this.m_5448_() != null && this.m_5448_().m_6084_();
   }

   @Override
   public boolean shouldStopMoving() {
      return false;
   }

   @Override
   public int getWaterSearchRange() {
      return 10;
   }

   private class AIAvoidFire extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      protected int runDelay = 20;
      private Vec3 fleeTarget;

      private AIAvoidFire() {
         this.searchLength = 20;
         this.verticalSearchRange = 1;
      }

      public boolean m_8045_() {
         return this.destinationBlock != null && this.isFire(EntityFroststalker.this.m_9236_(), this.destinationBlock.m_122032_()) && this.isCloseToFire(16.0);
      }

      public boolean isCloseToFire(double dist) {
         return this.destinationBlock == null || EntityFroststalker.this.m_20238_(Vec3.m_82512_(this.destinationBlock)) < dist * dist;
      }

      public boolean m_8036_() {
         if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 30 + EntityFroststalker.this.f_19796_.m_188503_(100);
            return this.searchForDestination();
         }
      }

      public void m_8056_() {
         EntityFroststalker.this.fleeFireFlag = 200;
         Vec3 vec = LandRandomPos.m_148521_(EntityFroststalker.this, 15, 5, Vec3.m_82512_(this.destinationBlock));
         if (vec != null) {
            EntityFroststalker.this.standFor(100 + EntityFroststalker.this.f_19796_.m_188503_(100));
            this.fleeTarget = vec;
            EntityFroststalker.this.m_21573_().m_26519_(vec.f_82479_, vec.f_82480_, vec.f_82481_, 1.2F);
         }
      }

      public void m_8037_() {
         if (this.isCloseToFire(16.0)) {
            EntityFroststalker.this.fleeFireFlag = 200;
            if (this.fleeTarget == null || EntityFroststalker.this.m_20238_(this.fleeTarget) < 2.0) {
               Vec3 vec = LandRandomPos.m_148521_(EntityFroststalker.this, 15, 5, Vec3.m_82512_(this.destinationBlock));
               if (vec != null) {
                  this.fleeTarget = vec;
               }
            }

            if (this.fleeTarget != null) {
               EntityFroststalker.this.m_21573_().m_26519_(this.fleeTarget.f_82479_, this.fleeTarget.f_82480_, this.fleeTarget.f_82481_, 1.0);
            }
         }
      }

      public void m_8041_() {
         this.fleeTarget = null;
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         int lvt_2_1_ = this.verticalSearchRange;
         BlockPos lvt_3_1_ = EntityFroststalker.this.m_20183_();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.m_122154_(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isFire(EntityFroststalker.this.m_9236_(), lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isFire(Level world, MutableBlockPos lvt_4_1_) {
         return world.m_8055_(lvt_4_1_).m_204336_(AMTagRegistry.FROSTSTALKER_FEARS);
      }
   }

   public static class SchoolSpawnGroupData implements SpawnGroupData {
      public final EntityFroststalker leader;

      public SchoolSpawnGroupData(EntityFroststalker p_27553_) {
         this.leader = p_27553_;
      }
   }
}
