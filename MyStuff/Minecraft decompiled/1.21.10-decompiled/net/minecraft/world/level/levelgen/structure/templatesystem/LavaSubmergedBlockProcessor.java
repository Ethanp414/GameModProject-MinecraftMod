package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LavaSubmergedBlockProcessor extends StructureProcessor {
   public static final MapCodec<LavaSubmergedBlockProcessor> CODEC = MapCodec.unit(
      (Supplier<LavaSubmergedBlockProcessor>)(() -> LavaSubmergedBlockProcessor.INSTANCE)
   );
   public static final LavaSubmergedBlockProcessor INSTANCE = new LavaSubmergedBlockProcessor();

   @Nullable
   @Override
   public StructureTemplate.StructureBlockInfo processBlock(
      LevelReader $$0,
      BlockPos $$1,
      BlockPos $$2,
      StructureTemplate.StructureBlockInfo $$3,
      StructureTemplate.StructureBlockInfo $$4,
      StructurePlaceSettings $$5
   ) {
      BlockPos $$6 = $$4.pos();
      boolean $$7 = $$0.getBlockState($$6).is(Blocks.LAVA);
      return $$7 && !Block.isShapeFullBlock($$4.state().getShape($$0, $$6))
         ? new StructureTemplate.StructureBlockInfo($$6, Blocks.LAVA.defaultBlockState(), $$4.nbt())
         : $$4;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
   }
}
