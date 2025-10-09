package com.github.alexthe666.alexsmobs.entity.ai;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AnimalAIFleeLight extends Goal {
   protected final PathfinderMob creature;
   private double shelterX;
   private double shelterY;
   private double shelterZ;
   private final double movementSpeed;
   private final Level world;
   private int executeChance = 50;
   private int lightLevel = 10;

   public AnimalAIFleeLight(PathfinderMob p_i1623_1_, double p_i1623_2_) {
      this.creature = p_i1623_1_;
      this.movementSpeed = p_i1623_2_;
      this.world = p_i1623_1_.m_9236_();
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public AnimalAIFleeLight(PathfinderMob p_i1623_1_, double p_i1623_2_, int chance, int level) {
      this.creature = p_i1623_1_;
      this.movementSpeed = p_i1623_2_;
      this.world = p_i1623_1_.m_9236_();
      this.executeChance = chance;
      this.lightLevel = level;
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public boolean m_8036_() {
      if (this.creature.m_5448_() != null || this.creature.m_217043_().m_188503_(this.executeChance) != 0) {
         return false;
      } else {
         return this.world.m_46803_(this.creature.m_20183_()) < this.lightLevel ? false : this.isPossibleShelter();
      }
   }

   protected boolean isPossibleShelter() {
      Vec3 lvt_1_1_ = this.findPossibleShelter();
      if (lvt_1_1_ == null) {
         return false;
      } else {
         this.shelterX = lvt_1_1_.f_82479_;
         this.shelterY = lvt_1_1_.f_82480_;
         this.shelterZ = lvt_1_1_.f_82481_;
         return true;
      }
   }

   public boolean m_8045_() {
      return !this.creature.m_21573_().m_26571_();
   }

   public void m_8056_() {
      this.creature.m_21573_().m_26519_(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   @Nullable
   protected Vec3 findPossibleShelter() {
      RandomSource lvt_1_1_ = this.creature.m_217043_();
      BlockPos lvt_2_1_ = this.creature.m_20183_();

      for (int lvt_3_1_ = 0; lvt_3_1_ < 10; lvt_3_1_++) {
         BlockPos lvt_4_1_ = lvt_2_1_.m_7918_(lvt_1_1_.m_188503_(20) - 10, lvt_1_1_.m_188503_(6) - 3, lvt_1_1_.m_188503_(20) - 10);
         if (this.creature.m_9236_().m_46803_(lvt_4_1_) < this.lightLevel) {
            return Vec3.m_82539_(lvt_4_1_);
         }
      }

      return null;
   }
}
