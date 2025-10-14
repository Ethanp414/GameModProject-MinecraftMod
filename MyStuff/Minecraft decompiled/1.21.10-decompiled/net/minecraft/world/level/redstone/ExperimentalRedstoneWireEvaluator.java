package net.minecraft.world.level.redstone;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class ExperimentalRedstoneWireEvaluator extends RedstoneWireEvaluator {
   private final Deque<BlockPos> wiresToTurnOff = new ArrayDeque();
   private final Deque<BlockPos> wiresToTurnOn = new ArrayDeque();
   private final Object2IntMap<BlockPos> updatedWires = new Object2IntLinkedOpenHashMap<>();

   public ExperimentalRedstoneWireEvaluator(RedStoneWireBlock $$0) {
      super($$0);
   }

   @Override
   public void updatePowerStrength(Level $$0, BlockPos $$1, BlockState $$2, @Nullable Orientation $$3, boolean $$4) {
      Orientation $$5 = getInitialOrientation($$0, $$3);
      this.calculateCurrentChanges($$0, $$1, $$5);
      ObjectIterator<Entry<BlockPos>> $$6 = this.updatedWires.object2IntEntrySet().iterator();

      for(boolean $$7 = true; $$6.hasNext(); $$7 = false) {
         Entry<BlockPos> $$8 = (Entry)$$6.next();
         BlockPos $$9 = (BlockPos)$$8.getKey();
         int $$10 = $$8.getIntValue();
         int $$11 = unpackPower($$10);
         BlockState $$12 = $$0.getBlockState($$9);
         if ($$12.is(this.wireBlock) && !((Integer)$$12.getValue(RedStoneWireBlock.POWER)).equals($$11)) {
            int $$13 = 2;
            if (!$$4 || !$$7) {
               $$13 |= 128;
            }

            $$0.setBlock($$9, $$12.setValue(RedStoneWireBlock.POWER, Integer.valueOf($$11)), $$13);
         } else {
            $$6.remove();
         }
      }

      this.causeNeighborUpdates($$0);
   }

   private void causeNeighborUpdates(Level $$0) {
      this.updatedWires.forEach(($$1x, $$2) -> {
         Orientation $$3 = unpackOrientation($$2);
         BlockState $$4 = $$0.getBlockState($$1x);

         for(Direction $$5 : $$3.getDirections()) {
            if (isConnected($$4, $$5)) {
               BlockPos $$6 = $$1x.relative($$5);
               BlockState $$7 = $$0.getBlockState($$6);
               Orientation $$8 = $$3.withFrontPreserveUp($$5);
               $$0.neighborChanged($$7, $$6, this.wireBlock, $$8, false);
               if ($$7.isRedstoneConductor($$0, $$6)) {
                  for(Direction $$9 : $$8.getDirections()) {
                     if ($$9 != $$5.getOpposite()) {
                        $$0.neighborChanged($$6.relative($$9), this.wireBlock, $$8.withFrontPreserveUp($$9));
                     }
                  }
               }
            }
         }
      });
      if ($$0 instanceof ServerLevel $$1 && $$1.debugSynchronizers().hasAnySubscriberFor(DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS)) {
         this.updatedWires
            .forEach(($$1x, $$2) -> $$1.debugSynchronizers().sendBlockValue($$1x, DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, unpackOrientation($$2)));
      }
   }

   private static boolean isConnected(BlockState $$0, Direction $$1) {
      EnumProperty<RedstoneSide> $$2 = (EnumProperty)RedStoneWireBlock.PROPERTY_BY_DIRECTION.get($$1);
      if ($$2 == null) {
         return $$1 == Direction.DOWN;
      } else {
         return ((RedstoneSide)$$0.getValue($$2)).isConnected();
      }
   }

   private static Orientation getInitialOrientation(Level $$0, @Nullable Orientation $$1) {
      Orientation $$2;
      if ($$1 != null) {
         $$2 = $$1;
      } else {
         $$2 = Orientation.random($$0.random);
      }

      return $$2.withUp(Direction.UP).withSideBias(Orientation.SideBias.LEFT);
   }

   private void calculateCurrentChanges(Level $$0, BlockPos $$1, Orientation $$2) {
      BlockState $$3 = $$0.getBlockState($$1);
      if ($$3.is(this.wireBlock)) {
         this.setPower($$1, $$3.getValue(RedStoneWireBlock.POWER), $$2);
         this.wiresToTurnOff.add($$1);
      } else {
         this.propagateChangeToNeighbors($$0, $$1, 0, $$2, true);
      }

      BlockPos $$4;
      Orientation $$6;
      int $$7;
      int $$10;
      int $$11;
      for(; !this.wiresToTurnOff.isEmpty(); this.propagateChangeToNeighbors($$0, $$4, $$11, $$6, $$7 > $$10)) {
         $$4 = (BlockPos)this.wiresToTurnOff.removeFirst();
         int $$5 = this.updatedWires.getInt($$4);
         $$6 = unpackOrientation($$5);
         $$7 = unpackPower($$5);
         int $$8 = this.getBlockSignal($$0, $$4);
         int $$9 = this.getIncomingWireSignal($$0, $$4);
         $$10 = Math.max($$8, $$9);
         if ($$10 < $$7) {
            if ($$8 > 0 && !this.wiresToTurnOn.contains($$4)) {
               this.wiresToTurnOn.add($$4);
            }

            $$11 = 0;
         } else {
            $$11 = $$10;
         }

         if ($$11 != $$7) {
            this.setPower($$4, $$11, $$6);
         }
      }

      int $$18;
      for(; !this.wiresToTurnOn.isEmpty(); this.propagateChangeToNeighbors($$0, $$4, $$18, $$19, false)) {
         $$4 = (BlockPos)this.wiresToTurnOn.removeFirst();
         int $$14 = this.updatedWires.getInt($$4);
         int $$15 = unpackPower($$14);
         $$7 = this.getBlockSignal($$0, $$4);
         int $$17 = this.getIncomingWireSignal($$0, $$4);
         $$18 = Math.max($$7, $$17);
         $$19 = unpackOrientation($$14);
         if ($$18 > $$15) {
            this.setPower($$4, $$18, $$19);
         } else if ($$18 < $$15) {
            throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
         }
      }
   }

   private static int packOrientationAndPower(Orientation $$0, int $$1) {
      return $$0.getIndex() << 4 | $$1;
   }

   private static Orientation unpackOrientation(int $$0) {
      return Orientation.fromIndex($$0 >> 4);
   }

   private static int unpackPower(int $$0) {
      return $$0 & 15;
   }

   private void setPower(BlockPos $$0, int $$1, Orientation $$2) {
      this.updatedWires.compute($$0, ($$2x, $$3) -> $$3 == null ? packOrientationAndPower($$2, $$1) : packOrientationAndPower(unpackOrientation($$3), $$1));
   }

   private void propagateChangeToNeighbors(Level $$0, BlockPos $$1, int $$2, Orientation $$3, boolean $$4) {
      for(Direction $$5 : $$3.getHorizontalDirections()) {
         BlockPos $$6 = $$1.relative($$5);
         this.enqueueNeighborWire($$0, $$6, $$2, $$3.withFront($$5), $$4);
      }

      for(Direction $$7 : $$3.getVerticalDirections()) {
         BlockPos $$8 = $$1.relative($$7);
         boolean $$9 = $$0.getBlockState($$8).isRedstoneConductor($$0, $$8);

         for(Direction $$10 : $$3.getHorizontalDirections()) {
            BlockPos $$11 = $$1.relative($$10);
            if ($$7 == Direction.UP && !$$9) {
               BlockPos $$12 = $$8.relative($$10);
               this.enqueueNeighborWire($$0, $$12, $$2, $$3.withFront($$10), $$4);
            } else if ($$7 == Direction.DOWN && !$$0.getBlockState($$11).isRedstoneConductor($$0, $$11)) {
               BlockPos $$13 = $$8.relative($$10);
               this.enqueueNeighborWire($$0, $$13, $$2, $$3.withFront($$10), $$4);
            }
         }
      }
   }

   private void enqueueNeighborWire(Level $$0, BlockPos $$1, int $$2, Orientation $$3, boolean $$4) {
      BlockState $$5 = $$0.getBlockState($$1);
      if ($$5.is(this.wireBlock)) {
         int $$6 = this.getWireSignal($$1, $$5);
         if ($$6 < $$2 - 1 && !this.wiresToTurnOn.contains($$1)) {
            this.wiresToTurnOn.add($$1);
            this.setPower($$1, $$6, $$3);
         }

         if ($$4 && $$6 > $$2 && !this.wiresToTurnOff.contains($$1)) {
            this.wiresToTurnOff.add($$1);
            this.setPower($$1, $$6, $$3);
         }
      }
   }

   @Override
   protected int getWireSignal(BlockPos $$0, BlockState $$1) {
      int $$2 = this.updatedWires.getOrDefault($$0, -1);
      return $$2 != -1 ? unpackPower($$2) : super.getWireSignal($$0, $$1);
   }
}
