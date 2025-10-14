package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TallFlowerBlock extends DoublePlantBlock implements BonemealableBlock {
   public static final MapCodec<TallFlowerBlock> CODEC = simpleCodec(TallFlowerBlock::new);

   @Override
   public MapCodec<TallFlowerBlock> codec() {
      return CODEC;
   }

   public TallFlowerBlock(BlockBehaviour.Properties $$0) {
      super($$0);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
      return true;
   }

   @Override
   public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
      popResource($$0, $$2, new ItemStack(this));
   }
}
