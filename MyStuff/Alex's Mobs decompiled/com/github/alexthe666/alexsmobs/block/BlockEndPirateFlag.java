package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateFlag;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateFlag extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   private static final VoxelShape AABB = Block.m_49796_(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

   public BlockEndPirateFlag() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283919_).m_60955_().m_60918_(SoundType.f_56736_).m_60978_(1.0F).m_60953_(i -> 15).m_60910_().m_60999_());
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH));
   }

   public RenderShape m_7514_(BlockState p_49232_) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
      return !state.m_60710_(level, pos) ? Blocks.f_50016_.m_49966_() : super.m_7417_(state, direction, state2, level, pos, p_52801_);
   }

   public VoxelShape m_5940_(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
      return AABB;
   }

   public boolean m_7898_(BlockState state, LevelReader world, BlockPos pos) {
      return m_49863_(world, pos.m_7495_(), Direction.UP) || m_49863_(world, pos.m_7494_(), Direction.DOWN);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new TileEntityEndPirateFlag(pos, state);
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_());
   }

   public BlockState m_6843_(BlockState state, Rotation rot) {
      return (BlockState)state.m_61124_(FACING, rot.m_55954_((Direction)state.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mirrorIn) {
      return state.m_60717_(mirrorIn.m_54846_((Direction)state.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.END_PIRATE_FLAG.get(), TileEntityEndPirateFlag::commonTick);
   }

   public void animateTick(BlockState p_53094_, Level p_53095_, BlockPos p_53096_, Random p_53097_) {
      if (p_53097_.nextInt(5) == 0) {
         double d0 = p_53096_.m_123341_() + 0.55 - p_53097_.nextFloat() * 0.1F;
         double d1 = p_53096_.m_123342_() + 0.55 - p_53097_.nextFloat() * 0.1F;
         double d2 = p_53096_.m_123343_() + 0.55 - p_53097_.nextFloat() * 0.1F;
         double d3 = 0.4F - (p_53097_.nextFloat() + p_53097_.nextFloat()) * 0.4F;
         p_53095_.m_7106_(
            ParticleTypes.f_123810_, d0, d1 + d3, d2, p_53097_.nextGaussian() * 0.005, p_53097_.nextGaussian() * 0.005, p_53097_.nextGaussian() * 0.005
         );
      }
   }
}
