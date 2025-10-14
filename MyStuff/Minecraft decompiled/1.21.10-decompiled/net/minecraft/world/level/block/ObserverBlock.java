package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;

public class ObserverBlock extends DirectionalBlock {
   public static final MapCodec<ObserverBlock> CODEC = simpleCodec(ObserverBlock::new);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   @Override
   public MapCodec<ObserverBlock> codec() {
      return CODEC;
   }

   public ObserverBlock(BlockBehaviour.Properties $$0) {
      super($$0);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.valueOf(false)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
      $$0.add(FACING, POWERED);
   }

   @Override
   protected BlockState rotate(BlockState $$0, Rotation $$1) {
      return $$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState $$0, Mirror $$1) {
      return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
   }

   @Override
   protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
      if ($$0.getValue(POWERED)) {
         $$1.setBlock($$2, $$0.setValue(POWERED, Boolean.valueOf(false)), 2);
      } else {
         $$1.setBlock($$2, $$0.setValue(POWERED, Boolean.valueOf(true)), 2);
         $$1.scheduleTick($$2, this, 2);
      }

      this.updateNeighborsInFront($$1, $$2, $$0);
   }

   @Override
   protected BlockState updateShape(
      BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7
   ) {
      if ($$0.getValue(FACING) == $$4 && !$$0.getValue(POWERED)) {
         this.startSignal($$1, $$2, $$3);
      }

      return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
   }

   private void startSignal(LevelReader $$0, ScheduledTickAccess $$1, BlockPos $$2) {
      if (!$$0.isClientSide() && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
         $$1.scheduleTick($$2, this, 2);
      }
   }

   protected void updateNeighborsInFront(Level $$0, BlockPos $$1, BlockState $$2) {
      Direction $$3 = $$2.getValue(FACING);
      BlockPos $$4 = $$1.relative($$3.getOpposite());
      Orientation $$5 = ExperimentalRedstoneUtils.initialOrientation($$0, $$3.getOpposite(), null);
      $$0.neighborChanged($$4, this, $$5);
      $$0.updateNeighborsAtExceptFromFacing($$4, this, $$3, $$5);
   }

   @Override
   protected boolean isSignalSource(BlockState $$0) {
      return true;
   }

   @Override
   protected int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
      return $$0.getSignal($$1, $$2, $$3);
   }

   @Override
   protected int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
      return $$0.getValue(POWERED) && $$0.getValue(FACING) == $$3 ? 15 : 0;
   }

   @Override
   protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
      if (!$$0.is($$3.getBlock())) {
         if (!$$1.isClientSide() && $$0.getValue(POWERED) && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
            BlockState $$5 = $$0.setValue(POWERED, Boolean.valueOf(false));
            $$1.setBlock($$2, $$5, 18);
            this.updateNeighborsInFront($$1, $$2, $$5);
         }
      }
   }

   @Override
   protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
      if ($$0.getValue(POWERED) && $$1.getBlockTicks().hasScheduledTick($$2, this)) {
         this.updateNeighborsInFront($$1, $$2, $$0.setValue(POWERED, Boolean.valueOf(false)));
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext $$0) {
      return this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite().getOpposite());
   }
}
