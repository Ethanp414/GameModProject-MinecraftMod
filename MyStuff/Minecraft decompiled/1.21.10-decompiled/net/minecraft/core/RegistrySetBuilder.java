package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;

public class RegistrySetBuilder {
   private final List<RegistrySetBuilder.RegistryStub<?>> entries = new ArrayList();

   static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> $$0) {
      return new RegistrySetBuilder.EmptyTagLookup<T>($$0) {
         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0x) {
            return $$0.get($$0);
         }
      };
   }

   static <T> HolderLookup.RegistryLookup<T> lookupFromMap(
      final ResourceKey<? extends Registry<? extends T>> $$0, final Lifecycle $$1, HolderOwner<T> $$2, final Map<ResourceKey<T>, Holder.Reference<T>> $$3
   ) {
      return new RegistrySetBuilder.EmptyTagRegistryLookup<T>($$2) {
         @Override
         public ResourceKey<? extends Registry<? extends T>> key() {
            return $$0;
         }

         @Override
         public Lifecycle registryLifecycle() {
            return $$1;
         }

         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0x) {
            return Optional.ofNullable((Holder.Reference)$$3.get($$0));
         }

         @Override
         public Stream<Holder.Reference<T>> listElements() {
            return $$3.values().stream();
         }
      };
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1, RegistrySetBuilder.RegistryBootstrap<T> $$2) {
      this.entries.add(new RegistrySetBuilder.RegistryStub<T>($$0, $$1, $$2));
      return this;
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> $$0, RegistrySetBuilder.RegistryBootstrap<T> $$1) {
      return this.add($$0, Lifecycle.stable(), $$1);
   }

   private RegistrySetBuilder.BuildState createState(RegistryAccess $$0) {
      RegistrySetBuilder.BuildState $$1 = RegistrySetBuilder.BuildState.create($$0, this.entries.stream().map(RegistrySetBuilder.RegistryStub::key));
      this.entries.forEach($$1x -> $$1x.apply($$1));
      return $$1;
   }

   private static HolderLookup.Provider buildProviderWithContext(
      RegistrySetBuilder.UniversalOwner $$0, RegistryAccess $$1, Stream<HolderLookup.RegistryLookup<?>> $$2
   ) {
      record Entry<T>(HolderLookup.RegistryLookup<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
         public static <T> Entry<T> createForContextRegistry(HolderLookup.RegistryLookup<T> $$0) {
            return new Entry<>(new RegistrySetBuilder.EmptyTagLookupWrapper<>($$0, $$0), RegistryOps.RegistryInfo.fromRegistryLookup($$0));
         }

         public static <T> Entry<T> createForNewRegistry(RegistrySetBuilder.UniversalOwner $$0, HolderLookup.RegistryLookup<T> $$1) {
            return new Entry<>(
               new RegistrySetBuilder.EmptyTagLookupWrapper<>($$0.cast(), $$1), new RegistryOps.RegistryInfo<>($$0.cast(), $$1, $$1.registryLifecycle())
            );
         }
      }

      final Map<ResourceKey<? extends Registry<?>>, Entry<?>> $$3 = new HashMap();
      $$1.registries().forEach($$1x -> $$3.put($$1x.key(), Entry.createForContextRegistry($$1x.value())));
      $$2.forEach($$2x -> $$3.put($$2x.key(), Entry.createForNewRegistry($$0, $$2x)));
      return new HolderLookup.Provider() {
         @Override
         public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return $$3.keySet().stream();
         }

         <T> Optional<Entry<T>> getEntry(ResourceKey<? extends Registry<? extends T>> $$0) {
            return Optional.ofNullable((Entry)$$3.get($$0));
         }

         @Override
         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
            return this.getEntry($$0).map(Entry::lookup);
         }

         @Override
         public <V> RegistryOps<V> createSerializationContext(DynamicOps<V> $$0) {
            return RegistryOps.create($$0, new RegistryOps.RegistryInfoLookup() {
               @Override
               public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
                  return getEntry($$0).map(Entry::opsInfo);
               }
            });
         }
      };
   }

   public HolderLookup.Provider build(RegistryAccess $$0) {
      RegistrySetBuilder.BuildState $$1 = this.createState($$0);
      Stream<HolderLookup.RegistryLookup<?>> $$2 = this.entries.stream().map($$1x -> $$1x.collectRegisteredValues($$1).buildAsLookup($$1.owner));
      HolderLookup.Provider $$3 = buildProviderWithContext($$1.owner, $$0, $$2);
      $$1.reportNotCollectedHolders();
      $$1.reportUnclaimedRegisteredValues();
      $$1.throwOnError();
      return $$3;
   }

   private HolderLookup.Provider createLazyFullPatchedRegistries(
      RegistryAccess $$0,
      HolderLookup.Provider $$1,
      Cloner.Factory $$2,
      Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> $$3,
      HolderLookup.Provider $$4
   ) {
      RegistrySetBuilder.UniversalOwner $$5 = new RegistrySetBuilder.UniversalOwner();
      MutableObject<HolderLookup.Provider> $$6 = new MutableObject<>();
      List<HolderLookup.RegistryLookup<?>> $$7 = (List)$$3.keySet()
         .stream()
         .map($$5x -> this.createLazyFullPatchedRegistries($$5, $$2, $$5x, $$4, $$1, $$6))
         .collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider $$8 = buildProviderWithContext($$5, $$0, $$7.stream());
      $$6.setValue($$8);
      return $$8;
   }

   private <T> HolderLookup.RegistryLookup<T> createLazyFullPatchedRegistries(
      HolderOwner<T> $$0,
      Cloner.Factory $$1,
      ResourceKey<? extends Registry<? extends T>> $$2,
      HolderLookup.Provider $$3,
      HolderLookup.Provider $$4,
      MutableObject<HolderLookup.Provider> $$5
   ) {
      Cloner<T> $$6 = $$1.cloner($$2);
      if ($$6 == null) {
         throw new NullPointerException("No cloner for " + $$2.location());
      } else {
         Map<ResourceKey<T>, Holder.Reference<T>> $$7 = new HashMap();
         HolderLookup.RegistryLookup<T> $$8 = $$3.lookupOrThrow($$2);
         $$8.listElements().forEach($$5x -> {
            ResourceKey<T> $$6xx = $$5x.key();
            RegistrySetBuilder.LazyHolder<T> $$7xx = new RegistrySetBuilder.LazyHolder<>($$0, $$6xx);
            $$7xx.supplier = () -> $$6.clone((T)$$5x.value(), $$3, $$5.getValue());
            $$7.put($$6xx, $$7xx);
         });
         HolderLookup.RegistryLookup<T> $$9 = $$4.lookupOrThrow($$2);
         $$9.listElements().forEach($$5x -> {
            ResourceKey<T> $$6xx = $$5x.key();
            $$7.computeIfAbsent($$6xx, $$6xx -> {
               RegistrySetBuilder.LazyHolder<T> $$7xx = new RegistrySetBuilder.LazyHolder<>($$0, $$6x);
               $$7xx.supplier = () -> $$6.clone((T)$$5x.value(), $$4, $$5.getValue());
               return $$7xx;
            });
         });
         Lifecycle $$10 = $$8.registryLifecycle().add($$9.registryLifecycle());
         return lookupFromMap($$2, $$10, $$0, $$7);
      }
   }

   public RegistrySetBuilder.PatchedRegistries buildPatch(RegistryAccess $$0, HolderLookup.Provider $$1, Cloner.Factory $$2) {
      RegistrySetBuilder.BuildState $$3 = this.createState($$0);
      Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> $$4 = new HashMap();
      this.entries.stream().map($$1x -> $$1x.collectRegisteredValues($$3)).forEach($$1x -> $$4.put($$1x.key, $$1x));
      Set<ResourceKey<? extends Registry<?>>> $$5 = (Set)$$0.listRegistryKeys().collect(Collectors.toUnmodifiableSet());
      $$1.listRegistryKeys()
         .filter($$1x -> !$$5.contains($$1x))
         .forEach($$1x -> $$4.putIfAbsent($$1x, new RegistrySetBuilder.RegistryContents($$1x, Lifecycle.stable(), Map.of())));
      Stream<HolderLookup.RegistryLookup<?>> $$6 = $$4.values().stream().map($$1x -> $$1x.buildAsLookup($$3.owner));
      HolderLookup.Provider $$7 = buildProviderWithContext($$3.owner, $$0, $$6);
      $$3.reportUnclaimedRegisteredValues();
      $$3.throwOnError();
      HolderLookup.Provider $$8 = this.createLazyFullPatchedRegistries($$0, $$1, $$2, $$4, $$7);
      return new RegistrySetBuilder.PatchedRegistries($$8, $$7);
   }

   static record BuildState(
      RegistrySetBuilder.UniversalOwner owner,
      RegistrySetBuilder.UniversalLookup lookup,
      Map<ResourceLocation, HolderGetter<?>> registries,
      Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues,
      List<RuntimeException> errors
   ) {
      final RegistrySetBuilder.UniversalOwner owner;
      final RegistrySetBuilder.UniversalLookup lookup;
      final Map<ResourceLocation, HolderGetter<?>> registries;
      final Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues;
      final List<RuntimeException> errors;

      public static RegistrySetBuilder.BuildState create(RegistryAccess $$0, Stream<ResourceKey<? extends Registry<?>>> $$1) {
         RegistrySetBuilder.UniversalOwner $$2 = new RegistrySetBuilder.UniversalOwner();
         List<RuntimeException> $$3 = new ArrayList();
         RegistrySetBuilder.UniversalLookup $$4 = new RegistrySetBuilder.UniversalLookup($$2);
         Builder<ResourceLocation, HolderGetter<?>> $$5 = ImmutableMap.builder();
         $$0.registries().forEach($$1x -> $$5.put($$1x.key().location(), RegistrySetBuilder.wrapContextLookup($$1x.value())));
         $$1.forEach($$2x -> $$5.put($$2x.location(), $$4));
         return new RegistrySetBuilder.BuildState($$2, $$4, $$5.build(), new HashMap(), $$3);
      }

      public <T> BootstrapContext<T> bootstrapContext() {
         return new BootstrapContext<T>() {
            @Override
            public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1, Lifecycle $$2) {
               RegistrySetBuilder.RegisteredValue<?> $$3 = (RegistrySetBuilder.RegisteredValue)BuildState.this.registeredValues
                  .put($$0, new RegistrySetBuilder.RegisteredValue($$1, $$2));
               if ($$3 != null) {
                  BuildState.this.errors.add(new IllegalStateException("Duplicate registration for " + $$0 + ", new=" + $$1 + ", old=" + $$3.value));
               }

               return BuildState.this.lookup.getOrCreate($$0);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> $$0) {
               return (HolderGetter<S>)BuildState.this.registries.getOrDefault($$0.location(), BuildState.this.lookup);
            }
         };
      }

      public void reportUnclaimedRegisteredValues() {
         this.registeredValues.forEach(($$0, $$1) -> this.errors.add(new IllegalStateException("Orpaned value " + $$1.value + " for key " + $$0)));
      }

      public void reportNotCollectedHolders() {
         for(ResourceKey<Object> $$0 : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + $$0));
         }
      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException $$0 = new IllegalStateException("Errors during registry creation");

            for(RuntimeException $$1 : this.errors) {
               $$0.addSuppressed($$1);
            }

            throw $$0;
         }
      }
   }

   abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
      protected final HolderOwner<T> owner;

      protected EmptyTagLookup(HolderOwner<T> $$0) {
         this.owner = $$0;
      }

      @Override
      public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
         return Optional.of(HolderSet.emptyNamed(this.owner, $$0));
      }
   }

   static class EmptyTagLookupWrapper<T> extends RegistrySetBuilder.EmptyTagRegistryLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {
      private final HolderLookup.RegistryLookup<T> parent;

      EmptyTagLookupWrapper(HolderOwner<T> $$0, HolderLookup.RegistryLookup<T> $$1) {
         super($$0);
         this.parent = $$1;
      }

      @Override
      public HolderLookup.RegistryLookup<T> parent() {
         return this.parent;
      }
   }

   abstract static class EmptyTagRegistryLookup<T> extends RegistrySetBuilder.EmptyTagLookup<T> implements HolderLookup.RegistryLookup<T> {
      protected EmptyTagRegistryLookup(HolderOwner<T> $$0) {
         super($$0);
      }

      @Override
      public Stream<HolderSet.Named<T>> listTags() {
         throw new UnsupportedOperationException("Tags are not available in datagen");
      }
   }

   static class LazyHolder<T> extends Holder.Reference<T> {
      @Nullable
      Supplier<T> supplier;

      protected LazyHolder(HolderOwner<T> $$0, @Nullable ResourceKey<T> $$1) {
         super(Holder.Reference.Type.STAND_ALONE, $$0, $$1, (T)null);
      }

      @Override
      protected void bindValue(T $$0) {
         super.bindValue($$0);
         this.supplier = null;
      }

      @Override
      public T value() {
         if (this.supplier != null) {
            this.bindValue((T)this.supplier.get());
         }

         return super.value();
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
   }

   static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
      final T value;
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstrapContext<T> var1);
   }

   static record RegistryContents<T>(
      ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values
   ) {
      final ResourceKey<? extends Registry<? extends T>> key;

      public HolderLookup.RegistryLookup<T> buildAsLookup(RegistrySetBuilder.UniversalOwner $$0) {
         Map<ResourceKey<T>, Holder.Reference<T>> $$1 = (Map)this.values
            .entrySet()
            .stream()
            .collect(
               Collectors.toUnmodifiableMap(
                  java.util.Map.Entry::getKey,
                  $$1x -> {
                     RegistrySetBuilder.ValueAndHolder<T> $$2 = (RegistrySetBuilder.ValueAndHolder)$$1x.getValue();
                     Holder.Reference<T> $$3 = (Holder.Reference)$$2.holder()
                        .orElseGet(() -> Holder.Reference.createStandAlone($$0.cast(), (ResourceKey<T>)$$1x.getKey()));
                     $$3.bindValue($$2.value().value());
                     return $$3;
                  }
               )
            );
         return RegistrySetBuilder.lookupFromMap(this.key, this.lifecycle, $$0.cast(), $$1);
      }
   }

   static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
      void apply(RegistrySetBuilder.BuildState $$0) {
         this.bootstrap.run($$0.bootstrapContext());
      }

      public RegistrySetBuilder.RegistryContents<T> collectRegisteredValues(RegistrySetBuilder.BuildState $$0) {
         Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> $$1 = new HashMap();
         Iterator<java.util.Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>>> $$2 = $$0.registeredValues.entrySet().iterator();

         while($$2.hasNext()) {
            java.util.Map.Entry<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> $$3 = (java.util.Map.Entry)$$2.next();
            ResourceKey<?> $$4 = (ResourceKey)$$3.getKey();
            if ($$4.isFor(this.key)) {
               RegistrySetBuilder.RegisteredValue<T> $$6 = (RegistrySetBuilder.RegisteredValue)$$3.getValue();
               Holder.Reference<T> $$7 = (Holder.Reference)$$0.lookup.holders.remove($$4);
               $$1.put($$4, new RegistrySetBuilder.ValueAndHolder<T>($$6, Optional.ofNullable($$7)));
               $$2.remove();
            }
         }

         return new RegistrySetBuilder.RegistryContents<>(this.key, this.lifecycle, $$1);
      }
   }

   static class UniversalLookup extends RegistrySetBuilder.EmptyTagLookup<Object> {
      final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();

      public UniversalLookup(HolderOwner<Object> $$0) {
         super($$0);
      }

      @Override
      public Optional<Holder.Reference<Object>> get(ResourceKey<Object> $$0) {
         return Optional.of(this.getOrCreate($$0));
      }

      <T> Holder.Reference<T> getOrCreate(ResourceKey<T> $$0) {
         return (Holder.Reference<T>)this.holders.computeIfAbsent($$0, $$0x -> Holder.Reference.createStandAlone(this.owner, $$0x));
      }
   }

   static class UniversalOwner implements HolderOwner<Object> {
      public <T> HolderOwner<T> cast() {
         return this;
      }
   }

   static record ValueAndHolder<T>(RegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
   }
}
