package net.minecraft.core;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {
   Stream<Holder.Reference<T>> listElements();

   default Stream<ResourceKey<T>> listElementIds() {
      return this.listElements().map(Holder.Reference::key);
   }

   Stream<HolderSet.Named<T>> listTags();

   default Stream<TagKey<T>> listTagIds() {
      return this.listTags().map(HolderSet.Named::key);
   }

   public interface Provider extends HolderGetter.Provider {
      Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys();

      default Stream<HolderLookup.RegistryLookup<?>> listRegistries() {
         return this.listRegistryKeys().map(this::lookupOrThrow);
      }

      @Override
      <T> Optional<? extends HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);

      default <T> HolderLookup.RegistryLookup<T> lookupOrThrow(ResourceKey<? extends Registry<? extends T>> $$0) {
         return (HolderLookup.RegistryLookup<T>)this.lookup($$0).orElseThrow(() -> new IllegalStateException("Registry " + $$0.location() + " not found"));
      }

      default <V> RegistryOps<V> createSerializationContext(DynamicOps<V> $$0) {
         return RegistryOps.create($$0, this);
      }

      static HolderLookup.Provider create(Stream<HolderLookup.RegistryLookup<?>> $$0) {
         final Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> $$1 = (Map)$$0.collect(
            Collectors.toUnmodifiableMap(HolderLookup.RegistryLookup::key, $$0x -> $$0x)
         );
         return new HolderLookup.Provider() {
            @Override
            public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
               return $$1.keySet().stream();
            }

            @Override
            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
               return Optional.ofNullable((HolderLookup.RegistryLookup)$$1.get($$0));
            }
         };
      }

      default Lifecycle allRegistriesLifecycle() {
         return (Lifecycle)this.listRegistries().map(HolderLookup.RegistryLookup::registryLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
      }
   }

   public interface RegistryLookup<T> extends HolderLookup<T>, HolderOwner<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      Lifecycle registryLifecycle();

      default HolderLookup.RegistryLookup<T> filterFeatures(FeatureFlagSet $$0) {
         return FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements($$1 -> ((FeatureElement)$$1).isEnabled($$0)) : this;
      }

      default HolderLookup.RegistryLookup<T> filterElements(final Predicate<T> $$0) {
         return new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            public HolderLookup.RegistryLookup<T> parent() {
               return RegistryLookup.this;
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0x) {
               return this.parent().get($$0).filter($$1 -> $$0.test($$1.value()));
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
               return this.parent().listElements().filter($$1 -> $$0.test($$1.value()));
            }
         };
      }

      public interface Delegate<T> extends HolderLookup.RegistryLookup<T> {
         HolderLookup.RegistryLookup<T> parent();

         @Override
         default ResourceKey<? extends Registry<? extends T>> key() {
            return this.parent().key();
         }

         @Override
         default Lifecycle registryLifecycle() {
            return this.parent().registryLifecycle();
         }

         @Override
         default Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
            return this.parent().get($$0);
         }

         @Override
         default Stream<Holder.Reference<T>> listElements() {
            return this.parent().listElements();
         }

         @Override
         default Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
            return this.parent().get($$0);
         }

         @Override
         default Stream<HolderSet.Named<T>> listTags() {
            return this.parent().listTags();
         }
      }
   }
}
