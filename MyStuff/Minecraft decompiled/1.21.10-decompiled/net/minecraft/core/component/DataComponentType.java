package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentType<T> {
   Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(() -> BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec());
   StreamCodec<RegistryFriendlyByteBuf, DataComponentType<?>> STREAM_CODEC = StreamCodec.recursive(
      $$0 -> ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)
   );
   Codec<DataComponentType<?>> PERSISTENT_CODEC = CODEC.validate(
      $$0 -> $$0.isTransient()
            ? DataResult.error(() -> "Encountered transient component " + BuiltInRegistries.DATA_COMPONENT_TYPE.getKey($$0))
            : DataResult.success($$0)
   );
   Codec<Map<DataComponentType<?>, Object>> VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, DataComponentType::codecOrThrow);

   static <T> DataComponentType.Builder<T> builder() {
      return new DataComponentType.Builder<>();
   }

   @Nullable
   Codec<T> codec();

   default Codec<T> codecOrThrow() {
      Codec<T> $$0 = this.codec();
      if ($$0 == null) {
         throw new IllegalStateException(this + " is not a persistent component");
      } else {
         return $$0;
      }
   }

   default boolean isTransient() {
      return this.codec() == null;
   }

   StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

   public static class Builder<T> {
      @Nullable
      private Codec<T> codec;
      @Nullable
      private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
      private boolean cacheEncoding;

      public DataComponentType.Builder<T> persistent(Codec<T> $$0) {
         this.codec = $$0;
         return this;
      }

      public DataComponentType.Builder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> $$0) {
         this.streamCodec = $$0;
         return this;
      }

      public DataComponentType.Builder<T> cacheEncoding() {
         this.cacheEncoding = true;
         return this;
      }

      public DataComponentType<T> build() {
         StreamCodec<? super RegistryFriendlyByteBuf, T> $$0 = (StreamCodec)Objects.requireNonNullElseGet(
            this.streamCodec, () -> ByteBufCodecs.fromCodecWithRegistries((Codec<T>)Objects.requireNonNull(this.codec, "Missing Codec for component"))
         );
         Codec<T> $$1 = this.cacheEncoding && this.codec != null ? DataComponents.ENCODER_CACHE.wrap(this.codec) : this.codec;
         return new DataComponentType.Builder.SimpleType<>($$1, $$0);
      }

      static class SimpleType<T> implements DataComponentType<T> {
         @Nullable
         private final Codec<T> codec;
         private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

         SimpleType(@Nullable Codec<T> $$0, StreamCodec<? super RegistryFriendlyByteBuf, T> $$1) {
            this.codec = $$0;
            this.streamCodec = $$1;
         }

         @Nullable
         @Override
         public Codec<T> codec() {
            return this.codec;
         }

         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
         }

         public String toString() {
            return Util.getRegisteredName(BuiltInRegistries.DATA_COMPONENT_TYPE, this);
         }
      }
   }
}
