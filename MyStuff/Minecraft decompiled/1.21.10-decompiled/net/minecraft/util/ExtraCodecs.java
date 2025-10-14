package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class ExtraCodecs {
   public static final Codec<JsonElement> JSON = converter(JsonOps.INSTANCE);
   public static final Codec<Object> JAVA = converter(JavaOps.INSTANCE);
   public static final Codec<Tag> NBT = converter(NbtOps.INSTANCE);
   public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT
      .listOf()
      .comapFlatMap($$0 -> Util.fixedSize($$0, 2).map($$0x -> new Vector2f($$0x.get(0), $$0x.get(1))), $$0 -> List.of($$0.x(), $$0.y()));
   public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
      .listOf()
      .comapFlatMap($$0 -> Util.fixedSize($$0, 3).map($$0x -> new Vector3f($$0x.get(0), $$0x.get(1), $$0x.get(2))), $$0 -> List.of($$0.x(), $$0.y(), $$0.z()));
   public static final Codec<Vector3i> VECTOR3I = Codec.INT
      .listOf()
      .comapFlatMap($$0 -> Util.fixedSize($$0, 3).map($$0x -> new Vector3i($$0x.get(0), $$0x.get(1), $$0x.get(2))), $$0 -> List.of($$0.x(), $$0.y(), $$0.z()));
   public static final Codec<Vector4f> VECTOR4F = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         $$0 -> Util.fixedSize($$0, 4).map($$0x -> new Vector4f($$0x.get(0), $$0x.get(1), $$0x.get(2), $$0x.get(3))),
         $$0 -> List.of($$0.x(), $$0.y(), $$0.z(), $$0.w())
      );
   public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         $$0 -> Util.fixedSize($$0, 4).map($$0x -> new Quaternionf($$0x.get(0), $$0x.get(1), $$0x.get(2), $$0x.get(3)).normalize()),
         $$0 -> List.of($$0.x, $$0.y, $$0.z, $$0.w)
      );
   public static final Codec<AxisAngle4f> AXISANGLE4F = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Codec.FLOAT.fieldOf("angle").forGetter($$0x -> $$0x.angle), VECTOR3F.fieldOf("axis").forGetter($$0x -> new Vector3f($$0x.x, $$0x.y, $$0x.z))
            )
            .apply($$0, AxisAngle4f::new)
   );
   public static final Codec<Quaternionf> QUATERNIONF = Codec.withAlternative(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
   public static final Codec<Matrix4fc> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap($$0 -> Util.fixedSize($$0, 16).map($$0x -> {
         Matrix4f $$1 = new Matrix4f();

         for(int $$2 = 0; $$2 < $$0x.size(); ++$$2) {
            $$1.setRowColumn($$2 >> 2, $$2 & 3, $$0x.get($$2));
         }

         return $$1.determineProperties();
      }), $$0 -> {
      FloatList $$1 = new FloatArrayList(16);

      for(int $$2 = 0; $$2 < 16; ++$$2) {
         $$1.add($$0.getRowColumn($$2 >> 2, $$2 & 3));
      }

      return $$1;
   });
   public static final Codec<Integer> RGB_COLOR_CODEC = Codec.withAlternative(Codec.INT, VECTOR3F, $$0 -> ARGB.colorFromFloat(1.0F, $$0.x(), $$0.y(), $$0.z()));
   public static final Codec<Integer> ARGB_COLOR_CODEC = Codec.withAlternative(
      Codec.INT, VECTOR4F, $$0 -> ARGB.colorFromFloat($$0.w(), $$0.x(), $$0.y(), $$0.z())
   );
   public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE
      .flatComapMap(
         UnsignedBytes::toInt,
         $$0 -> $$0 > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + $$0 + " > 255") : DataResult.success($$0.byteValue())
      );
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, $$0 -> "Value must be non-negative: " + $$0);
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, $$0 -> "Value must be positive: " + $$0);
   public static final Codec<Float> NON_NEGATIVE_FLOAT = floatRangeMinInclusiveWithMessage(0.0F, Float.MAX_VALUE, $$0 -> "Value must be non-negative: " + $$0);
   public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, $$0 -> "Value must be positive: " + $$0);
   public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap($$0 -> {
      try {
         return DataResult.success(Pattern.compile($$0));
      } catch (PatternSyntaxException var2) {
         return DataResult.error(() -> "Invalid regex pattern '" + $$0 + "': " + var2.getMessage());
      }
   }, Pattern::pattern);
   public static final Codec<Instant> INSTANT_ISO8601 = temporalCodec(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
   public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap($$0 -> {
      try {
         return DataResult.success(Base64.getDecoder().decode($$0));
      } catch (IllegalArgumentException var2) {
         return DataResult.error(() -> "Malformed base64 string");
      }
   }, $$0 -> Base64.getEncoder().encodeToString($$0));
   public static final Codec<String> ESCAPED_STRING = Codec.STRING
      .comapFlatMap($$0 -> DataResult.success(StringEscapeUtils.unescapeJava($$0)), StringEscapeUtils::escapeJava);
   public static final Codec<ExtraCodecs.TagOrElementLocation> TAG_OR_ELEMENT_ID = Codec.STRING
      .comapFlatMap(
         $$0 -> $$0.startsWith("#")
               ? ResourceLocation.read($$0.substring(1)).map($$0x -> new ExtraCodecs.TagOrElementLocation($$0x, true))
               : ResourceLocation.read($$0).map($$0x -> new ExtraCodecs.TagOrElementLocation($$0x, false)),
         ExtraCodecs.TagOrElementLocation::decoratedId
      );
   public static final Function<Optional<Long>, OptionalLong> toOptionalLong = $$0 -> (OptionalLong)$$0.map(OptionalLong::of).orElseGet(OptionalLong::empty);
   public static final Function<OptionalLong, Optional<Long>> fromOptionalLong = $$0 -> $$0.isPresent() ? Optional.of($$0.getAsLong()) : Optional.empty();
   public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap($$0 -> BitSet.valueOf($$0.toArray()), $$0 -> Arrays.stream($$0.toLongArray()));
   private static final Codec<Property> PROPERTY = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Codec.STRING.fieldOf("name").forGetter(Property::name),
               Codec.STRING.fieldOf("value").forGetter(Property::value),
               Codec.STRING.lenientOptionalFieldOf("signature").forGetter($$0x -> Optional.ofNullable($$0x.signature()))
            )
            .apply($$0, ($$0x, $$1, $$2) -> new Property($$0x, $$1, (String)$$2.orElse(null)))
   );
   public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf())
      .xmap($$0 -> {
         Builder<String, Property> $$1 = ImmutableMultimap.builder();
         $$0.ifLeft($$1x -> $$1x.forEach(($$1xx, $$2) -> {
               for(String $$3 : $$2) {
                  $$1.put($$1xx, new Property($$1xx, $$3));
               }
            })).ifRight($$1x -> {
            for(Property $$2 : $$1x) {
               $$1.put($$2.name(), $$2);
            }
         });
         return new PropertyMap($$1.build());
      }, $$0 -> Either.right($$0.values().stream().toList()));
   public static final Codec<String> PLAYER_NAME = Codec.string(0, 16)
      .validate(
         $$0 -> StringUtil.isValidPlayerName($$0)
               ? DataResult.success($$0)
               : DataResult.error(() -> "Player name contained disallowed characters: '" + $$0 + "'")
      );
   public static final Codec<GameProfile> AUTHLIB_GAME_PROFILE = gameProfileCodec(UUIDUtil.AUTHLIB_CODEC).codec();
   public static final MapCodec<GameProfile> STORED_GAME_PROFILE = gameProfileCodec(UUIDUtil.CODEC);
   public static final Codec<String> NON_EMPTY_STRING = Codec.STRING
      .validate($$0 -> $$0.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success($$0));
   public static final Codec<Integer> CODEPOINT = Codec.STRING.comapFlatMap($$0 -> {
      int[] $$1 = $$0.codePoints().toArray();
      return $$1.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + $$0) : DataResult.success($$1[0]);
   }, Character::toString);
   public static final Codec<String> RESOURCE_PATH_CODEC = Codec.STRING
      .validate(
         $$0 -> !ResourceLocation.isValidPath($$0)
               ? DataResult.error(() -> "Invalid string to use as a resource path element: " + $$0)
               : DataResult.success($$0)
      );
   public static final Codec<URI> UNTRUSTED_URI = Codec.STRING.comapFlatMap($$0 -> {
      try {
         return DataResult.success(Util.parseAndValidateUntrustedUri($$0));
      } catch (URISyntaxException var2) {
         return DataResult.error(var2::getMessage);
      }
   }, URI::toString);
   public static final Codec<String> CHAT_STRING = Codec.STRING.validate($$0 -> {
      for(int $$1 = 0; $$1 < $$0.length(); ++$$1) {
         char $$2 = $$0.charAt($$1);
         if (!StringUtil.isAllowedChatCharacter($$2)) {
            return DataResult.error(() -> "Disallowed chat character: '" + $$2 + "'");
         }
      }

      return DataResult.success($$0);
   });

   public static <T> Codec<T> converter(DynamicOps<T> $$0) {
      return Codec.PASSTHROUGH.xmap($$1 -> $$1.convert($$0).getValue(), $$1 -> new Dynamic<>($$0, (T)$$1));
   }

   public static <P, I> Codec<I> intervalCodec(
      Codec<P> $$0, String $$1, String $$2, BiFunction<P, P, DataResult<I>> $$3, Function<I, P> $$4, Function<I, P> $$5
   ) {
      Codec<I> $$6 = Codec.list($$0).comapFlatMap($$1x -> Util.fixedSize($$1x, 2).flatMap($$1xx -> {
            P $$2xx = (P)$$1xx.get(0);
            P $$3xx = (P)$$1xx.get(1);
            return (DataResult)$$3.apply($$2xx, $$3xx);
         }), $$2x -> ImmutableList.of($$4.apply($$2x), $$5.apply($$2x)));
      Codec<I> $$7 = RecordCodecBuilder.create(
            $$3x -> $$3x.group($$0.fieldOf($$1).forGetter(Pair::getFirst), $$0.fieldOf($$2).forGetter(Pair::getSecond)).apply($$3x, Pair::of)
         )
         .comapFlatMap($$1x -> (DataResult)$$3.apply($$1x.getFirst(), $$1x.getSecond()), $$2x -> Pair.of($$4.apply($$2x), $$5.apply($$2x)));
      Codec<I> $$8 = Codec.withAlternative($$6, $$7);
      return Codec.either($$0, $$8).comapFlatMap($$1x -> $$1x.map($$1xx -> (DataResult)$$3.apply($$1xx, $$1xx), DataResult::success), $$2x -> {
         P $$3xx = (P)$$4.apply($$2x);
         P $$4xx = (P)$$5.apply($$2x);
         return Objects.equals($$3xx, $$4xx) ? Either.left($$3xx) : Either.right($$2x);
      });
   }

   public static <A> ResultFunction<A> orElsePartial(final A $$0) {
      return new ResultFunction<A>() {
         @Override
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> $$0x, T $$1, DataResult<Pair<A, T>> $$2) {
            MutableObject<String> $$3 = new MutableObject();
            Optional<Pair<A, T>> $$4 = $$2.resultOrPartial($$3::setValue);
            return $$4.isPresent() ? $$2 : DataResult.error(() -> "(" + (String)$$3.getValue() + " -> using default)", Pair.of($$0, $$1));
         }

         @Override
         public <T> DataResult<T> coApply(DynamicOps<T> $$0x, A $$1, DataResult<T> $$2) {
            return $$2;
         }

         public String toString() {
            return "OrElsePartial[" + $$0 + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> $$0, IntFunction<E> $$1, int $$2) {
      return Codec.INT
         .flatXmap(
            $$1x -> (DataResult)Optional.ofNullable($$1.apply($$1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + $$1x)),
            $$2x -> {
               int $$3 = $$0.applyAsInt($$2x);
               return $$3 == $$2 ? DataResult.error(() -> "Element with unknown id: " + $$2x) : DataResult.success($$3);
            }
         );
   }

   public static <I, E> Codec<E> idResolverCodec(Codec<I> $$0, Function<I, E> $$1, Function<E, I> $$2) {
      return $$0.flatXmap($$1x -> {
         E $$2xx = (E)$$1.apply($$1x);
         return $$2xx == null ? DataResult.error(() -> "Unknown element id: " + $$1x) : DataResult.success($$2xx);
      }, $$1x -> {
         I $$2xx = (I)$$2.apply($$1x);
         return $$2xx == null ? DataResult.error(() -> "Element with unknown id: " + $$1x) : DataResult.success($$2xx);
      });
   }

   public static <E> Codec<E> orCompressed(final Codec<E> $$0, final Codec<E> $$1) {
      return new Codec<E>() {
         @Override
         public <T> DataResult<T> encode(E $$0x, DynamicOps<T> $$1x, T $$2) {
            return $$1.compressMaps() ? $$1.encode($$0, $$1, $$2) : $$0.encode($$0, $$1, $$2);
         }

         @Override
         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> $$0x, T $$1x) {
            return $$0.compressMaps() ? $$1.decode($$0, $$1) : $$0.decode($$0, $$1);
         }

         public String toString() {
            return $$0 + " orCompressed " + $$1;
         }
      };
   }

   public static <E> MapCodec<E> orCompressed(final MapCodec<E> $$0, final MapCodec<E> $$1) {
      return new MapCodec<E>() {
         @Override
         public <T> RecordBuilder<T> encode(E $$0x, DynamicOps<T> $$1x, RecordBuilder<T> $$2) {
            return $$1.compressMaps() ? $$1.encode($$0, $$1, $$2) : $$0.encode($$0, $$1, $$2);
         }

         @Override
         public <T> DataResult<E> decode(DynamicOps<T> $$0x, MapLike<T> $$1x) {
            return $$0.compressMaps() ? $$1.decode($$0, $$1) : $$0.decode($$0, $$1);
         }

         @Override
         public <T> Stream<T> keys(DynamicOps<T> $$0x) {
            return $$1.keys($$0);
         }

         public String toString() {
            return $$0 + " orCompressed " + $$1;
         }
      };
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> $$0, final Function<E, Lifecycle> $$1, final Function<E, Lifecycle> $$2) {
      return $$0.mapResult(new ResultFunction<E>() {
         @Override
         public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> $$0, T $$1x, DataResult<Pair<E, T>> $$2x) {
            return (DataResult<Pair<E, T>>)$$2.result().map($$2xxx -> $$2.setLifecycle((Lifecycle)$$1.apply($$2xxx.getFirst()))).orElse($$2);
         }

         @Override
         public <T> DataResult<T> coApply(DynamicOps<T> $$0, E $$1x, DataResult<T> $$2x) {
            return $$2.setLifecycle((Lifecycle)$$2.apply($$1));
         }

         public String toString() {
            return "WithLifecycle[" + $$1 + " " + $$2 + "]";
         }
      });
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> $$0, Function<E, Lifecycle> $$1) {
      return overrideLifecycle($$0, $$1, $$1);
   }

   public static <K, V> ExtraCodecs.StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> $$0, Codec<V> $$1) {
      return new ExtraCodecs.StrictUnboundedMapCodec<>($$0, $$1);
   }

   public static <E> Codec<List<E>> compactListCodec(Codec<E> $$0) {
      return compactListCodec($$0, $$0.listOf());
   }

   public static <E> Codec<List<E>> compactListCodec(Codec<E> $$0, Codec<List<E>> $$1) {
      return Codec.either($$1, $$0)
         .xmap($$0x -> $$0x.map($$0xx -> $$0xx, List::of), $$0x -> $$0x.size() == 1 ? Either.right($$0x.getFirst()) : Either.left($$0x));
   }

   private static Codec<Integer> intRangeWithMessage(int $$0, int $$1, Function<Integer, String> $$2) {
      return Codec.INT
         .validate($$3 -> $$3.compareTo($$0) >= 0 && $$3.compareTo($$1) <= 0 ? DataResult.success($$3) : DataResult.error(() -> (String)$$2.apply($$3)));
   }

   public static Codec<Integer> intRange(int $$0, int $$1) {
      return intRangeWithMessage($$0, $$1, $$2 -> "Value must be within range [" + $$0 + ";" + $$1 + "]: " + $$2);
   }

   private static Codec<Float> floatRangeMinInclusiveWithMessage(float $$0, float $$1, Function<Float, String> $$2) {
      return Codec.FLOAT
         .validate($$3 -> $$3.compareTo($$0) >= 0 && $$3.compareTo($$1) <= 0 ? DataResult.success($$3) : DataResult.error(() -> (String)$$2.apply($$3)));
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float $$0, float $$1, Function<Float, String> $$2) {
      return Codec.FLOAT
         .validate($$3 -> $$3.compareTo($$0) > 0 && $$3.compareTo($$1) <= 0 ? DataResult.success($$3) : DataResult.error(() -> (String)$$2.apply($$3)));
   }

   public static Codec<Float> floatRange(float $$0, float $$1) {
      return floatRangeMinInclusiveWithMessage($$0, $$1, $$2 -> "Value must be within range [" + $$0 + ";" + $$1 + "]: " + $$2);
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> $$0) {
      return $$0.validate($$0x -> $$0x.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success($$0x));
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> $$0) {
      return $$0.validate(
         $$0x -> $$0x.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> "List must have contents") : DataResult.success($$0x)
      );
   }

   public static <M extends Map<?, ?>> Codec<M> nonEmptyMap(Codec<M> $$0) {
      return $$0.validate($$0x -> $$0x.isEmpty() ? DataResult.error(() -> "Map must have contents") : DataResult.success($$0x));
   }

   public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> $$0) {
      class ContextRetrievalCodec extends MapCodec<E> {
         @Override
         public <T> RecordBuilder<T> encode(E $$0x, DynamicOps<T> $$1, RecordBuilder<T> $$2) {
            return $$2;
         }

         @Override
         public <T> DataResult<E> decode(DynamicOps<T> $$0x, MapLike<T> $$1) {
            return (DataResult<E>)$$0.apply($$0);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + $$0 + "]";
         }

         @Override
         public <T> Stream<T> keys(DynamicOps<T> $$0x) {
            return Stream.empty();
         }
      }

      return new ContextRetrievalCodec();
   }

   public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> $$0) {
      return $$1 -> {
         Iterator<E> $$2 = $$1.iterator();
         if ($$2.hasNext()) {
            T $$3 = (T)$$0.apply($$2.next());

            while($$2.hasNext()) {
               E $$4 = (E)$$2.next();
               T $$5 = (T)$$0.apply($$4);
               if ($$5 != $$3) {
                  return DataResult.error(() -> "Mixed type list: element " + $$4 + " had type " + $$5 + ", but list is of type " + $$3);
               }
            }
         }

         return DataResult.success($$1, Lifecycle.stable());
      };
   }

   public static <A> Codec<A> catchDecoderException(final Codec<A> $$0) {
      return Codec.of($$0, new Decoder<A>() {
         @Override
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> $$0x, T $$1) {
            try {
               return $$0.decode($$0, $$1);
            } catch (Exception var4) {
               return DataResult.error(() -> "Caught exception decoding " + $$1 + ": " + var4.getMessage());
            }
         }
      });
   }

   public static Codec<TemporalAccessor> temporalCodec(DateTimeFormatter $$0) {
      return Codec.STRING.comapFlatMap($$1 -> {
         try {
            return DataResult.success($$0.parse($$1));
         } catch (Exception var3) {
            return DataResult.error(var3::getMessage);
         }
      }, $$0::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> $$0) {
      return $$0.xmap(toOptionalLong, fromOptionalLong);
   }

   private static MapCodec<GameProfile> gameProfileCodec(Codec<UUID> $$0) {
      return RecordCodecBuilder.mapCodec(
         $$1 -> $$1.group(
                  $$0.fieldOf("id").forGetter(GameProfile::id),
                  PLAYER_NAME.fieldOf("name").forGetter(GameProfile::name),
                  PROPERTY_MAP.optionalFieldOf("properties", PropertyMap.EMPTY).forGetter(GameProfile::properties)
               )
               .apply($$1, GameProfile::new)
      );
   }

   public static <K, V> Codec<Map<K, V>> sizeLimitedMap(Codec<Map<K, V>> $$0, int $$1) {
      return $$0.validate(
         $$1x -> $$1x.size() > $$1 ? DataResult.error(() -> "Map is too long: " + $$1x.size() + ", expected range [0-" + $$1 + "]") : DataResult.success($$1x)
      );
   }

   public static <T> Codec<Object2BooleanMap<T>> object2BooleanMap(Codec<T> $$0) {
      return Codec.unboundedMap($$0, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
   }

   @Deprecated
   public static <K, V> MapCodec<V> dispatchOptionalValue(
      final String $$0,
      final String $$1,
      final Codec<K> $$2,
      final Function<? super V, ? extends K> $$3,
      final Function<? super K, ? extends Codec<? extends V>> $$4
   ) {
      return new MapCodec<V>() {
         @Override
         public <T> Stream<T> keys(DynamicOps<T> $$0x) {
            return Stream.of($$0.createString($$0), $$0.createString($$1));
         }

         @Override
         public <T> DataResult<V> decode(DynamicOps<T> $$0x, MapLike<T> $$1x) {
            T $$2x = $$1.get($$0);
            return $$2x == null ? DataResult.error(() -> "Missing \"" + $$0 + "\" in: " + $$1) : $$2.decode($$0, $$2x).flatMap($$4xx -> {
               T $$5 = (T)Objects.requireNonNullElseGet($$1.get($$1), $$0::emptyMap);
               return ((Codec)$$4.apply($$4xx.getFirst())).decode($$0, $$5).map(Pair::getFirst);
            });
         }

         @Override
         public <T> RecordBuilder<T> encode(V $$0x, DynamicOps<T> $$1x, RecordBuilder<T> $$2x) {
            K $$3x = (K)$$3.apply($$0);
            $$2.add($$0, $$2.encodeStart($$1, $$3x));
            DataResult<T> $$4x = this.encode((Codec)$$4.apply($$3x), $$0, $$1);
            if ($$4x.result().isEmpty() || !Objects.equals($$4x.result().get(), $$1.emptyMap())) {
               $$2.add($$1, $$4x);
            }

            return $$2;
         }

         private <T, V2 extends V> DataResult<T> encode(Codec<V2> $$0x, V $$1x, DynamicOps<T> $$2x) {
            return $$0.encodeStart($$2, $$1);
         }
      };
   }

   public static <A> Codec<Optional<A>> optionalEmptyMap(final Codec<A> $$0) {
      return new Codec<Optional<A>>() {
         @Override
         public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> $$0x, T $$1) {
            return isEmptyMap($$0, $$1) ? DataResult.success(Pair.of(Optional.empty(), $$1)) : $$0.decode($$0, $$1).map($$0xx -> $$0xx.mapFirst(Optional::of));
         }

         private static <T> boolean isEmptyMap(DynamicOps<T> $$0x, T $$1) {
            Optional<MapLike<T>> $$2 = $$0.getMap($$1).result();
            return $$2.isPresent() && ((MapLike)$$2.get()).entries().findAny().isEmpty();
         }

         public <T> DataResult<T> encode(Optional<A> $$0x, DynamicOps<T> $$1, T $$2) {
            return $$0.isEmpty() ? DataResult.success($$1.emptyMap()) : $$0.encode((A)$$0.get(), $$1, $$2);
         }
      };
   }

   @Deprecated
   public static <E extends Enum<E>> Codec<E> legacyEnum(Function<String, E> $$0) {
      return Codec.STRING.comapFlatMap($$1 -> {
         try {
            return DataResult.success((Enum)$$0.apply($$1));
         } catch (IllegalArgumentException var3) {
            return DataResult.error(() -> "No value with id: " + $$1);
         }
      }, Enum::toString);
   }

   public static class LateBoundIdMapper<I, V> {
      private final BiMap<I, V> idToValue = HashBiMap.create();

      public Codec<V> codec(Codec<I> $$0) {
         BiMap<V, I> $$1 = this.idToValue.inverse();
         return ExtraCodecs.idResolverCodec($$0, this.idToValue::get, $$1::get);
      }

      public ExtraCodecs.LateBoundIdMapper<I, V> put(I $$0, V $$1) {
         Objects.requireNonNull($$1, () -> "Value for " + $$0 + " is null");
         this.idToValue.put($$0, $$1);
         return this;
      }

      public Set<V> values() {
         return Collections.unmodifiableSet(this.idToValue.values());
      }
   }

   public static record StrictUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements Codec<Map<K, V>>, BaseMapCodec<K, V> {
      @Override
      public <T> DataResult<Map<K, V>> decode(DynamicOps<T> $$0, MapLike<T> $$1) {
         com.google.common.collect.ImmutableMap.Builder<K, V> $$2 = ImmutableMap.builder();

         for(Pair<T, T> $$3 : $$1.entries().toList()) {
            DataResult<K> $$4 = this.keyCodec().parse($$0, $$3.getFirst());
            DataResult<V> $$5 = this.elementCodec().parse($$0, $$3.getSecond());
            DataResult<Pair<K, V>> $$6 = $$4.apply2stable(Pair::of, $$5);
            Optional<Error<Pair<K, V>>> $$7 = $$6.error();
            if ($$7.isPresent()) {
               String $$8 = ((Error)$$7.get()).message();
               return DataResult.error(() -> $$4.result().isPresent() ? "Map entry '" + $$4.result().get() + "' : " + $$8 : $$8);
            }

            if (!$$6.result().isPresent()) {
               return DataResult.error(() -> "Empty or invalid map contents are not allowed");
            }

            Pair<K, V> $$9 = (Pair)$$6.result().get();
            $$2.put($$9.getFirst(), $$9.getSecond());
         }

         Map<K, V> $$10 = $$2.build();
         return DataResult.success($$10);
      }

      @Override
      public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> $$0, T $$1) {
         return $$0.getMap($$1).setLifecycle(Lifecycle.stable()).flatMap($$1x -> this.decode($$0, $$1x)).map($$1x -> Pair.of($$1x, $$1));
      }

      public <T> DataResult<T> encode(Map<K, V> $$0, DynamicOps<T> $$1, T $$2) {
         return this.encode($$0, $$1, $$1.mapBuilder()).build($$2);
      }

      public String toString() {
         return "StrictUnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + "]";
      }
   }

   public static record TagOrElementLocation(ResourceLocation id, boolean tag) {
      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + this.id : this.id.toString();
      }
   }
}
