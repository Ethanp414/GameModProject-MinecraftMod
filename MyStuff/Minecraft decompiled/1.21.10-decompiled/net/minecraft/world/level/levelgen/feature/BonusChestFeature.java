package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
   public BonusChestFeature(Codec<NoneFeatureConfiguration> $$0) {
      super($$0);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
      RandomSource $$1 = $$0.random();
      WorldGenLevel $$2 = $$0.level();
      ChunkPos $$3 = new ChunkPos($$0.origin());
      IntArrayList $$4 = Util.toShuffledList(IntStream.rangeClosed($$3.getMinBlockX(), $$3.getMaxBlockX()), $$1);
      IntArrayList $$5 = Util.toShuffledList(IntStream.rangeClosed($$3.getMinBlockZ(), $$3.getMaxBlockZ()), $$1);
      BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();

      for(Integer $$7 : $$4) {
         for(Integer $$8 : $$5) {
            $$6.set($$7, 0, $$8);
            BlockPos $$9 = $$2.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$6);
            if ($$2.isEmptyBlock($$9) || $$2.getBlockState($$9).getCollisionShape($$2, $$9).isEmpty()) {
               $$2.setBlock($$9, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainer.setBlockEntityLootTable($$2, $$1, $$9, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState $$10 = Blocks.TORCH.defaultBlockState();

               for(Direction $$11 : Direction.Plane.HORIZONTAL) {
                  BlockPos $$12 = $$9.relative($$11);
                  if ($$10.canSurvive($$2, $$12)) {
                     $$2.setBlock($$12, $$10, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
