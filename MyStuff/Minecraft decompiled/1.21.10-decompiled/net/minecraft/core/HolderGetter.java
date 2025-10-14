package net.minecraft.core;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

public interface HolderGetter<T> {
   Optional<Holder.Reference<T>> get(ResourceKey<T> var1);

   default Holder.Reference<T> getOrThrow(ResourceKey<T> $$0) {
      return (Holder.Reference<T>)this.get($$0).orElseThrow(() -> new IllegalStateException("Missing element " + $$0));
   }

   Optional<HolderSet.Named<T>> get(TagKey<T> var1);

   default HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
      return (HolderSet.Named<T>)this.get($$0).orElseThrow(() -> new IllegalStateException("Missing tag " + $$0));
   }

   default Optional<Holder<T>> getRandomElementOf(TagKey<T> $$0, RandomSource $$1) {
      return this.get($$0).flatMap($$1x -> $$1x.getRandomElement($$1));
   }

   public interface Provider {
      <T> Optional<? extends HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

      default <T> HolderGetter<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> $$0) {
         return (HolderGetter<T>)this.lookup($$0).orElseThrow(() -> new IllegalStateException("Registry " + $$0.location() + " not found"));
      }

      default <T> Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
         return this.lookup($$0.registryKey()).flatMap($$1 -> $$1.get($$0));
      }

      default <T> Holder.Reference<T> getOrThrow(ResourceKey<T> $$0) {
         return (Holder.Reference<T>)this.lookup($$0.registryKey())
            .flatMap($$1 -> $$1.get($$0))
            .orElseThrow(() -> new IllegalStateException("Missing element " + $$0));
      }
   }
}
