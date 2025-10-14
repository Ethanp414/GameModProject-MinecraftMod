package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {
   public RandomPatchFeature(Codec<RandomPatchConfiguration> $$0) {
      super($$0);
   }

   @Override
   public boolean place(FeaturePlaceContext<RandomPatchConfiguration> $$0) {
      RandomPatchConfiguration $$1 = $$0.config();
      RandomSource $$2 = $$0.random();
      BlockPos $$3 = $$0.origin();
      WorldGenLevel $$4 = $$0.level();
      int $$5 = 0;
      BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
      int $$7 = $$1.xzSpread() + 1;
      int $$8 = $$1.ySpread() + 1;

      for(int $$9 = 0; $$9 < $$1.tries(); ++$$9) {
         $$6.setWithOffset($$3, $$2.nextInt($$7) - $$2.nextInt($$7), $$2.nextInt($$8) - $$2.nextInt($$8), $$2.nextInt($$7) - $$2.nextInt($$7));
         if (((PlacedFeature)$$1.feature().value()).place($$4, $$0.chunkGenerator(), $$2, $$6)) {
            ++$$5;
         }
      }

      return $$5 > 0;
   }
}
