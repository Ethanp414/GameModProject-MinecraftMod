package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

public class BlockCrystalizedMucus extends AbstractGlassBlock {
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE = BlockStateProperties.f_61414_;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.f_61447_;
   private static final int TICK_DELAY = 1;

   protected BlockCrystalizedMucus() {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283832_)
            .m_60978_(0.1F)
            .m_60918_(SoundType.f_56744_)
            .m_60955_()
            .m_60960_((s, s1, s2) -> false)
            .m_60971_((s, s1, s2) -> false)
      );
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(DISTANCE, 7)).m_61124_(PERSISTENT, false));
   }

   public boolean m_6724_(BlockState p_54449_) {
      return (Integer)p_54449_.m_61143_(DISTANCE) == 7 && !(Boolean)p_54449_.m_61143_(PERSISTENT);
   }

   public void m_213898_(BlockState p_221379_, ServerLevel p_221380_, BlockPos p_221381_, RandomSource p_221382_) {
      if (this.decaying(p_221379_)) {
         m_49950_(p_221379_, p_221380_, p_221381_);
         p_221380_.m_7471_(p_221381_, true);
      }
   }

   protected boolean decaying(BlockState p_221386_) {
      return !(Boolean)p_221386_.m_61143_(PERSISTENT) && (Integer)p_221386_.m_61143_(DISTANCE) == 7;
   }

   public void m_213897_(BlockState p_221369_, ServerLevel p_221370_, BlockPos p_221371_, RandomSource p_221372_) {
      p_221370_.m_7731_(p_221371_, updateDistance(p_221369_, p_221370_, p_221371_), 3);
   }

   public int m_7753_(BlockState p_54460_, BlockGetter p_54461_, BlockPos p_54462_) {
      return 1;
   }

   public BlockState m_7417_(BlockState p_54440_, Direction p_54441_, BlockState p_54442_, LevelAccessor p_54443_, BlockPos p_54444_, BlockPos p_54445_) {
      int i = getDistanceAt(p_54442_) + 1;
      if (i != 1 || (Integer)p_54440_.m_61143_(DISTANCE) != i) {
         p_54443_.m_186460_(p_54444_, this, 1);
      }

      return p_54440_;
   }

   private static BlockState updateDistance(BlockState p_54436_, LevelAccessor p_54437_, BlockPos p_54438_) {
      int i = 7;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for (Direction direction : Direction.values()) {
         blockpos$mutableblockpos.m_122159_(p_54438_, direction);
         i = Math.min(i, getDistanceAt(p_54437_.m_8055_(blockpos$mutableblockpos)) + 1);
         if (i == 1) {
            break;
         }
      }

      return (BlockState)p_54436_.m_61124_(DISTANCE, i);
   }

   private static int getDistanceAt(BlockState p_54464_) {
      if (p_54464_.m_60713_((Block)AMBlockRegistry.BANANA_SLUG_SLIME_BLOCK.get())) {
         return 0;
      } else {
         return p_54464_.m_60734_() instanceof BlockCrystalizedMucus ? (Integer)p_54464_.m_61143_(DISTANCE) : 7;
      }
   }

   protected void m_7926_(Builder<Block, BlockState> p_54447_) {
      p_54447_.m_61104_(new Property[]{DISTANCE, PERSISTENT});
   }

   public BlockState m_5573_(BlockPlaceContext p_54424_) {
      FluidState fluidstate = p_54424_.m_43725_().m_6425_(p_54424_.m_8083_());
      BlockState blockstate = (BlockState)this.m_49966_().m_61124_(PERSISTENT, true);
      return updateDistance(blockstate, p_54424_.m_43725_(), p_54424_.m_8083_());
   }
}
