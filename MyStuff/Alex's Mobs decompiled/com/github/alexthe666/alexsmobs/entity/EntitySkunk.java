package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntitySkunk extends Animal {
   public float prevSprayProgress;
   public float sprayProgress;
   private int prevSprayTime = 0;
   private int harassedTime;
   private int sprayCooldown;
   private Vec3 sprayAt;
   private static final EntityDataAccessor<Integer> SPRAY_TIME = SynchedEntityData.m_135353_(EntitySkunk.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> SPRAY_YAW = SynchedEntityData.m_135353_(EntitySkunk.class, EntityDataSerializers.f_135029_);

   protected EntitySkunk(EntityType<? extends Animal> type, Level level) {
      super(type, level);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 8.0).m_22268_(Attributes.f_22281_, 1.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(SPRAY_YAW, 0.0F);
      this.f_19804_.m_135372_(SPRAY_TIME, 0);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new EntitySkunk.SprayGoal());
      this.f_21345_.m_25352_(1, new PanicGoal(this, 1.5) {
         public void m_8037_() {
            super.m_8037_();
            EntitySkunk.this.harassedTime += 10;
         }
      });
      this.f_21345_.m_25352_(3, new TemptGoal(this, 1.1, Ingredient.m_204132_(AMTagRegistry.SKUNK_BREEDABLES), false));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new RandomStrollGoal(this, 1.0, 60));
      this.f_21345_.m_25352_(5, new FollowParentGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(7, new RandomLookAroundGoal(this));
      this.f_21345_
         .m_25352_(
            3,
            new AvoidEntityGoal(
               this, LivingEntity.class, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.SKUNK_FEARS), 10.0F, 1.3, 1.1, EntitySelector.f_20406_
            ) {
               public boolean m_8036_() {
                  return super.m_8036_() && EntitySkunk.this.getSprayTime() <= 0;
               }

               public boolean m_8045_() {
                  return super.m_8045_() && EntitySkunk.this.getSprayTime() <= 0;
               }

               public void m_8037_() {
                  super.m_8037_();
                  if (this.f_25016_ != null) {
                     EntitySkunk.this.sprayAt = this.f_25016_.m_20182_();
                  }

                  EntitySkunk.this.harassedTime += 4;
               }
            }
         );
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.skunkSpawnRolls, this.m_217043_(), spawnReasonIn) && super.m_5545_(worldIn, spawnReasonIn);
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SKUNK_BREEDABLES);
   }

   public float getSprayYaw() {
      return (Float)this.f_19804_.m_135370_(SPRAY_YAW);
   }

   public void setSprayYaw(float yaw) {
      this.f_19804_.m_135381_(SPRAY_YAW, yaw);
   }

   public int getSprayTime() {
      return (Integer)this.f_19804_.m_135370_(SPRAY_TIME);
   }

   public void setSprayTime(int time) {
      this.f_19804_.m_135381_(SPRAY_TIME, time);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SKUNK_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SKUNK_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SKUNK_HURT.get();
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevSprayProgress = this.sprayProgress;
      if (this.getSprayTime() > 0) {
         if (this.sprayProgress < 5.0F) {
            this.sprayProgress++;
         }

         this.setSprayTime(this.getSprayTime() - 1);
         if (this.getSprayTime() == 0) {
            this.spawnLingeringCloud();
         } else if (this.getSprayTime() % 6 == 0) {
            this.m_216990_((SoundEvent)AMSoundRegistry.SKUNK_SPRAY.get());
         }

         this.f_20883_ = this.m_146908_();
         this.m_146922_(this.approachRotation(this.getSprayYaw(), this.m_146908_() + 10.0F, 15.0F));
      }

      if (this.getSprayTime() <= 0 && this.sprayProgress > 0.0F) {
         this.sprayProgress--;
      }

      if (!this.m_9236_().f_46443_) {
         if (this.harassedTime > 200 && this.sprayCooldown == 0 && !this.m_6162_()) {
            this.harassedTime = 0;
            this.sprayCooldown = 200 + this.f_19796_.m_188503_(200);
            this.setSprayTime(60 + this.f_19796_.m_188503_(60));
         }

         if (this.harassedTime > 0) {
            this.harassedTime--;
         }

         if (this.sprayCooldown > 0) {
            this.sprayCooldown--;
         }

         Entity lastHurt = this.m_21188_();
         if (lastHurt != null) {
            this.sprayAt = lastHurt.m_20182_();
         }
      }

      this.prevSprayTime = this.getSprayTime();
   }

   private void spawnLingeringCloud() {
      Collection<MobEffectInstance> collection = this.m_21220_();
      if (!collection.isEmpty()) {
         float fartDistance = 2.5F;
         Vec3 modelBack = new Vec3(0.0, 0.4F, -2.5)
            .m_82496_(-this.m_146909_() * (float) (Math.PI / 180.0))
            .m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0));
         Vec3 fartAt = this.m_20182_().m_82549_(modelBack);
         AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.m_9236_(), fartAt.f_82479_, fartAt.f_82480_, fartAt.f_82481_);
         areaeffectcloud.m_19712_(2.5F);
         areaeffectcloud.m_19732_(-0.25F);
         areaeffectcloud.m_19740_(20);
         areaeffectcloud.m_19734_(areaeffectcloud.m_19748_() / 2);
         areaeffectcloud.m_19738_(-areaeffectcloud.m_19743_() / areaeffectcloud.m_19748_());

         for (MobEffectInstance mobeffectinstance : collection) {
            areaeffectcloud.m_19716_(new MobEffectInstance(mobeffectinstance));
         }

         this.m_9236_().m_7967_(areaeffectcloud);
      }
   }

   public void m_7822_(byte id) {
      if (id == 48) {
         Vec3 modelBack = new Vec3(0.0, 0.4F, -0.4F)
            .m_82496_(-this.m_146909_() * (float) (Math.PI / 180.0))
            .m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0));
         Vec3 particleFrom = this.m_20182_().m_82549_(modelBack);
         float scale = this.f_19796_.m_188501_() * 0.5F + 1.0F;
         Vec3 particleTo = modelBack.m_82542_(scale, 1.0, scale);

         for (int i = 0; i < 3; i++) {
            double d0 = this.f_19796_.m_188583_() * 0.1;
            double d1 = this.f_19796_.m_188583_() * 0.1;
            double d2 = this.f_19796_.m_188583_() * 0.1;
            this.m_9236_()
               .m_7106_(
                  (ParticleOptions)AMParticleRegistry.SMELLY.get(),
                  particleFrom.f_82479_,
                  particleFrom.f_82480_,
                  particleFrom.f_82481_,
                  particleTo.f_82479_ + d0,
                  particleTo.f_82480_ - 0.4F + d1,
                  particleTo.f_82481_ + d2
               );
         }
      } else {
         super.m_7822_(id);
      }
   }

   private float approachRotation(float current, float target, float max) {
      float f = Mth.m_14177_(target - current);
      if (f > max) {
         f = max;
      }

      if (f < -max) {
         f = -max;
      }

      return Mth.m_14177_(current + f);
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel level, AgeableMob mob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.SKUNK.get()).m_20615_(this.m_9236_());
   }

   private class SprayGoal extends Goal {
      private int actualSprayTime = 0;

      public SprayGoal() {
         this.m_7021_(EnumSet.of(Flag.LOOK, Flag.MOVE));
      }

      public boolean m_8036_() {
         return EntitySkunk.this.getSprayTime() > 0;
      }

      public void m_8041_() {
         this.actualSprayTime = 0;
      }

      public void m_8037_() {
         EntitySkunk.this.m_21573_().m_26573_();
         Vec3 sprayAt = this.getSprayAt();
         double d0 = EntitySkunk.this.m_20185_() - sprayAt.f_82479_;
         double d2 = EntitySkunk.this.m_20189_() - sprayAt.f_82481_;
         float f = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
         EntitySkunk.this.setSprayYaw(f);
         if (EntitySkunk.this.sprayProgress >= 5.0F) {
            EntitySkunk.this.m_9236_().m_7605_(EntitySkunk.this, (byte)48);
            if (this.actualSprayTime > 10 && EntitySkunk.this.f_19796_.m_188503_(2) == 0) {
               Vec3 skunkPos = new Vec3(EntitySkunk.this.m_20185_(), EntitySkunk.this.m_20188_(), EntitySkunk.this.m_20189_());
               float xAdd = EntitySkunk.this.f_19796_.m_188501_() * 20.0F - 10.0F;
               float yAdd = EntitySkunk.this.f_19796_.m_188501_() * 20.0F - 10.0F;
               float maxSprayDist = 5.0F;
               Vec3 modelBack = new Vec3(0.0, 0.0, -5.0)
                  .m_82496_((xAdd - EntitySkunk.this.m_146909_()) * (float) (Math.PI / 180.0))
                  .m_82524_((yAdd - EntitySkunk.this.m_146908_()) * (float) (Math.PI / 180.0));
               HitResult hitResult = EntitySkunk.this.m_9236_()
                  .m_45547_(new ClipContext(skunkPos, skunkPos.m_82549_(modelBack), Block.COLLIDER, Fluid.NONE, EntitySkunk.this));
               if (hitResult != null) {
                  BlockPos pos;
                  Direction dir;
                  if (hitResult instanceof BlockHitResult block) {
                     pos = block.m_82425_().m_121945_(block.m_82434_());
                     dir = block.m_82434_().m_122424_();
                  } else {
                     pos = AMBlockPos.fromVec3(hitResult.m_82450_());
                     dir = Direction.UP;
                  }

                  BlockState currentState = EntitySkunk.this.m_9236_().m_8055_(pos);
                  BlockState sprayState = ((MultifaceBlock)AMBlockRegistry.SKUNK_SPRAY.get())
                     .m_153940_(EntitySkunk.this.m_9236_().m_8055_(pos), EntitySkunk.this.m_9236_(), pos, dir);
                  if ((currentState.m_60795_() || currentState.m_247087_())
                     && sprayState != null
                     && sprayState.m_60713_((net.minecraft.world.level.block.Block)AMBlockRegistry.SKUNK_SPRAY.get())) {
                     EntitySkunk.this.m_9236_().m_46597_(pos, sprayState);
                  }

                  double sprayDist = hitResult.m_82450_().m_82546_(skunkPos).m_82553_() / 5.0;
                  AABB poisonBox = new AABB(skunkPos, skunkPos.m_82549_(modelBack.m_82490_(sprayDist)).m_82520_(0.0, 1.5, 0.0)).m_82400_(1.0);
                  Collection<MobEffectInstance> collection = EntitySkunk.this.m_21220_();

                  for (LivingEntity entity : EntitySkunk.this.m_9236_().m_45976_(LivingEntity.class, poisonBox)) {
                     if (!(entity instanceof EntitySkunk)) {
                        entity.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 300));
                        if (entity instanceof ServerPlayer serverPlayer) {
                           AMAdvancementTriggerRegistry.SKUNK_SPRAY.trigger(serverPlayer);
                        }

                        for (MobEffectInstance mobeffectinstance : collection) {
                           entity.m_7292_(new MobEffectInstance(mobeffectinstance));
                        }
                     }
                  }
               }
            }

            this.actualSprayTime++;
         }
      }

      private Vec3 getSprayAt() {
         Entity last = EntitySkunk.this.m_21188_();
         if (EntitySkunk.this.sprayAt != null) {
            return EntitySkunk.this.sprayAt;
         } else if (last != null) {
            return last.m_20182_();
         } else {
            Vec3 modelBack = new Vec3(0.0, 0.4F, -1.0)
               .m_82496_(-EntitySkunk.this.m_146909_() * (float) (Math.PI / 180.0))
               .m_82524_(-EntitySkunk.this.m_146908_() * (float) (Math.PI / 180.0));
            return EntitySkunk.this.m_20182_().m_82549_(modelBack);
         }
      }
   }
}
