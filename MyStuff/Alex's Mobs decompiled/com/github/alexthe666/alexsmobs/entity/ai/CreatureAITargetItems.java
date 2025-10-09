package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreatureAITargetItems<T extends ItemEntity> extends TargetGoal {
   protected final CreatureAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super ItemEntity> targetEntitySelector;
   protected int executionChance;
   protected boolean mustUpdate;
   protected ItemEntity targetEntity;
   protected ITargetsDroppedItems hunter;
   private final int tickThreshold;
   private float radius = 9.0F;
   private int walkCooldown = 0;

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight) {
      this(creature, checkSight, false);
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, int tickThreshold) {
      this(creature, checkSight, false, tickThreshold, 9);
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 10, checkSight, onlyNearby, null, 0);
   }

   public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
      this(creature, 10, checkSight, onlyNearby, null, tickThreshold);
      this.radius = radius;
   }

   public CreatureAITargetItems(
      PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector, int ticksExisted
   ) {
      super(creature, checkSight, onlyNearby);
      this.executionChance = chance;
      this.tickThreshold = ticksExisted;
      this.hunter = (ITargetsDroppedItems)creature;
      this.theNearestAttackableTargetSorter = new CreatureAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<ItemEntity>() {
         public boolean apply(@Nullable ItemEntity item) {
            ItemStack stack = item.m_32055_();
            return !stack.m_41619_() && CreatureAITargetItems.this.hunter.canTargetItem(stack) && item.f_19797_ > CreatureAITargetItems.this.tickThreshold;
         }
      };
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public boolean m_8036_() {
      if (!this.f_26135_.m_20159_() && (!this.f_26135_.m_20160_() || this.f_26135_.m_6688_() == null)) {
         if (!this.f_26135_.m_21120_(InteractionHand.MAIN_HAND).m_41619_()) {
            return false;
         } else {
            if (!this.mustUpdate) {
               long worldTime = this.f_26135_.m_9236_().m_46467_() % 10L;
               if (this.f_26135_.m_21216_() >= 100 && worldTime != 0L) {
                  return false;
               }

               if (this.f_26135_.m_217043_().m_188503_(this.executionChance) != 0 && worldTime != 0L) {
                  return false;
               }
            }

            List<ItemEntity> list = this.f_26135_.m_9236_().m_6443_(ItemEntity.class, this.getTargetableArea(this.m_7623_()), this.targetEntitySelector);
            if (list.isEmpty()) {
               return false;
            } else {
               Collections.sort(list, this.theNearestAttackableTargetSorter);
               this.targetEntity = list.get(0);
               this.mustUpdate = false;
               this.hunter.onFindTarget(this.targetEntity);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   protected double m_7623_() {
      return 16.0;
   }

   protected AABB getTargetableArea(double targetDistance) {
      Vec3 renderCenter = new Vec3(this.f_26135_.m_20185_() + 0.5, this.f_26135_.m_20186_() + 0.5, this.f_26135_.m_20189_() + 0.5);
      AABB aabb = new AABB(-this.radius, -this.radius, -this.radius, this.radius, this.radius, this.radius);
      return aabb.m_82383_(renderCenter);
   }

   public void m_8056_() {
      this.moveTo();
      super.m_8056_();
   }

   protected void moveTo() {
      if (this.walkCooldown > 0) {
         this.walkCooldown--;
      } else {
         this.f_26135_.m_21573_().m_26519_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
         this.walkCooldown = 30 + this.f_26135_.m_217043_().m_188503_(40);
      }
   }

   public void m_8041_() {
      super.m_8041_();
      this.f_26135_.m_21573_().m_26573_();
      this.targetEntity = null;
   }

   public void m_8037_() {
      super.m_8037_();
      if (this.targetEntity != null && (this.targetEntity == null || this.targetEntity.m_6084_())) {
         this.moveTo();
      } else {
         this.m_8041_();
         this.f_26135_.m_21573_().m_26573_();
      }

      if (this.targetEntity != null && this.f_26135_.m_142582_(this.targetEntity) && this.f_26135_.m_20205_() > 2.0 && this.f_26135_.m_20096_()) {
         this.f_26135_.m_21566_().m_6849_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
      }

      if (this.targetEntity != null
         && this.targetEntity.m_6084_()
         && this.f_26135_.m_20280_(this.targetEntity) < this.hunter.getMaxDistToItem()
         && this.f_26135_.m_21120_(InteractionHand.MAIN_HAND).m_41619_()) {
         this.hunter.onGetItem(this.targetEntity);
         this.targetEntity.m_32055_().m_41774_(1);
         this.m_8041_();
      }
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public boolean m_8045_() {
      boolean path = this.f_26135_.m_20205_() > 2.0 || !this.f_26135_.m_21573_().m_26571_();
      return path && this.targetEntity != null && this.targetEntity.m_6084_();
   }

   public record Sorter(Entity theEntity) implements Comparator<Entity> {
      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.m_20280_(p_compare_1_);
         double d1 = this.theEntity.m_20280_(p_compare_2_);
         return Double.compare(d0, d1);
      }
   }
}
