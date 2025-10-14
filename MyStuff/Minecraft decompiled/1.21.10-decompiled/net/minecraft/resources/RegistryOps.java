package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
   private final RegistryOps.RegistryInfoLookup lookupProvider;

   public static <T> RegistryOps<T> create(DynamicOps<T> $$0, HolderLookup.Provider $$1) {
      return create($$0, new RegistryOps.HolderLookupAdapter($$1));
   }

   public static <T> RegistryOps<T> create(DynamicOps<T> $$0, RegistryOps.RegistryInfoLookup $$1) {
      return new RegistryOps<>($$0, $$1);
   }

   public static <T> Dynamic<T> injectRegistryContext(Dynamic<T> $$0, HolderLookup.Provider $$1) {
      return new Dynamic<>($$1.createSerializationContext($$0.getOps()), $$0.getValue());
   }

   private RegistryOps(DynamicOps<T> $$0, RegistryOps.RegistryInfoLookup $$1) {
      super($$0);
      this.lookupProvider = $$1;
   }

   public <U> RegistryOps<U> withParent(DynamicOps<U> $$0) {
      return $$0 == this.delegate ? this : new RegistryOps<>($$0, this.lookupProvider);
   }

   public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> $$0) {
      return this.lookupProvider.lookup($$0).map(RegistryOps.RegistryInfo::owner);
   }

   public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> $$0) {
      return this.lookupProvider.lookup($$0).map(RegistryOps.RegistryInfo::getter);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if ($$0 != null && this.getClass() == $$0.getClass()) {
         RegistryOps<?> $$1 = (RegistryOps)$$0;
         return this.delegate.equals($$1.delegate) && this.lookupProvider.equals($$1.lookupProvider);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.lookupProvider.hashCode();
   }

   public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> $$0) {
      return ExtraCodecs.<HolderGetter<E>>retrieveContext(
            $$1 -> $$1 instanceof RegistryOps $$2
                  ? (DataResult)$$2.lookupProvider
                     .lookup($$0)
                     .map($$0xx -> DataResult.success($$0xx.getter(), $$0xx.elementsLifecycle()))
                     .orElseGet(() -> DataResult.error(() -> "Unknown registry: " + $$0))
                  : DataResult.error(() -> "Not a registry ops")
         )
         .forGetter($$0x -> null);
   }

   public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> $$0) {
      ResourceKey<? extends Registry<E>> $$1 = ResourceKey.createRegistryKey($$0.registry());
      return ExtraCodecs.<Holder.Reference<E>>retrieveContext(
            $$2 -> $$2 instanceof RegistryOps $$3
                  ? (DataResult)$$3.lookupProvider
                     .lookup($$1)
                     .flatMap($$1xx -> $$1xx.getter().get($$0))
                     .map(DataResult::success)
                     .orElseGet(() -> DataResult.error(() -> "Can't find value: " + $$0))
                  : DataResult.error(() -> "Not a registry ops")
         )
         .forGetter($$0x -> null);
   }

   static final class HolderLookupAdapter implements RegistryOps.RegistryInfoLookup {
      private final HolderLookup.Provider lookupProvider;
      private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryOps.RegistryInfo<?>>> lookups = new ConcurrentHashMap();

      public HolderLookupAdapter(HolderLookup.Provider $$0) {
         this.lookupProvider = $$0;
      }

      @Override
      public <E> Optional<RegistryOps.RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> $$0) {
         return (Optional<RegistryOps.RegistryInfo<E>>)this.lookups.computeIfAbsent($$0, this::createLookup);
      }

      private Optional<RegistryOps.RegistryInfo<Object>> createLookup(ResourceKey<? extends Registry<?>> $$0) {
         return this.lookupProvider.lookup($$0).map(RegistryOps.RegistryInfo::fromRegistryLookup);
      }

      public boolean equals(Object $$0) {
         if (this == $$0) {
            return true;
         } else {
            if ($$0 instanceof RegistryOps.HolderLookupAdapter $$1 && this.lookupProvider.equals($$1.lookupProvider)) {
               return true;
            }

            return false;
         }
      }

      public int hashCode() {
         return this.lookupProvider.hashCode();
      }
   }

   public static record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
      public static <T> RegistryOps.RegistryInfo<T> fromRegistryLookup(HolderLookup.RegistryLookup<T> $$0) {
         return new RegistryOps.RegistryInfo<>($$0, $$0, $$0.registryLifecycle());
      }
   }

   public interface RegistryInfoLookup {
      <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);
   }
}
