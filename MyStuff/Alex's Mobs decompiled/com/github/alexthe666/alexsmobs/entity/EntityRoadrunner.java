package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityRoadrunner extends Animal {
   public float oFlapSpeed;
   public float oFlap;
   public float wingRotDelta = 1.0F;
   public float wingRotation;
   public float destPos;
   public float prevAttackProgress;
   public float attackProgress;
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.m_135353_(EntityRoadrunner.class, EntityDataSerializers.f_135028_);
   public int timeUntilNextFeather = this.f_19796_.m_188503_(24000) + 24000;
   private boolean hasMeepSpeed = false;

   protected EntityRoadrunner(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new PanicGoal(this, 1.1));
      this.f_21345_.m_25352_(1, new MeleeAttackGoal(this, 1.0, false));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new FollowParentGoal(this, 1.1));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.ROADRUNNER_BREEDABLES), false));
      this.f_21345_.m_25352_(5, new AnimalAIWanderRanged(this, 50, 1.0, 25, 7));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(7, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(this, EntityRattlesnake.class, 55, true, true, null));
      this.f_21346_.m_25352_(2, new HurtByTargetGoal(this, new Class[]{EntityRattlesnake.class, Player.class}).m_26044_(new Class[0]));
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128441_("FeatherTime")) {
         this.timeUntilNextFeather = compound.m_128451_("FeatherTime");
      }
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.roadrunnerSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128405_("FeatherTime", this.timeUntilNextFeather);
   }

   protected SoundEvent m_7515_() {
      return !this.isMeep() && this.f_19796_.m_188503_(2000) != 0
         ? (SoundEvent)AMSoundRegistry.ROADRUNNER_IDLE.get()
         : (SoundEvent)AMSoundRegistry.ROADRUNNER_MEEP.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.ROADRUNNER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.ROADRUNNER_HURT.get();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(ATTACK_TICK, 0);
   }

   public boolean m_7327_(Entity entityIn) {
      this.f_19804_.m_135381_(ATTACK_TICK, 5);
      return true;
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268585_) || source.m_19385_().equals("anvil") || super.m_6673_(source);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 8.0)
         .m_22268_(Attributes.f_22281_, 1.0)
         .m_22268_(Attributes.f_22279_, 0.45F)
         .m_22268_(Attributes.f_22277_, 10.0);
   }

   public void m_8107_() {
      super.m_8107_();
      this.oFlap = this.wingRotation;
      this.prevAttackProgress = this.attackProgress;
      this.oFlapSpeed = this.destPos;
      this.destPos = (float)(this.destPos + (this.m_20096_() ? -1 : 4) * 0.3);
      this.destPos = Mth.m_14036_(this.destPos, 0.0F, 1.0F);
      if (!this.m_20096_() && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      if (!this.m_9236_().f_46443_ && this.m_6084_() && !this.m_6162_() && --this.timeUntilNextFeather <= 0) {
         this.m_19998_((ItemLike)AMItemRegistry.ROADRUNNER_FEATHER.get());
         this.timeUntilNextFeather = this.f_19796_.m_188503_(24000) + 24000;
      }

      this.wingRotDelta = (float)(this.wingRotDelta * 0.9);
      Vec3 vector3d = this.m_20184_();
      if (!this.m_20096_() && vector3d.f_82480_ < 0.0) {
         this.m_20256_(vector3d.m_82542_(1.0, 0.8, 1.0));
      }

      this.wingRotation = this.wingRotation + this.wingRotDelta * 2.0F;
      if ((Integer)this.f_19804_.m_135370_(ATTACK_TICK) > 0) {
         if ((Integer)this.f_19804_.m_135370_(ATTACK_TICK) == 2 && this.m_5448_() != null && this.m_20270_(this.m_5448_()) < 1.3) {
            this.m_5448_().m_6469_(this.m_269291_().m_269333_(this), 2.0F);
         }

         this.f_19804_.m_135381_(ATTACK_TICK, (Integer)this.f_19804_.m_135370_(ATTACK_TICK) - 1);
         if (this.attackProgress < 5.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.isMeep()) {
         if (!this.hasMeepSpeed) {
            this.m_21051_(Attributes.f_22279_).m_22100_(1.0);
            this.hasMeepSpeed = true;
         }
      } else if (this.hasMeepSpeed) {
         this.m_21051_(Attributes.f_22279_).m_22100_(0.45F);
         this.hasMeepSpeed = false;
      }

      if (this.m_9236_().f_46443_ && this.isMeep() && this.m_20096_() && !this.m_20072_() && this.m_20184_().m_82556_() > 0.03) {
         Vec3 vector3d = this.m_20252_(0.0F);
         float yRotRad = this.m_146908_() * (float) (Math.PI / 180.0);
         float f = Mth.m_14089_(yRotRad) * 0.2F;
         float f1 = Mth.m_14031_(yRotRad) * 0.2F;
         float f2 = 1.2F - this.f_19796_.m_188501_() * 0.7F;

         for (int i = 0; i < 2; i++) {
            this.m_9236_()
               .m_7106_(
                  ParticleTypes.f_123777_,
                  this.m_20185_() - vector3d.f_82479_ * f2 + f,
                  this.m_20186_() + this.f_19796_.m_188501_() * 0.2F,
                  this.m_20189_() - vector3d.f_82481_ * f2 + f1,
                  0.0,
                  0.0,
                  0.0
               );
            this.m_9236_()
               .m_7106_(
                  ParticleTypes.f_123777_,
                  this.m_20185_() - vector3d.f_82479_ * f2 - f,
                  this.m_20186_() + this.f_19796_.m_188501_() * 0.2F,
                  this.m_20189_() - vector3d.f_82481_ * f2 - f1,
                  0.0,
                  0.0,
                  0.0
               );
         }
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void m_7355_(BlockPos pos, BlockState blockIn) {
      if (!this.isMeep()) {
         this.m_5496_(SoundEvents.f_11754_, 0.15F, 1.0F);
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.ROADRUNNER_BREEDABLES);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return (AgeableMob)((EntityType)AMEntityRegistry.ROADRUNNER.get()).m_20615_(p_241840_1_);
   }

   public static boolean canRoadrunnerSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
      boolean spawnBlock = worldIn.m_8055_(pos.m_7495_()).m_204336_(AMTagRegistry.ROADRUNNER_SPAWNS);
      return spawnBlock && worldIn.m_45524_(pos, 0) > 8;
   }

   public boolean isMeep() {
      String s = ChatFormatting.m_126649_(this.m_7755_().getString());
      return s != null && s.toLowerCase().contains("meep") || AlexsMobs.isAprilFools();
   }
}
