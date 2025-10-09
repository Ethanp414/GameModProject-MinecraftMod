package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.MimiCubeAIRangedAttack;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;

public class EntityMimicube extends Monster implements RangedAttackMob {
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.m_135353_(EntityMimicube.class, EntityDataSerializers.f_135028_);
   private final MimiCubeAIRangedAttack aiArrowAttack = new MimiCubeAIRangedAttack(this, 1.0, 10, 15.0F);
   private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2, false);
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   public float leftSwapProgress = 0.0F;
   public float prevLeftSwapProgress = 0.0F;
   public float rightSwapProgress = 0.0F;
   public float prevRightSwapProgress = 0.0F;
   public float helmetSwapProgress = 0.0F;
   public float prevHelmetSwapProgress = 0.0F;
   public float prevAttackProgress;
   public float attackProgress;
   private boolean wasOnGround;
   private int eatingTicks;

   protected EntityMimicube(EntityType type, Level world) {
      super(type, world);
      this.f_21342_ = new EntityMimicube.MimicubeMoveHelper(this);
      this.f_21344_ = new DirectPathNavigator(this, world);
      this.setCombatTask();
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22281_, 2.0)
         .m_22268_(Attributes.f_22279_, 0.45F);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mimicubeSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(ATTACK_TICK, 0);
   }

   public boolean m_7327_(Entity entityIn) {
      this.f_19804_.m_135381_(ATTACK_TICK, 5);
      return true;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new AnimalAIWanderRanged(this, 60, 1.0, 10, 7));
      this.f_21345_.m_25352_(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(2, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
   }

   public void setCombatTask() {
      if (this.m_9236_() != null && !this.m_9236_().f_46443_) {
         this.f_21345_.m_25363_(this.aiAttackOnCollide);
         this.f_21345_.m_25363_(this.aiArrowAttack);
         ItemStack itemstack = this.m_21205_();
         if (!(itemstack.m_41720_() instanceof ProjectileWeaponItem) && !(itemstack.m_41720_() instanceof TridentItem)) {
            this.f_21345_.m_25352_(4, this.aiAttackOnCollide);
         } else {
            int i = 10;
            if (this.m_9236_().m_46791_() != Difficulty.HARD) {
               i = 30;
            }

            this.aiArrowAttack.setAttackCooldown(i);
            this.f_21345_.m_25352_(4, this.aiArrowAttack);
         }
      }
   }

   public void attackEntityWithRangedAttackTrident(LivingEntity target, float distanceFactor) {
      ThrownTrident tridententity = new ThrownTrident(this.m_9236_(), this, new ItemStack(Items.f_42713_));
      double d0 = target.m_20185_() - this.m_20185_();
      double d1 = target.m_20227_(0.3333333333333333) - tridententity.m_20186_();
      double d2 = target.m_20189_() - this.m_20189_();
      double d3 = Mth.m_14116_((float)(d0 * d0 + d2 * d2));
      tridententity.m_6686_(d0, d1 + d3 * 0.2F, d2, 1.6F, 14 - this.m_9236_().m_46791_().m_19028_() * 4);
      this.m_146850_(GameEvent.f_157778_);
      this.m_5496_(SoundEvents.f_11821_, 1.0F, 1.0F / (this.m_217043_().m_188501_() * 0.4F + 0.8F));
      this.m_9236_().m_7967_(tridententity);
   }

   public void m_6504_(LivingEntity target, float distanceFactor) {
      if (this.m_21205_().m_41720_() instanceof TridentItem) {
         this.attackEntityWithRangedAttackTrident(target, distanceFactor);
      } else {
         ItemStack itemstack = this.m_6298_(this.m_21205_());
         AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
         if (this.m_21205_().m_41720_() instanceof BowItem) {
            abstractarrowentity = ((BowItem)this.m_21205_().m_41720_()).customArrow(abstractarrowentity);
         }

         double d0 = target.m_20185_() - this.m_20185_();
         double d1 = target.m_20227_(0.3333333333333333) - abstractarrowentity.m_20186_();
         double d2 = target.m_20189_() - this.m_20189_();
         double d3 = Mth.m_14116_((float)(d0 * d0 + d2 * d2));
         abstractarrowentity.m_6686_(d0, d1 + d3 * 0.2F, d2, 1.6F, 14 - this.m_9236_().m_46791_().m_19028_() * 4);
         this.m_146850_(GameEvent.f_157778_);
         this.m_5496_(SoundEvents.f_12382_, 1.0F, 1.0F / (this.m_217043_().m_188501_() * 0.4F + 0.8F));
         this.m_9236_().m_7967_(abstractarrowentity);
      }
   }

   protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor) {
      return ProjectileUtil.m_37300_(this, arrowStack, distanceFactor);
   }

   public boolean m_5886_(ProjectileWeaponItem p_230280_1_) {
      return p_230280_1_ == Items.f_42411_;
   }

   public void m_8061_(EquipmentSlot slotIn, ItemStack stack) {
      switch (slotIn) {
         case HEAD:
            if (!ItemStack.m_41656_(stack, this.m_6844_(EquipmentSlot.HEAD))) {
               this.helmetSwapProgress = 5.0F;
               this.m_9236_().m_7605_(this, (byte)45);
            }
            break;
         case MAINHAND:
            if (!ItemStack.m_41656_(stack, this.m_6844_(EquipmentSlot.MAINHAND))) {
               this.rightSwapProgress = 5.0F;
               this.m_9236_().m_7605_(this, (byte)46);
            }
            break;
         case OFFHAND:
            if (!ItemStack.m_41656_(stack, this.m_6844_(EquipmentSlot.OFFHAND))) {
               this.leftSwapProgress = 5.0F;
               this.m_9236_().m_7605_(this, (byte)47);
            }
      }

      super.m_8061_(slotIn, stack);
      if (!this.m_9236_().f_46443_) {
         this.setCombatTask();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      super.m_7822_(id);
      switch (id) {
         case 45:
            this.helmetSwapProgress = 5.0F;
            break;
         case 46:
            this.rightSwapProgress = 5.0F;
            break;
         case 47:
            this.leftSwapProgress = 5.0F;
      }
   }

   public boolean m_21254_() {
      return this.m_21205_().canPerformAction(ToolActions.SHIELD_BLOCK) || this.m_21206_().canPerformAction(ToolActions.SHIELD_BLOCK);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      Entity trueSource = source.m_7639_();
      if (trueSource != null && trueSource instanceof LivingEntity attacker) {
         if (!attacker.m_6844_(EquipmentSlot.HEAD).m_41619_()) {
            this.m_8061_(EquipmentSlot.HEAD, this.mimicStack(attacker.m_6844_(EquipmentSlot.HEAD)));
         }

         if (!attacker.m_6844_(EquipmentSlot.OFFHAND).m_41619_()) {
            this.m_8061_(EquipmentSlot.OFFHAND, this.mimicStack(attacker.m_6844_(EquipmentSlot.OFFHAND)));
         }

         if (!attacker.m_6844_(EquipmentSlot.MAINHAND).m_41619_()) {
            this.m_8061_(EquipmentSlot.MAINHAND, this.mimicStack(attacker.m_6844_(EquipmentSlot.MAINHAND)));
         }
      }

      return super.m_6469_(source, amount);
   }

   private ItemStack mimicStack(ItemStack stack) {
      ItemStack copy = stack.m_41777_();
      if (copy.m_41763_()) {
         copy.m_41721_(copy.m_41776_());
      }

      return copy;
   }

   public void m_8119_() {
      super.m_8119_();
      this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      this.prevHelmetSwapProgress = this.helmetSwapProgress;
      this.prevRightSwapProgress = this.rightSwapProgress;
      this.prevLeftSwapProgress = this.leftSwapProgress;
      this.prevAttackProgress = this.attackProgress;
      if (this.rightSwapProgress > 0.0F) {
         this.rightSwapProgress -= 0.5F;
      }

      if (this.leftSwapProgress > 0.0F) {
         this.leftSwapProgress -= 0.5F;
      }

      if (this.helmetSwapProgress > 0.0F) {
         this.helmetSwapProgress -= 0.5F;
      }

      if (this.m_20096_() && !this.wasOnGround) {
         for (int j = 0; j < 8; j++) {
            float f = this.f_19796_.m_188501_() * (float) (Math.PI * 2);
            float f1 = this.f_19796_.m_188501_() * 0.5F + 0.5F;
            float f2 = Mth.m_14031_(f) * 0.5F * f1;
            float f3 = Mth.m_14089_(f) * 0.5F * f1;
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, new ItemStack((ItemLike)AMItemRegistry.MIMICREAM.get())),
                  this.m_20185_() + f2,
                  this.m_20186_(),
                  this.m_20189_() + f3,
                  0.0,
                  0.0,
                  0.0
               );
         }

         this.m_5496_(this.getSquishSound(), this.m_6121_(), ((this.f_19796_.m_188501_() - this.f_19796_.m_188501_()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.35F;
      } else if (!this.m_20096_() && this.wasOnGround) {
         this.squishAmount = 2.0F;
      }

      if (this.m_20069_()) {
         this.m_20256_(this.m_20184_().m_82520_(0.0, 0.05, 0.0));
      }

      if (this.m_21206_().m_41720_().m_41472_() && this.m_21223_() < this.m_21233_()) {
         if (this.eatingTicks < 100) {
            for (int i = 0; i < 3; i++) {
               double d2 = this.f_19796_.m_188583_() * 0.02;
               double d0 = this.f_19796_.m_188583_() * 0.02;
               double d1 = this.f_19796_.m_188583_() * 0.02;
               this.m_9236_()
                  .m_7106_(
                     new ItemParticleOption(ParticleTypes.f_123752_, this.m_21120_(InteractionHand.OFF_HAND)),
                     this.m_20185_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     this.m_20186_() + this.m_20206_() * 0.5F + this.f_19796_.m_188501_() * this.m_20206_() * 0.5F,
                     this.m_20189_() + this.f_19796_.m_188501_() * this.m_20205_() - this.m_20205_() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            if (this.eatingTicks % 6 == 0) {
               this.m_146850_(GameEvent.f_157806_);
               this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
            }

            this.eatingTicks++;
         }

         if (this.eatingTicks == 100) {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_12321_, this.m_6121_(), this.m_6100_());
            this.m_21206_().m_41774_(1);
            this.m_5634_(5.0F);
            this.eatingTicks = 0;
         }
      } else if (this.m_21205_().m_41720_().m_41472_() && this.m_21223_() < this.m_21233_()) {
         if (this.eatingTicks < 100) {
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

            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
            if (this.eatingTicks % 6 == 0) {
               this.m_146850_(GameEvent.f_157806_);
               this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
            }

            this.eatingTicks++;
         }

         if (this.eatingTicks == 100) {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_12321_, this.m_6121_(), this.m_6100_());
            this.m_21205_().m_41774_(1);
            this.m_5634_(5.0F);
         }
      } else {
         this.eatingTicks = 0;
      }

      this.wasOnGround = this.m_20096_();
      this.alterSquishAmount();
      LivingEntity livingentity = this.m_5448_();
      if (livingentity != null && this.m_20280_(livingentity) < 144.0) {
         this.f_21342_.m_6849_(livingentity.m_20185_(), livingentity.m_20186_(), livingentity.m_20189_(), this.f_21342_.m_24999_());
         this.wasOnGround = true;
      }

      if ((Integer)this.f_19804_.m_135370_(ATTACK_TICK) > 0) {
         if ((Integer)this.f_19804_.m_135370_(ATTACK_TICK) == 2 && this.m_5448_() != null && this.m_20270_(this.m_5448_()) < 2.3) {
            super.m_7327_(this.m_5448_());
         }

         this.f_19804_.m_135381_(ATTACK_TICK, (Integer)this.f_19804_.m_135370_(ATTACK_TICK) - 1);
         if (this.attackProgress < 3.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }
   }

   protected float m_21519_(EquipmentSlot slotIn) {
      return 0.0F;
   }

   private SoundEvent getSquishSound() {
      return (SoundEvent)AMSoundRegistry.MIMICUBE_JUMP.get();
   }

   private SoundEvent getJumpSound() {
      return (SoundEvent)AMSoundRegistry.MIMICUBE_JUMP.get();
   }

   protected void m_6135_() {
      Vec3 vector3d = this.m_20184_();
      this.m_20334_(vector3d.f_82479_, this.m_6118_(), vector3d.f_82481_);
      this.f_19812_ = true;
   }

   protected int getJumpDelay() {
      return this.f_19796_.m_188503_(20) + 10;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   public boolean shouldShoot() {
      return this.m_21205_().m_41720_() instanceof ProjectileWeaponItem || this.m_21205_().m_41720_() instanceof TridentItem;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.MIMICUBE_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.MIMICUBE_HURT.get();
   }

   private static class MimicubeMoveHelper extends MoveControl {
      private final EntityMimicube slime;
      private float yRot;
      private int jumpDelay;
      private boolean isAggressive;

      public MimicubeMoveHelper(EntityMimicube slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
         this.yRot = 180.0F * slimeIn.m_146908_() / (float) Math.PI;
      }

      public void setDirection(float yRotIn, boolean aggressive) {
         this.yRot = yRotIn;
         this.isAggressive = aggressive;
      }

      public void setSpeed(double speedIn) {
         this.f_24978_ = speedIn;
         this.f_24981_ = Operation.MOVE_TO;
      }

      public void m_8126_() {
         if (this.f_24974_.m_20096_()) {
            this.f_24974_.m_7910_((float)(this.f_24978_ * this.f_24974_.m_21133_(Attributes.f_22279_)));
            if (this.jumpDelay-- <= 0 && this.f_24981_ != Operation.WAIT) {
               this.jumpDelay = this.slime.getJumpDelay();
               if (this.f_24974_.m_5448_() != null) {
                  this.jumpDelay /= 3;
               }

               this.slime.m_21569_().m_24901_();
               this.slime.m_5496_(this.slime.getJumpSound(), this.slime.m_6121_(), this.slime.m_6100_());
            } else {
               this.slime.f_20900_ = 0.0F;
               this.slime.f_20902_ = 0.0F;
               this.f_24974_.m_7910_(0.0F);
            }
         }

         super.m_8126_();
      }
   }
}
