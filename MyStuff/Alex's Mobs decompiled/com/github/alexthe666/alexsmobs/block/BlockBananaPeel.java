package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBananaPeel extends BushBlock {
   protected static final VoxelShape SHAPE_COLLISON = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
   protected static final VoxelShape SHAPE = Block.m_49796_(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

   public BlockBananaPeel() {
      super(Properties.m_284310_().m_60988_().m_60918_(SoundType.f_56752_).m_60910_().m_60999_().m_60978_(0.2F).m_60911_(1.0F));
   }

   public void m_7892_(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
   }

   protected boolean m_6266_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return m_49936_(worldIn, pos);
   }

   public OffsetType getOffsetType() {
      return OffsetType.XZ;
   }

   @Deprecated
   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE_COLLISON;
   }

   public VoxelShape m_7947_(BlockState state, BlockGetter reader, BlockPos pos) {
      return SHAPE_COLLISON;
   }

   public VoxelShape m_5909_(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
