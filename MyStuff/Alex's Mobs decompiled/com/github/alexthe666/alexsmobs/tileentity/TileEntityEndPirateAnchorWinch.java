package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPirateAnchorWinch extends BlockEntity {
   public float clientRoll;
   public int windCounter = 0;
   private int prevTargetChainLength;
   private int targetChainLength = 0;
   private float prevMaximumChainLength;
   private float chainLength;
   private float prevChainLength;
   private int windTime = 0;
   private int ticksExisted = 0;
   private float windProgress;
   private float prevWindProgress;
   private boolean draggingAnchor;
   private boolean anchorEW;
   private boolean pullingUp;
   private boolean hasPower;
   private int anchorPlaceCooldown = 0;

   public TileEntityEndPirateAnchorWinch(BlockPos pos, BlockState state) {
      super((BlockEntityType)AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), pos, state);
      this.prevTargetChainLength = this.targetChainLength;
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateAnchorWinch entity) {
      entity.tick();
   }

   private int calcChainLength(boolean goBelowAnchor) {
      BlockPos down = this.m_58899_().m_7495_();

      while (
         this.f_58857_ != null
            && down.m_123342_() > this.f_58857_.m_141937_()
            && !this.isAnchorTop(this.f_58857_, down)
            && (this.isEmptyBlock(down) || this.isAnchorChain(this.f_58857_, down))
      ) {
         down = down.m_7495_();
      }

      int i = 0;
      if (this.isAnchorTop(this.f_58857_, down) || goBelowAnchor) {
         if (goBelowAnchor) {
            i = this.m_58899_().m_123342_() - 1 - this.keepMovingBelowAnchor(down.m_6625_(2));
         } else {
            i = this.m_58899_().m_123342_() - 1 - down.m_123342_();
         }
      }

      return this.draggingAnchor ? i - 3 : i;
   }

   private int keepMovingBelowAnchor(BlockPos below) {
      while (below.m_123342_() > this.f_58857_.m_141937_() && this.isEmptyBlock(below)) {
         below = below.m_7495_();
      }

      return below.m_123342_();
   }

   private boolean isEmptyBlock(BlockPos pos) {
      return this.f_58857_.m_46859_(pos) || this.isAnchorChain(this.f_58857_, pos) || this.f_58857_.m_8055_(pos).m_247087_();
   }

   private boolean isAnchorChain(Level level, BlockPos pos) {
      return level.m_8055_(pos).m_60734_() instanceof BlockEndPirateAnchor
         && level.m_8055_(pos).m_61143_(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   private boolean isAnchorTop(Level level, BlockPos pos) {
      return level.m_8055_(pos).m_60734_() instanceof BlockEndPirateAnchor
         && level.m_8055_(pos.m_6625_(2)).m_60734_() instanceof BlockEndPirateAnchor
         && level.m_8055_(pos.m_6625_(2)).m_61143_(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR;
   }

   private void tick() {
      this.prevChainLength = this.chainLength;
      this.prevWindProgress = this.windProgress;
      this.prevTargetChainLength = this.targetChainLength;
      this.ticksExisted++;
      boolean powered = false;
      if (this.m_58900_().m_60734_() instanceof BlockEndPirateAnchorWinch) {
         powered = (Boolean)this.m_58900_().m_61143_(BlockEndPirateAnchorWinch.POWERED);
      }

      if (powered && this.pullingUp) {
         this.sendDownChains();
      }

      if (!powered && !this.pullingUp) {
         this.pullUpChains();
      }

      if (this.chainLength < this.targetChainLength) {
         this.chainLength = Math.min(this.chainLength + 0.1F, (float)this.targetChainLength);
      }

      if (this.chainLength > this.targetChainLength) {
         this.chainLength = Math.max(this.chainLength - 0.1F, (float)this.targetChainLength);
      }

      if (Math.abs(this.targetChainLength - this.chainLength) > 0.2F) {
         this.windTime = 5;
      }

      if (this.windTime > 0) {
         this.windCounter++;
         this.windTime--;
         if (this.windProgress < 1.0F) {
            this.windProgress += 0.25F;
         }
      } else {
         this.windCounter = 0;
         if (this.windProgress > 0.0F) {
            this.windProgress -= 0.25F;
         }
      }

      if (this.anchorPlaceCooldown > 0) {
         this.anchorPlaceCooldown--;
      }

      if (this.chainLength != this.targetChainLength && this.isWindingUp() && !this.draggingAnchor) {
         BlockPos down = this.m_58899_();
         if (this.anchorPlaceCooldown == 0
            && (this.checkAndBreakAnchor(down.m_7495_()) || this.checkAndBreakAnchor(down.m_6625_(1 + (int)Math.ceil(this.chainLength))))) {
            this.draggingAnchor = true;
         }
      }

      if (this.chainLength == this.targetChainLength && this.draggingAnchor) {
         int offset = this.isWindingUp() ? 0 : this.targetChainLength;
         if (this.anchorPlaceCooldown == 0 && this.tryPlaceAnchor(offset)) {
            this.draggingAnchor = false;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return INFINITE_EXTENT_AABB;
   }

   public boolean checkAndBreakAnchor(BlockPos down) {
      if (this.f_58857_.m_8055_(down).m_60734_() instanceof BlockEndPirateAnchor) {
         this.anchorEW = (Boolean)this.f_58857_.m_8055_(down).m_61143_(BlockEndPirateAnchor.EASTORWEST);
         BlockPos actualAnchorPos = down.m_6625_(2);
         if (this.f_58857_.m_8055_(actualAnchorPos).m_60734_() instanceof BlockEndPirateAnchor) {
            BlockEndPirateAnchor.removeAnchor(this.f_58857_, actualAnchorPos, this.f_58857_.m_8055_(actualAnchorPos));
            this.removeChainBlocks();
            return true;
         }
      }

      return false;
   }

   public boolean tryPlaceAnchor(int offset) {
      BlockPos at = this.m_58899_().m_6625_(3 + offset);
      if (BlockEndPirateAnchor.isClearForPlacement(this.f_58857_, at, this.anchorEW)) {
         BlockState anchorState = null;
         this.f_58857_.m_7731_(at, anchorState, 2);
         BlockEndPirateAnchor.placeAnchor(this.f_58857_, at, anchorState);
         this.placeChainBlocks(offset);
         return true;
      } else {
         return false;
      }
   }

   private void placeChainBlocks(int offset) {
      BlockPos at = this.m_58899_().m_6625_(3 + offset);
      BlockPos chainPos = at.m_6630_(3);

      while (chainPos.m_123342_() < this.m_58899_().m_123342_() - 1 && this.isEmptyBlock(chainPos)) {
         chainPos = chainPos.m_7494_();
      }
   }

   private void removeChainBlocks() {
      for (BlockPos chainPos = this.m_58899_().m_6625_(1 + (int)Math.ceil(this.chainLength));
         chainPos.m_123342_() < this.m_58899_().m_123342_();
         chainPos = chainPos.m_7494_()
      ) {
         if (this.isAnchorChain(this.f_58857_, chainPos)) {
            this.f_58857_.m_7731_(chainPos, Blocks.f_50016_.m_49966_(), 3);
         }
      }
   }

   public void recalculateChains() {
      if (this.targetChainLength != 0) {
         this.prevMaximumChainLength = this.targetChainLength;
      }

      BlockPos at = this.m_58899_().m_6625_(1);
      if (this.isAnchorTop(this.f_58857_, at) && this.anchorPlaceCooldown == 0 && this.checkAndBreakAnchor(at)) {
         this.draggingAnchor = true;
      }

      this.targetChainLength = this.calcChainLength(this.draggingAnchor);
   }

   public void sendDownChains() {
      this.recalculateChains();
      this.pullingUp = false;
   }

   public void pullUpChains() {
      if (this.targetChainLength != 0) {
         this.prevMaximumChainLength = this.targetChainLength;
      }

      this.targetChainLength = 0;
      this.pullingUp = true;
   }

   public void onInteract() {
   }

   public float getChainLengthForRender() {
      return Math.max((float)this.targetChainLength, this.prevMaximumChainLength);
   }

   public float getChainLength(float partialTick) {
      return this.prevChainLength + (this.chainLength - this.prevChainLength) * partialTick;
   }

   public float getWindProgress(float partialTick) {
      return this.prevWindProgress + (this.windProgress - this.prevWindProgress) * partialTick;
   }

   public boolean isAnchorEW() {
      return this.anchorEW;
   }

   public boolean isWinching() {
      return this.windTime > 0;
   }

   public boolean isWindingUp() {
      return this.pullingUp;
   }

   public boolean hasAnchor() {
      return this.draggingAnchor;
   }

   public void m_142466_(CompoundTag compound) {
      super.m_142466_(compound);
      this.pullingUp = compound.m_128471_("PullingUp");
      this.draggingAnchor = compound.m_128471_("DraggingAnchor");
      this.anchorEW = compound.m_128471_("EWAnchor");
      this.prevChainLength = this.chainLength = compound.m_128457_("ChainLength");
      this.targetChainLength = compound.m_128451_("TargetChainLength");
   }

   protected void m_183515_(CompoundTag compound) {
      super.m_183515_(compound);
      compound.m_128379_("PullingUp", this.pullingUp);
      compound.m_128379_("DraggingAnchor", this.draggingAnchor);
      compound.m_128379_("EWAnchor", this.anchorEW);
      compound.m_128350_("ChainLength", this.chainLength);
      compound.m_128405_("TargetChainLength", this.targetChainLength);
   }
}
