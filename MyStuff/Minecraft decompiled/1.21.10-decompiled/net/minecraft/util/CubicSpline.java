package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C, I extends BoundedFloatFunction<C>> extends BoundedFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> var1);

   static <C, I extends BoundedFloatFunction<C>> Codec<CubicSpline<C, I>> codec(Codec<I> $$0) {
      MutableObject<Codec<CubicSpline<C, I>>> $$1 = new MutableObject<>();

      record Point<C, I extends BoundedFloatFunction<C>>(float location, CubicSpline<C, I> value, float derivative) {
      }

      Codec<Point<C, I>> $$2 = RecordCodecBuilder.create(
         $$1x -> $$1x.group(
                  Codec.FLOAT.fieldOf("location").forGetter(Point::location),
                  Codec.lazyInitialized($$1::getValue).fieldOf("value").forGetter(Point::value),
                  Codec.FLOAT.fieldOf("derivative").forGetter(Point::derivative)
               )
               .apply($$1x, ($$0xx, $$1xx, $$2x) -> new Point($$0xx, $$1xx, $$2x))
      );
      Codec<CubicSpline.Multipoint<C, I>> $$3 = RecordCodecBuilder.create(
         $$2x -> $$2x.group(
                  $$0.fieldOf("coordinate").forGetter(CubicSpline.Multipoint::coordinate),
                  ExtraCodecs.nonEmptyList($$2.listOf())
                     .fieldOf("points")
                     .forGetter(
                        $$0xx -> IntStream.range(0, $$0xx.locations.length)
                              .mapToObj($$1xx -> new Point($$0xx.locations()[$$1xx], (CubicSpline<C, I>)$$0xx.values().get($$1xx), $$0xx.derivatives()[$$1xx]))
                              .toList()
                     )
               )
               .apply($$2x, ($$0xx, $$1xx) -> {
                  float[] $$2xxx = new float[$$1xx.size()];
                  ImmutableList.Builder<CubicSpline<C, I>> $$3xx = ImmutableList.builder();
                  float[] $$4 = new float[$$1xx.size()];
      
                  for(int $$5 = 0; $$5 < $$1xx.size(); ++$$5) {
                     Point<C, I> $$6 = (Point)$$1xx.get($$5);
                     $$2xxx[$$5] = $$6.location();
                     $$3xx.add($$6.value());
                     $$4[$$5] = $$6.derivative();
                  }
      
                  return CubicSpline.Multipoint.create((I)$$0xx, $$2xxx, $$3xx.build(), $$4);
               })
      );
      $$1.setValue(
         Codec.either(Codec.FLOAT, $$3)
            .xmap(
               $$0x -> $$0x.map(CubicSpline.Constant::new, $$0xx -> $$0xx),
               $$0x -> $$0x instanceof CubicSpline.Constant $$1xx ? Either.left($$1xx.value()) : Either.right((CubicSpline.Multipoint)$$0x)
            )
      );
      return $$1.getValue();
   }

   static <C, I extends BoundedFloatFunction<C>> CubicSpline<C, I> constant(float $$0) {
      return new CubicSpline.Constant<>($$0);
   }

   static <C, I extends BoundedFloatFunction<C>> CubicSpline.Builder<C, I> builder(I $$0) {
      return new CubicSpline.Builder<>($$0);
   }

   static <C, I extends BoundedFloatFunction<C>> CubicSpline.Builder<C, I> builder(I $$0, BoundedFloatFunction<Float> $$1) {
      return new CubicSpline.Builder<>($$0, $$1);
   }

   public static final class Builder<C, I extends BoundedFloatFunction<C>> {
      private final I coordinate;
      private final BoundedFloatFunction<Float> valueTransformer;
      private final FloatList locations = new FloatArrayList();
      private final List<CubicSpline<C, I>> values = Lists.<CubicSpline<C, I>>newArrayList();
      private final FloatList derivatives = new FloatArrayList();

      protected Builder(I $$0) {
         this($$0, BoundedFloatFunction.IDENTITY);
      }

      protected Builder(I $$0, BoundedFloatFunction<Float> $$1) {
         this.coordinate = $$0;
         this.valueTransformer = $$1;
      }

      public CubicSpline.Builder<C, I> addPoint(float $$0, float $$1) {
         return this.addPoint($$0, new CubicSpline.Constant<>(this.valueTransformer.apply((C)$$1)), 0.0F);
      }

      public CubicSpline.Builder<C, I> addPoint(float $$0, float $$1, float $$2) {
         return this.addPoint($$0, new CubicSpline.Constant<>(this.valueTransformer.apply((C)$$1)), $$2);
      }

      public CubicSpline.Builder<C, I> addPoint(float $$0, CubicSpline<C, I> $$1) {
         return this.addPoint($$0, $$1, 0.0F);
      }

      private CubicSpline.Builder<C, I> addPoint(float $$0, CubicSpline<C, I> $$1, float $$2) {
         if (!this.locations.isEmpty() && $$0 <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add($$0);
            this.values.add($$1);
            this.derivatives.add($$2);
            return this;
         }
      }

      public CubicSpline<C, I> build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return CubicSpline.Multipoint.create(
               this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray()
            );
         }
      }
   }

   @VisibleForDebug
   public static record Constant<C, I extends BoundedFloatFunction<C>>(float value) implements CubicSpline<C, I> {
      @Override
      public float apply(C $$0) {
         return this.value;
      }

      @Override
      public String parityString() {
         return String.format(Locale.ROOT, "k=%.3f", this.value);
      }

      @Override
      public float minValue() {
         return this.value;
      }

      @Override
      public float maxValue() {
         return this.value;
      }

      @Override
      public CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> $$0) {
         return this;
      }
   }

   public interface CoordinateVisitor<I> {
      I visit(I var1);
   }

   @VisibleForDebug
   public static record Multipoint<C, I extends BoundedFloatFunction<C>>(
      I coordinate, float[] locations, List<CubicSpline<C, I>> values, float[] derivatives, float minValue, float maxValue
   ) implements CubicSpline<C, I> {
      final float[] locations;

      public Multipoint(I param1, float[] param2, List<CubicSpline<C, I>> param3, float[] param4, float param5, float param6) {
         validateSizes($$1, $$2, $$3);
         this.coordinate = $$0;
         this.locations = $$1;
         this.values = $$2;
         this.derivatives = $$3;
         this.minValue = $$4;
         this.maxValue = $$5;
      }

      static <C, I extends BoundedFloatFunction<C>> CubicSpline.Multipoint<C, I> create(I $$0, float[] $$1, List<CubicSpline<C, I>> $$2, float[] $$3) {
         validateSizes($$1, $$2, $$3);
         int $$4 = $$1.length - 1;
         float $$5 = Float.POSITIVE_INFINITY;
         float $$6 = Float.NEGATIVE_INFINITY;
         float $$7 = $$0.minValue();
         float $$8 = $$0.maxValue();
         if ($$7 < $$1[0]) {
            float $$9 = linearExtend($$7, $$1, ((CubicSpline)$$2.get(0)).minValue(), $$3, 0);
            float $$10 = linearExtend($$7, $$1, ((CubicSpline)$$2.get(0)).maxValue(), $$3, 0);
            $$5 = Math.min($$5, Math.min($$9, $$10));
            $$6 = Math.max($$6, Math.max($$9, $$10));
         }

         if ($$8 > $$1[$$4]) {
            float $$11 = linearExtend($$8, $$1, ((CubicSpline)$$2.get($$4)).minValue(), $$3, $$4);
            float $$12 = linearExtend($$8, $$1, ((CubicSpline)$$2.get($$4)).maxValue(), $$3, $$4);
            $$5 = Math.min($$5, Math.min($$11, $$12));
            $$6 = Math.max($$6, Math.max($$11, $$12));
         }

         for(CubicSpline<C, I> $$13 : $$2) {
            $$5 = Math.min($$5, $$13.minValue());
            $$6 = Math.max($$6, $$13.maxValue());
         }

         for(int $$14 = 0; $$14 < $$4; ++$$14) {
            float $$15 = $$1[$$14];
            float $$16 = $$1[$$14 + 1];
            float $$17 = $$16 - $$15;
            CubicSpline<C, I> $$18 = (CubicSpline)$$2.get($$14);
            CubicSpline<C, I> $$19 = (CubicSpline)$$2.get($$14 + 1);
            float $$20 = $$18.minValue();
            float $$21 = $$18.maxValue();
            float $$22 = $$19.minValue();
            float $$23 = $$19.maxValue();
            float $$24 = $$3[$$14];
            float $$25 = $$3[$$14 + 1];
            if ($$24 != 0.0F || $$25 != 0.0F) {
               float $$26 = $$24 * $$17;
               float $$27 = $$25 * $$17;
               float $$28 = Math.min($$20, $$22);
               float $$29 = Math.max($$21, $$23);
               float $$30 = $$26 - $$23 + $$20;
               float $$31 = $$26 - $$22 + $$21;
               float $$32 = -$$27 + $$22 - $$21;
               float $$33 = -$$27 + $$23 - $$20;
               float $$34 = Math.min($$30, $$32);
               float $$35 = Math.max($$31, $$33);
               $$5 = Math.min($$5, $$28 + 0.25F * $$34);
               $$6 = Math.max($$6, $$29 + 0.25F * $$35);
            }
         }

         return new CubicSpline.Multipoint<>($$0, $$1, $$2, $$3, $$5, $$6);
      }

      private static float linearExtend(float $$0, float[] $$1, float $$2, float[] $$3, int $$4) {
         float $$5 = $$3[$$4];
         return $$5 == 0.0F ? $$2 : $$2 + $$5 * ($$0 - $$1[$$4]);
      }

      private static <C, I extends BoundedFloatFunction<C>> void validateSizes(float[] $$0, List<CubicSpline<C, I>> $$1, float[] $$2) {
         if ($$0.length != $$1.size() || $$0.length != $$2.length) {
            throw new IllegalArgumentException("All lengths must be equal, got: " + $$0.length + " " + $$1.size() + " " + $$2.length);
         } else if ($$0.length == 0) {
            throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
         }
      }

      @Override
      public float apply(C $$0) {
         float $$1 = this.coordinate.apply($$0);
         int $$2 = findIntervalStart(this.locations, $$1);
         int $$3 = this.locations.length - 1;
         if ($$2 < 0) {
            return linearExtend($$1, this.locations, ((CubicSpline)this.values.get(0)).apply($$0), this.derivatives, 0);
         } else if ($$2 == $$3) {
            return linearExtend($$1, this.locations, ((CubicSpline)this.values.get($$3)).apply($$0), this.derivatives, $$3);
         } else {
            float $$4 = this.locations[$$2];
            float $$5 = this.locations[$$2 + 1];
            float $$6 = ($$1 - $$4) / ($$5 - $$4);
            BoundedFloatFunction<C> $$7 = (BoundedFloatFunction)this.values.get($$2);
            BoundedFloatFunction<C> $$8 = (BoundedFloatFunction)this.values.get($$2 + 1);
            float $$9 = this.derivatives[$$2];
            float $$10 = this.derivatives[$$2 + 1];
            float $$11 = $$7.apply($$0);
            float $$12 = $$8.apply($$0);
            float $$13 = $$9 * ($$5 - $$4) - ($$12 - $$11);
            float $$14 = -$$10 * ($$5 - $$4) + ($$12 - $$11);
            return Mth.lerp($$6, $$11, $$12) + $$6 * (1.0F - $$6) * Mth.lerp($$6, $$13, $$14);
         }
      }

      private static int findIntervalStart(float[] $$0, float $$1) {
         return Mth.binarySearch(0, $$0.length, $$2 -> $$1 < $$0[$$2]) - 1;
      }

      @VisibleForTesting
      @Override
      public String parityString() {
         return "Spline{coordinate="
            + this.coordinate
            + ", locations="
            + this.toString(this.locations)
            + ", derivatives="
            + this.toString(this.derivatives)
            + ", values="
            + (String)this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]"))
            + "}";
      }

      private String toString(float[] $$0) {
         return "["
            + (String)IntStream.range(0, $$0.length)
               .mapToDouble($$1 -> (double)$$0[$$1])
               .mapToObj($$0x -> String.format(Locale.ROOT, "%.3f", $$0x))
               .collect(Collectors.joining(", "))
            + "]";
      }

      @Override
      public CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> $$0) {
         return create($$0.visit(this.coordinate), this.locations, this.values().stream().map($$1 -> $$1.mapAll($$0)).toList(), this.derivatives);
      }
   }
}
