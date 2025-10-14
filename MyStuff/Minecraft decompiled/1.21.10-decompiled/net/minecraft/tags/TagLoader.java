package net.minecraft.tags;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   final TagLoader.ElementLookup<T> elementLookup;
   private final String directory;

   public TagLoader(TagLoader.ElementLookup<T> $$0, String $$1) {
      this.elementLookup = $$0;
      this.directory = $$1;
   }

   public Map<ResourceLocation, List<TagLoader.EntryWithSource>> load(ResourceManager $$0) {
      Map<ResourceLocation, List<TagLoader.EntryWithSource>> $$1 = new HashMap();
      FileToIdConverter $$2 = FileToIdConverter.json(this.directory);

      for(Entry<ResourceLocation, List<Resource>> $$3 : $$2.listMatchingResourceStacks($$0).entrySet()) {
         ResourceLocation $$4 = (ResourceLocation)$$3.getKey();
         ResourceLocation $$5 = $$2.fileToId($$4);

         for(Resource $$6 : (List)$$3.getValue()) {
            try {
               Reader $$7 = $$6.openAsReader();

               try {
                  JsonElement $$8 = StrictJsonParser.parse($$7);
                  List<TagLoader.EntryWithSource> $$9 = (List)$$1.computeIfAbsent($$5, $$0x -> new ArrayList());
                  TagFile $$10 = (TagFile)TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, $$8)).getOrThrow();
                  if ($$10.replace()) {
                     $$9.clear();
                  }

                  String $$11 = $$6.sourcePackId();
                  $$10.entries().forEach($$2x -> $$9.add(new TagLoader.EntryWithSource($$2x, $$11)));
               } catch (Throwable var16) {
                  if ($$7 != null) {
                     try {
                        $$7.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if ($$7 != null) {
                  $$7.close();
               }
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", $$5, $$4, $$6.sourcePackId(), var17);
            }
         }
      }

      return $$1;
   }

   private Either<List<TagLoader.EntryWithSource>, List<T>> tryBuildTag(TagEntry.Lookup<T> $$0, List<TagLoader.EntryWithSource> $$1) {
      SequencedSet<T> $$2 = new LinkedHashSet();
      List<TagLoader.EntryWithSource> $$3 = new ArrayList();

      for(TagLoader.EntryWithSource $$4 : $$1) {
         if (!$$4.entry().build($$0, $$2::add)) {
            $$3.add($$4);
         }
      }

      return $$3.isEmpty() ? Either.right(List.copyOf($$2)) : Either.left($$3);
   }

   public Map<ResourceLocation, List<T>> build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> $$0) {
      final Map<ResourceLocation, List<T>> $$1 = new HashMap();
      TagEntry.Lookup<T> $$2 = new TagEntry.Lookup<T>() {
         @Nullable
         @Override
         public T element(ResourceLocation $$0, boolean $$1x) {
            return (T)TagLoader.this.elementLookup.get($$0, $$1).orElse(null);
         }

         @Nullable
         @Override
         public Collection<T> tag(ResourceLocation $$0) {
            return (Collection<T>)$$1.get($$0);
         }
      };
      DependencySorter<ResourceLocation, TagLoader.SortingEntry> $$3 = new DependencySorter<>();
      $$0.forEach(($$1x, $$2x) -> $$3.addEntry($$1x, new TagLoader.SortingEntry($$2x)));
      $$3.orderByDependencies(
         ($$2x, $$3x) -> this.tryBuildTag($$2, $$3x.entries)
               .ifLeft(
                  $$1xx -> LOGGER.error(
                        "Couldn't load tag {} as it is missing following references: {}",
                        $$2x,
                        $$1xx.stream().map(Objects::toString).collect(Collectors.joining(", "))
                     )
               )
               .ifRight($$2xx -> $$1.put($$2x, $$2xx))
      );
      return $$1;
   }

   public static <T> void loadTagsFromNetwork(TagNetworkSerialization.NetworkPayload $$0, WritableRegistry<T> $$1) {
      $$0.resolve($$1).tags.forEach($$1::bindTag);
   }

   public static List<Registry.PendingTags<?>> loadTagsForExistingRegistries(ResourceManager $$0, RegistryAccess $$1) {
      return (List<Registry.PendingTags<?>>)$$1.registries()
         .map($$1x -> loadPendingTags($$0, $$1x.value()))
         .flatMap(Optional::stream)
         .collect(Collectors.toUnmodifiableList());
   }

   public static <T> void loadTagsForRegistry(ResourceManager $$0, WritableRegistry<T> $$1) {
      ResourceKey<? extends Registry<T>> $$2 = $$1.key();
      TagLoader<Holder<T>> $$3 = new TagLoader<>(TagLoader.ElementLookup.fromWritableRegistry($$1), Registries.tagsDirPath($$2));
      $$3.build($$3.load($$0)).forEach(($$2x, $$3x) -> $$1.bindTag(TagKey.create($$2, $$2x), $$3x));
   }

   private static <T> Map<TagKey<T>, List<Holder<T>>> wrapTags(ResourceKey<? extends Registry<T>> $$0, Map<ResourceLocation, List<Holder<T>>> $$1) {
      return (Map<TagKey<T>, List<Holder<T>>>)$$1.entrySet()
         .stream()
         .collect(Collectors.toUnmodifiableMap($$1x -> TagKey.create($$0, (ResourceLocation)$$1x.getKey()), Entry::getValue));
   }

   private static <T> Optional<Registry.PendingTags<T>> loadPendingTags(ResourceManager $$0, Registry<T> $$1) {
      ResourceKey<? extends Registry<T>> $$2 = $$1.key();
      TagLoader<Holder<T>> $$3 = new TagLoader<>(TagLoader.ElementLookup.fromFrozenRegistry($$1), Registries.tagsDirPath($$2));
      TagLoader.LoadResult<T> $$4 = new TagLoader.LoadResult<>($$2, wrapTags($$1.key(), $$3.build($$3.load($$0))));
      return $$4.tags().isEmpty() ? Optional.empty() : Optional.of($$1.prepareTagReload($$4));
   }

   public static List<HolderLookup.RegistryLookup<?>> buildUpdatedLookups(RegistryAccess.Frozen $$0, List<Registry.PendingTags<?>> $$1) {
      List<HolderLookup.RegistryLookup<?>> $$2 = new ArrayList();
      $$0.registries().forEach($$2x -> {
         Registry.PendingTags<?> $$3 = findTagsForRegistry($$1, $$2x.key());
         $$2.add($$3 != null ? $$3.lookup() : $$2x.value());
      });
      return $$2;
   }

   @Nullable
   private static Registry.PendingTags<?> findTagsForRegistry(List<Registry.PendingTags<?>> $$0, ResourceKey<? extends Registry<?>> $$1) {
      for(Registry.PendingTags<?> $$2 : $$0) {
         if ($$2.key() == $$1) {
            return $$2;
         }
      }

      return null;
   }

   public interface ElementLookup<T> {
      Optional<? extends T> get(ResourceLocation var1, boolean var2);

      static <T> TagLoader.ElementLookup<? extends Holder<T>> fromFrozenRegistry(Registry<T> $$0) {
         return ($$1, $$2) -> $$0.get($$1);
      }

      static <T> TagLoader.ElementLookup<Holder<T>> fromWritableRegistry(WritableRegistry<T> $$0) {
         HolderGetter<T> $$1 = $$0.createRegistrationLookup();
         return ($$2, $$3) -> ((HolderGetter<T>)($$3 ? $$1 : $$0)).get(ResourceKey.create($$0.key(), $$2));
      }
   }

   public static record EntryWithSource(TagEntry entry, String source) {
      final TagEntry entry;

      public String toString() {
         return this.entry + " (from " + this.source + ")";
      }
   }

   public static record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<TagKey<T>, List<Holder<T>>> tags) {
      final Map<TagKey<T>, List<Holder<T>>> tags;
   }

   static record SortingEntry(List<TagLoader.EntryWithSource> entries) implements DependencySorter.Entry<ResourceLocation> {
      final List<TagLoader.EntryWithSource> entries;

      @Override
      public void visitRequiredDependencies(Consumer<ResourceLocation> $$0) {
         this.entries.forEach($$1 -> $$1.entry.visitRequiredDependencies($$0));
      }

      @Override
      public void visitOptionalDependencies(Consumer<ResourceLocation> $$0) {
         this.entries.forEach($$1 -> $$1.entry.visitOptionalDependencies($$0));
      }
   }
}
