package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFleeAdult;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.KomodoDragonAIBreed;
import com.github.alexthe666.alexsmobs.entity.ai.KomodoDragonAIJostle;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class EntityKomodoDragon extends TamableAnimal implements ITargetsDroppedItems, IFollower {
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.m_204132_(AMTagRegistry.KOMODO_DRAGON_TAMEABLES);
   public int slaughterCooldown = 0;
   public int timeUntilSpit = this.f_19796_.m_188503_(12000) + 24000;
   public float nextJostleAngleFromServer;
   private int riderAttackCooldown = 0;
   public static final Predicate<EntityKomodoDragon> HURT_OR_BABY = p_213616_0_ -> p_213616_0_.m_6162_()
      || p_213616_0_.m_21223_() <= 0.7F * p_213616_0_.m_21233_();
   protected static final EntityDimensions JOSTLING_SIZE = EntityDimensions.m_20395_(1.35F, 1.85F);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntityKomodoDragon.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> JOSTLING = SynchedEntityData.m_135353_(EntityKomodoDragon.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> JOSTLE_ANGLE = SynchedEntityData.m_135353_(EntityKomodoDragon.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID = SynchedEntityData.m_135353_(EntityKomodoDragon.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.m_135353_(EntityKomodoDragon.class, EntityDataSerializers.f_135035_);
   public float prevJostleAngle;
   public float prevJostleProgress;
   public float jostleProgress;
   public float prevSitProgress;
   public float sitProgress;
   public boolean jostleDirection;
   public int jostleTimer = 0;
   public boolean instantlyTriggerJostleAI = false;
   public int jostleCooldown = 100 + this.f_19796_.m_188503_(40);
   private boolean hasJostlingSize;

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(JOSTLING, false);
      this.f_19804_.m_135372_(SADDLED, false);
      this.f_19804_.m_135372_(JOSTLE_ANGLE, 0.0F);
      this.f_19804_.m_135372_(JOSTLER_UUID, Optional.empty());
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public static <T extends Mob> boolean canKomodoDragonSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.KOMODO_DRAGON_SPAWNS);
      return spawnBlock && worldIn.m_45524_(pos, 0) > 8;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.komodoDragonSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(2, new MeleeAttackGoal(this, 2.0, false));
      this.f_21345_.m_25352_(3, new TameableAIFollowOwner(this, 1.2, 6.0F, 3.0F, false));
      this.f_21345_.m_25352_(4, new KomodoDragonAIJostle(this));
      this.f_21345_.m_25352_(5, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS, false));
      this.f_21345_.m_25352_(5, new AnimalAIFleeAdult(this, 1.25, 32.0));
      this.f_21345_.m_25352_(6, new KomodoDragonAIBreed(this, 1.0));
      this.f_21345_.m_25352_(6, new RandomStrollGoal(this, 1.0, 50));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new OwnerHurtByTargetGoal(this));
      this.f_21346_.m_25352_(2, new OwnerHurtTargetGoal(this));
      this.f_21346_.m_25352_(3, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(4, new CreatureAITargetItems(this, false));
      this.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(this, EntityKomodoDragon.class, 50, true, false, HURT_OR_BABY));
      this.f_21346_.m_25352_(7, new NearestAttackableTargetGoal(this, Player.class, 150, true, true, null));
      this.f_21346_
         .m_25352_(
            8,
            new EntityAINearestTarget3D<LivingEntity>(
               this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)
            )
         );
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
      return (float)(this.m_21133_(Attributes.f_22279_) * 2.0);
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
      return (SoundEvent)AMSoundRegistry.KOMODO_DRAGON_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.KOMODO_DRAGON_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.KOMODO_DRAGON_HURT.get();
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128441_("SpitTime")) {
         this.timeUntilSpit = compound.m_128451_("SpitTime");
      }

      this.setCommand(compound.m_128451_("KomodoCommand"));
      this.jostleCooldown = compound.m_128451_("JostlingCooldown");
      this.setSaddled(compound.m_128471_("Saddle"));
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128405_("SpitTime", this.timeUntilSpit);
      compound.m_128405_("KomodoCommand", this.getCommand());
      compound.m_128379_("Saddle", this.isSaddled());
      compound.m_128405_("JostlingCooldown", this.jostleCooldown);
   }

   public boolean m_6898_(ItemStack stack) {
      Item item = stack.m_41720_();
      return this.m_21824_() && stack.m_204117_(AMTagRegistry.KOMODO_DRAGON_BREEDABLES);
   }

   public void m_8119_() {
      this.prevJostleAngle = this.getJostleAngle();
      super.m_8119_();
      this.prevJostleProgress = this.jostleProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.slaughterCooldown > 0) {
         this.slaughterCooldown--;
      }

      if (!this.m_9236_().f_46443_ && this.m_6084_() && !this.m_6162_() && --this.timeUntilSpit <= 0) {
         this.m_19998_((ItemLike)AMItemRegistry.KOMODO_SPIT.get());
         this.timeUntilSpit = this.f_19796_.m_188503_(12000) + 24000;
      }

      if (this.riderAttackCooldown > 0) {
         this.riderAttackCooldown--;
      }

      if (this.m_6688_() != null && this.m_6688_() instanceof Player) {
         Player rider = (Player)this.m_6688_();
         if (rider.m_21214_() != null && this.m_20270_(rider.m_21214_()) < this.m_20205_() + 3.0F && !this.m_7307_(rider.m_21214_())) {
            UUID preyUUID = rider.m_21214_().m_20148_();
            if (!this.m_20148_().equals(preyUUID) && this.riderAttackCooldown == 0) {
               this.m_7327_(rider.m_21214_());
               this.riderAttackCooldown = 20;
            }
         }
      }

      if (!this.hasJostlingSize && this.isJostling()) {
         this.m_6210_();
         this.hasJostlingSize = true;
      }

      if (this.hasJostlingSize && !this.isJostling()) {
         this.m_6210_();
         this.hasJostlingSize = false;
      }

      if (this.isJostling()) {
         if (this.jostleProgress < 5.0F) {
            this.jostleProgress++;
         }
      } else if (this.jostleProgress > 0.0F) {
         this.jostleProgress--;
      }

      if (this.m_21827_()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.getCommand() == 2 && !this.m_20160_()) {
         this.m_21839_(true);
      } else {
         this.m_21839_(false);
      }

      if (this.jostleCooldown > 0) {
         this.jostleCooldown--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.getJostleAngle() < this.nextJostleAngleFromServer) {
            this.setJostleAngle(this.getJostleAngle() + 1.0F);
         }

         if (this.getJostleAngle() > this.nextJostleAngleFromServer) {
            this.setJostleAngle(this.getJostleAngle() - 1.0F);
         }
      }
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isJostling() && !this.m_6162_() ? JOSTLING_SIZE.m_20388_(this.m_6134_()) : super.m_6972_(poseIn);
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

   public boolean m_7327_(Entity entityIn) {
      if (super.m_7327_(entityIn)) {
         if (entityIn instanceof LivingEntity) {
            int i = 5;
            if (this.m_9236_().m_46791_() == Difficulty.NORMAL) {
               i = 10;
            } else if (this.m_9236_().m_46791_() == Difficulty.HARD) {
               i = 20;
            }

            ((LivingEntity)entityIn).m_7292_(new MobEffectInstance(MobEffects.f_19614_, i * 20, 0));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean m_7301_(MobEffectInstance potioneffectIn) {
      return potioneffectIn.m_19544_() == MobEffects.f_19614_ ? false : super.m_7301_(potioneffectIn);
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

   public void m_19956_(Entity passenger, MoveFunction moveFunc) {
      if (this.m_20363_(passenger)) {
         float radius = 0.0F;
         float angle = (float) (Math.PI / 180.0) * this.f_20883_;
         double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
         double extraZ = radius * Mth.m_14089_(angle);
         passenger.m_6034_(this.m_20185_() + extraX, this.m_20186_() + this.m_6048_() + passenger.m_6049_(), this.m_20189_() + extraZ);
      }
   }

   public double m_6048_() {
      float f = Math.min(0.25F, this.f_267362_.m_267731_());
      float f1 = this.f_267362_.m_267756_();
      return this.m_20206_() - 0.2 + 0.12F * Mth.m_14089_(f1 * 0.7F) * 0.7F * f;
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (itemstack.m_204117_(AMTagRegistry.KOMODO_DRAGON_TAMEABLES)) {
         if (!this.m_21824_()) {
            int size = itemstack.m_41613_();
            int tameAmount = 58 + this.f_19796_.m_188503_(16);
            if (size > tameAmount) {
               this.m_21828_(player);
            }

            itemstack.m_41774_(size);
            return InteractionResult.SUCCESS;
         }

         if (this.m_21223_() <= this.m_21233_()) {
            this.m_142075_(player, hand, itemstack);
            this.m_5634_(10.0F);
            return InteractionResult.SUCCESS;
         }
      }

      InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
      if (interactionresult == InteractionResult.SUCCESS || type == InteractionResult.SUCCESS || !this.m_21824_() || !this.m_21830_(player)) {
         return type;
      } else if (this.m_6898_(itemstack)) {
         this.m_27601_(600);
         this.m_142075_(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else if (itemstack.m_41720_() == Items.f_42450_ && !this.isSaddled()) {
         this.m_142075_(player, hand, itemstack);
         this.setSaddled(true);
         return InteractionResult.SUCCESS;
      } else if (itemstack.m_204117_(net.minecraftforge.common.Tags.Items.SHEARS) && this.isSaddled()) {
         this.setSaddled(false);
         this.m_19998_(Items.f_42450_);
         return InteractionResult.SUCCESS;
      } else if (!player.m_6144_() && !this.m_6162_() && this.isSaddled()) {
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
            this.m_21839_(true);
            return InteractionResult.SUCCESS;
         } else {
            this.m_21839_(false);
            return InteractionResult.SUCCESS;
         }
      }
   }

   protected EntityKomodoDragon(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected float m_6108_() {
      return 0.98F;
   }

   public void m_6710_(@Nullable LivingEntity entitylivingbaseIn) {
      if (!this.m_6162_() || this.slaughterCooldown > 0) {
         super.m_6710_(entitylivingbaseIn);
      }
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22284_, 0.0)
         .m_22268_(Attributes.f_22281_, 4.0)
         .m_22268_(Attributes.f_22279_, 0.23F);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.KOMODO_DRAGON.get()).m_20615_(p_241840_1_);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.KOMODO_DRAGON_TAMEABLES) || stack.m_41720_().m_41473_() != null && stack.m_41720_().m_41473_().m_38746_();
   }

   public boolean isSaddled() {
      return (Boolean)this.f_19804_.m_135370_(SADDLED);
   }

   public void setSaddled(boolean saddled) {
      this.f_19804_.m_135381_(SADDLED, saddled);
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

   public void pushBackJostling(EntityKomodoDragon entityMoose, float strength) {
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

   public boolean canJostleWith(EntityKomodoDragon moose) {
      return !moose.m_21827_() && !moose.m_20160_() && !moose.m_6162_() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
   }

   public void playJostleSound() {
   }

   protected void m_5907_() {
      super.m_5907_();
      if (this.isSaddled() && !this.m_9236_().f_46443_) {
         this.m_19998_(Items.f_42450_);
      }

      this.setSaddled(false);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.m_5634_(10.0F);
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public boolean isMaid() {
      String s = ChatFormatting.m_126649_(this.m_7755_().getString());
      return s != null && (s.toLowerCase().contains("maid") || s.toLowerCase().contains("coda"));
   }
}
