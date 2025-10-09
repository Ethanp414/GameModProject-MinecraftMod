package com.github.alexthe666.alexsmobs.block;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateSail extends Block {
   public static final BooleanProperty EASTORWEST = BooleanProperty.m_61465_("eastorwest");
   public static final EnumProperty<BlockEndPirateSail.SailType> SAIL = EnumProperty.m_61587_("sail", BlockEndPirateSail.SailType.class);
   protected static final VoxelShape EW_AABB = Block.m_49796_(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   protected static final VoxelShape NS_AABB = Block.m_49796_(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);

   public BlockEndPirateSail(boolean spectre) {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283743_)
            .m_60955_()
            .m_60991_((a, b, c) -> true)
            .m_60918_(SoundType.f_56745_)
            .m_60953_(state -> 5)
            .m_60999_()
            .m_60978_(0.4F)
      );
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(EASTORWEST, false)).m_61124_(SAIL, BlockEndPirateSail.SailType.SINGLE)
      );
   }

   public VoxelShape m_5940_(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
      return p_52807_.m_61143_(EASTORWEST) ? EW_AABB : NS_AABB;
   }

   protected void m_7926_(Builder<Block, BlockState> p_58032_) {
      p_58032_.m_61104_(new Property[]{EASTORWEST, SAIL});
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      LevelReader levelreader = context.m_43725_();
      BlockPos blockpos = context.m_8083_();
      BlockPos actualPos = context.m_8083_().m_121945_(context.m_43719_().m_122424_());
      BlockPos u = blockpos.m_7494_();
      BlockPos d = blockpos.m_7495_();
      BlockState clickState = levelreader.m_8055_(actualPos);
      BlockState upState = levelreader.m_8055_(u);
      BlockState downState = levelreader.m_8055_(d);
      boolean axis = context.m_43719_().m_122434_() == Axis.Y ? context.m_8125_().m_122434_() == Axis.X : context.m_43719_().m_122434_() != Axis.X;
      if (clickState.m_60734_() instanceof BlockEndPirateSail) {
         axis = (Boolean)clickState.m_61143_(EASTORWEST);
      }

      BlockState axisState = (BlockState)this.m_49966_().m_61124_(EASTORWEST, axis);
      return (BlockState)axisState.m_61124_(SAIL, getSailTypeFor(axisState, downState, upState));
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState state2, LevelAccessor levelreader, BlockPos blockpos, BlockPos pos2) {
      BlockPos u = blockpos.m_7494_();
      BlockPos d = blockpos.m_7495_();
      BlockState upState = levelreader.m_8055_(u);
      BlockState downState = levelreader.m_8055_(d);
      return (BlockState)state.m_61124_(SAIL, getSailTypeFor(state, downState, upState));
   }

   private static BlockEndPirateSail.SailType getSailTypeFor(BlockState us, BlockState below, BlockState above) {
      if (below.m_60734_() instanceof BlockEndPirateSail && below.m_61143_(EASTORWEST) == us.m_61143_(EASTORWEST)) {
         return above.m_60734_() instanceof BlockEndPirateSail ? BlockEndPirateSail.SailType.MIDDLE : BlockEndPirateSail.SailType.TOP;
      } else {
         return above.m_60734_() instanceof BlockEndPirateSail && above.m_61143_(EASTORWEST) == us.m_61143_(EASTORWEST)
            ? BlockEndPirateSail.SailType.BOTTOM
            : BlockEndPirateSail.SailType.SINGLE;
      }
   }

   private static enum SailType implements StringRepresentable {
      SINGLE,
      TOP,
      MIDDLE,
      BOTTOM;

      @Override
      public String toString() {
         return this.m_7912_();
      }

      public String m_7912_() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
