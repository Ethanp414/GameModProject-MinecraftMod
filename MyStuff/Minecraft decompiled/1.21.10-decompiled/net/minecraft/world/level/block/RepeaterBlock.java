package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock extends DiodeBlock {
   public static final MapCodec<RepeaterBlock> CODEC = simpleCodec(RepeaterBlock::new);
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

   @Override
   public MapCodec<RepeaterBlock> codec() {
      return CODEC;
   }

   protected RepeaterBlock(BlockBehaviour.Properties $$0) {
      super($$0);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(DELAY, Integer.valueOf(1))
            .setValue(LOCKED, Boolean.valueOf(false))
            .setValue(POWERED, Boolean.valueOf(false))
      );
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
      if (!$$3.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         $$1.setBlock($$2, $$0.cycle(DELAY), 3);
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected int getDelay(BlockState $$0) {
      return $$0.getValue(DELAY) * 2;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext $$0) {
      BlockState $$1 = super.getStateForPlacement($$0);
      return $$1.setValue(LOCKED, Boolean.valueOf(this.isLocked($$0.getLevel(), $$0.getClickedPos(), $$1)));
   }

   @Override
   protected BlockState updateShape(
      BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7
   ) {
      if ($$4 == Direction.DOWN && !this.canSurviveOn($$1, $$5, $$6)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return !$$1.isClientSide() && $$4.getAxis() != ((Direction)$$0.getValue(FACING)).getAxis()
            ? $$0.setValue(LOCKED, Boolean.valueOf(this.isLocked($$1, $$3, $$0)))
            : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
      }
   }

   @Override
   public boolean isLocked(LevelReader $$0, BlockPos $$1, BlockState $$2) {
      return this.getAlternateSignal($$0, $$1, $$2) > 0;
   }

   @Override
   protected boolean sideInputDiodesOnly() {
      return true;
   }

   @Override
   public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
      if ($$0.getValue(POWERED)) {
         Direction $$4 = $$0.getValue(FACING);
         double $$5 = (double)$$2.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2;
         double $$6 = (double)$$2.getY() + 0.4 + ($$3.nextDouble() - 0.5) * 0.2;
         double $$7 = (double)$$2.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2;
         float $$8 = -5.0F;
         if ($$3.nextBoolean()) {
            $$8 = (float)($$0.getValue(DELAY) * 2 - 1);
         }

         $$8 /= 16.0F;
         double $$9 = (double)($$8 * (float)$$4.getStepX());
         double $$10 = (double)($$8 * (float)$$4.getStepZ());
         $$1.addParticle(DustParticleOptions.REDSTONE, $$5 + $$9, $$6, $$7 + $$10, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
      $$0.add(FACING, DELAY, LOCKED, POWERED);
   }
}
