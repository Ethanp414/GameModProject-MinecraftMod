package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   RegistryAccess.Frozen EMPTY = new RegistryAccess.ImmutableRegistryAccess(Map.of()).freeze();

   @Override
   <E> Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> var1);

   default <E> Registry<E> lookupOrThrow(ResourceKey<? extends Registry<? extends E>> $$0) {
      return (Registry<E>)this.lookup($$0).orElseThrow(() -> new IllegalStateException("Missing registry: " + $$0));
   }

   Stream<RegistryAccess.RegistryEntry<?>> registries();

   @Override
   default Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
      return this.registries().map($$0 -> $$0.key);
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> $$0) {
      return new RegistryAccess.Frozen() {
         @Override
         public <T> Optional<Registry<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0x) {
            Registry<Registry<T>> $$1 = $$0;
            return $$1.getOptional($$0);
         }

         @Override
         public Stream<RegistryAccess.RegistryEntry<?>> registries() {
            return $$0.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }

         @Override
         public RegistryAccess.Frozen freeze() {
            return this;
         }
      };
   }

   default RegistryAccess.Frozen freeze() {
      class FrozenAccess extends RegistryAccess.ImmutableRegistryAccess implements RegistryAccess.Frozen {
         protected FrozenAccess(final Stream<RegistryAccess.RegistryEntry<?>> param1, final Stream param2) {
            super($$1);
         }
      }

      return new FrozenAccess(this, this.registries().map(RegistryAccess.RegistryEntry::freeze));
   }

   public interface Frozen extends RegistryAccess {
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(List<? extends Registry<?>> $$0) {
         this.registries = (Map)$$0.stream().collect(Collectors.toUnmodifiableMap(Registry::key, $$0x -> $$0x));
      }

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> $$0) {
         this.registries = Map.copyOf($$0);
      }

      public ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> $$0) {
         this.registries = (Map)$$0.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      @Override
      public <E> Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> $$0) {
         return Optional.ofNullable((Registry)this.registries.get($$0)).map($$0x -> $$0x);
      }

      @Override
      public Stream<RegistryAccess.RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
      final ResourceKey<? extends Registry<T>> key;

      private static <T, R extends Registry<? extends T>> RegistryAccess.RegistryEntry<T> fromMapEntry(
         Entry<? extends ResourceKey<? extends Registry<?>>, R> $$0
      ) {
         return fromUntyped((ResourceKey<? extends Registry<?>>)$$0.getKey(), (Registry<?>)$$0.getValue());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> $$0, Registry<?> $$1) {
         return new RegistryAccess.RegistryEntry<>($$0, $$1);
      }

      private RegistryAccess.RegistryEntry<T> freeze() {
         return new RegistryAccess.RegistryEntry<>(this.key, this.value.freeze());
      }
   }
}
