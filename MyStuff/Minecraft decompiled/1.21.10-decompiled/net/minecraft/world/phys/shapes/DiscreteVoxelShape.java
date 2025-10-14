package net.minecraft.world.phys.shapes;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;

public abstract class DiscreteVoxelShape {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected DiscreteVoxelShape(int $$0, int $$1, int $$2) {
      if ($$0 >= 0 && $$1 >= 0 && $$2 >= 0) {
         this.xSize = $$0;
         this.ySize = $$1;
         this.zSize = $$2;
      } else {
         throw new IllegalArgumentException("Need all positive sizes: x: " + $$0 + ", y: " + $$1 + ", z: " + $$2);
      }
   }

   public DiscreteVoxelShape rotate(OctahedralGroup $$0) {
      if ($$0 == OctahedralGroup.IDENTITY) {
         return this;
      } else {
         Direction.Axis $$1 = $$0.permute(Direction.Axis.X);
         Direction.Axis $$2 = $$0.permute(Direction.Axis.Y);
         Direction.Axis $$3 = $$0.permute(Direction.Axis.Z);
         int $$4 = $$1.choose(this.xSize, this.ySize, this.zSize);
         int $$5 = $$2.choose(this.xSize, this.ySize, this.zSize);
         int $$6 = $$3.choose(this.xSize, this.ySize, this.zSize);
         boolean $$7 = $$0.inverts($$1);
         boolean $$8 = $$0.inverts($$2);
         boolean $$9 = $$0.inverts($$3);
         boolean $$10 = $$1.choose($$7, $$8, $$9);
         boolean $$11 = $$2.choose($$7, $$8, $$9);
         boolean $$12 = $$3.choose($$7, $$8, $$9);
         DiscreteVoxelShape $$13 = new BitSetDiscreteVoxelShape($$4, $$5, $$6);

         for(int $$14 = 0; $$14 < this.xSize; ++$$14) {
            for(int $$15 = 0; $$15 < this.ySize; ++$$15) {
               for(int $$16 = 0; $$16 < this.zSize; ++$$16) {
                  if (this.isFull($$14, $$15, $$16)) {
                     int $$17 = $$1.choose($$14, $$15, $$16);
                     int $$18 = $$2.choose($$14, $$15, $$16);
                     int $$19 = $$3.choose($$14, $$15, $$16);
                     $$13.fill($$10 ? $$4 - 1 - $$17 : $$17, $$11 ? $$5 - 1 - $$18 : $$18, $$12 ? $$6 - 1 - $$19 : $$19);
                  }
               }
            }
         }

         return $$13;
      }
   }

   public boolean isFullWide(AxisCycle $$0, int $$1, int $$2, int $$3) {
      return this.isFullWide(
         $$0.cycle($$1, $$2, $$3, Direction.Axis.X), $$0.cycle($$1, $$2, $$3, Direction.Axis.Y), $$0.cycle($$1, $$2, $$3, Direction.Axis.Z)
      );
   }

   public boolean isFullWide(int $$0, int $$1, int $$2) {
      if ($$0 < 0 || $$1 < 0 || $$2 < 0) {
         return false;
      } else {
         return $$0 < this.xSize && $$1 < this.ySize && $$2 < this.zSize ? this.isFull($$0, $$1, $$2) : false;
      }
   }

   public boolean isFull(AxisCycle $$0, int $$1, int $$2, int $$3) {
      return this.isFull($$0.cycle($$1, $$2, $$3, Direction.Axis.X), $$0.cycle($$1, $$2, $$3, Direction.Axis.Y), $$0.cycle($$1, $$2, $$3, Direction.Axis.Z));
   }

   public abstract boolean isFull(int var1, int var2, int var3);

   public abstract void fill(int var1, int var2, int var3);

   public boolean isEmpty() {
      for(Direction.Axis $$0 : AXIS_VALUES) {
         if (this.firstFull($$0) >= this.lastFull($$0)) {
            return true;
         }
      }

      return false;
   }

   public abstract int firstFull(Direction.Axis var1);

   public abstract int lastFull(Direction.Axis var1);

   public int firstFull(Direction.Axis $$0, int $$1, int $$2) {
      int $$3 = this.getSize($$0);
      if ($$1 >= 0 && $$2 >= 0) {
         Direction.Axis $$4 = AxisCycle.FORWARD.cycle($$0);
         Direction.Axis $$5 = AxisCycle.BACKWARD.cycle($$0);
         if ($$1 < this.getSize($$4) && $$2 < this.getSize($$5)) {
            AxisCycle $$6 = AxisCycle.between(Direction.Axis.X, $$0);

            for(int $$7 = 0; $$7 < $$3; ++$$7) {
               if (this.isFull($$6, $$7, $$1, $$2)) {
                  return $$7;
               }
            }

            return $$3;
         } else {
            return $$3;
         }
      } else {
         return $$3;
      }
   }

   public int lastFull(Direction.Axis $$0, int $$1, int $$2) {
      if ($$1 >= 0 && $$2 >= 0) {
         Direction.Axis $$3 = AxisCycle.FORWARD.cycle($$0);
         Direction.Axis $$4 = AxisCycle.BACKWARD.cycle($$0);
         if ($$1 < this.getSize($$3) && $$2 < this.getSize($$4)) {
            int $$5 = this.getSize($$0);
            AxisCycle $$6 = AxisCycle.between(Direction.Axis.X, $$0);

            for(int $$7 = $$5 - 1; $$7 >= 0; --$$7) {
               if (this.isFull($$6, $$7, $$1, $$2)) {
                  return $$7 + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis $$0) {
      return $$0.choose(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   public void forAllEdges(DiscreteVoxelShape.IntLineConsumer $$0, boolean $$1) {
      this.forAllAxisEdges($$0, AxisCycle.NONE, $$1);
      this.forAllAxisEdges($$0, AxisCycle.FORWARD, $$1);
      this.forAllAxisEdges($$0, AxisCycle.BACKWARD, $$1);
   }

   private void forAllAxisEdges(DiscreteVoxelShape.IntLineConsumer $$0, AxisCycle $$1, boolean $$2) {
      AxisCycle $$3 = $$1.inverse();
      int $$4 = this.getSize($$3.cycle(Direction.Axis.X));
      int $$5 = this.getSize($$3.cycle(Direction.Axis.Y));
      int $$6 = this.getSize($$3.cycle(Direction.Axis.Z));

      for(int $$7 = 0; $$7 <= $$4; ++$$7) {
         for(int $$8 = 0; $$8 <= $$5; ++$$8) {
            int $$9 = -1;

            for(int $$10 = 0; $$10 <= $$6; ++$$10) {
               int $$11 = 0;
               int $$12 = 0;

               for(int $$13 = 0; $$13 <= 1; ++$$13) {
                  for(int $$14 = 0; $$14 <= 1; ++$$14) {
                     if (this.isFullWide($$3, $$7 + $$13 - 1, $$8 + $$14 - 1, $$10)) {
                        ++$$11;
                        $$12 ^= $$13 ^ $$14;
                     }
                  }
               }

               if ($$11 == 1 || $$11 == 3 || $$11 == 2 && ($$12 & 1) == 0) {
                  if ($$2) {
                     if ($$9 == -1) {
                        $$9 = $$10;
                     }
                  } else {
                     $$0.consume(
                        $$3.cycle($$7, $$8, $$10, Direction.Axis.X),
                        $$3.cycle($$7, $$8, $$10, Direction.Axis.Y),
                        $$3.cycle($$7, $$8, $$10, Direction.Axis.Z),
                        $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.X),
                        $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.Y),
                        $$3.cycle($$7, $$8, $$10 + 1, Direction.Axis.Z)
                     );
                  }
               } else if ($$9 != -1) {
                  $$0.consume(
                     $$3.cycle($$7, $$8, $$9, Direction.Axis.X),
                     $$3.cycle($$7, $$8, $$9, Direction.Axis.Y),
                     $$3.cycle($$7, $$8, $$9, Direction.Axis.Z),
                     $$3.cycle($$7, $$8, $$10, Direction.Axis.X),
                     $$3.cycle($$7, $$8, $$10, Direction.Axis.Y),
                     $$3.cycle($$7, $$8, $$10, Direction.Axis.Z)
                  );
                  $$9 = -1;
               }
            }
         }
      }
   }

   public void forAllBoxes(DiscreteVoxelShape.IntLineConsumer $$0, boolean $$1) {
      BitSetDiscreteVoxelShape.forAllBoxes(this, $$0, $$1);
   }

   public void forAllFaces(DiscreteVoxelShape.IntFaceConsumer $$0) {
      this.forAllAxisFaces($$0, AxisCycle.NONE);
      this.forAllAxisFaces($$0, AxisCycle.FORWARD);
      this.forAllAxisFaces($$0, AxisCycle.BACKWARD);
   }

   private void forAllAxisFaces(DiscreteVoxelShape.IntFaceConsumer $$0, AxisCycle $$1) {
      AxisCycle $$2 = $$1.inverse();
      Direction.Axis $$3 = $$2.cycle(Direction.Axis.Z);
      int $$4 = this.getSize($$2.cycle(Direction.Axis.X));
      int $$5 = this.getSize($$2.cycle(Direction.Axis.Y));
      int $$6 = this.getSize($$3);
      Direction $$7 = Direction.fromAxisAndDirection($$3, Direction.AxisDirection.NEGATIVE);
      Direction $$8 = Direction.fromAxisAndDirection($$3, Direction.AxisDirection.POSITIVE);

      for(int $$9 = 0; $$9 < $$4; ++$$9) {
         for(int $$10 = 0; $$10 < $$5; ++$$10) {
            boolean $$11 = false;

            for(int $$12 = 0; $$12 <= $$6; ++$$12) {
               boolean $$13 = $$12 != $$6 && this.isFull($$2, $$9, $$10, $$12);
               if (!$$11 && $$13) {
                  $$0.consume(
                     $$7,
                     $$2.cycle($$9, $$10, $$12, Direction.Axis.X),
                     $$2.cycle($$9, $$10, $$12, Direction.Axis.Y),
                     $$2.cycle($$9, $$10, $$12, Direction.Axis.Z)
                  );
               }

               if ($$11 && !$$13) {
                  $$0.consume(
                     $$8,
                     $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.X),
                     $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.Y),
                     $$2.cycle($$9, $$10, $$12 - 1, Direction.Axis.Z)
                  );
               }

               $$11 = $$13;
            }
         }
      }
   }

   public interface IntFaceConsumer {
      void consume(Direction var1, int var2, int var3, int var4);
   }

   public interface IntLineConsumer {
      void consume(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
