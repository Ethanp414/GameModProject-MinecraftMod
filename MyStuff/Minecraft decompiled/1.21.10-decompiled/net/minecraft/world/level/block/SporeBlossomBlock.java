package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SporeBlossomBlock extends Block {
   public static final MapCodec<SporeBlossomBlock> CODEC = simpleCodec(SporeBlossomBlock::new);
   private static final VoxelShape SHAPE = Block.column(12.0, 13.0, 16.0);
   private static final int ADD_PARTICLE_ATTEMPTS = 14;
   private static final int PARTICLE_XZ_RADIUS = 10;
   private static final int PARTICLE_Y_MAX = 10;

   @Override
   public MapCodec<SporeBlossomBlock> codec() {
      return CODEC;
   }

   public SporeBlossomBlock(BlockBehaviour.Properties $$0) {
      super($$0);
   }

   @Override
   protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
      return Block.canSupportCenter($$1, $$2.above(), Direction.DOWN) && !$$1.isWaterAt($$2);
   }

   @Override
   protected BlockState updateShape(
      BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7
   ) {
      return $$4 == Direction.UP && !this.canSurvive($$0, $$1, $$3)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
   }

   @Override
   public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
      int $$4 = $$2.getX();
      int $$5 = $$2.getY();
      int $$6 = $$2.getZ();
      double $$7 = (double)$$4 + $$3.nextDouble();
      double $$8 = (double)$$5 + 0.7;
      double $$9 = (double)$$6 + $$3.nextDouble();
      $$1.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, $$7, $$8, $$9, 0.0, 0.0, 0.0);
      BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();

      for(int $$11 = 0; $$11 < 14; ++$$11) {
         $$10.set($$4 + Mth.nextInt($$3, -10, 10), $$5 - $$3.nextInt(10), $$6 + Mth.nextInt($$3, -10, 10));
         BlockState $$12 = $$1.getBlockState($$10);
         if (!$$12.isCollisionShapeFullBlock($$1, $$10)) {
            $$1.addParticle(
               ParticleTypes.SPORE_BLOSSOM_AIR,
               (double)$$10.getX() + $$3.nextDouble(),
               (double)$$10.getY() + $$3.nextDouble(),
               (double)$$10.getZ() + $$3.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }
      }
   }

   @Override
   protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
      return SHAPE;
   }
}
