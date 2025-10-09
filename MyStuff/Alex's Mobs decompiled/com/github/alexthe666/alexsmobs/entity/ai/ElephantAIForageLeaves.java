package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class ElephantAIForageLeaves extends MoveToBlockGoal {
   private final EntityElephant elephant;
   private int idleAtLeavesTime = 0;
   private boolean isAboveDestinationBear;
   private int moveCooldown = 0;

   public ElephantAIForageLeaves(EntityElephant elephant) {
      super(elephant, 0.7, 32, 5);
      this.elephant = elephant;
   }

   public boolean m_8036_() {
      return !this.elephant.m_6162_()
         && this.elephant.m_6688_() == null
         && this.elephant.getControllingVillager() == null
         && this.elephant.m_21205_().m_41619_()
         && !this.elephant.aiItemFlag
         && super.m_8036_();
   }

   public void m_8041_() {
      this.idleAtLeavesTime = 0;
   }

   public void m_8056_() {
      super.m_8056_();
      this.moveCooldown = 30 + this.elephant.m_217043_().m_188503_(50);
   }

   public double m_8052_() {
      return 4.0;
   }

   public boolean m_8064_() {
      return this.moveCooldown == 0;
   }

   public void m_8037_() {
      if (this.moveCooldown > 0) {
         this.moveCooldown--;
      }

      BlockPos blockpos = this.m_6669_();
      if (!this.isWithinXZDist(blockpos, this.f_25598_.m_20182_(), this.m_8052_())) {
         this.isAboveDestinationBear = false;
         this.f_25601_++;
         if (this.m_8064_()) {
            this.moveCooldown = 30 + this.elephant.m_217043_().m_188503_(50);
            this.f_25598_.m_21573_().m_26519_(blockpos.m_123341_() + 0.5, blockpos.m_123342_(), blockpos.m_123343_() + 0.5, this.f_25599_);
         }
      } else {
         this.isAboveDestinationBear = true;
         this.f_25601_ = 0;
      }

      if (this.m_25625_() && Math.abs(this.elephant.m_20186_() - this.f_25602_.m_123342_()) <= 3.0) {
         this.elephant.m_7618_(Anchor.EYES, new Vec3(this.f_25602_.m_123341_() + 0.5, this.f_25602_.m_123342_(), this.f_25602_.m_123343_() + 0.5));
         if (this.elephant.m_20186_() + 2.0 < this.f_25602_.m_123342_()) {
            if (this.elephant.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
               this.elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
            }

            this.elephant.setStanding(true);
            this.elephant.maxStandTime = 15;
         } else {
            this.elephant.setAnimation(EntityElephant.ANIMATION_BREAKLEAVES);
            this.elephant.setStanding(false);
         }

         if (this.idleAtLeavesTime >= 10) {
            this.breakLeaves();
         } else {
            this.idleAtLeavesTime++;
         }
      }
   }

   protected void m_25624_() {
   }

   protected int m_6099_(PathfinderMob p_203109_1_) {
      return 100 + p_203109_1_.m_217043_().m_188503_(200);
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.m_123331_(new BlockPos((int)positionVec.m_7096_(), blockpos.m_123342_(), (int)positionVec.m_7094_())) < distance * distance;
   }

   protected boolean m_25625_() {
      return this.isAboveDestinationBear;
   }

   private void breakLeaves() {
      if (ForgeEventFactory.getMobGriefingEvent(this.elephant.m_9236_(), this.elephant)) {
         BlockState blockstate = this.elephant.m_9236_().m_8055_(this.f_25602_);
         if (blockstate.m_204336_(AMTagRegistry.ELEPHANT_FOODBLOCKS)) {
            this.elephant.m_9236_().m_46961_(this.f_25602_, false);
            RandomSource rand = this.elephant.m_217043_();
            ItemStack stack = new ItemStack(blockstate.m_60734_().m_5456_());
            ItemEntity itementity = new ItemEntity(
               this.elephant.m_9236_(),
               this.f_25602_.m_123341_() + rand.m_188501_(),
               this.f_25602_.m_123342_() + rand.m_188501_(),
               this.f_25602_.m_123343_() + rand.m_188501_(),
               stack
            );
            itementity.m_32060_();
            this.elephant.m_9236_().m_7967_(itementity);
            if (blockstate.m_204336_(AMTagRegistry.DROPS_ACACIA_BLOSSOMS) && rand.m_188503_(30) == 0) {
               ItemStack banana = new ItemStack((ItemLike)AMItemRegistry.ACACIA_BLOSSOM.get());
               ItemEntity itementity2 = new ItemEntity(
                  this.elephant.m_9236_(),
                  this.f_25602_.m_123341_() + rand.m_188501_(),
                  this.f_25602_.m_123342_() + rand.m_188501_(),
                  this.f_25602_.m_123343_() + rand.m_188501_(),
                  banana
               );
               itementity2.m_32060_();
               this.elephant.m_9236_().m_7967_(itementity2);
            }

            this.m_8041_();
         }
      }
   }

   protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
      return !this.elephant.aiItemFlag && worldIn.m_8055_(pos).m_204336_(AMTagRegistry.ELEPHANT_FOODBLOCKS) && this.canSeeBlock(pos);
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.elephant.m_20185_(), this.elephant.m_20188_(), this.elephant.m_20189_());
      Vec3 blockVec = Vec3.m_82512_(destinationBlock);
      BlockHitResult result = this.elephant.m_9236_().m_45547_(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this.elephant));
      return result.m_82425_().equals(destinationBlock);
   }
}
