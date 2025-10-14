package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Codec<Vec3i> CODEC = Codec.INT_STREAM
      .comapFlatMap(
         $$0 -> Util.fixedSize($$0, 3).map($$0x -> new Vec3i($$0x[0], $$0x[1], $$0x[2])), $$0 -> IntStream.of(new int[]{$$0.getX(), $$0.getY(), $$0.getZ()})
      );
   public static final StreamCodec<ByteBuf, Vec3i> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, Vec3i::getX, ByteBufCodecs.VAR_INT, Vec3i::getY, ByteBufCodecs.VAR_INT, Vec3i::getZ, Vec3i::new
   );
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   private int x;
   private int y;
   private int z;

   public static Codec<Vec3i> offsetCodec(int $$0) {
      return CODEC.validate(
         $$1 -> Math.abs($$1.getX()) < $$0 && Math.abs($$1.getY()) < $$0 && Math.abs($$1.getZ()) < $$0
               ? DataResult.success($$1)
               : DataResult.error(() -> "Position out of range, expected at most " + $$0 + ": " + $$1)
      );
   }

   public Vec3i(int $$0, int $$1, int $$2) {
      this.x = $$0;
      this.y = $$1;
      this.z = $$2;
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if (!($$0 instanceof Vec3i)) {
         return false;
      } else {
         Vec3i $$1 = (Vec3i)$$0;
         if (this.getX() != $$1.getX()) {
            return false;
         } else if (this.getY() != $$1.getY()) {
            return false;
         } else {
            return this.getZ() == $$1.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i $$0) {
      if (this.getY() == $$0.getY()) {
         return this.getZ() == $$0.getZ() ? this.getX() - $$0.getX() : this.getZ() - $$0.getZ();
      } else {
         return this.getY() - $$0.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   protected Vec3i setX(int $$0) {
      this.x = $$0;
      return this;
   }

   protected Vec3i setY(int $$0) {
      this.y = $$0;
      return this;
   }

   protected Vec3i setZ(int $$0) {
      this.z = $$0;
      return this;
   }

   public Vec3i offset(int $$0, int $$1, int $$2) {
      return $$0 == 0 && $$1 == 0 && $$2 == 0 ? this : new Vec3i(this.getX() + $$0, this.getY() + $$1, this.getZ() + $$2);
   }

   public Vec3i offset(Vec3i $$0) {
      return this.offset($$0.getX(), $$0.getY(), $$0.getZ());
   }

   public Vec3i subtract(Vec3i $$0) {
      return this.offset(-$$0.getX(), -$$0.getY(), -$$0.getZ());
   }

   public Vec3i multiply(int $$0) {
      if ($$0 == 1) {
         return this;
      } else {
         return $$0 == 0 ? ZERO : new Vec3i(this.getX() * $$0, this.getY() * $$0, this.getZ() * $$0);
      }
   }

   public Vec3i above() {
      return this.above(1);
   }

   public Vec3i above(int $$0) {
      return this.relative(Direction.UP, $$0);
   }

   public Vec3i below() {
      return this.below(1);
   }

   public Vec3i below(int $$0) {
      return this.relative(Direction.DOWN, $$0);
   }

   public Vec3i north() {
      return this.north(1);
   }

   public Vec3i north(int $$0) {
      return this.relative(Direction.NORTH, $$0);
   }

   public Vec3i south() {
      return this.south(1);
   }

   public Vec3i south(int $$0) {
      return this.relative(Direction.SOUTH, $$0);
   }

   public Vec3i west() {
      return this.west(1);
   }

   public Vec3i west(int $$0) {
      return this.relative(Direction.WEST, $$0);
   }

   public Vec3i east() {
      return this.east(1);
   }

   public Vec3i east(int $$0) {
      return this.relative(Direction.EAST, $$0);
   }

   public Vec3i relative(Direction $$0) {
      return this.relative($$0, 1);
   }

   public Vec3i relative(Direction $$0, int $$1) {
      return $$1 == 0 ? this : new Vec3i(this.getX() + $$0.getStepX() * $$1, this.getY() + $$0.getStepY() * $$1, this.getZ() + $$0.getStepZ() * $$1);
   }

   public Vec3i relative(Direction.Axis $$0, int $$1) {
      if ($$1 == 0) {
         return this;
      } else {
         int $$2 = $$0 == Direction.Axis.X ? $$1 : 0;
         int $$3 = $$0 == Direction.Axis.Y ? $$1 : 0;
         int $$4 = $$0 == Direction.Axis.Z ? $$1 : 0;
         return new Vec3i(this.getX() + $$2, this.getY() + $$3, this.getZ() + $$4);
      }
   }

   public Vec3i cross(Vec3i $$0) {
      return new Vec3i(
         this.getY() * $$0.getZ() - this.getZ() * $$0.getY(),
         this.getZ() * $$0.getX() - this.getX() * $$0.getZ(),
         this.getX() * $$0.getY() - this.getY() * $$0.getX()
      );
   }

   public boolean closerThan(Vec3i $$0, double $$1) {
      return this.distSqr($$0) < Mth.square($$1);
   }

   public boolean closerToCenterThan(Position $$0, double $$1) {
      return this.distToCenterSqr($$0) < Mth.square($$1);
   }

   public double distSqr(Vec3i $$0) {
      return this.distToLowCornerSqr((double)$$0.getX(), (double)$$0.getY(), (double)$$0.getZ());
   }

   public double distToCenterSqr(Position $$0) {
      return this.distToCenterSqr($$0.x(), $$0.y(), $$0.z());
   }

   public double distToCenterSqr(double $$0, double $$1, double $$2) {
      double $$3 = (double)this.getX() + 0.5 - $$0;
      double $$4 = (double)this.getY() + 0.5 - $$1;
      double $$5 = (double)this.getZ() + 0.5 - $$2;
      return $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
   }

   public double distToLowCornerSqr(double $$0, double $$1, double $$2) {
      double $$3 = (double)this.getX() - $$0;
      double $$4 = (double)this.getY() - $$1;
      double $$5 = (double)this.getZ() - $$2;
      return $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
   }

   public int distManhattan(Vec3i $$0) {
      float $$1 = (float)Math.abs($$0.getX() - this.getX());
      float $$2 = (float)Math.abs($$0.getY() - this.getY());
      float $$3 = (float)Math.abs($$0.getZ() - this.getZ());
      return (int)($$1 + $$2 + $$3);
   }

   public int distChessboard(Vec3i $$0) {
      int $$1 = Math.abs(this.getX() - $$0.getX());
      int $$2 = Math.abs(this.getY() - $$0.getY());
      int $$3 = Math.abs(this.getZ() - $$0.getZ());
      return Math.max(Math.max($$1, $$2), $$3);
   }

   public int get(Direction.Axis $$0) {
      return $$0.choose(this.x, this.y, this.z);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   public String toShortString() {
      return this.getX() + ", " + this.getY() + ", " + this.getZ();
   }
}
