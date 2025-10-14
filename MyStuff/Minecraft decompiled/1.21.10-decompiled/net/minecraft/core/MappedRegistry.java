package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.RandomSource;

public class MappedRegistry<T> implements WritableRegistry<T> {
   private final ResourceKey<? extends Registry<T>> key;
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList<>(256);
   private final Reference2IntMap<T> toId = Util.make(new Reference2IntOpenHashMap<>(), $$0x -> $$0x.defaultReturnValue(-1));
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap();
   private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos = new IdentityHashMap();
   private Lifecycle registryLifecycle;
   private final Map<TagKey<T>, HolderSet.Named<T>> frozenTags = new IdentityHashMap();
   MappedRegistry.TagSet<T> allTags = MappedRegistry.TagSet.unbound();
   private boolean frozen;
   @Nullable
   private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

   @Override
   public Stream<HolderSet.Named<T>> listTags() {
      return this.getTags();
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1) {
      this($$0, $$1, false);
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1, boolean $$2) {
      this.key = $$0;
      this.registryLifecycle = $$1;
      if ($$2) {
         this.unregisteredIntrusiveHolders = new IdentityHashMap();
      }
   }

   @Override
   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   public String toString() {
      return "Registry[" + this.key + " (" + this.registryLifecycle + ")]";
   }

   private void validateWrite() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void validateWrite(ResourceKey<T> $$0) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + $$0 + ")");
      }
   }

   @Override
   public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1, RegistrationInfo $$2) {
      this.validateWrite($$0);
      Objects.requireNonNull($$0);
      Objects.requireNonNull($$1);
      if (this.byLocation.containsKey($$0.location())) {
         throw (IllegalStateException)Util.pauseInIde((T)(new IllegalStateException("Adding duplicate key '" + $$0 + "' to registry")));
      } else if (this.byValue.containsKey($$1)) {
         throw (IllegalStateException)Util.pauseInIde((T)(new IllegalStateException("Adding duplicate value '" + $$1 + "' to registry")));
      } else {
         Holder.Reference<T> $$3;
         if (this.unregisteredIntrusiveHolders != null) {
            $$3 = (Holder.Reference)this.unregisteredIntrusiveHolders.remove($$1);
            if ($$3 == null) {
               throw new AssertionError("Missing intrusive holder for " + $$0 + ":" + $$1);
            }

            $$3.bindKey($$0);
         } else {
            $$3 = (Holder.Reference)this.byKey.computeIfAbsent($$0, $$0x -> Holder.Reference.createStandAlone(this, $$0x));
         }

         this.byKey.put($$0, $$3);
         this.byLocation.put($$0.location(), $$3);
         this.byValue.put($$1, $$3);
         int $$5 = this.byId.size();
         this.byId.add($$3);
         this.toId.put($$1, $$5);
         this.registrationInfos.put($$0, $$2);
         this.registryLifecycle = this.registryLifecycle.add($$2.lifecycle());
         return $$3;
      }
   }

   @Nullable
   @Override
   public ResourceLocation getKey(T $$0) {
      Holder.Reference<T> $$1 = (Holder.Reference)this.byValue.get($$0);
      return $$1 != null ? $$1.key().location() : null;
   }

   @Override
   public Optional<ResourceKey<T>> getResourceKey(T $$0) {
      return Optional.ofNullable((Holder.Reference)this.byValue.get($$0)).map(Holder.Reference::key);
   }

   @Override
   public int getId(@Nullable T $$0) {
      return this.toId.getInt($$0);
   }

   @Nullable
   @Override
   public T getValue(@Nullable ResourceKey<T> $$0) {
      return getValueFromNullable((Holder.Reference<T>)this.byKey.get($$0));
   }

   @Nullable
   @Override
   public T byId(int $$0) {
      return (T)($$0 >= 0 && $$0 < this.byId.size() ? ((Holder.Reference)this.byId.get($$0)).value() : null);
   }

   @Override
   public Optional<Holder.Reference<T>> get(int $$0) {
      return $$0 >= 0 && $$0 < this.byId.size() ? Optional.ofNullable((Holder.Reference)this.byId.get($$0)) : Optional.empty();
   }

   @Override
   public Optional<Holder.Reference<T>> get(ResourceLocation $$0) {
      return Optional.ofNullable((Holder.Reference)this.byLocation.get($$0));
   }

   @Override
   public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
      return Optional.ofNullable((Holder.Reference)this.byKey.get($$0));
   }

   @Override
   public Optional<Holder.Reference<T>> getAny() {
      return this.byId.isEmpty() ? Optional.empty() : Optional.of((Holder.Reference)this.byId.getFirst());
   }

   @Override
   public Holder<T> wrapAsHolder(T $$0) {
      Holder.Reference<T> $$1 = (Holder.Reference)this.byValue.get($$0);
      return (Holder<T>)($$1 != null ? $$1 : Holder.direct($$0));
   }

   Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> $$0) {
      return (Holder.Reference<T>)this.byKey.computeIfAbsent($$0, $$0x -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite($$0x);
            return Holder.Reference.createStandAlone(this, $$0x);
         }
      });
   }

   @Override
   public int size() {
      return this.byKey.size();
   }

   @Override
   public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> $$0) {
      return Optional.ofNullable((RegistrationInfo)this.registrationInfos.get($$0));
   }

   @Override
   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.byId.iterator(), Holder::value);
   }

   @Nullable
   @Override
   public T getValue(@Nullable ResourceLocation $$0) {
      Holder.Reference<T> $$1 = (Holder.Reference)this.byLocation.get($$0);
      return getValueFromNullable($$1);
   }

   @Nullable
   private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> $$0) {
      return $$0 != null ? $$0.value() : null;
   }

   @Override
   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   @Override
   public Set<ResourceKey<T>> registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   @Override
   public Set<Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Util.mapValuesLazy(this.byKey, Holder::value).entrySet());
   }

   @Override
   public Stream<Holder.Reference<T>> listElements() {
      return this.byId.stream();
   }

   @Override
   public Stream<HolderSet.Named<T>> getTags() {
      return this.allTags.getTags();
   }

   HolderSet.Named<T> getOrCreateTagForRegistration(TagKey<T> $$0) {
      return (HolderSet.Named<T>)this.frozenTags.computeIfAbsent($$0, this::createTag);
   }

   private HolderSet.Named<T> createTag(TagKey<T> $$0) {
      return new HolderSet.Named<>(this, $$0);
   }

   @Override
   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   @Override
   public Optional<Holder.Reference<T>> getRandom(RandomSource $$0) {
      return Util.getRandomSafe(this.byId, $$0);
   }

   @Override
   public boolean containsKey(ResourceLocation $$0) {
      return this.byLocation.containsKey($$0);
   }

   @Override
   public boolean containsKey(ResourceKey<T> $$0) {
      return this.byKey.containsKey($$0);
   }

   @Override
   public Registry<T> freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.byValue.forEach(($$0x, $$1x) -> $$1x.bindValue((T)$$0x));
         List<ResourceLocation> $$0 = this.byKey
            .entrySet()
            .stream()
            .filter($$0x -> !((Holder.Reference)$$0x.getValue()).isBound())
            .map($$0x -> ((ResourceKey)$$0x.getKey()).location())
            .sorted()
            .toList();
         if (!$$0.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + $$0);
         } else {
            if (this.unregisteredIntrusiveHolders != null) {
               if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
               }

               this.unregisteredIntrusiveHolders = null;
            }

            if (this.allTags.isBound()) {
               throw new IllegalStateException("Tags already present before freezing");
            } else {
               List<ResourceLocation> $$1 = this.frozenTags
                  .entrySet()
                  .stream()
                  .filter($$0x -> !((HolderSet.Named)$$0x.getValue()).isBound())
                  .map($$0x -> ((TagKey)$$0x.getKey()).location())
                  .sorted()
                  .toList();
               if (!$$1.isEmpty()) {
                  throw new IllegalStateException("Unbound tags in registry " + this.key() + ": " + $$1);
               } else {
                  this.allTags = MappedRegistry.TagSet.fromMap(this.frozenTags);
                  this.refreshTagsInHolders();
                  return this;
               }
            }
         }
      }
   }

   @Override
   public Holder.Reference<T> createIntrusiveHolder(T $$0) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return (Holder.Reference<T>)this.unregisteredIntrusiveHolders.computeIfAbsent($$0, $$0x -> Holder.Reference.createIntrusive(this, (T)$$0x));
      }
   }

   @Override
   public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
      return this.allTags.get($$0);
   }

   private Holder.Reference<T> validateAndUnwrapTagElement(TagKey<T> $$0, Holder<T> $$1) {
      if (!$$1.canSerializeIn(this)) {
         throw new IllegalStateException("Can't create named set " + $$0 + " containing value " + $$1 + " from outside registry " + this);
      } else if ($$1 instanceof Holder.Reference) {
         return (Holder.Reference<T>)$$1;
      } else {
         throw new IllegalStateException("Found direct holder " + $$1 + " value in tag " + $$0);
      }
   }

   @Override
   public void bindTag(TagKey<T> $$0, List<Holder<T>> $$1) {
      this.validateWrite();
      this.getOrCreateTagForRegistration($$0).bind($$1);
   }

   void refreshTagsInHolders() {
      Map<Holder.Reference<T>, List<TagKey<T>>> $$0 = new IdentityHashMap();
      this.byKey.values().forEach($$1 -> $$0.put($$1, new ArrayList()));
      this.allTags.forEach(($$1, $$2) -> {
         for(Holder<T> $$3 : $$2) {
            Holder.Reference<T> $$4 = this.validateAndUnwrapTagElement($$1, $$3);
            ((List)$$0.get($$4)).add($$1);
         }
      });
      $$0.forEach(Holder.Reference::bindTags);
   }

   public void bindAllTagsToEmpty() {
      this.validateWrite();
      this.frozenTags.values().forEach($$0 -> $$0.bind(List.of()));
   }

   @Override
   public HolderGetter<T> createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter<T>() {
         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> $$0) {
            return Optional.of(this.getOrThrow($$0));
         }

         @Override
         public Holder.Reference<T> getOrThrow(ResourceKey<T> $$0) {
            return MappedRegistry.this.getOrCreateHolderOrThrow($$0);
         }

         @Override
         public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
            return Optional.of(this.getOrThrow($$0));
         }

         @Override
         public HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
            return MappedRegistry.this.getOrCreateTagForRegistration($$0);
         }
      };
   }

   @Override
   public Registry.PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> $$0) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         Builder<TagKey<T>, HolderSet.Named<T>> $$1 = ImmutableMap.builder();
         final Map<TagKey<T>, List<Holder<T>>> $$2 = new HashMap();
         $$0.tags().forEach(($$2x, $$3x) -> {
            HolderSet.Named<T> $$4xx = (HolderSet.Named)this.frozenTags.get($$2x);
            if ($$4xx == null) {
               $$4xx = this.createTag($$2x);
            }

            $$1.put($$2x, $$4xx);
            $$2.put($$2x, List.copyOf($$3x));
         });
         final ImmutableMap<TagKey<T>, HolderSet.Named<T>> $$3 = $$1.build();
         final HolderLookup.RegistryLookup<T> $$4 = new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            public HolderLookup.RegistryLookup<T> parent() {
               return MappedRegistry.this;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
               return Optional.ofNullable($$3.get($$0));
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
               return $$3.values().stream();
            }
         };
         return new Registry.PendingTags<T>() {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
               return MappedRegistry.this.key();
            }

            @Override
            public int size() {
               return $$2.size();
            }

            @Override
            public HolderLookup.RegistryLookup<T> lookup() {
               return $$4;
            }

            @Override
            public void apply() {
               $$3.forEach(($$1, $$2xx) -> {
                  List<Holder<T>> $$3xxx = (List)$$2.getOrDefault($$1, List.of());
                  $$2xx.bind($$3xxx);
               });
               MappedRegistry.this.allTags = MappedRegistry.TagSet.fromMap($$3);
               MappedRegistry.this.refreshTagsInHolders();
            }
         };
      }
   }

   interface TagSet<T> {
      static <T> MappedRegistry.TagSet<T> unbound() {
         return new MappedRegistry.TagSet<T>() {
            @Override
            public boolean isBound() {
               return false;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
               throw new IllegalStateException("Tags not bound, trying to access " + $$0);
            }

            @Override
            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> $$0) {
               throw new IllegalStateException("Tags not bound");
            }

            @Override
            public Stream<HolderSet.Named<T>> getTags() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static <T> MappedRegistry.TagSet<T> fromMap(final Map<TagKey<T>, HolderSet.Named<T>> $$0) {
         return new MappedRegistry.TagSet<T>() {
            @Override
            public boolean isBound() {
               return true;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> $$0x) {
               return Optional.ofNullable((HolderSet.Named)$$0.get($$0));
            }

            @Override
            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> $$0x) {
               $$0.forEach($$0);
            }

            @Override
            public Stream<HolderSet.Named<T>> getTags() {
               return $$0.values().stream();
            }
         };
      }

      boolean isBound();

      Optional<HolderSet.Named<T>> get(TagKey<T> var1);

      void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1);

      Stream<HolderSet.Named<T>> getTags();
   }
}
