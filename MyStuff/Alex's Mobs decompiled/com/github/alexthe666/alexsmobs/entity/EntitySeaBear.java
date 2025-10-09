package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySeaBear extends WaterAnimal implements IAnimatedEntity {
   public static final Animation ANIMATION_ATTACK = Animation.create(17);
   public static final Animation ANIMATION_POINT = Animation.create(25);
   public float prevOnLandProgress;
   public float onLandProgress;
   public int circleCooldown = 0;
   private int animationTick;
   private Animation currentAnimation;
   private BlockPos lastCircle = null;
   public static final Predicate<LivingEntity> SOMBRERO = player -> player.m_6844_(EquipmentSlot.HEAD).m_150930_((Item)AMItemRegistry.SOMBRERO.get());

   protected EntitySeaBear(EntityType entityType, Level level) {
      super(entityType, level);
      this.f_21342_ = new AquaticMoveController(this, 1.0F, 10.0F);
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return !this.m_8023_();
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.m_8077_();
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 200.0).m_22268_(Attributes.f_22281_, 8.0).m_22268_(Attributes.f_22279_, 0.325F);
   }

   public static boolean isMobSafe(Entity entity) {
      if (entity instanceof Player && ((Player)entity).m_7500_()) {
         return true;
      } else {
         BlockState state = entity.m_9236_().m_8055_(entity.m_20183_().m_7495_());
         return state.m_60713_((Block)AMBlockRegistry.SAND_CIRCLE.get()) || state.m_60713_((Block)AMBlockRegistry.RED_SAND_CIRCLE.get());
      }
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.GRIZZLY_BEAR_DIE.get();
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(2, new EntitySeaBear.AttackAI());
      this.f_21345_.m_25352_(3, new EntitySeaBear.AvoidCircleAI());
      this.f_21345_.m_25352_(4, new AnimalAISwimBottom(this, 1.0, 7) {
         public boolean m_8036_() {
            return super.m_8036_() && EntitySeaBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION;
         }

         public boolean m_8045_() {
            return super.m_8045_() && EntitySeaBear.this.getAnimation() == IAnimatedEntity.NO_ANIMATION;
         }
      });
      this.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(this, LivingEntity.class, false, SOMBRERO));
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevOnLandProgress = this.onLandProgress;
      if (this.m_20069_()) {
         if (this.onLandProgress > 0.0F) {
            this.onLandProgress--;
         }
      } else if (this.onLandProgress < 5.0F) {
         this.onLandProgress++;
      }

      if (this.m_20096_() && !this.m_20069_()) {
         this.m_20256_(this.m_20184_().m_82520_((this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F, 0.5, (this.f_19796_.m_188501_() * 2.0F - 1.0F) * 0.2F));
         this.m_146922_(this.f_19796_.m_188501_() * 360.0F);
         this.m_6853_(false);
         this.f_19812_ = true;
      }

      if (this.circleCooldown > 0) {
         this.circleCooldown--;
         this.m_6710_(null);
         this.m_6703_(null);
      }

      if (this.getAnimation() == ANIMATION_POINT) {
         this.f_20883_ = this.m_6080_();
         this.f_20896_ = this.m_6080_();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new SemiAquaticPathNavigator(this, worldIn);
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_5829_() {
      return false;
   }

   public boolean m_7337_(Entity e) {
      return !isMobSafe(e);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.getAnimation() == ANIMATION_POINT) {
         super.m_7023_(Vec3.f_82478_);
      } else if (this.m_21515_() && this.m_20069_()) {
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

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_POINT, ANIMATION_ATTACK};
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public void m_6710_(@Nullable LivingEntity entity) {
      if (entity == null || !isMobSafe(entity)) {
         super.m_6710_(entity);
      }
   }

   public void m_7334_(Entity entity) {
      if (!isMobSafe(entity)) {
         super.m_7334_(entity);
      }
   }

   private class AttackAI extends Goal {
      public AttackAI() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         return EntitySeaBear.this.m_5448_() != null
            && EntitySeaBear.this.m_5448_().m_20072_()
            && EntitySeaBear.this.m_5448_().m_6084_()
            && (EntitySeaBear.this.circleCooldown == 0 || EntitySeaBear.this.getAnimation() == EntitySeaBear.ANIMATION_POINT);
      }

      public void m_8037_() {
         LivingEntity enemy = EntitySeaBear.this.m_5448_();
         if (EntitySeaBear.this.getAnimation() == EntitySeaBear.ANIMATION_POINT) {
            EntitySeaBear.this.m_21573_().m_26573_();
            EntitySeaBear.this.m_20256_(EntitySeaBear.this.m_20184_().m_82542_(0.0, 1.0, 0.0));
            EntitySeaBear.this.m_21391_(enemy, 360.0F, 50.0F);
         } else if (EntitySeaBear.isMobSafe(enemy) && EntitySeaBear.this.m_20270_(enemy) < 6.0F) {
            EntitySeaBear.this.circleCooldown = 100 + EntitySeaBear.this.f_19796_.m_188503_(100);
            EntitySeaBear.this.setAnimation(EntitySeaBear.ANIMATION_POINT);
            EntitySeaBear.this.m_21391_(enemy, 360.0F, 50.0F);
            EntitySeaBear.this.lastCircle = enemy.m_20183_();
         } else {
            EntitySeaBear.this.m_21573_().m_26519_(enemy.m_20185_(), enemy.m_20227_(0.5), enemy.m_20189_(), 1.6);
            if (EntitySeaBear.this.m_142582_(enemy) && EntitySeaBear.this.m_20270_(enemy) < 3.5F) {
               EntitySeaBear.this.setAnimation(EntitySeaBear.ANIMATION_ATTACK);
               if (EntitySeaBear.this.getAnimationTick() % 5 == 0) {
                  enemy.m_6469_(EntitySeaBear.this.m_269291_().m_269333_(EntitySeaBear.this), 6.0F);
               }
            }
         }
      }
   }

   private class AvoidCircleAI extends Goal {
      private Vec3 target = null;

      public AvoidCircleAI() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         return EntitySeaBear.this.circleCooldown > 0
            && EntitySeaBear.this.lastCircle != null
            && EntitySeaBear.this.getAnimation() != EntitySeaBear.ANIMATION_POINT;
      }

      public void m_8037_() {
         BlockPos pos = EntitySeaBear.this.lastCircle;
         if (this.target == null
            || EntitySeaBear.this.m_20238_(this.target) < 2.0
            || !EntitySeaBear.this.m_9236_().m_6425_(AMBlockPos.fromVec3(this.target).m_7494_()).m_205070_(FluidTags.f_13131_)) {
            this.target = DefaultRandomPos.m_148407_(EntitySeaBear.this, 20, 7, Vec3.m_82512_(pos));
         }

         if (this.target != null && EntitySeaBear.this.m_9236_().m_6425_(AMBlockPos.fromVec3(this.target).m_7494_()).m_205070_(FluidTags.f_13131_)) {
            EntitySeaBear.this.m_21573_().m_26519_(this.target.f_82479_, this.target.f_82480_, this.target.f_82481_, 1.0);
         }
      }
   }
}
