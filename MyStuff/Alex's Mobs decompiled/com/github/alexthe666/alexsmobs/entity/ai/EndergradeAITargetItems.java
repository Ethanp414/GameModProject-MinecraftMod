package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EndergradeAITargetItems<T extends ItemEntity> extends TargetGoal {
   protected final EndergradeAITargetItems.Sorter theNearestAttackableTargetSorter;
   protected final Predicate<? super ItemEntity> targetEntitySelector;
   protected int executionChance;
   protected boolean mustUpdate;
   protected ItemEntity targetEntity;
   private EntityEndergrade endergrade;

   public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight) {
      this(creature, checkSight, false);
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight, boolean onlyNearby) {
      this(creature, 10, checkSight, onlyNearby, null);
   }

   public EndergradeAITargetItems(EntityEndergrade creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable Predicate<? super T> targetSelector) {
      super(creature, checkSight, onlyNearby);
      this.executionChance = chance;
      this.endergrade = creature;
      this.theNearestAttackableTargetSorter = new EndergradeAITargetItems.Sorter(creature);
      this.targetEntitySelector = new Predicate<ItemEntity>() {
         public boolean apply(@Nullable ItemEntity item) {
            ItemStack stack = item.m_32055_();
            return !stack.m_41619_() && EndergradeAITargetItems.this.endergrade.canTargetItem(stack);
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
               this.endergrade.stopWandering = true;
               this.endergrade.hasItemTarget = true;
               this.mustUpdate = false;
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
      double renderRadius = 9.0;
      AABB aabb = new AABB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
      return aabb.m_82383_(renderCenter);
   }

   public void m_8056_() {
      this.f_26135_.m_21566_().m_6849_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
      super.m_8056_();
   }

   public void m_8037_() {
      super.m_8037_();
      if (this.targetEntity != null && (this.targetEntity == null || this.targetEntity.m_6084_())) {
         this.f_26135_.m_21566_().m_6849_(this.targetEntity.m_20185_(), this.targetEntity.m_20186_(), this.targetEntity.m_20189_(), 1.0);
      } else {
         this.m_8041_();
      }

      if (this.targetEntity != null
         && this.targetEntity.m_6084_()
         && this.f_26135_.m_20280_(this.targetEntity) < 2.0
         && this.f_26135_.m_21120_(InteractionHand.MAIN_HAND).m_41619_()) {
         ItemStack duplicate = this.targetEntity.m_32055_().m_41777_();
         this.endergrade.bite();
         duplicate.m_41764_(1);
         if (!this.f_26135_.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && !this.f_26135_.m_9236_().f_46443_) {
            this.f_26135_.m_5552_(this.f_26135_.m_21120_(InteractionHand.MAIN_HAND), 0.0F);
         }

         this.f_26135_.m_21008_(InteractionHand.MAIN_HAND, duplicate);
         this.endergrade.onGetItem(this.targetEntity);
         this.targetEntity.m_32055_().m_41774_(1);
         this.m_8041_();
      }
   }

   public void m_8041_() {
      this.targetEntity = null;
      this.endergrade.hasItemTarget = false;
      this.endergrade.stopWandering = false;
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public boolean m_8045_() {
      return this.f_26135_.m_21566_().m_24995_();
   }

   public record Sorter(Entity theEntity) implements Comparator<Entity> {
      public int compare(Entity p_compare_1_, Entity p_compare_2_) {
         double d0 = this.theEntity.m_20280_(p_compare_1_);
         double d1 = this.theEntity.m_20280_(p_compare_2_);
         return Double.compare(d0, d1);
      }
   }
}
