package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T> extends Keyable, HolderLookup.RegistryLookup<T>, IdMap<T> {
   @Override
   ResourceKey<? extends Registry<T>> key();

   default Codec<T> byNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap(Holder.Reference::value, $$0 -> this.safeCastToReference(this.wrapAsHolder((T)$$0)));
   }

   default Codec<Holder<T>> holderByNameCodec() {
      return this.referenceHolderWithLifecycle().flatComapMap($$0 -> $$0, this::safeCastToReference);
   }

   private Codec<Holder.Reference<T>> referenceHolderWithLifecycle() {
      Codec<Holder.Reference<T>> $$0 = ResourceLocation.CODEC
         .comapFlatMap(
            $$0x -> (DataResult)this.get($$0x)
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.key() + ": " + $$0x)),
            $$0x -> $$0x.key().location()
         );
      return ExtraCodecs.overrideLifecycle(
         $$0, $$0x -> (Lifecycle)this.registrationInfo($$0x.key()).map(RegistrationInfo::lifecycle).orElse(Lifecycle.experimental())
      );
   }

   private DataResult<Holder.Reference<T>> safeCastToReference(Holder<T> $$0) {
      return $$0 instanceof Holder.Reference $$1 ? DataResult.success($$1) : DataResult.error(() -> "Unregistered holder in " + this.key() + ": " + $$0);
   }

   @Override
   default <U> Stream<U> keys(DynamicOps<U> $$0) {
      return this.keySet().stream().map($$1 -> $$0.createString($$1.toString()));
   }

   @Nullable
   ResourceLocation getKey(T var1);

   Optional<ResourceKey<T>> getResourceKey(T var1);

   @Override
   int getId(@Nullable T var1);

   @Nullable
   T getValue(@Nullable ResourceKey<T> var1);

   @Nullable
   T getValue(@Nullable ResourceLocation var1);

   Optional<RegistrationInfo> registrationInfo(ResourceKey<T> var1);

   default Optional<T> getOptional(@Nullable ResourceLocation $$0) {
      return Optional.ofNullable(this.getValue($$0));
   }

   default Optional<T> getOptional(@Nullable ResourceKey<T> $$0) {
      return Optional.ofNullable(this.getValue($$0));
   }

   Optional<Holder.Reference<T>> getAny();

   default T getValueOrThrow(ResourceKey<T> $$0) {
      T $$1 = this.getValue($$0);
      if ($$1 == null) {
         throw new IllegalStateException("Missing key in " + this.key() + ": " + $$0);
      } else {
         return $$1;
      }
   }

   Set<ResourceLocation> keySet();

   Set<Entry<ResourceKey<T>, T>> entrySet();

   Set<ResourceKey<T>> registryKeySet();

   Optional<Holder.Reference<T>> getRandom(RandomSource var1);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsKey(ResourceLocation var1);

   boolean containsKey(ResourceKey<T> var1);

   static <T> T register(Registry<? super T> $$0, String $$1, T $$2) {
      return register($$0, ResourceLocation.parse($$1), $$2);
   }

   static <V, T extends V> T register(Registry<V> $$0, ResourceLocation $$1, T $$2) {
      return register($$0, ResourceKey.create($$0.key(), $$1), $$2);
   }

   static <V, T extends V> T register(Registry<V> $$0, ResourceKey<V> $$1, T $$2) {
      ((WritableRegistry)$$0).register($$1, (V)$$2, RegistrationInfo.BUILT_IN);
      return $$2;
   }

   static <R, T extends R> Holder.Reference<T> registerForHolder(Registry<R> $$0, ResourceKey<R> $$1, T $$2) {
      return ((WritableRegistry)$$0).register($$1, (R)$$2, RegistrationInfo.BUILT_IN);
   }

   static <R, T extends R> Holder.Reference<T> registerForHolder(Registry<R> $$0, ResourceLocation $$1, T $$2) {
      return registerForHolder($$0, ResourceKey.create($$0.key(), $$1), $$2);
   }

   Registry<T> freeze();

   Holder.Reference<T> createIntrusiveHolder(T var1);

   Optional<Holder.Reference<T>> get(int var1);

   Optional<Holder.Reference<T>> get(ResourceLocation var1);

   Holder<T> wrapAsHolder(T var1);

   default Iterable<Holder<T>> getTagOrEmpty(TagKey<T> $$0) {
      return DataFixUtils.orElse(this.get($$0), List.of());
   }

   Stream<HolderSet.Named<T>> getTags();

   default IdMap<Holder<T>> asHolderIdMap() {
      return new IdMap<Holder<T>>() {
         public int getId(Holder<T> $$0) {
            return Registry.this.getId($$0.value());
         }

         @Nullable
         public Holder<T> byId(int $$0) {
            return (Holder<T>)Registry.this.get($$0).orElse(null);
         }

         @Override
         public int size() {
            return Registry.this.size();
         }

         public Iterator<Holder<T>> iterator() {
            return Registry.this.listElements().map($$0 -> $$0).iterator();
         }
      };
   }

   Registry.PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> var1);

   public interface PendingTags<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      HolderLookup.RegistryLookup<T> lookup();

      void apply();

      int size();
   }
}
