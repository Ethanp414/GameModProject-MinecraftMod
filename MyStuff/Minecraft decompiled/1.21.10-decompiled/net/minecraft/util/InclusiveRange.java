package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public record InclusiveRange<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
   public static final Codec<InclusiveRange<Integer>> INT = codec(Codec.INT);

   public InclusiveRange(T param1, T param2) {
      if ($$0.compareTo($$1) > 0) {
         throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
      } else {
         this.minInclusive = $$0;
         this.maxInclusive = $$1;
      }
   }

   public InclusiveRange(T $$0) {
      this($$0, $$0);
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> $$0) {
      return ExtraCodecs.intervalCodec(
         $$0, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive
      );
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> $$0, T $$1, T $$2) {
      return codec($$0)
         .validate(
            $$2x -> {
               if ($$2x.minInclusive().compareTo($$1) < 0) {
                  return DataResult.error(() -> "Range limit too low, expected at least " + $$1 + " [" + $$2x.minInclusive() + "-" + $$2x.maxInclusive() + "]");
               } else {
                  return $$2x.maxInclusive().compareTo($$2) > 0
                     ? DataResult.error(() -> "Range limit too high, expected at most " + $$2 + " [" + $$2x.minInclusive() + "-" + $$2x.maxInclusive() + "]")
                     : DataResult.success($$2x);
               }
            }
         );
   }

   public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(T $$0, T $$1) {
      return $$0.compareTo($$1) <= 0
         ? DataResult.success(new InclusiveRange($$0, $$1))
         : DataResult.error(() -> "min_inclusive must be less than or equal to max_inclusive");
   }

   public <S extends Comparable<S>> InclusiveRange<S> map(Function<? super T, ? extends S> $$0) {
      return new InclusiveRange<>((S)$$0.apply(this.minInclusive), (S)$$0.apply(this.maxInclusive));
   }

   public boolean isValueInRange(T $$0) {
      return $$0.compareTo(this.minInclusive) >= 0 && $$0.compareTo(this.maxInclusive) <= 0;
   }

   public boolean contains(InclusiveRange<T> $$0) {
      return $$0.minInclusive().compareTo(this.minInclusive) >= 0 && $$0.maxInclusive.compareTo(this.maxInclusive) <= 0;
   }

   public String toString() {
      return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
   }
}
