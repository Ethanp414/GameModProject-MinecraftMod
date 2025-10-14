package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnum(Supplier<E[]> $$0) {
      return fromEnumWithMapping($$0, $$0x -> $$0x);
   }

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnumWithMapping(Supplier<E[]> $$0, Function<String, String> $$1) {
      E[] $$2 = (E[])$$0.get();
      Function<String, E> $$3 = createNameLookup($$2, $$1x -> (String)$$1.apply(((StringRepresentable)$$1x).getSerializedName()));
      return new StringRepresentable.EnumCodec<>($$2, $$3);
   }

   static <T extends StringRepresentable> Codec<T> fromValues(Supplier<T[]> $$0) {
      T[] $$1 = (T[])$$0.get();
      Function<String, T> $$2 = createNameLookup($$1);
      ToIntFunction<T> $$3 = Util.createIndexLookup(Arrays.asList($$1));
      return new StringRepresentable.StringRepresentableCodec<>($$1, $$2, $$3);
   }

   static <T extends StringRepresentable> Function<String, T> createNameLookup(T[] $$0) {
      return createNameLookup($$0, StringRepresentable::getSerializedName);
   }

   static <T> Function<String, T> createNameLookup(T[] $$0, Function<T, String> $$1) {
      if ($$0.length > 16) {
         Map<String, T> $$2 = (Map)Arrays.stream($$0).collect(Collectors.toMap($$1, $$0x -> $$0x));
         return $$1x -> $$1x == null ? null : $$2.get($$1x);
      } else {
         return $$2x -> {
            for(T $$3 : $$0) {
               if (((String)$$1.apply($$3)).equals($$2x)) {
                  return $$3;
               }
            }

            return null;
         };
      }
   }

   static Keyable keys(final StringRepresentable[] $$0) {
      return new Keyable() {
         @Override
         public <T> Stream<T> keys(DynamicOps<T> $$0x) {
            return Arrays.stream($$0).map(StringRepresentable::getSerializedName).map($$0::createString);
         }
      };
   }

   public static class EnumCodec<E extends Enum<E> & StringRepresentable> extends StringRepresentable.StringRepresentableCodec<E> {
      private final Function<String, E> resolver;

      public EnumCodec(E[] $$0, Function<String, E> $$1) {
         super($$0, $$1, $$0x -> ((Enum)$$0x).ordinal());
         this.resolver = $$1;
      }

      @Nullable
      public E byName(@Nullable String $$0) {
         return (E)this.resolver.apply($$0);
      }

      public E byName(@Nullable String $$0, E $$1) {
         return (E)Objects.requireNonNullElse(this.byName($$0), $$1);
      }

      public E byName(@Nullable String $$0, Supplier<? extends E> $$1) {
         return (E)Objects.requireNonNullElseGet(this.byName($$0), $$1);
      }
   }

   public static class StringRepresentableCodec<S extends StringRepresentable> implements Codec<S> {
      private final Codec<S> codec;

      public StringRepresentableCodec(S[] $$0, Function<String, S> $$1, ToIntFunction<S> $$2) {
         this.codec = ExtraCodecs.orCompressed(
            Codec.stringResolver(StringRepresentable::getSerializedName, $$1),
            ExtraCodecs.idResolverCodec($$2, $$1x -> $$1x >= 0 && $$1x < $$0.length ? $$0[$$1x] : null, -1)
         );
      }

      @Override
      public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> $$0, T $$1) {
         return this.codec.decode($$0, $$1);
      }

      public <T> DataResult<T> encode(S $$0, DynamicOps<T> $$1, T $$2) {
         return this.codec.encode($$0, $$1, $$2);
      }
   }
}
