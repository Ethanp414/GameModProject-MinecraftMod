package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public abstract class AbstractHugeMushroomFeature extends Feature<HugeMushroomFeatureConfiguration> {
   public AbstractHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> $$0) {
      super($$0);
   }

   protected void placeTrunk(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, HugeMushroomFeatureConfiguration $$3, int $$4, BlockPos.MutableBlockPos $$5) {
      for(int $$6 = 0; $$6 < $$4; ++$$6) {
         $$5.set($$2).move(Direction.UP, $$6);
         this.placeMushroomBlock($$0, $$5, $$3.stemProvider.getState($$1, $$2));
      }
   }

   protected void placeMushroomBlock(LevelAccessor $$0, BlockPos.MutableBlockPos $$1, BlockState $$2) {
      BlockState $$3 = $$0.getBlockState($$1);
      if ($$3.isAir() || $$3.is(BlockTags.REPLACEABLE_BY_MUSHROOMS)) {
         this.setBlock($$0, $$1, $$2);
      }
   }

   protected int getTreeHeight(RandomSource $$0) {
      int $$1 = $$0.nextInt(3) + 4;
      if ($$0.nextInt(12) == 0) {
         $$1 *= 2;
      }

      return $$1;
   }

   protected boolean isValidPosition(LevelAccessor $$0, BlockPos $$1, int $$2, BlockPos.MutableBlockPos $$3, HugeMushroomFeatureConfiguration $$4) {
      int $$5 = $$1.getY();
      if ($$5 >= $$0.getMinY() + 1 && $$5 + $$2 + 1 <= $$0.getMaxY()) {
         BlockState $$6 = $$0.getBlockState($$1.below());
         if (!isDirt($$6) && !$$6.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
         } else {
            for(int $$7 = 0; $$7 <= $$2; ++$$7) {
               int $$8 = this.getTreeRadiusForHeight(-1, -1, $$4.foliageRadius, $$7);

               for(int $$9 = -$$8; $$9 <= $$8; ++$$9) {
                  for(int $$10 = -$$8; $$10 <= $$8; ++$$10) {
                     BlockState $$11 = $$0.getBlockState($$3.setWithOffset($$1, $$9, $$7, $$10));
                     if (!$$11.isAir() && !$$11.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> $$0) {
      WorldGenLevel $$1 = $$0.level();
      BlockPos $$2 = $$0.origin();
      RandomSource $$3 = $$0.random();
      HugeMushroomFeatureConfiguration $$4 = $$0.config();
      int $$5 = this.getTreeHeight($$3);
      BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
      if (!this.isValidPosition($$1, $$2, $$5, $$6, $$4)) {
         return false;
      } else {
         this.makeCap($$1, $$3, $$2, $$5, $$6, $$4);
         this.placeTrunk($$1, $$3, $$2, $$4, $$5, $$6);
         return true;
      }
   }

   protected abstract int getTreeRadiusForHeight(int var1, int var2, int var3, int var4);

   protected abstract void makeCap(
      LevelAccessor var1, RandomSource var2, BlockPos var3, int var4, BlockPos.MutableBlockPos var5, HugeMushroomFeatureConfiguration var6
   );
}
