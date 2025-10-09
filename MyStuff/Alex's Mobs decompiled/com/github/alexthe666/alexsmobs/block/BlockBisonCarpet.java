package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBisonCarpet extends CarpetBlock {
   protected static final VoxelShape SELECTION_SHAPE = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

   public BlockBisonCarpet() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_60913_(0.6F, 1.0F).m_60918_(SoundType.f_56745_));
   }

   public VoxelShape m_5909_(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
      return SELECTION_SHAPE;
   }
}
