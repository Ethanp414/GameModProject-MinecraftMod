package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrowAICircleCrops extends MoveToBlockGoal {
   private final EntityCrow crow;
   private int idleAtFlowerTime = 0;
   private boolean isAboveDestinationBear;
   float circlingTime = 0.0F;
   float circleDistance = 2.0F;
   float maxCirclingTime = 80.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   boolean circlePhase = false;

   public CrowAICircleCrops(EntityCrow bird) {
      super(bird, 1.0, 32, 8);
      this.crow = bird;
   }

   public void m_8056_() {
      super.m_8056_();
      this.circlePhase = true;
      this.clockwise = this.crow.m_217043_().m_188499_();
      this.yLevel = 1 + this.crow.m_217043_().m_188503_(3);
      this.circleDistance = 1 + this.crow.m_217043_().m_188503_(3);
   }

   public boolean m_8036_() {
      return !this.crow.m_6162_()
         && AMConfig.crowsStealCrops
         && (this.crow.m_5448_() == null || !this.crow.m_5448_().m_6084_())
         && !this.crow.m_21824_()
         && this.crow.fleePumpkinFlag == 0
         && !this.crow.aiItemFlag
         && super.m_8036_();
   }

   public boolean m_8045_() {
      return this.f_25602_ != null
         && AMConfig.crowsStealCrops
         && (this.crow.m_5448_() == null || !this.crow.m_5448_().m_6084_())
         && !this.crow.m_21824_()
         && !this.crow.aiItemFlag
         && this.crow.fleePumpkinFlag == 0
         && super.m_8045_();
   }

   public void m_8041_() {
      this.idleAtFlowerTime = 0;
      this.circlingTime = 0.0F;
      this.f_25601_ = 0;
      this.f_25602_ = BlockPos.f_121853_;
   }

   public double m_8052_() {
      return 1.0;
   }

   public void m_8037_() {
      if (this.f_25602_ != null) {
         BlockPos blockpos = this.m_6669_();
         if (this.circlePhase) {
            this.f_25601_ = 0;
            BlockPos circlePos = this.getVultureCirclePos(blockpos);
            if (circlePos != null) {
               this.crow.setFlying(true);
               this.crow.m_21566_().m_6849_(circlePos.m_123341_() + 0.5, circlePos.m_123342_() + 0.5, circlePos.m_123343_() + 0.5, 0.7F);
            }

            this.circlingTime++;
            if (this.circlingTime > 200.0F) {
               this.circlingTime = 0.0F;
               this.circlePhase = false;
            }
         } else {
            super.m_8037_();
            if (this.crow.m_20096_()) {
               this.crow.setFlying(false);
            }

            if (!this.isWithinXZDist(blockpos, this.f_25598_.m_20182_(), this.m_8052_())) {
               this.isAboveDestinationBear = false;
               this.f_25601_++;
               this.f_25598_.m_21573_().m_26519_(blockpos.m_123341_() + 0.5, blockpos.m_123342_() - 0.5, blockpos.m_123343_() + 0.5, 1.0);
            } else {
               this.isAboveDestinationBear = true;
               this.f_25601_--;
            }

            if (this.m_25625_()) {
               this.crow.m_7618_(Anchor.EYES, new Vec3(this.f_25602_.m_123341_() + 0.5, this.f_25602_.m_123342_(), this.f_25602_.m_123343_() + 0.5));
               if (this.idleAtFlowerTime >= 5) {
                  this.destroyCrop();
                  this.m_8041_();
               } else {
                  this.crow.peck();
                  this.idleAtFlowerTime++;
               }
            }
         }
      }
   }

   public BlockPos getVultureCirclePos(BlockPos target) {
      float angle = 0.13962634F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.m_14031_(angle);
      double extraZ = this.circleDistance * Mth.m_14089_(angle);
      BlockPos pos = AMBlockPos.fromCoords(target.m_123341_() + 0.5F + extraX, target.m_123342_() + 1 + this.yLevel, target.m_123343_() + 0.5F + extraZ);
      return this.crow.m_9236_().m_46859_(pos) ? pos : null;
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.m_123331_(AMBlockPos.fromCoords(positionVec.m_7096_(), blockpos.m_123342_(), positionVec.m_7094_())) < distance * distance;
   }

   protected boolean m_25625_() {
      return this.isAboveDestinationBear;
   }

   private void destroyCrop() {
      if (!this.canSeeBlock(this.f_25602_)) {
         this.m_8041_();
         this.f_25601_ = 1200;
      } else {
         if (this.crow.m_9236_().m_8055_(this.f_25602_).m_60734_() instanceof CropBlock) {
            if (this.crow.m_9236_().m_46469_().m_46207_(GameRules.f_46132_)) {
               CropBlock block = (CropBlock)this.crow.m_9236_().m_8055_(this.f_25602_).m_60734_();
               int cropAge = block.m_52305_(this.crow.m_9236_().m_8055_(this.f_25602_));
               if (cropAge > 0) {
                  this.crow.m_9236_().m_46597_(this.f_25602_, block.m_52289_(Math.max(0, cropAge - 1)));
               } else {
                  this.crow.m_9236_().m_46961_(this.f_25602_, true);
               }
            }
         } else if (this.crow.m_9236_().m_46469_().m_46207_(GameRules.f_46132_)) {
            this.crow.m_9236_().m_46961_(this.f_25602_, true);
         }

         this.m_8041_();
         this.f_25601_ = 1200;
      }
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.crow.m_20185_(), this.crow.m_20188_(), this.crow.m_20189_());
      Vec3 blockVec = Vec3.m_82512_(destinationBlock);
      BlockHitResult result = this.crow.m_9236_().m_45547_(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.crow));
      return result.m_82425_().equals(destinationBlock);
   }

   protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
      return worldIn.m_8055_(pos).m_204336_(AMTagRegistry.CROW_FOODBLOCKS);
   }
}
