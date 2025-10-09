package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockVoidWormBeak extends BaseEntityBlock {
   public static final DirectionProperty FACING = DirectionalBlock.f_52588_;
   public static final BooleanProperty POWERED = BlockStateProperties.f_61448_;
   private static final VoxelShape AABB = Block.m_49796_(0.0, 4.0, 0.0, 16.0, 12.0, 16.0);
   private static final VoxelShape AABB_VERTICAL = Block.m_49796_(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);

   public BlockVoidWormBeak() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60955_().m_60918_(SoundType.f_56726_).m_60978_(1.0F).m_60910_().m_60999_());
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH)).m_61124_(POWERED, false));
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return ((Direction)state.m_61143_(FACING)).m_122434_() == Axis.Y ? AABB_VERTICAL : AABB;
   }

   public void m_6861_(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.f_46443_) {
         this.updateState(state, worldIn, pos, blockIn);
      }
   }

   public void m_213898_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if (!worldIn.f_46443_) {
         this.updateState(state, worldIn, pos, state.m_60734_());
      }
   }

   public void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
      boolean flag = (Boolean)state.m_61143_(POWERED);
      boolean flag1 = worldIn.m_276867_(pos);
      if (flag1 != flag) {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, flag1), 3);
         worldIn.m_46672_(pos.m_7495_(), this);
      }
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new TileEntityVoidWormBeak(pos, state);
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.m_49966_().m_61124_(FACING, context.m_43719_())).m_61124_(POWERED, context.m_43725_().m_276867_(context.m_8083_()));
   }

   public BlockState m_6843_(BlockState state, Rotation rot) {
      return (BlockState)state.m_61124_(FACING, rot.m_55954_((Direction)state.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mirrorIn) {
      return state.m_60717_(mirrorIn.m_54846_((Direction)state.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING, POWERED});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.VOID_WORM_BEAK.get(), TileEntityVoidWormBeak::commonTick);
   }
}
