package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock extends Block {
   public static final MapCodec<HugeMushroomBlock> CODEC = simpleCodec(HugeMushroomBlock::new);
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty DOWN = PipeBlock.DOWN;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

   @Override
   public MapCodec<HugeMushroomBlock> codec() {
      return CODEC;
   }

   public HugeMushroomBlock(BlockBehaviour.Properties $$0) {
      super($$0);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(true))
            .setValue(EAST, Boolean.valueOf(true))
            .setValue(SOUTH, Boolean.valueOf(true))
            .setValue(WEST, Boolean.valueOf(true))
            .setValue(UP, Boolean.valueOf(true))
            .setValue(DOWN, Boolean.valueOf(true))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext $$0) {
      BlockGetter $$1 = $$0.getLevel();
      BlockPos $$2 = $$0.getClickedPos();
      return this.defaultBlockState()
         .setValue(DOWN, Boolean.valueOf(!$$1.getBlockState($$2.below()).is(this)))
         .setValue(UP, Boolean.valueOf(!$$1.getBlockState($$2.above()).is(this)))
         .setValue(NORTH, Boolean.valueOf(!$$1.getBlockState($$2.north()).is(this)))
         .setValue(EAST, Boolean.valueOf(!$$1.getBlockState($$2.east()).is(this)))
         .setValue(SOUTH, Boolean.valueOf(!$$1.getBlockState($$2.south()).is(this)))
         .setValue(WEST, Boolean.valueOf(!$$1.getBlockState($$2.west()).is(this)));
   }

   @Override
   protected BlockState updateShape(
      BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7
   ) {
      return $$6.is(this)
         ? $$0.setValue((Property)PROPERTY_BY_DIRECTION.get($$4), Boolean.valueOf(false))
         : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
   }

   @Override
   protected BlockState rotate(BlockState $$0, Rotation $$1) {
      return $$0.setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.NORTH)), (Boolean)$$0.getValue(NORTH))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.SOUTH)), (Boolean)$$0.getValue(SOUTH))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.EAST)), (Boolean)$$0.getValue(EAST))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.WEST)), (Boolean)$$0.getValue(WEST))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.UP)), (Boolean)$$0.getValue(UP))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.DOWN)), (Boolean)$$0.getValue(DOWN));
   }

   @Override
   protected BlockState mirror(BlockState $$0, Mirror $$1) {
      return $$0.setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.NORTH)), (Boolean)$$0.getValue(NORTH))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.SOUTH)), (Boolean)$$0.getValue(SOUTH))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.EAST)), (Boolean)$$0.getValue(EAST))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.WEST)), (Boolean)$$0.getValue(WEST))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.UP)), (Boolean)$$0.getValue(UP))
         .setValue((Property)PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.DOWN)), (Boolean)$$0.getValue(DOWN));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
      $$0.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}
