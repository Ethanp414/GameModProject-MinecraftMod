package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class UnderwaterMagmaFeature extends Feature<UnderwaterMagmaConfiguration> {
   public UnderwaterMagmaFeature(Codec<UnderwaterMagmaConfiguration> $$0) {
      super($$0);
   }

   @Override
   public boolean place(FeaturePlaceContext<UnderwaterMagmaConfiguration> $$0) {
      WorldGenLevel $$1 = $$0.level();
      BlockPos $$2 = $$0.origin();
      UnderwaterMagmaConfiguration $$3 = $$0.config();
      RandomSource $$4 = $$0.random();
      OptionalInt $$5 = getFloorY($$1, $$2, $$3);
      if ($$5.isEmpty()) {
         return false;
      } else {
         BlockPos $$6 = $$2.atY($$5.getAsInt());
         Vec3i $$7 = new Vec3i($$3.placementRadiusAroundFloor, $$3.placementRadiusAroundFloor, $$3.placementRadiusAroundFloor);
         BoundingBox $$8 = BoundingBox.fromCorners($$6.subtract($$7), $$6.offset($$7));
         return BlockPos.betweenClosedStream($$8)
               .filter($$2x -> $$4.nextFloat() < $$3.placementProbabilityPerValidPosition)
               .filter($$1x -> this.isValidPlacement($$1, $$1x))
               .mapToInt($$1x -> {
                  $$1.setBlock($$1x, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                  return 1;
               })
               .sum()
            > 0;
      }
   }

   private static OptionalInt getFloorY(WorldGenLevel $$0, BlockPos $$1, UnderwaterMagmaConfiguration $$2) {
      Predicate<BlockState> $$3 = $$0x -> $$0x.is(Blocks.WATER);
      Predicate<BlockState> $$4 = $$0x -> !$$0x.is(Blocks.WATER);
      Optional<Column> $$5 = Column.scan($$0, $$1, $$2.floorSearchRange, $$3, $$4);
      return (OptionalInt)$$5.map(Column::getFloor).orElseGet(OptionalInt::empty);
   }

   private boolean isValidPlacement(WorldGenLevel $$0, BlockPos $$1) {
      if (!this.isWaterOrAir($$0, $$1) && !this.isWaterOrAir($$0, $$1.below())) {
         for(Direction $$2 : Direction.Plane.HORIZONTAL) {
            if (this.isWaterOrAir($$0, $$1.relative($$2))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isWaterOrAir(LevelAccessor $$0, BlockPos $$1) {
      BlockState $$2 = $$0.getBlockState($$1);
      return $$2.is(Blocks.WATER) || $$2.isAir();
   }
}
