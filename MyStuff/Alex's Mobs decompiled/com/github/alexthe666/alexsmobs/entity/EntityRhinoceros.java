package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRhinoceros extends Animal implements IAnimatedEntity {
   public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(35);
   public static final Animation ANIMATION_FLING = Animation.create(15);
   public static final Animation ANIMATION_SLASH = Animation.create(30);
   private static final EntityDataAccessor<String> APPLIED_POTION = SynchedEntityData.m_135353_(EntityRhinoceros.class, EntityDataSerializers.f_135030_);
   private static final EntityDataAccessor<Integer> POTION_LEVEL = SynchedEntityData.m_135353_(EntityRhinoceros.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> INFLICTED_COUNT = SynchedEntityData.m_135353_(EntityRhinoceros.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> POTION_DURATION = SynchedEntityData.m_135353_(EntityRhinoceros.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0 = SynchedEntityData.m_135353_(
      EntityRhinoceros.class, EntityDataSerializers.f_135041_
   );
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1 = SynchedEntityData.m_135353_(
      EntityRhinoceros.class, EntityDataSerializers.f_135041_
   );
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.m_135353_(EntityRhinoceros.class, EntityDataSerializers.f_135035_);
   private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap();
   private int animationTick;
   private Animation currentAnimation;

   protected EntityRhinoceros(EntityType type, Level level) {
      super(type, level);
      this.m_274367_(1.1F);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22281_, 8.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22279_, 0.25)
         .m_22268_(Attributes.f_22284_, 12.0)
         .m_22268_(Attributes.f_22285_, 4.0)
         .m_22268_(Attributes.f_22278_, 0.9)
         .m_22268_(Attributes.f_22282_, 2.0);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_TRUSTED_ID_0, Optional.empty());
      this.f_19804_.m_135372_(DATA_TRUSTED_ID_1, Optional.empty());
      this.f_19804_.m_135372_(APPLIED_POTION, "");
      this.f_19804_.m_135372_(POTION_LEVEL, 0);
      this.f_19804_.m_135372_(INFLICTED_COUNT, 0);
      this.f_19804_.m_135372_(POTION_DURATION, 0);
      this.f_19804_.m_135372_(ANGRY, false);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new MeleeAttackGoal(this, 1.4, true));
      this.f_21345_.m_25352_(2, new AnimalAIPanicBaby(this, 1.25));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_
         .m_25352_(
            4,
            new TemptGoal(
               this,
               1.0,
               Ingredient.m_43938_(Stream.of(new TagValue(AMTagRegistry.RHINOCEROS_FOODSTUFFS), new TagValue(AMTagRegistry.RHINOCEROS_BREEDABLES))),
               false
            )
         );
      this.f_21345_.m_25352_(5, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(6, new AnimalAIWanderRanged(this, 90, 1.0, 18, 7));
      this.f_21345_.m_25352_(7, new EntityRhinoceros.StrollGoal(200));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new EntityRhinoceros.DefendTrustedTargetGoal(LivingEntity.class, false, false, entity -> !this.trusts(entity.m_20148_())));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal<Raider>(this, Raider.class, 50, true, true, null) {
         public boolean m_8036_() {
            return super.m_8036_() && !EntityRhinoceros.this.m_6162_();
         }
      });
      this.f_21346_.m_25352_(3, new EntityRhinoceros.AIAttackNearPlayers());
      this.f_21346_.m_25352_(4, new AnimalAIHurtByTargetNotBaby(this));
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.rhinocerosSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public void m_8119_() {
      super.m_8119_();
      AnimationHandler.INSTANCE.updateAnimations(this);
      if (!this.m_9236_().f_46443_) {
         if (this.getAnimation() == NO_ANIMATION && (this.m_5448_() == null || !this.m_5448_().m_6084_())) {
            if (this.m_20184_().m_82556_() < 0.03
               && this.m_217043_().m_188503_(500) == 0
               && this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_)) {
               this.setAnimation(ANIMATION_EAT_GRASS);
            } else if (this.m_217043_().m_188503_(200) == 0) {
               this.setAnimation(ANIMATION_FLICK_EARS);
            }
         }

         if (this.getAnimation() == ANIMATION_EAT_GRASS
            && this.getAnimationTick() == 30
            && this.m_9236_().m_8055_(this.m_20183_().m_7495_()).m_60713_(Blocks.f_50440_)) {
            BlockPos down = this.m_20183_().m_7495_();
            this.m_9236_().m_46796_(2001, down, Block.m_49956_(Blocks.f_50440_.m_49966_()));
            this.m_9236_().m_7731_(down, Blocks.f_50493_.m_49966_(), 2);
            this.m_5634_(10.0F);
         }

         LivingEntity target = this.m_5448_();
         if (target != null && target.m_6084_()) {
            this.setAngry(this.m_20270_(target) < 20.0F);
            double dist = this.m_20270_(target);
            if (this.m_142582_(target)) {
               this.m_21391_(target, 30.0F, 30.0F);
               this.f_20883_ = this.m_146908_();
            }

            if (dist < this.m_20205_() + 3.0F) {
               if (this.getAnimation() == NO_ANIMATION) {
                  this.setAnimation(this.f_19796_.m_188499_() ? ANIMATION_SLASH : ANIMATION_FLING);
               }

               if (dist < this.m_20205_() + 1.5F && this.m_142582_(target)) {
                  if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 8) {
                     float dmg = (float)this.m_21051_(Attributes.f_22281_).m_22115_();
                     if (target instanceof Raider) {
                        dmg = 10.0F;
                     }

                     this.attackWithPotion(target, dmg);
                     this.launch(target, 0.0F, 1.0F);

                     for (LivingEntity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(1.0))) {
                        if (!(entity instanceof Animal) && !this.trusts(entity.m_20148_()) && entity != target) {
                           this.attackWithPotion(entity, Math.max(dmg - 5.0F, 1.0F));
                           this.launch(entity, 0.0F, 0.5F);
                        }
                     }
                  }

                  if (this.getAnimation() == ANIMATION_SLASH
                     && (this.getAnimationTick() >= 9 && this.getAnimationTick() <= 11 || this.getAnimationTick() >= 19 && this.getAnimationTick() <= 21)) {
                     float dmg = (float)this.m_21051_(Attributes.f_22281_).m_22115_();
                     if (target instanceof Raider) {
                        dmg = 10.0F;
                     }

                     this.attackWithPotion(target, dmg);
                     this.launch(target, this.getAnimationTick() <= 15 ? -90.0F : 90.0F, 1.0F);

                     for (LivingEntity entityx : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(1.0))) {
                        if (!(entityx instanceof Animal) && !this.trusts(entityx.m_20148_()) && entityx != target) {
                           this.attackWithPotion(entityx, Math.max(dmg - 5.0F, 1.0F));
                           this.launch(entityx, this.getAnimationTick() <= 15 ? -90.0F : 90.0F, 0.5F);
                        }
                     }
                  }
               }
            }
         } else {
            this.setAngry(false);
         }
      }
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
      if (!this.m_6162_()) {
         this.m_5496_((SoundEvent)AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.2F);
      } else {
         super.m_7355_(pos, state);
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.RHINOCEROS_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.RHINOCEROS_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.RHINOCEROS_HURT.get();
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.RHINOCEROS_BREEDABLES);
   }

   public String getAppliedPotionId() {
      return (String)this.f_19804_.m_135370_(APPLIED_POTION);
   }

   public void setAppliedPotionId(String potionId) {
      this.f_19804_.m_135381_(APPLIED_POTION, potionId);
   }

   public int getPotionColor() {
      String s = this.getAppliedPotionId();
      if (s.isEmpty()) {
         return -1;
      } else if (!potionToColor.containsKey(s)) {
         MobEffect effect = this.getPotionEffect();
         if (effect != null) {
            int color = effect.m_19484_();
            potionToColor.put(s, color);
            return color;
         } else {
            return -1;
         }
      } else {
         return potionToColor.getInt(s);
      }
   }

   public MobEffect getPotionEffect() {
      return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.getAppliedPotionId()));
   }

   public int getPotionDuration() {
      return (Integer)this.f_19804_.m_135370_(POTION_DURATION);
   }

   public void setPotionDuration(int time) {
      this.f_19804_.m_135381_(POTION_DURATION, time);
   }

   public int getPotionLevel() {
      return (Integer)this.f_19804_.m_135370_(POTION_LEVEL);
   }

   public void setPotionLevel(int time) {
      this.f_19804_.m_135381_(POTION_LEVEL, time);
   }

   public int getInflictedCount() {
      return (Integer)this.f_19804_.m_135370_(INFLICTED_COUNT);
   }

   public void setInflictedCount(int count) {
      this.f_19804_.m_135381_(INFLICTED_COUNT, count);
   }

   public void resetPotion() {
      this.setAppliedPotionId("");
      this.setPotionDuration(0);
      this.setPotionLevel(0);
      this.setInflictedCount(0);
   }

   private List<UUID> getTrustedUUIDs() {
      List<UUID> list = Lists.newArrayList();
      list.add((UUID)((Optional)this.f_19804_.m_135370_(DATA_TRUSTED_ID_0)).orElse((UUID)null));
      list.add((UUID)((Optional)this.f_19804_.m_135370_(DATA_TRUSTED_ID_1)).orElse((UUID)null));
      return list;
   }

   private void addTrustedUUID(@Nullable UUID p_28516_) {
      if (((Optional)this.f_19804_.m_135370_(DATA_TRUSTED_ID_0)).isPresent()) {
         this.f_19804_.m_135381_(DATA_TRUSTED_ID_1, Optional.ofNullable(p_28516_));
      } else {
         this.f_19804_.m_135381_(DATA_TRUSTED_ID_0, Optional.ofNullable(p_28516_));
      }
   }

   private void launch(Entity launch, float angle, float scale) {
      float rot = 180.0F + angle + this.m_146908_();
      float hugeScale = 1.0F + this.f_19796_.m_188501_() * 0.5F * scale;
      float strength = (float)(hugeScale * (1.0 - ((LivingEntity)launch).m_21133_(Attributes.f_22278_)));
      float rotRad = rot * (float) (Math.PI / 180.0);
      float x = Mth.m_14031_(rotRad);
      float z = -Mth.m_14089_(rotRad);
      launch.f_19812_ = true;
      Vec3 vec3 = this.m_20184_();
      Vec3 vec31 = vec3.m_82549_(new Vec3(x, 0.0, z).m_82541_().m_82490_(strength));
      launch.m_20334_(vec31.f_82479_, hugeScale * 0.3F, vec31.f_82481_);
      launch.m_6853_(false);
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

   private boolean trusts(UUID uuid) {
      return this.getTrustedUUIDs().contains(uuid);
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_EAT_GRASS, ANIMATION_FLING, ANIMATION_SLASH};
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob m_142606_(ServerLevel serverLevel, AgeableMob ageableMob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.RHINOCEROS.get()).m_20615_(serverLevel);
   }

   public boolean isAngry() {
      return (Boolean)this.f_19804_.m_135370_(ANGRY);
   }

   public void setAngry(boolean angry) {
      this.f_19804_.m_135381_(ANGRY, angry);
   }

   private void attackWithPotion(LivingEntity target, float dmg) {
      MobEffect potion = this.getPotionEffect();
      target.m_6469_(this.m_269291_().m_269333_(this), dmg);
      if (potion != null) {
         MobEffectInstance instance = new MobEffectInstance(potion, this.getPotionDuration(), this.getPotionLevel());
         if (!target.m_21023_(potion) && target.m_7292_(instance)) {
            this.setInflictedCount(this.getInflictedCount() + 1);
         }
      }

      if (this.getInflictedCount() > 15 && this.f_19796_.m_188503_(3) == 0 || this.getInflictedCount() > 20) {
         this.resetPotion();
      }
   }

   public boolean m_7327_(Entity entity) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(this.f_19796_.m_188499_() ? ANIMATION_SLASH : ANIMATION_FLING);
         return true;
      } else {
         return false;
      }
   }

   public boolean m_7307_(Entity entityIn) {
      return entityIn instanceof TamableAnimal tamableAnimal && tamableAnimal.m_21805_() != null && this.trusts(tamableAnimal.m_21805_())
         ? true
         : super.m_7307_(entityIn) || this.trusts(entityIn.m_20148_());
   }

   public void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      List<UUID> list = this.getTrustedUUIDs();
      ListTag listtag = new ListTag();

      for (UUID uuid : list) {
         if (uuid != null) {
            listtag.add(NbtUtils.m_129226_(uuid));
         }
      }

      tag.m_128365_("Trusted", listtag);
      tag.m_128379_("Sleeping", this.m_5803_());
      tag.m_128359_("PotionName", this.getAppliedPotionId());
      tag.m_128405_("PotionLevel", this.getPotionLevel());
      tag.m_128405_("PotionDuration", this.getPotionDuration());
      tag.m_128405_("InflictedCount", this.getInflictedCount());
   }

   public void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      ListTag listtag = tag.m_128437_("Trusted", 11);

      for (int i = 0; i < listtag.size(); i++) {
         this.addTrustedUUID(NbtUtils.m_129233_(listtag.get(i)));
      }

      this.setAppliedPotionId(tag.m_128461_("PotionName"));
      this.setPotionLevel(tag.m_128451_("PotionLevel"));
      this.setPotionDuration(tag.m_128451_("PotionDuration"));
      this.setInflictedCount(tag.m_128451_("InflictedCount"));
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (!this.m_6162_() && (itemstack.m_41720_() == Items.f_42589_ || itemstack.m_41720_() == Items.f_42736_ || itemstack.m_41720_() == Items.f_42739_)) {
         Potion contained = PotionUtils.m_43579_(itemstack);
         if (this.applyPotion(contained)) {
            this.m_146850_(GameEvent.f_223708_);
            this.m_216990_(SoundEvents.f_144133_);
            this.m_142075_(player, hand, itemstack);
            ItemStack bottle = new ItemStack(Items.f_42590_);
            if (!player.m_36356_(bottle)) {
               player.m_36176_(bottle, false);
            }

            return InteractionResult.SUCCESS;
         }
      } else if (itemstack.m_204117_(AMTagRegistry.RHINOCEROS_FOODSTUFFS) && !this.trusts(player.m_20148_())) {
         this.addTrustedUUID(player.m_20148_());
         this.m_142075_(player, hand, itemstack);
         this.m_146850_(GameEvent.f_157806_);
         this.m_216990_(SoundEvents.f_11976_);
         return InteractionResult.SUCCESS;
      }

      return type;
   }

   public boolean applyPotion(Potion potion) {
      if (potion != null && potion != Potions.f_43599_) {
         if (potion.m_43488_().size() >= 1) {
            MobEffectInstance first = (MobEffectInstance)potion.m_43488_().get(0);
            ResourceLocation loc = ForgeRegistries.MOB_EFFECTS.getKey(first.m_19544_());
            if (loc != null) {
               this.setAppliedPotionId(loc.toString());
               this.setPotionLevel(first.m_19564_());
               this.setPotionDuration(first.m_19557_());
               this.setInflictedCount(0);
               return true;
            }
         }

         return false;
      } else {
         this.resetPotion();
         return true;
      }
   }

   private boolean trustsAny() {
      return ((Optional)this.f_19804_.m_135370_(DATA_TRUSTED_ID_0)).isPresent() || ((Optional)this.f_19804_.m_135370_(DATA_TRUSTED_ID_1)).isPresent();
   }

   class AIAttackNearPlayers extends NearestAttackableTargetGoal<Player> {
      public AIAttackNearPlayers() {
         super(EntityRhinoceros.this, Player.class, 80, true, true, null);
      }

      public boolean m_8036_() {
         return !EntityRhinoceros.this.m_6162_() && !EntityRhinoceros.this.m_27593_() && !EntityRhinoceros.this.trustsAny() ? super.m_8036_() : false;
      }

      protected double m_7623_() {
         return 3.0;
      }
   }

   class DefendTrustedTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
      private LivingEntity trustedLastHurtBy;
      private LivingEntity trustedLastHurt;
      private LivingEntity trusted;
      private int timestamp;

      public DefendTrustedTargetGoal(Class<LivingEntity> entities, boolean b, @Nullable boolean b2, Predicate<LivingEntity> pred) {
         super(EntityRhinoceros.this, entities, 10, b, b2, pred);
      }

      public boolean m_8036_() {
         if ((this.f_26049_ <= 0 || this.f_26135_.m_217043_().m_188503_(this.f_26049_) == 0) && !this.f_26135_.m_6162_()) {
            for (UUID uuid : EntityRhinoceros.this.getTrustedUUIDs()) {
               if (uuid != null
                  && EntityRhinoceros.this.m_9236_() instanceof ServerLevel
                  && ((ServerLevel)EntityRhinoceros.this.m_9236_()).m_8791_(uuid) instanceof LivingEntity livingentity) {
                  this.trusted = livingentity;
                  this.trustedLastHurtBy = livingentity.m_21188_();
                  this.trustedLastHurt = livingentity.m_21214_();
                  int i = livingentity.m_21213_();
                  int j = livingentity.m_21215_();
                  if (i != this.timestamp && this.m_26150_(this.trustedLastHurtBy, this.f_26051_)) {
                     return true;
                  }

                  if (j != this.timestamp && this.m_26150_(this.trustedLastHurt, this.f_26051_)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void m_8056_() {
         if (this.trustedLastHurtBy != null) {
            this.m_26070_(this.trustedLastHurtBy);
            this.f_26050_ = this.trustedLastHurtBy;
            if (this.trusted != null) {
               this.timestamp = this.trusted.m_21213_();
            }
         } else {
            this.m_26070_(this.trustedLastHurt);
            this.f_26050_ = this.trustedLastHurt;
            if (this.trusted != null) {
               this.timestamp = this.trusted.m_21215_();
            }
         }

         super.m_8056_();
      }
   }

   class StrollGoal extends MoveThroughVillageGoal {
      public StrollGoal(int timr) {
         super(EntityRhinoceros.this, 1.0, true, timr, () -> false);
      }

      public void m_8056_() {
         super.m_8056_();
      }

      public boolean m_8036_() {
         return super.m_8036_() && this.canRhinoWander();
      }

      public boolean m_8045_() {
         return super.m_8045_() && this.canRhinoWander();
      }

      private boolean canRhinoWander() {
         return !EntityRhinoceros.this.getTrustedUUIDs().isEmpty();
      }
   }
}
