package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
   public static final Codec<Direction> VERTICAL_CODEC = CODEC.validate(Direction::verifyVertical);
   public static final IntFunction<Direction> BY_ID = ByIdMap.continuous(Direction::get3DDataValue, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final StreamCodec<ByteBuf, Direction> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Direction::get3DDataValue);
   @Deprecated
   public static final Codec<Direction> LEGACY_ID_CODEC = Codec.BYTE.xmap(Direction::from3DDataValue, $$0 -> (byte)$$0.get3DDataValue());
   @Deprecated
   public static final Codec<Direction> LEGACY_ID_CODEC_2D = Codec.BYTE.xmap(Direction::from2DDataValue, $$0 -> (byte)$$0.get2DDataValue());
   private static final ImmutableList<Direction.Axis> YXZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);
   private static final ImmutableList<Direction.Axis> YZX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X);
   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i normal;
   private final Vec3 normalVec3;
   private final Vector3fc normalVec3f;
   private static final Direction[] VALUES = values();
   private static final Direction[] BY_3D_DATA = (Direction[])Arrays.stream(VALUES)
      .sorted(Comparator.comparingInt($$0 -> $$0.data3d))
      .toArray($$0 -> new Direction[$$0]);
   private static final Direction[] BY_2D_DATA = (Direction[])Arrays.stream(VALUES)
      .filter($$0 -> $$0.getAxis().isHorizontal())
      .sorted(Comparator.comparingInt($$0 -> $$0.data2d))
      .toArray($$0 -> new Direction[$$0]);

   private Direction(
      final int param3,
      final int param4,
      final int param5,
      final String param6,
      final Direction.AxisDirection param7,
      final Direction.Axis param8,
      final Vec3i param9
   ) {
      this.data3d = $$0;
      this.data2d = $$2;
      this.oppositeIndex = $$1;
      this.name = $$3;
      this.axis = $$5;
      this.axisDirection = $$4;
      this.normal = $$6;
      this.normalVec3 = Vec3.atLowerCornerOf($$6);
      this.normalVec3f = new Vector3f((float)$$6.getX(), (float)$$6.getY(), (float)$$6.getZ());
   }

   public static Direction[] orderedByNearest(Entity $$0) {
      float $$1 = $$0.getViewXRot(1.0F) * (float) (Math.PI / 180.0);
      float $$2 = -$$0.getViewYRot(1.0F) * (float) (Math.PI / 180.0);
      float $$3 = Mth.sin($$1);
      float $$4 = Mth.cos($$1);
      float $$5 = Mth.sin($$2);
      float $$6 = Mth.cos($$2);
      boolean $$7 = $$5 > 0.0F;
      boolean $$8 = $$3 < 0.0F;
      boolean $$9 = $$6 > 0.0F;
      float $$10 = $$7 ? $$5 : -$$5;
      float $$11 = $$8 ? -$$3 : $$3;
      float $$12 = $$9 ? $$6 : -$$6;
      float $$13 = $$10 * $$4;
      float $$14 = $$12 * $$4;
      Direction $$15 = $$7 ? EAST : WEST;
      Direction $$16 = $$8 ? UP : DOWN;
      Direction $$17 = $$9 ? SOUTH : NORTH;
      if ($$10 > $$12) {
         if ($$11 > $$13) {
            return makeDirectionArray($$16, $$15, $$17);
         } else {
            return $$14 > $$11 ? makeDirectionArray($$15, $$17, $$16) : makeDirectionArray($$15, $$16, $$17);
         }
      } else if ($$11 > $$14) {
         return makeDirectionArray($$16, $$17, $$15);
      } else {
         return $$13 > $$11 ? makeDirectionArray($$17, $$15, $$16) : makeDirectionArray($$17, $$16, $$15);
      }
   }

   private static Direction[] makeDirectionArray(Direction $$0, Direction $$1, Direction $$2) {
      return new Direction[]{$$0, $$1, $$2, $$2.getOpposite(), $$1.getOpposite(), $$0.getOpposite()};
   }

   public static Direction rotate(Matrix4fc $$0, Direction $$1) {
      Vector3f $$2 = $$0.transformDirection($$1.normalVec3f, new Vector3f());
      return getApproximateNearest($$2.x(), $$2.y(), $$2.z());
   }

   public static Collection<Direction> allShuffled(RandomSource $$0) {
      return Util.<Direction>shuffledCopy(values(), $$0);
   }

   public static Stream<Direction> stream() {
      return Stream.of(VALUES);
   }

   public static float getYRot(Direction $$0) {
      return switch($$0.ordinal()) {
         case 2 -> 180.0F;
         case 3 -> 0.0F;
         case 4 -> 90.0F;
         case 5 -> -90.0F;
         default -> throw new IllegalStateException("No y-Rot for vertical axis: " + $$0);
      };
   }

   public Quaternionf getRotation() {
      return switch(this.ordinal()) {
         case 0 -> new Quaternionf().rotationX((float) Math.PI);
         case 1 -> new Quaternionf();
         case 2 -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) Math.PI);
         case 3 -> new Quaternionf().rotationX((float) (Math.PI / 2));
         case 4 -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2));
         case 5 -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (-Math.PI / 2));
         default -> throw new MatchException(null, null);
      };
   }

   public int get3DDataValue() {
      return this.data3d;
   }

   public int get2DDataValue() {
      return this.data2d;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public static Direction getFacingAxis(Entity $$0, Direction.Axis $$1) {
      return switch($$1.ordinal()) {
         case 0 -> EAST.isFacingAngle($$0.getViewYRot(1.0F)) ? EAST : WEST;
         case 1 -> $$0.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
         case 2 -> SOUTH.isFacingAngle($$0.getViewYRot(1.0F)) ? SOUTH : NORTH;
         default -> throw new MatchException(null, null);
      };
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(Direction.Axis $$0) {
      return switch($$0.ordinal()) {
         case 0 -> this != WEST && this != EAST ? this.getClockWiseX() : this;
         case 1 -> this != UP && this != DOWN ? this.getClockWise() : this;
         case 2 -> this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
         default -> throw new MatchException(null, null);
      };
   }

   public Direction getCounterClockWise(Direction.Axis $$0) {
      return switch($$0.ordinal()) {
         case 0 -> this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
         case 1 -> this != UP && this != DOWN ? this.getCounterClockWise() : this;
         case 2 -> this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
         default -> throw new MatchException(null, null);
      };
   }

   public Direction getClockWise() {
      return switch(this.ordinal()) {
         case 2 -> EAST;
         case 3 -> WEST;
         case 4 -> NORTH;
         case 5 -> SOUTH;
         default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      };
   }

   private Direction getClockWiseX() {
      return switch(this.ordinal()) {
         case 0 -> SOUTH;
         case 1 -> NORTH;
         case 2 -> DOWN;
         case 3 -> UP;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      };
   }

   private Direction getCounterClockWiseX() {
      return switch(this.ordinal()) {
         case 0 -> NORTH;
         case 1 -> SOUTH;
         case 2 -> UP;
         case 3 -> DOWN;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      };
   }

   private Direction getClockWiseZ() {
      return switch(this.ordinal()) {
         case 0 -> WEST;
         case 1 -> EAST;
         default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case 4 -> UP;
         case 5 -> DOWN;
      };
   }

   private Direction getCounterClockWiseZ() {
      return switch(this.ordinal()) {
         case 0 -> EAST;
         case 1 -> WEST;
         default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case 4 -> DOWN;
         case 5 -> UP;
      };
   }

   public Direction getCounterClockWise() {
      return switch(this.ordinal()) {
         case 2 -> WEST;
         case 3 -> EAST;
         case 4 -> SOUTH;
         case 5 -> NORTH;
         default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
      };
   }

   public int getStepX() {
      return this.normal.getX();
   }

   public int getStepY() {
      return this.normal.getY();
   }

   public int getStepZ() {
      return this.normal.getZ();
   }

   public Vector3f step() {
      return new Vector3f(this.normalVec3f);
   }

   public String getName() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String $$0) {
      return (Direction)CODEC.byName($$0);
   }

   public static Direction from3DDataValue(int $$0) {
      return BY_3D_DATA[Mth.abs($$0 % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(int $$0) {
      return BY_2D_DATA[Mth.abs($$0 % BY_2D_DATA.length)];
   }

   public static Direction fromYRot(double $$0) {
      return from2DDataValue(Mth.floor($$0 / 90.0 + 0.5) & 3);
   }

   public static Direction fromAxisAndDirection(Direction.Axis $$0, Direction.AxisDirection $$1) {
      return switch($$0.ordinal()) {
         case 0 -> $$1 == Direction.AxisDirection.POSITIVE ? EAST : WEST;
         case 1 -> $$1 == Direction.AxisDirection.POSITIVE ? UP : DOWN;
         case 2 -> $$1 == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
         default -> throw new MatchException(null, null);
      };
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(RandomSource $$0) {
      return Util.getRandom(VALUES, $$0);
   }

   public static Direction getApproximateNearest(double $$0, double $$1, double $$2) {
      return getApproximateNearest((float)$$0, (float)$$1, (float)$$2);
   }

   public static Direction getApproximateNearest(float $$0, float $$1, float $$2) {
      Direction $$3 = NORTH;
      float $$4 = Float.MIN_VALUE;

      for(Direction $$5 : VALUES) {
         float $$6 = $$0 * (float)$$5.normal.getX() + $$1 * (float)$$5.normal.getY() + $$2 * (float)$$5.normal.getZ();
         if ($$6 > $$4) {
            $$4 = $$6;
            $$3 = $$5;
         }
      }

      return $$3;
   }

   public static Direction getApproximateNearest(Vec3 $$0) {
      return getApproximateNearest($$0.x, $$0.y, $$0.z);
   }

   @Nullable
   @Contract("_,_,_,!null->!null;_,_,_,_->_")
   public static Direction getNearest(int $$0, int $$1, int $$2, @Nullable Direction $$3) {
      int $$4 = Math.abs($$0);
      int $$5 = Math.abs($$1);
      int $$6 = Math.abs($$2);
      if ($$4 > $$6 && $$4 > $$5) {
         return $$0 < 0 ? WEST : EAST;
      } else if ($$6 > $$4 && $$6 > $$5) {
         return $$2 < 0 ? NORTH : SOUTH;
      } else if ($$5 > $$4 && $$5 > $$6) {
         return $$1 < 0 ? DOWN : UP;
      } else {
         return $$3;
      }
   }

   @Nullable
   @Contract("_,!null->!null;_,_->_")
   public static Direction getNearest(Vec3i $$0, @Nullable Direction $$1) {
      return getNearest($$0.getX(), $$0.getY(), $$0.getZ(), $$1);
   }

   public String toString() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   private static DataResult<Direction> verifyVertical(Direction $$0) {
      return $$0.getAxis().isVertical() ? DataResult.success($$0) : DataResult.error(() -> "Expected a vertical direction");
   }

   public static Direction get(Direction.AxisDirection $$0, Direction.Axis $$1) {
      for(Direction $$2 : VALUES) {
         if ($$2.getAxisDirection() == $$0 && $$2.getAxis() == $$1) {
            return $$2;
         }
      }

      throw new IllegalArgumentException("No such direction: " + $$0 + " " + $$1);
   }

   public static ImmutableList<Direction.Axis> axisStepOrder(Vec3 $$0) {
      return Math.abs($$0.x) < Math.abs($$0.z) ? YZX_AXIS_ORDER : YXZ_AXIS_ORDER;
   }

   public Vec3i getUnitVec3i() {
      return this.normal;
   }

   public Vec3 getUnitVec3() {
      return this.normalVec3;
   }

   public Vector3fc getUnitVec3f() {
      return this.normalVec3f;
   }

   public boolean isFacingAngle(float $$0) {
      float $$1 = $$0 * (float) (Math.PI / 180.0);
      float $$2 = -Mth.sin($$1);
      float $$3 = Mth.cos($$1);
      return (float)this.normal.getX() * $$2 + (float)this.normal.getZ() * $$3 > 0.0F;
   }

   public static enum Axis implements StringRepresentable, Predicate<Direction> {
      X("x") {
         @Override
         public int choose(int $$0, int $$1, int $$2) {
            return $$0;
         }

         @Override
         public boolean choose(boolean $$0, boolean $$1, boolean $$2) {
            return $$0;
         }

         @Override
         public double choose(double $$0, double $$1, double $$2) {
            return $$0;
         }

         @Override
         public Direction getPositive() {
            return Direction.EAST;
         }

         @Override
         public Direction getNegative() {
            return Direction.WEST;
         }
      },
      Y("y") {
         @Override
         public int choose(int $$0, int $$1, int $$2) {
            return $$1;
         }

         @Override
         public double choose(double $$0, double $$1, double $$2) {
            return $$1;
         }

         @Override
         public boolean choose(boolean $$0, boolean $$1, boolean $$2) {
            return $$1;
         }

         @Override
         public Direction getPositive() {
            return Direction.UP;
         }

         @Override
         public Direction getNegative() {
            return Direction.DOWN;
         }
      },
      Z("z") {
         @Override
         public int choose(int $$0, int $$1, int $$2) {
            return $$2;
         }

         @Override
         public double choose(double $$0, double $$1, double $$2) {
            return $$2;
         }

         @Override
         public boolean choose(boolean $$0, boolean $$1, boolean $$2) {
            return $$2;
         }

         @Override
         public Direction getPositive() {
            return Direction.SOUTH;
         }

         @Override
         public Direction getNegative() {
            return Direction.NORTH;
         }
      };

      public static final Direction.Axis[] VALUES = values();
      public static final StringRepresentable.EnumCodec<Direction.Axis> CODEC = StringRepresentable.fromEnum(Direction.Axis::values);
      private final String name;

      Axis(final String param3) {
         this.name = $$0;
      }

      @Nullable
      public static Direction.Axis byName(String $$0) {
         return (Direction.Axis)CODEC.byName($$0);
      }

      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public abstract Direction getPositive();

      public abstract Direction getNegative();

      public Direction[] getDirections() {
         return new Direction[]{this.getPositive(), this.getNegative()};
      }

      public String toString() {
         return this.name;
      }

      public static Direction.Axis getRandom(RandomSource $$0) {
         return Util.getRandom(VALUES, $$0);
      }

      public boolean test(@Nullable Direction $$0) {
         return $$0 != null && $$0.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         return switch(this.ordinal()) {
            case 0, 2 -> Direction.Plane.HORIZONTAL;
            case 1 -> Direction.Plane.VERTICAL;
            default -> throw new MatchException(null, null);
         };
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(int var1, int var2, int var3);

      public abstract double choose(double var1, double var3, double var5);

      public abstract boolean choose(boolean var1, boolean var2, boolean var3);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int step;
      private final String name;

      private AxisDirection(final int param3, final String param4) {
         this.step = $$0;
         this.name = $$1;
      }

      public int getStep() {
         return this.step;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public Direction.AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Direction.Axis[] axis;

      private Plane(final Direction[] param3, final Direction.Axis[] param4) {
         this.faces = $$0;
         this.axis = $$1;
      }

      public Direction getRandomDirection(RandomSource $$0) {
         return Util.getRandom(this.faces, $$0);
      }

      public Direction.Axis getRandomAxis(RandomSource $$0) {
         return Util.getRandom(this.axis, $$0);
      }

      public boolean test(@Nullable Direction $$0) {
         return $$0 != null && $$0.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }

      public List<Direction> shuffledCopy(RandomSource $$0) {
         return Util.shuffledCopy(this.faces, $$0);
      }

      public int length() {
         return this.faces.length;
      }
   }
}
