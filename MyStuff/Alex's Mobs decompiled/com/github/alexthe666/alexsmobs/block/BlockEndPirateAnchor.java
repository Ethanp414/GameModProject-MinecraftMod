package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateAnchor extends BaseEntityBlock implements AMSpecialRenderBlock {
   public static final BooleanProperty EASTORWEST = BooleanProperty.m_61465_("eastorwest");
   public static final EnumProperty<BlockEndPirateAnchor.PieceType> PIECE = EnumProperty.m_61587_("piece", BlockEndPirateAnchor.PieceType.class);
   protected static final VoxelShape FULL_AABB_EW = Block.m_49796_(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
   protected static final VoxelShape FULL_AABB_NS = Block.m_49796_(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
   protected static final VoxelShape CHAIN_AABB = Block.m_49796_(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   protected BlockEndPirateAnchor() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283927_).m_60911_(0.97F).m_60978_(10.0F).m_60953_(i -> 6).m_60918_(SoundType.f_56742_).m_60955_());
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(EASTORWEST, false)).m_61124_(PIECE, BlockEndPirateAnchor.PieceType.ANCHOR)
      );
   }

   public static boolean isClearForPlacement(LevelReader reader, BlockPos center, boolean eastOrWest) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest)) {
         BlockPos check = center.m_121955_(offset);
         if (!reader.m_46859_(check) || !reader.m_8055_(check).m_247087_()) {
            return false;
         }
      }

      return true;
   }

   public static void placeAnchor(Level level, BlockPos pos, BlockState state) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions((Boolean)state.m_61143_(EASTORWEST))) {
         if (!offset.equals(BlockPos.f_121853_)) {
            level.m_7731_(pos.m_121955_(offset), (BlockState)state.m_61124_(PIECE, BlockEndPirateAnchor.PieceType.ANCHOR_SIDE), 2);
         }
      }
   }

   public static void removeAnchor(Level level, BlockPos pos, BlockState state) {
      for (BlockPos offset : TileEntityEndPirateAnchor.getValidBBPositions((Boolean)state.m_61143_(EASTORWEST))) {
         level.m_7731_(pos.m_121955_(offset), Blocks.f_50016_.m_49966_(), 67);
      }
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      LevelReader levelreader = context.m_43725_();
      BlockPos blockpos = context.m_8083_();
      BlockPos actualPos = context.m_8083_().m_121945_(context.m_43719_().m_122424_());
      BlockPos u = blockpos.m_7494_();
      BlockPos d = blockpos.m_7495_();
      BlockState clickState = levelreader.m_8055_(actualPos);
      boolean axis = context.m_8125_().m_122434_() == Axis.X;
      if (clickState.m_60734_() instanceof BlockEndPirateAnchor) {
         axis = (Boolean)clickState.m_61143_(EASTORWEST);
      }

      return isClearForPlacement(levelreader, blockpos, axis) ? (BlockState)this.m_49966_().m_61124_(EASTORWEST, axis) : null;
   }

   public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   public boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
   }

   public void m_7892_(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof LivingEntity && state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
         LivingEntity livingEntity = (LivingEntity)entity;
         if (livingEntity.f_19862_ && !livingEntity.m_20069_()) {
            livingEntity.f_19789_ = 0.0F;
            Vec3 motion = livingEntity.m_20184_();
            double d0 = Mth.m_14008_(motion.f_82479_, -0.15F, 0.15F);
            double d1 = Mth.m_14008_(motion.f_82481_, -0.15F, 0.15F);
            double d2 = 0.3;
            if (d2 < 0.0 && livingEntity.m_5791_()) {
               d2 = 0.0;
            }

            motion = new Vec3(d0, d2, d1);
            livingEntity.m_20256_(motion);
         }
      }
   }

   public void m_6861_(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
      if (state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE) {
         for (int i = -2; i <= 2; i++) {
            for (int j = -3; j <= 3; j++) {
               for (int k = -2; k <= 2; k++) {
                  BlockPos offsetPos = pos.m_7918_(i, j, k);
                  if (level.m_7702_(offsetPos) instanceof TileEntityEndPirateAnchor anchor && !anchor.hasAllAnchorBlocks()) {
                     removeAnchor(level, offsetPos, level.m_8055_(offsetPos));
                     level.m_46961_(offsetPos, true);
                  }
               }
            }
         }
      }

      if (!this.canSurviveAnchor(state, level, pos)) {
         level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
      }
   }

   public void m_6402_(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
      placeAnchor(level, pos, state);
   }

   public boolean canSurviveAnchor(BlockState state, LevelReader world, BlockPos pos) {
      if (state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR) {
         return true;
      } else {
         if (state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE) {
            for (int i = -1; i <= 1; i++) {
               for (int j = -3; j <= 0; j++) {
                  for (int k = -1; k <= 1; k++) {
                     BlockPos offsetPos = pos.m_7918_(i, j, k);
                     BlockState anchorState = world.m_8055_(offsetPos);
                     if (anchorState.m_60734_() instanceof BlockEndPirateAnchor
                        && anchorState.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR
                        && this.isPartOfAnchor(anchorState, world, offsetPos, pos, (Boolean)state.m_61143_(EASTORWEST))) {
                        return true;
                     }
                  }
               }
            }
         } else if (state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
            BlockPos below = pos.m_7495_();
            BlockState chainBelow = world.m_8055_(below);
            BlockState chainAbove = world.m_8055_(below);
            return chainBelow.m_60734_() instanceof BlockEndPirateAnchor
               && (chainAbove.m_60734_() instanceof BlockEndPirateAnchor || chainAbove.m_60734_() instanceof BlockEndPirateAnchorWinch);
         }

         return false;
      }
   }

   public boolean isPartOfAnchor(BlockState anchor, LevelReader level, BlockPos center, BlockPos pos, boolean eastOrWest) {
      if ((Boolean)anchor.m_61143_(EASTORWEST) == eastOrWest) {
         BlockPos offset = pos.m_121996_(center);
         return TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest).contains(offset);
      } else {
         return false;
      }
   }

   protected void m_7926_(Builder<Block, BlockState> p_58032_) {
      p_58032_.m_61104_(new Property[]{EASTORWEST, PIECE});
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      if (state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.CHAIN) {
         return CHAIN_AABB;
      } else {
         return state.m_61143_(EASTORWEST) ? FULL_AABB_NS : FULL_AABB_EW;
      }
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR ? new TileEntityEndPirateAnchor(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR
         ? m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), TileEntityEndPirateAnchor::commonTick)
         : null;
   }

   public RenderShape m_7514_(BlockState state) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR_SIDE ? RenderShape.INVISIBLE : RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public List<ItemStack> m_49635_(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return state.m_61143_(PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR ? super.m_49635_(state, builder) : Collections.emptyList();
   }

   public static enum PieceType implements StringRepresentable {
      ANCHOR,
      ANCHOR_SIDE,
      CHAIN;

      @Override
      public String toString() {
         return this.m_7912_();
      }

      public String m_7912_() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
