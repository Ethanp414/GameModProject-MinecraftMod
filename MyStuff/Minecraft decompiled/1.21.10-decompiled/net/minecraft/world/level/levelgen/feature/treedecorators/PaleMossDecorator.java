package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossDecorator extends TreeDecorator {
   public static final MapCodec<PaleMossDecorator> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               Codec.floatRange(0.0F, 1.0F).fieldOf("leaves_probability").forGetter($$0x -> $$0x.leavesProbability),
               Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter($$0x -> $$0x.trunkProbability),
               Codec.floatRange(0.0F, 1.0F).fieldOf("ground_probability").forGetter($$0x -> $$0x.groundProbability)
            )
            .apply($$0, PaleMossDecorator::new)
   );
   private final float leavesProbability;
   private final float trunkProbability;
   private final float groundProbability;

   @Override
   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.PALE_MOSS;
   }

   public PaleMossDecorator(float $$0, float $$1, float $$2) {
      this.leavesProbability = $$0;
      this.trunkProbability = $$1;
      this.groundProbability = $$2;
   }

   @Override
   public void place(TreeDecorator.Context $$0) {
      RandomSource $$1 = $$0.random();
      WorldGenLevel $$2 = (WorldGenLevel)$$0.level();
      List<BlockPos> $$3 = Util.shuffledCopy($$0.logs(), $$1);
      if (!$$3.isEmpty()) {
         Mutable<BlockPos> $$4 = new MutableObject<>((BlockPos)$$3.getFirst());
         $$3.forEach($$1x -> {
            if ($$1x.getY() < $$4.getValue().getY()) {
               $$4.setValue($$1x);
            }
         });
         BlockPos $$5 = $$4.getValue();
         if ($$1.nextFloat() < this.groundProbability) {
            $$2.registryAccess()
               .lookup(Registries.CONFIGURED_FEATURE)
               .flatMap($$0x -> $$0x.get(VegetationFeatures.PALE_MOSS_PATCH))
               .ifPresent($$3x -> ((ConfiguredFeature)$$3x.value()).place($$2, $$2.getLevel().getChunkSource().getGenerator(), $$1, $$5.above()));
         }

         $$0.logs().forEach($$2x -> {
            if ($$1.nextFloat() < this.trunkProbability) {
               BlockPos $$3xx = $$2x.below();
               if ($$0.isAir($$3xx)) {
                  addMossHanger($$3xx, $$0);
               }
            }
         });
         $$0.leaves().forEach($$2x -> {
            if ($$1.nextFloat() < this.leavesProbability) {
               BlockPos $$3xx = $$2x.below();
               if ($$0.isAir($$3xx)) {
                  addMossHanger($$3xx, $$0);
               }
            }
         });
      }
   }

   private static void addMossHanger(BlockPos $$0, TreeDecorator.Context $$1) {
      while($$1.isAir($$0.below()) && !((double)$$1.random().nextFloat() < 0.5)) {
         $$1.setBlock($$0, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, Boolean.valueOf(false)));
         $$0 = $$0.below();
      }

      $$1.setBlock($$0, Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, Boolean.valueOf(true)));
   }
}
