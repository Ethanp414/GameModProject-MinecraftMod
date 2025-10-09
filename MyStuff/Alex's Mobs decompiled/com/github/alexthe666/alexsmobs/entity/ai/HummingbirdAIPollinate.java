package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HummingbirdAIPollinate extends MoveToBlockGoal {
   private final EntityHummingbird bird;
   private int idleAtFlowerTime = 0;
   private boolean isAboveDestinationBear;

   public HummingbirdAIPollinate(EntityHummingbird bird) {
      super(bird, 1.0, 32, 8);
      this.bird = bird;
   }

   public boolean m_8036_() {
      return !this.bird.m_6162_() && this.bird.pollinateCooldown == 0 && super.m_8036_();
   }

   public void m_8041_() {
      this.idleAtFlowerTime = 0;
   }

   public double m_8052_() {
      return 3.0;
   }

   public void m_8037_() {
      super.m_8037_();
      BlockPos blockpos = this.m_6669_();
      if (!this.isWithinXZDist(blockpos, this.f_25598_.m_20182_(), this.m_8052_())) {
         this.isAboveDestinationBear = false;
         this.f_25601_++;
         double speedLoc = this.f_25599_;
         if (this.f_25598_.m_20275_(blockpos.m_123341_() + 0.5, blockpos.m_123342_() + 0.5, blockpos.m_123343_() + 0.5) >= 3.0) {
            speedLoc = this.f_25599_ * 0.3;
         }

         this.f_25598_.m_21566_().m_6849_(blockpos.m_123341_() + 0.5, blockpos.m_123342_(), blockpos.m_123343_() + 0.5, speedLoc);
      } else {
         this.isAboveDestinationBear = true;
         this.f_25601_--;
      }

      if (this.m_25625_() && Math.abs(this.bird.m_20186_() - this.f_25602_.m_123342_()) <= 2.0) {
         this.bird.m_7618_(Anchor.EYES, new Vec3(this.f_25602_.m_123341_() + 0.5, this.f_25602_.m_123342_(), this.f_25602_.m_123343_() + 0.5));
         if (this.idleAtFlowerTime >= 20) {
            this.pollinate();
            this.m_8041_();
         } else {
            this.idleAtFlowerTime++;
         }
      }
   }

   private boolean isGrowable(BlockPos pos, ServerLevel world) {
      BlockState blockstate = world.m_8055_(pos);
      Block block = blockstate.m_60734_();
      return block instanceof CropBlock && !((CropBlock)block).m_52307_(blockstate);
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.m_123331_(AMBlockPos.fromCoords(positionVec.m_7096_(), blockpos.m_123342_(), positionVec.m_7094_())) < distance * distance;
   }

   protected boolean m_25625_() {
      return this.isAboveDestinationBear;
   }

   private void pollinate() {
      this.bird.m_9236_().m_46796_(2005, this.f_25602_, 0);
      this.bird.setCropsPollinated(this.bird.getCropsPollinated() + 1);
      this.bird.pollinateCooldown = 200;
      if (this.bird.getCropsPollinated() > 3) {
         if (this.isGrowable(this.f_25602_, (ServerLevel)this.bird.m_9236_())) {
            BoneMealItem.m_40627_(new ItemStack(Items.f_42499_), this.bird.m_9236_(), this.f_25602_);
         }

         this.bird.setCropsPollinated(0);
      }
   }

   protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
      return !worldIn.m_8055_(pos).m_204336_(AMTagRegistry.HUMMINGBIRD_POLLINATES) ? false : this.bird.pollinateCooldown == 0 && this.bird.canBlockBeSeen(pos);
   }
}
