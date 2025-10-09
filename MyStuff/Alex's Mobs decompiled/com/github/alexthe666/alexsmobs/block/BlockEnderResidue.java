package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;

public class BlockEnderResidue extends AbstractGlassBlock {
   public static final IntegerProperty AGE = BlockStateProperties.f_61407_;
   public static final BooleanProperty SLOW_DECAY = BooleanProperty.m_61465_("slow_decay");

   public BlockEnderResidue() {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283869_)
            .m_60955_()
            .m_60982_((i, j, k) -> true)
            .m_60991_((i, j, k) -> true)
            .m_60953_(i -> 3)
            .m_60978_(0.2F)
            .m_60918_(SoundType.f_154654_)
            .m_60977_()
            .m_60955_()
      );
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(AGE, 0)).m_61124_(SLOW_DECAY, false));
   }

   public void m_213898_(BlockState p_53588_, ServerLevel p_53589_, BlockPos p_53590_, RandomSource p_53591_) {
      this.m_213897_(p_53588_, p_53589_, p_53590_, p_53591_);
   }

   public void m_213897_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (random.m_188503_(state.m_61143_(SLOW_DECAY) ? 15 : 5) == 0) {
         MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

         for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.m_122159_(pos, direction);
            BlockState blockstate = level.m_8055_(blockpos$mutableblockpos);
            if (blockstate.m_60713_(this) && !this.incrementAge(blockstate, level, blockpos$mutableblockpos)) {
               level.m_186460_(blockpos$mutableblockpos, this, Mth.m_216271_(random, 20, 40));
            }
         }

         this.incrementAge(state, level, pos);
      } else {
         level.m_186460_(pos, this, Mth.m_216271_(random, 20, 40));
      }
   }

   private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
      int i = (Integer)state.m_61143_(AGE);
      if (i < 3) {
         level.m_7731_(pos, (BlockState)state.m_61124_(AGE, i + 1), 2);
         return false;
      } else {
         level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
         return true;
      }
   }

   public void m_6861_(BlockState p_53579_, Level p_53580_, BlockPos p_53581_, Block p_53582_, BlockPos p_53583_, boolean p_53584_) {
      super.m_6861_(p_53579_, p_53580_, p_53581_, p_53582_, p_53583_, p_53584_);
   }

   private boolean fewerNeigboursThan(BlockGetter p_53566_, BlockPos p_53567_, int p_53568_) {
      int i = 0;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for (Direction direction : Direction.values()) {
         blockpos$mutableblockpos.m_122159_(p_53567_, direction);
         if (p_53566_.m_8055_(blockpos$mutableblockpos).m_60713_(this)) {
            if (++i >= p_53568_) {
               return false;
            }
         }
      }

      return true;
   }

   protected void m_7926_(Builder<Block, BlockState> p_53586_) {
      p_53586_.m_61104_(new Property[]{AGE, SLOW_DECAY});
   }

   public ItemStack m_7397_(BlockGetter p_53570_, BlockPos p_53571_, BlockState p_53572_) {
      return ItemStack.f_41583_;
   }
}
