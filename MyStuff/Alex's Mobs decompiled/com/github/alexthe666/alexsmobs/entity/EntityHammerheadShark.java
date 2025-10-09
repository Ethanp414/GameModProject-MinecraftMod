package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityHammerheadShark extends WaterAnimal {
   private static final Predicate<LivingEntity> INJURED_PREDICATE = mob -> mob.m_21223_() <= mob.m_21233_() / 2.0;

   protected EntityHammerheadShark(EntityType type, Level worldIn) {
      super(type, worldIn);
      this.f_21342_ = new AquaticMoveController(this, 1.0F);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.hammerheadSharkSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn);
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_11759_;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return SoundEvents.f_11761_;
   }

   public void m_7023_(Vec3 travelVector) {
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
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(1, new EntityHammerheadShark.CirclePreyGoal(this, 1.0F));
      this.f_21345_.m_25352_(4, new RandomSwimmingGoal(this, 0.6F, 7));
      this.f_21345_.m_25352_(4, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(8, new FollowBoatGoal(this));
      this.f_21345_.m_25352_(9, new AvoidEntityGoal(this, Guardian.class, 8.0F, 1.0, 1.0));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D<LivingEntity>(this, LivingEntity.class, 50, false, true, INJURED_PREDICATE));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, Squid.class, 50, false, true, null));
      this.f_21346_.m_25352_(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 80, false, true, null));
      this.f_21346_.m_25352_(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 70, false, true, null));
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() == Type.BLOCK;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22284_, 0.0)
         .m_22268_(Attributes.f_22281_, 5.0)
         .m_22268_(Attributes.f_22279_, 0.5);
   }

   public static <T extends Mob> boolean canHammerheadSharkSpawn(
      EntityType<EntityHammerheadShark> p_223364_0_, LevelAccessor p_223364_1_, MobSpawnType reason, BlockPos p_223364_3_, RandomSource p_223364_4_
   ) {
      return p_223364_3_.m_123342_() > 45 && p_223364_3_.m_123342_() < p_223364_1_.m_5736_()
         ? p_223364_1_.m_6425_(p_223364_3_).m_205070_(FluidTags.f_13131_)
         : false;
   }

   private static class CirclePreyGoal extends Goal {
      EntityHammerheadShark shark;
      float speed;
      float circlingTime = 0.0F;
      float circleDistance = 5.0F;
      float maxCirclingTime = 80.0F;
      boolean clockwise = false;

      public CirclePreyGoal(EntityHammerheadShark shark, float speed) {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.shark = shark;
         this.speed = speed;
      }

      public boolean m_8036_() {
         return this.shark.m_5448_() != null;
      }

      public boolean m_8045_() {
         return this.shark.m_5448_() != null;
      }

      public void m_8056_() {
         this.circlingTime = 0.0F;
         this.maxCirclingTime = 360 + this.shark.f_19796_.m_188503_(80);
         this.circleDistance = 5.0F + this.shark.f_19796_.m_188501_() * 5.0F;
         this.clockwise = this.shark.f_19796_.m_188499_();
      }

      public void m_8041_() {
         this.circlingTime = 0.0F;
         this.maxCirclingTime = 360 + this.shark.f_19796_.m_188503_(80);
         this.circleDistance = 5.0F + this.shark.f_19796_.m_188501_() * 5.0F;
         this.clockwise = this.shark.f_19796_.m_188499_();
      }

      public void m_8037_() {
         LivingEntity prey = this.shark.m_5448_();
         if (prey != null) {
            double dist = this.shark.m_20270_(prey);
            if (this.circlingTime >= this.maxCirclingTime) {
               this.shark.m_21391_(prey, 30.0F, 30.0F);
               this.shark.m_21573_().m_5624_(prey, 1.5);
               if (dist < 2.0) {
                  this.shark.m_7327_(prey);
                  if (this.shark.f_19796_.m_188501_() < 0.3F) {
                     this.shark.m_19983_(new ItemStack((ItemLike)AMItemRegistry.SHARK_TOOTH.get()));
                  }

                  this.m_8041_();
               }
            } else if (dist <= 25.0) {
               this.circlingTime++;
               BlockPos circlePos = this.getSharkCirclePos(prey);
               if (circlePos != null) {
                  this.shark.m_21573_().m_26519_(circlePos.m_123341_() + 0.5, circlePos.m_123342_() + 0.5, circlePos.m_123343_() + 0.5, 0.6);
               }
            } else {
               this.shark.m_21391_(prey, 30.0F, 30.0F);
               this.shark.m_21573_().m_5624_(prey, 0.8);
            }
         }
      }

      public BlockPos getSharkCirclePos(LivingEntity target) {
         float angle = (float) (Math.PI / 180.0) * (this.clockwise ? -this.circlingTime : this.circlingTime);
         double extraX = this.circleDistance * Mth.m_14031_(angle);
         double extraZ = this.circleDistance * Mth.m_14089_(angle);
         BlockPos ground = AMBlockPos.fromCoords(target.m_20185_() + 0.5 + extraX, this.shark.m_20186_(), target.m_20189_() + 0.5 + extraZ);
         return this.shark.m_9236_().m_6425_(ground).m_205070_(FluidTags.f_13131_) ? ground : null;
      }
   }
}
